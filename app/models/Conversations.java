package models;

import java.util.HashMap;
import java.util.Map;

/**
 * contains everyone conversations  
 */
public class Conversations {
	
	// associate a phone number to a conversation
	private Map<String, Conversation> conversations = new HashMap<String, Conversation>();
	
	public Conversation getConversation(String phoneNumber) {
		return conversations.get(phoneNumber);
	}
	
	
}
