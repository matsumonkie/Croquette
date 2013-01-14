package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Optional;

public class Sessions {

	private static Map<UUID, String> usersTokens = new HashMap<UUID, String>();
	
	public static void registerUser(UUID userUUID, String accessToken) { 
		usersTokens.put(userUUID, accessToken);
	}
	
	public static Optional<String> getUserToken(UUID userUUID) {
		return Optional.fromNullable(usersTokens.get(userUUID));
	}
}
