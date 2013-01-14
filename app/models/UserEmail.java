package models;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.Response;

public class UserEmail {

	private static final String USER_EMAIL_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

	/**
	 * return the user email address
	 */
	public String getUserEmailAddress(String accessToken) {
		Promise<Response> promiseResponse = WS.url(USER_EMAIL_URL).setQueryParameter("access_token", accessToken).get();
		JsonNode jsonResponse = promiseResponse.get().asJson();
		return jsonResponse.findPath("email").asText();
	}
}
