package models;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Contient l'ensemble des conversations avec un numéro de téléphone
 * @author Julien & Iori
 */
public class ListConversations {
	
	Collection<Conversation> conversations = new ArrayList<Conversation>();
	
	
	/**
	 * Constructeur par défaut
	 */
	public ListConversations() {
	}
	
	
	/**
	 * Obtenir la liste des conversations
	 * @return Conversations
	 */
	public Collection<Conversation> getConversations() {
		return conversations;
	}
	
	
	/**
	 * Obtenir la conversation associée à au contact
	 * @param contact Contact
	 * @return Conversation
	 */
	public Conversation getConversation(String phoneNumber) {
		// Conversation existante
		for (Conversation conversation : conversations)
			if (conversation.getPhoneNumber().equals(phoneNumber))
				return conversation;
		
		// Nouvelle conversation
		Conversation newConversation = new Conversation(phoneNumber);
		conversations.add(newConversation);
		return newConversation;
	}
}
