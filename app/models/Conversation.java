package models;

import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jackson.node.ArrayNode;
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
	public ArrayNode getConversationAsJson() {
		ObjectNode conv = Json.newObject();
		
		ArrayNode arrayNode = conv.putArray("conversation");
		for (Message msg : conversation) {
			arrayNode.add(msg.asJson());
		}

		return arrayNode;
	}
	
	
	public boolean isEmpty() {
		return conversation.isEmpty();
	}

}
