package models;

import java.util.ArrayList;
import java.util.Collection;

/**
 * contains the messages send and receive to one same person 
 */
public class Conversation {
	
	private Collection <Message> conversation = new ArrayList<Message>();
	
	public void addMessage(Message message) {
		conversation.add(message);
	}
	
}
