package models;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

/**
 * contains everyone conversations  
 */
public class Conversations {
	
	// associate a phone number to a conversation
	private Map<String, Conversation> conversations = new HashMap<String, Conversation>();
	
	public Conversation getConversation(String phoneNumber) {
		Conversation conv = conversations.get(phoneNumber);
		if (conv == null) {
			conv = new Conversation();
			conversations.put(phoneNumber, conv);
		}
		return conv;
	}
	
	public int size() { return conversations.size(); }
	
}
