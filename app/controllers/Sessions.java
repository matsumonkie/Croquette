package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import models.Contact;
import models.Contacts;
import models.Conversations;
import play.Logger;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * store access token and email address relative to a user uuid
 */
public class Sessions {

	private static final Config conf = ConfigFactory.load();
	private static final int MAX_USER_HANDLED = conf.getInt("max_user_handled");
	private static final int CONTACT_CACHE_EXPIRE_TIME_IN_MIN = conf.getInt("contact_cache_expire_time_in_min");
	
	private static Map<UUID, String> usersTokens = new HashMap<UUID, String>();
	private static Map<UUID, String> usersEmailAddresses = new HashMap<UUID, String>();
	private static Map<UUID, Conversations> usersConversations = new HashMap<UUID, Conversations>();
	private static LoadingCache<UUID, Collection<Contact>> usersContacts = CacheBuilder.newBuilder()
			.maximumSize(MAX_USER_HANDLED)
			.expireAfterWrite(CONTACT_CACHE_EXPIRE_TIME_IN_MIN, TimeUnit.MINUTES)
			.build(
				new CacheLoader<UUID, Collection<Contact>>() {
					@Override
					public Collection<Contact> load(UUID userUUID) throws Exception {
						Logger.info("Téléchargement des contacts Google...");
						String userToken = Sessions.getUserToken(userUUID).get();
						Collection<Contact> contacts = new Contacts(userToken).getContacts();
						return contacts;
					}
				});
	
	
	// Enregistrement
	public static void registerToken(UUID userUUID, String accessToken) {
		Preconditions.checkNotNull(userUUID, "userUUID cannot be null");
		Preconditions.checkNotNull(accessToken, "accessToken cannot be null");
		usersTokens.put(userUUID, accessToken);
	}
	
	
	public static void registerEmailAddress(UUID userUUID, String emailAddress) { 
		Preconditions.checkNotNull(userUUID, "userUUID cannot be null");
		Preconditions.checkNotNull(emailAddress, "emailAddress cannot be null");
		usersEmailAddresses.put(userUUID, emailAddress);
	}
	
	
	// Suppressions
	public static void removeUserData(UUID userUUID) {
		Preconditions.checkNotNull(userUUID, "userUUID cannot be null");
		usersTokens.remove(userUUID);
		usersEmailAddresses.remove(userUUID);
		usersConversations.remove(userUUID);
		Logger.info("-> cleaning user token and email address");
	}
	
	
	// Getters
	public static Optional<String> getUserToken(UUID userUUID) {
		return Optional.fromNullable(usersTokens.get(userUUID));
	}
	
	
	public static Optional<String> getUserEmailAddress(UUID userUUID) {
		return Optional.fromNullable(usersEmailAddresses.get(userUUID));
	}
	
	
	public static Conversations getUserConversations(UUID userUUID) {
		Conversations conversations = usersConversations.get(userUUID);
		if (conversations == null) {
			conversations = new Conversations();
			usersConversations.put(userUUID, conversations);
		}
		return conversations;
	}
	
	
	public static Collection<Contact> getUserContacts(UUID userUUID) {
		try {
			return usersContacts.get(userUUID);
		} catch (ExecutionException e) {
			return new ArrayList<Contact>();
		}
	}
}
