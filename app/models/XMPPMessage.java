package models;

import org.codehaus.jackson.JsonNode;
import org.jivesoftware.smack.packet.Message;

import play.libs.Json;

/**
 *  
 */
public class XMPPMessage extends org.jivesoftware.smack.packet.Message {

	private JsonNode jsonMsg = null;
	
	public XMPPMessage(Message msg) {
		// recreate the xmpp message
		super(msg.getTo(), Type.chat);
		super.setBody(msg.getBody());
		// parse the body to get the json
		jsonMsg = Json.parse(this.getBody());
	}
	
	public boolean isSMSMessage() {
		if (jsonMsg != null && jsonMsg.has("action")) {
			String actionReceived = jsonMsg.findPath("action").getTextValue();
			if (actionReceived == models.Message.Action.RECEIVE_SMS.toString()) {
				return true;
			}
		}
		return false;
	}
	
	public String getAuthorPhoneNumber() {
		return jsonMsg.findValue("author").getTextValue();
	}
	
	public models.Message.Action getAction() {
		String action = jsonMsg.findValue("action").getTextValue();
		return models.Message.Action.valueOf(action);
	}
	
	public String getRecipient() {
		return jsonMsg.findValue("recipient").getTextValue();
	}
	
	public String getContent() {
		return jsonMsg.findValue("content").getTextValue();
	}

}
