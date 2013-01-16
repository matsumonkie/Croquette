package models;

import org.codehaus.jackson.JsonNode;

import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.Response;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class UserEmail {

	private static final String USER_EMAIL_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

	/**
	 * return the user email address
	 */
	public String getUserEmailAddress(String accessToken) {
		Preconditions.checkNotNull(accessToken,	"accessToken cannot be null");
		
		Promise<Response> promiseResponse = WS.url(USER_EMAIL_URL).setQueryParameter("access_token", accessToken).get();
		JsonNode jsonResponse = promiseResponse.get().asJson();
		String email = jsonResponse.findPath("email").asText();
		
		if (email == null || email.isEmpty()) {
			throw new RuntimeException("email cannot be null");
		}
		
		return email;
	}
}
