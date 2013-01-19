package models;

/**
 * Message reçu ou envoyé par l'utilisateur.
 * @author Julien & Iori
 */
public class Message {
	
	private String text = "";
	private Boolean send = false;
	
	
	/**
	 * Constructeur par défaut
	 */
	public Message() {
	}
	
	
	/**
	 * Constructeur
	 * @param text Texte du message
	 */
	public Message(String text) {
		this.text = text;
	}
	
	
	/**
	 * Constructeur
	 * @param text Texte du message
	 * @param send True si c'est un message reçu, False si c'est l'utilisateur qui l'envoie
	 */
	public Message(String text, Boolean send) {
		this.text = text;
		this.send = send;
	}
	
	
	/**
	 * Obtenir le texte du message
	 * @return Texte
	 */
	public String getText() {
		return text;
	}
	
	
	/**
	 * Définir le texte du message
	 * @param text Texte
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	
	/**
	 * Messayé envoyé par l'utilisateur
	 * @return True ou False
	 */
	public Boolean sent() {
		return send;
	}
	
	
	/**
	 * Messayé reçu par l'utilisateur
	 * @return True ou False
	 */
	public Boolean received() {
		return !send;
	}
}
