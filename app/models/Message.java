package models;

import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.JsonNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

	private String authorPhoneNumber;
	private String recipient;
	private String content;
	private Action action;
	private DateTime date;

	/**
	 * retrieve the content to json format
	 */
	public Message(org.jivesoftware.smack.packet.Message xmppMsg) {
		this(Json.parse(xmppMsg.getBody()));
	}

	public Message(JsonNode jsonNode) {
		authorPhoneNumber = jsonNode.findValue("authorPhoneNumber").getTextValue();
		recipient = jsonNode.findValue("recipient").getTextValue();
		content = jsonNode.findValue("content").getTextValue();

		String action = jsonNode.findValue("action").getTextValue();
		if (action != null) {
			this.action = Action.fromString(action);
		}
		Logger.info("nom message : "+jsonNode);
		this.date = new DateTime();
	}

	public Message(Action action, String authorPhoneNumber, String recipient, String content) {
		this.action = action;
		this.authorPhoneNumber = authorPhoneNumber;
		this.recipient = recipient;
		this.content = content;
		this.date = new DateTime();
	}

	/**
	 * check wether the xmpp message is wrapping an sms
	 */
	public boolean isNewIncomingSMS() {
		return (action == models.Message.Action.RECEIVE_SMS);
	}

	public boolean messageToSend() {
		return (action == models.Message.Action.SEND_SMS);
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

		DateTimeFormatter fmt = DateTimeFormat.forPattern("HH'h'mm");
		msg.put("date", fmt.print(date));

		return msg;
	}
}
