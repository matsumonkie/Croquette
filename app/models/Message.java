package models;

import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.JsonNode;

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

	/**
	 *  represent an xmpp message send from us to us 
	 */
	public static class XMPPMessage extends org.jivesoftware.smack.packet.Message {

		private JsonNode jsonMsg = null;
		
		public XMPPMessage(org.jivesoftware.smack.packet.Message msg) {
			// recreate the xmpp message
			super(msg.getTo(), Type.chat);
			super.setBody(msg.getBody());
			// parse the body to get the json
			jsonMsg = Json.parse(this.getBody());
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


	/**
	 * represents a simple message with the possibility of Json exportation  
	 */
	public static class BasicMessage {

		private String authorPhoneNumber;
		private String recipient;
		private String content;
		private Action action;

		
		public BasicMessage(XMPPMessage xmppMsg) {
			this(xmppMsg.getAction(), xmppMsg.getAuthorPhoneNumber(), xmppMsg.getRecipient(), xmppMsg.getContent());
		}

		
		public BasicMessage(Action action, String authorPhoneNumber, String recipient, String content) {
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
}
