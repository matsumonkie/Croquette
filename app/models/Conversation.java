package models;

import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

/**
 * contains the messages send and receive to one same person
 */
public class Conversation {

	private Collection<Message.BasicMessage> conversation = new ArrayList<Message.BasicMessage>();

	public void addMessage(Message.BasicMessage message) {
		conversation.add(message);
	}

	public ObjectNode getConversationAsJson() {
		ObjectNode conv = Json.newObject();
		
		conv.putArray("conversation");
		for (Message.BasicMessage msg : conversation) {
			conv.put("message", msg.asJson());
		}

		return conv;
	}

}
