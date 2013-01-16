package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import play.Logger;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class Sessions {

	// a user UUID map the user access token
	private static Map<UUID, String> usersTokens = new HashMap<UUID, String>();
	private static Map<UUID, String> usersEmailAddress = new HashMap<UUID, String>();
	
	public static void registerToken(UUID userUUID, String accessToken) {
		Preconditions.checkNotNull(userUUID, "userUUID cannot be null");
		Preconditions.checkNotNull(accessToken, "accessToken cannot be null");
		usersTokens.put(userUUID, accessToken);
	}
	
	public static void registerEmailAddress(UUID userUUID, String emailAddress) { 
		Preconditions.checkNotNull(userUUID, "userUUID cannot be null");
		Preconditions.checkNotNull(emailAddress, "emailAddress cannot be null");
		usersEmailAddress.put(userUUID, emailAddress);
	}
	
	public static void removeUserData(UUID userUUID) {
		Preconditions.checkNotNull(userUUID, "userUUID cannot be null");
		usersTokens.remove(userUUID);
		usersEmailAddress.remove(userUUID);
		Logger.info("-> cleaning user token and email address");
	}
	
	public static Optional<String> getUserToken(UUID userUUID) {
		return Optional.fromNullable(usersTokens.get(userUUID));
	}
	
	public static Optional<String> getUserEmailAddress(UUID userUUID) {
		return Optional.fromNullable(usersTokens.get(userUUID));
	}
}
