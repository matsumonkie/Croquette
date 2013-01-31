package models;

import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.JsonNode;

import akka.actor.dsl.Creators.Act;

import play.Logger;
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

		public String getText() {
			return text;
		}

		public static Action fromString(String text) {
			if (text != null) {
				for (Action action : Action.values()) {
					if (text.equals(action.text))
						return action;
				}
			}
			return null;
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

		if (jsonMsg != null) {
			authorPhoneNumber = jsonMsg.findValue("authorPhoneNumber").getTextValue();
			recipient = jsonMsg.findValue("recipient").getTextValue();
			content = jsonMsg.findValue("content").getTextValue();

			String action = jsonMsg.findValue("action").getTextValue();
			Logger.info(action);
			if (action != null) {
				this.action = Action.fromString("receive-sms-action");
			}
		}
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
	public boolean isNewIncomingSMS() {
		if (jsonMsg != null) {
			if (action == models.Message.Action.RECEIVE_SMS) {
				return true;
			}
		}
		return false;
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
		msg.put("action", action.getText());
		msg.put("authorPhoneNumber", authorPhoneNumber);
		msg.put("content", content);
		msg.put("recipient", recipient);

		return msg;
	}
}
