package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import models.Contact;
import models.Contacts;
import models.Conversation;
import models.ListConversations;
import play.Logger;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * store access token and email address relative to a user uuid
 */
public class Sessions {

	private static Map<UUID, String> usersTokens = new HashMap<UUID, String>();
	private static Map<UUID, String> usersEmailAddresses = new HashMap<UUID, String>();
	private static Map<UUID, Collection<Contact>> usersContacts = new HashMap<UUID, Collection<Contact>>();
	private static Map<UUID, ListConversations> usersConversations = new HashMap<UUID, ListConversations>();
	
	
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
	
	
	// Suppression
	public static void removeUserData(UUID userUUID) {
		Preconditions.checkNotNull(userUUID, "userUUID cannot be null");
		usersTokens.remove(userUUID);
		usersEmailAddresses.remove(userUUID);
		Logger.info("-> cleaning user token and email address");
	}
	
	
	// Getters
	public static Optional<String> getUserToken(UUID userUUID) {
		return Optional.fromNullable(usersTokens.get(userUUID));
	}
	
	
	public static Optional<String> getUserEmailAddress(UUID userUUID) {
		return Optional.fromNullable(usersEmailAddresses.get(userUUID));
	}
	
	
	// TODO: guava
	public static Collection<Contact> getUserContacts(UUID userUUID) {
		Collection<Contact> contacts = usersContacts.get(userUUID);
		if (contacts == null) {
			Logger.info("Téléchargement des contacts...");
			
			String userToken = Sessions.getUserToken(userUUID).get();
			contacts = new Contacts(userToken).getContacts();
			usersContacts.put(userUUID, contacts);
		}
		return contacts;
	}
	
	
	public static ListConversations getUserConversations(UUID userUUID) {
		ListConversations conversations = usersConversations.get(userUUID);
		if (conversations == null) {
			conversations = new ListConversations();
			usersConversations.put(userUUID, conversations);
		}
		return conversations;
	}
}
