package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang.WordUtils;

import com.google.common.base.Function;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.PhoneNumber;

public class Contact implements Comparable<Contact> {

	private String name = "";
	private List<String> phoneNumbers = new ArrayList<String>();
	
	public Contact(String name, Collection<PhoneNumber> phoneNumbers) {
		// name have a capital!  
		this.name = WordUtils.capitalizeFully(name.toLowerCase());
		
		for (PhoneNumber phoneNum : phoneNumbers) {
			// replace every characters that is not a digit
			String formatedPhoneNum = phoneNum.getPhoneNumber().replaceAll("\\D+","");
			this.phoneNumbers.add(formatedPhoneNum);
		}
	}
	
	
	public String getName() {
		return name;
	}

	
	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}
	
	
	public int compareTo(Contact other) {
		return this.name.compareToIgnoreCase(other.name);
	}
	
	
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Contact))
			return false;
		
		Contact objContact = (Contact) obj;
		if (! this.name.equals(objContact))
			return false;
		
		return true;
	}
}
