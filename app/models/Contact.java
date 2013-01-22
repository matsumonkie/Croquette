package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.google.gdata.data.extensions.PhoneNumber;

public class Contact implements Comparable<Contact> {

	private String name = "";
	private List<String> phoneNumbers = new ArrayList<String>();
	private Boolean notification = false;

	
	/**
	 * Constructeur
	 * @param name Nom
	 * @param phoneNumbers Numéros de téléphone
	 */
	public Contact(String name, Collection<PhoneNumber> phoneNumbers) {
		// name have a capital!  
		this.name = WordUtils.capitalizeFully(name.toLowerCase());
		
		for (PhoneNumber number : phoneNumbers) {
			this.phoneNumbers.add(number.getPhoneNumber());
		}
	}
	
	
	/**
	 * Obtenir le nom du contact
	 * @return Nom
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * Obtenir la liste des numéros de téléphone
	 * @return Numéros
	 */
	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}
	
	
	/**
	 * Contact ayant un message non lu
	 * @return True ou False
	 */
	public Boolean haveNotification() {
		return notification;
	}
	
	
	/**
	 * Comparaison de 2 contacts
	 * @param other Contact
	 * @return 
	 */
	public int compareTo(Contact other) {
		return this.name.compareToIgnoreCase(other.name);
	}
	
	
	/**
	 * Test d'égalité avec un objet
	 * @param obj Objet
	 * @return Vrai ou Faux
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Contact))
			return false;
		
		Contact objContact = (Contact) obj;
		if (! this.name.equals(objContact))
			return false;
		// TODO: test sur les numéro de téléphone ?
		
		return true;
	}
}
