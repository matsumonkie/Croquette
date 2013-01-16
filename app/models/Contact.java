package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.google.gdata.data.extensions.PhoneNumber;

public class Contact implements Comparable<Contact> {

	private String name;
	private List<String> phoneNumbers = new ArrayList<String>();

	public Contact (String name, Collection<PhoneNumber> phoneNumbers) {
		// name have a capital!  
		this.name = WordUtils.capitalizeFully(name.toLowerCase());
		
		for(PhoneNumber number : phoneNumbers) {
			this.phoneNumbers.add(number.getPhoneNumber());
		}
	}
	
	public int compareTo(Contact other) {
		return this.name.compareToIgnoreCase(other.name);
	}	
	
	public String getName() {
		return name;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}
}
