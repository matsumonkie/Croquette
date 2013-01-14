package controllers;

import java.util.ArrayList;
import java.util.List;

import models.GoogleContacts;
import structures.Contact;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.gdata.data.contacts.ContactEntry;

public class Contacts {

	private String accessToken;
	private GoogleContacts googleContacts;
	private List<Contact> contacts = new ArrayList<Contact>();
	
	public Contacts(String accessToken) {
		this.accessToken = accessToken;
		googleContacts = new GoogleContacts(accessToken);
	}
	
	public List<String> getContacts() {
		String myContactId = googleContacts.getIdOfMyContactsGroup();
		List<ContactEntry> rawContacts = googleContacts.getContacts(myContactId);
		List<ContactEntry> filteredContacts = filterContacts(rawContacts);
		return castGoogleContacts(rawContacts);
	}
	
	private List<ContactEntry> filterContacts(List<ContactEntry> contacts) {
		
		return contacts;
		//Collections2.filter(contacts, predicate);
	}
	
	//Predicate<ContactEntry>
	
	private List<String> castGoogleContacts(List<ContactEntry> entries) {
		List<String> fullnames = new ArrayList<String>();
		for (ContactEntry contact : entries) {
			if (contact.hasName()) {
				fullnames.add(contact.getName().getFullName().getValue());
			}
		}
		return fullnames;
	}
}
