package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Ensemble de messages échangés (reçus et envoyés) entre un contact et l'utilisateur.
 * @author Julien & Iori
 */
public class Conversation {
	
	private String phoneNumber = "";
	private List<Message> messages = new ArrayList<Message>();
	
	
	/**
	 * Constructeur par défaut
	 */
	public Conversation() {
	}
	
	
	/**
	 * Constructeur par défaut
	 */
	public Conversation(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
	/**
	 * Obtenir l'interlocuteur
	 * @return Contact
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	
	/**
	 * Définir l'interlocuteur
	 * @param contact Contact
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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
