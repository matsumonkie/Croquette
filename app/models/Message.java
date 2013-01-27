package models;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

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

	private String authorPhoneNumber;
	private String recipient;
	private String content;
	private Action action;

	public Message(XMPPMessage xmppMsg) {
		this(xmppMsg.getAction(), xmppMsg.getAuthorPhoneNumber(), xmppMsg.getRecipient(), xmppMsg.getContent());
	}
	
	public Message(Action action, String authorPhoneNumber, String recipient, String content) {
		this.action = action;
		this.authorPhoneNumber = authorPhoneNumber;
		this.recipient = recipient;
		this.content = content;
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
	
	public ObjectNode asJson() {
		ObjectNode msg = Json.newObject();
		msg.put("authorPhoneNumber", authorPhoneNumber);
		msg.put("content", content);
		msg.put("recipient", recipient);
		
		return msg;
	}
}
