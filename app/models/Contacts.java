package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.PhoneNumber;

/**
 * filter, sort, format google raw contacts 
 */
public class Contacts {

	private GoogleContacts googleContacts;

	/**
	 * filter the contact who only have at least a given name
	 */
	private final Predicate<ContactEntry> CONTACT_HAS_NAME = new Predicate<ContactEntry>() {
		@Override
		public boolean apply(ContactEntry contact) {
			if (contact.hasName()) {
				if (contact.getName().hasFullName() || contact.getName().hasGivenName()) {
					return true;
				}
			}
			return false;
		}
	};

	/**
	 * filter contacts who have at least one valid cellphone number
	 */
	private final Predicate<ContactEntry> CONTACT_HAS_VALID_PHONE_NUMBER = new Predicate<ContactEntry>() {
		@Override
		public boolean apply(ContactEntry contact) {
			if (contact.hasPhoneNumbers()) {
				List<PhoneNumber> numbers = contact.getPhoneNumbers();
				return !Collections2.filter(numbers, VALID_PHONE_NUMBER).isEmpty();
			}
			return false;
		}
	};

	/*
	 * check if phone number is valid, for now, only french numbers are
	 * available (e.g start with 06 for example)
	 */
	private final Predicate<PhoneNumber> VALID_PHONE_NUMBER = new Predicate<PhoneNumber>() {
		private List<String> allowedCellphonePrefix = Arrays.asList("06", "07", "+336", "+337");

		@Override
		public boolean apply(PhoneNumber phoneNumber) {
			for (String prefix : allowedCellphonePrefix) {
				if (phoneNumber.getPhoneNumber().startsWith(prefix)) {
					return true;
				}
			}
			return false;
		}
	};

	/**
	 * from a valid user name and valid phone numbers, create a contact
	 */
	private Function<ContactEntry, Contact> CREATE_CONTACT = new Function<ContactEntry, Contact>() {
		private String ANONYMOUS = "Anonymous";

		@Override
		public Contact apply(ContactEntry contact) {
			String contactName = ANONYMOUS;
			Collection<PhoneNumber> validPhoneNumbers = null;
			if (contact.hasName()) {
				Name name = contact.getName();
				if (name.hasFullName()) {
					contactName = name.getFullName().getValue();
				} else if (name.hasGivenName()) {
					contactName = name.getGivenName().getValue();
				}
			}
			if (contact.hasPhoneNumbers()) {
				List<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
				validPhoneNumbers = Collections2.filter(phoneNumbers, VALID_PHONE_NUMBER);
			}
			return new Contact(contactName, validPhoneNumbers);
		}
	};

	public Contacts(String accessToken) {
		googleContacts = new GoogleContacts(accessToken);
	}

	public Collection<Contact> getContacts() {
		// first retrieve "My Contact" group id
		String myContactId = googleContacts.getIdOfMyContactsGroup();
		// retrieve contacts from "My Contact" group
		List<ContactEntry> rawContacts = googleContacts.getContacts(myContactId);
		// filter contacts who do not have cellphone number or name
		Collection<ContactEntry> filteredContacts = filterContacts(rawContacts);
		// cast ContactEntry to simple Contact and sort them by name
		return castContacts(filteredContacts);
	}

	private Collection<ContactEntry> filterContacts(List<ContactEntry> contacts) {
		return Collections2.filter(contacts, Predicates.and(CONTACT_HAS_NAME, CONTACT_HAS_VALID_PHONE_NUMBER));
	}

	private Collection<Contact> castContacts(Collection<ContactEntry> contacts) {
		Collection<Contact> castedContacts = Collections2.transform(contacts, CREATE_CONTACT);
		List<Contact> sortedContacts = new ArrayList<Contact>(castedContacts);
		Collections.sort(sortedContacts);
		return sortedContacts;
	}

}
