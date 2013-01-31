package models;

import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

/**
 * contains the messages send and receive to one same person
 */
public class Conversation {

	private Collection<Message> conversation = new ArrayList<Message>();

	public void addMessage(Message message) {
		conversation.add(message);
	}

	/**
	 * create a json object containing a conversation 
	 */
	public ObjectNode getConversationAsJson() {
		ObjectNode conv = Json.newObject();
		
		conv.putArray("conversation");
		for (Message msg : conversation) {
			conv.put("message", msg.asJson());
		}

		return conv;
	}
	
	public boolean isEmpty() {
		return conversation.isEmpty();
	}

}
