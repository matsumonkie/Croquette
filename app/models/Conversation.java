package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Ensemble de messages échangés (reçus et envoyés) entre un contact et l'utilisateur.
 * @author Julien & Iori
 */
public class Conversation {
	
	private Contact contact = null;
	private List<Message> messages = new ArrayList<Message>();
	
	
	/**
	 * Constructeur par défaut
	 */
	public Conversation() {
	}
	
	
	/**
	 * Constructeur par défaut
	 */
	public Conversation(Contact contact) {
		this.contact = contact;
	}
	
	
	/**
	 * Obtenir l'interlocuteur
	 * @return Contact
	 */
	public Contact getContact() {
		return contact;
	}
	
	
	/**
	 * Définir l'interlocuteur
	 * @param contact Contact
	 */
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	
	/**
	 * Obtenir la liste des messages
	 * @return Messages
	 */
	public List<Message> getMessages() {
		return messages;
	}
	
	
	/**
	 * Ajouter une message à la conversation
	 * @param message Message
	 */
	public void addMessage(Message message) {
		messages.add(message);
	}
}
