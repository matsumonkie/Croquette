package models;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import play.Logger;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.xml.XmlWriter;

public class GoogleContacts {

	private static final String GROUPS_URL = "https://www.google.com/m8/feeds/groups/default/full";
	private static final String CONTACTS_URL = "https://www.google.com/m8/feeds/contacts/default/full";

	private String accessToken;
	
	public GoogleContacts(String accessToken) {
		this.accessToken = accessToken;
	}
	
	/**
	 * return the id of google main system group "My Contacts"
	 */
	public String getIdOfMyContactsGroup() {
		ContactsService contactsService = new ContactsService("Croquette");
		contactsService.setHeader("Authorization", "Bearer " + accessToken);

		try {
			URL feedUrl = new URL(GROUPS_URL);
			ContactGroupFeed resultFeed = contactsService.getFeed(feedUrl, ContactGroupFeed.class);
			// "My Contacts" group will always be the first one in the answer
			ContactGroupEntry entry = resultFeed.getEntries().get(0);

			return entry.getId();
		} catch (MalformedURLException e) {
			Logger.error(" creating URL:" + e);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * return the list of contacts present in "My Contacts" group 
	 * @param group
	 * @return
	 */
	public List<ContactEntry> getContacts(String group) {
		ContactsService contactsService = new ContactsService("Croquette");
		contactsService.setHeader("Authorization", "Bearer " + accessToken);

		try {
			URL feedUrl = new URL(CONTACTS_URL);
			Query myQuery = new Query(feedUrl);
			myQuery.setStringCustomParameter("group", group);
			myQuery.setMaxResults(1000);
			ContactFeed resultFeed = contactsService.query(myQuery, ContactFeed.class);
			List<ContactEntry> contactEntries = resultFeed.getEntries();
			dispAllContacts(contactEntries);
			/*
			ExtensionProfile extensionProfile = contactsService.getExtensionProfile();
			dispRawXML(resultFeed, extensionProfile);
			*/
			
			return contactEntries;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private static void dispRawXML(ContactFeed feed, ExtensionProfile extension) {
		StringWriter stringWriter = new StringWriter();
		try {
			XmlWriter xmlWriter = new XmlWriter(stringWriter, "UTF-8");
			feed.generate(xmlWriter, extension);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void dispAllContacts(List<ContactEntry> contacts) {
		for (ContactEntry entry : contacts) {
			if (entry.hasName()) {
				System.out.println(entry.getName().getFullName().getValue());
			}
		}
	}
}
