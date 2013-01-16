package controllers;

import java.util.UUID;

import com.google.common.base.Optional;

import play.mvc.Controller;

public class User extends Controller {
	
	private static final String USER_UUID_SESSION_PARAM = "userUUID";
	
	/**
	 * a user is connected if we have a session with its UUID
	 */
	public static boolean isConnected() {
		return session(USER_UUID_SESSION_PARAM) != null;
	}

	/**
	 * create a new random UUID
	 */
	public static UUID generateRandomUUID() {
		return UUID.randomUUID();
	}

	/**
	 * save the given uuid to the user session
	 */
	public static void saveUserUUIDToSession(UUID userUUID) {
		session(USER_UUID_SESSION_PARAM, userUUID.toString());
	}

	/**
	 * return the user UUID from the session if it exists
	 */
	public static Optional<UUID> getUserUUID() {
		if (!isConnected()) {
			return Optional.absent();
		} else {
			return Optional.of(UUID.fromString(session(USER_UUID_SESSION_PARAM)));
		}
	}
}
