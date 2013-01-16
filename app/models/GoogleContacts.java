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
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * retrieve raw google contacts using google api
 */
public class GoogleContacts {

	private static final String GROUPS_URL = "https://www.google.com/m8/feeds/groups/default/full";
	private static final String CONTACTS_URL = "https://www.google.com/m8/feeds/contacts/default/full";
	// max number of contacts to retrieve
	private static final int MAX_NB_CONTACTS = 1000;
	private static final String APPLICATION_NAME = ConfigFactory.load().getString("application_name");

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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * return the list of contacts present in "My Contacts" group
	 */
	public List<ContactEntry> getContacts(String group) {
		ContactsService contactsService = new ContactsService(APPLICATION_NAME);
		contactsService.setHeader("Authorization", "Bearer " + accessToken);

		try {
			URL feedUrl = new URL(CONTACTS_URL);
			Query myQuery = new Query(feedUrl);
			myQuery.setStringCustomParameter("group", group);
			myQuery.setMaxResults(MAX_NB_CONTACTS);
			ContactFeed resultFeed = contactsService.query(myQuery, ContactFeed.class);
			List<ContactEntry> contactEntries = resultFeed.getEntries();

			return contactEntries;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		return null;
	}
}
