package models;

import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.JsonNode;

import play.libs.Json;

/**
 * represents a message send from us to us with xmpp
 */
public class Message {

	public enum Action {
		SEND_SMS("send-sms-action"), RECEIVE_SMS("receive-sms-action");

		private final String text;

		private Action(String text) {
			this.text = text;
		}

		public String toString() {
			return this.text;
		}
	}

	private JsonNode jsonMsg = null;

	private String authorPhoneNumber;
	private String recipient;
	private String content;
	private Action action;

	/**
	 * retrieve the content to json format 
	 */
	public Message(org.jivesoftware.smack.packet.Message xmppMsg) {
		jsonMsg = Json.parse(xmppMsg.getBody());
	}

	public Message(Action action, String authorPhoneNumber, String recipient, String content) {
		this.action = action;
		this.authorPhoneNumber = authorPhoneNumber;
		this.recipient = recipient;
		this.content = content;
	}

	/**
	 * check wether the xmpp message is wrapping an sms
	 */
	public boolean isSMSMessage() {
		if (jsonMsg != null && jsonMsg.has("action")) {
			String actionReceived = jsonMsg.findPath("action").getTextValue();
			if (actionReceived == models.Message.Action.RECEIVE_SMS.toString()) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * retrieve elements from the xmpp message to create our object
	 */
	public void initMessage() {
		if (jsonMsg != null) {
			this.authorPhoneNumber = jsonMsg.findValue("author").getTextValue();
			String action = jsonMsg.findValue("action").getTextValue();
			this.action = models.Message.Action.valueOf(action);
			this.recipient = jsonMsg.findValue("recipient").getTextValue();
			this.content = jsonMsg.findValue("content").getTextValue();
		}
	}

	public String getAuthorPhoneNumber() {
		return authorPhoneNumber;
	}

	public String getRecipient() {
		return recipient;
	}

	public String getContent() {
		return content;
	}

	public Action getAction() {
		return action;
	}

	/**
	 * format message as a json object
	 */
	public ObjectNode asJson() {
		ObjectNode msg = Json.newObject();
		msg.put("authorPhoneNumber", authorPhoneNumber);
		msg.put("content", content);
		msg.put("recipient", recipient);

		return msg;
	}
}
