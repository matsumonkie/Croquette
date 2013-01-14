package controllers;

import java.util.UUID;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.Response;

import com.google.common.base.Optional;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class GoogleAuth extends Controller {

	private static Config conf = ConfigFactory.load();
	private static String CLIENT_ID = conf.getString("google_client_id");
	private static String CLIENT_SECRET = conf.getString("google_client_secret");
	private static String REDIRECT_URI = conf.getString("google_redirect_uri");

	// Infos for user authorization and simple token
	private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/auth";

	private static final String CLIENT_ID_PARAM = "client_id";
	private static final String SCOPE_PARAM = "scope";
	private static final String REDIRECT_URI_PARAM = "redirect_uri";
	private static final String RESPONSE_TYPE_PARAM = "response_type";
	private static final String APPROVAL_PROMPT_PARAM = "approval_prompt";

	private static final String USER_EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	private static final String CONTACTS_SCOPE = "https://www.google.com/m8/feeds";
	private static final String RESPONSE_TYPE = "code";
	private static final String APPROVAL_PROMPT = "auto";

	// Infos for access token
	private static final String CLIENT_SECRET_PARAM = "client_secret";
	private static final String TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
	private static final String CODE_PARAM = "code";
	private static final String GRANT_TYPE_PARAM = "grant_type";
	private static final String GRANT_TYPE = "authorization_code";

	private static final String ACCESS_TOKEN_PARAM = "access_token";

	/**
	 * a user is connected if we have a session with its UUID
	 */
	public static boolean isConnected() {
		return session("userUUID") != null;
	}

	/**
	 * create a new random UUID
	 */
	private static UUID generateRandomUUID() {
		return UUID.randomUUID();
	}

	/**
	 * save the given uuid to the user session
	 */
	public static void saveUUIDToSession(UUID userUUID) {
		session("userUUID", userUUID.toString());
	}

	/**
	 * return the user UUID from the session if it exists
	 */
	public static Optional<UUID> getUserUUID() {
		String userUUID = session("userUUID");
		if (userUUID == null || userUUID.isEmpty()) {
			return Optional.absent();
		} else {
			return Optional.of(UUID.fromString(userUUID));
		}
	}

	/**
	 * Authenticate user using OAuth 2.0 if user is not already connected if
	 * user never gave his approval, ask for permission to retrieve his contacts
	 * and his email address. If user grants permission, go to registerUser just
	 * below
	 */
	public static Result authenticate() {
		StringBuilder rq = new StringBuilder(AUTH_URL);
		rq.append("?" + RESPONSE_TYPE_PARAM + "=" + RESPONSE_TYPE);
		rq.append("&" + CLIENT_ID_PARAM + "=" + CLIENT_ID);
		rq.append("&" + REDIRECT_URI_PARAM + "=" + REDIRECT_URI);
		rq.append("&" + SCOPE_PARAM + "=" + CONTACTS_SCOPE + "+" + USER_EMAIL_SCOPE);
		rq.append("&" + APPROVAL_PROMPT_PARAM + "=" + APPROVAL_PROMPT);

		return redirect(rq.toString());
	}

	/**
	 * 
	 */
	public static Result registerUser(String token) {
		String accessToken = null;

		if (token.isEmpty()) {
			// authentification failed, retry authentify
			return redirect("/authenticate");
		} else {
			Optional<UUID> uuid = getUserUUID();
			// if user is not registered in the application : no uuid yet or no access token
			if (!uuid.isPresent() || !Sessions.getUserToken(uuid.get()).isPresent()) {
				// build post request, execute it and retrieve the Json response
				JsonNode jsonResponse = postAccessTokenRequest(token);
				accessToken = jsonResponse.findPath(ACCESS_TOKEN_PARAM).asText();

				// generate a uuid and save it to the session
				UUID userUUID = generateRandomUUID();
				saveUUIDToSession(userUUID);
				// register uuid and access token in the application
				Sessions.registerUser(userUUID, accessToken);
			}
			return redirect("/");
		}
	}

	/**
	 * execute a post request to retrieve the access token from the given code
	 */
	private final static JsonNode postAccessTokenRequest(String code) {
		play.libs.WS.WSRequestHolder rq = WS.url(TOKEN_URL);
		rq.setHeader("Content-Type", "application/x-www-form-urlencoded");

		// build parameters
		StringBuilder parameters = new StringBuilder();
		parameters.append(CODE_PARAM + "=" + code);
		parameters.append("&" + CLIENT_ID_PARAM + "=" + CLIENT_ID);
		parameters.append("&" + CLIENT_SECRET_PARAM + "=" + CLIENT_SECRET);
		parameters.append("&" + REDIRECT_URI_PARAM + "=" + REDIRECT_URI);
		parameters.append("&" + GRANT_TYPE_PARAM + "=" + GRANT_TYPE);

		return rq.post(parameters.toString()).get().asJson();
	}

	/**
	 * disconnect the user by cleaning the whole session
	 */
	public static void disconnect() {
		session().clear();
	}
}
