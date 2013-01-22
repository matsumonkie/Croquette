package models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Message reçu ou envoyé par l'utilisateur.
 * 
 * @author Julien & Iori
 */
public class Message extends JSONObject {

	private String ACTION_PARAM = "action";
	private String SENDER_PARAM = "sender";
	private String RECIPIENT_PARAM = "recipient";
	private String BODY_PARAM = "body";
	
	private String RECEIVE_SMS_ACTION = "receive-sms-action";
	private String SEND_SMS_ACTION = "send-sms-action";

	/**
	 * Constructeur par défaut
	 */
	public Message(String action, String sender, String body) {
		try {
			put(ACTION_PARAM, action);
			put(RECIPIENT_PARAM, sender);
			put(BODY_PARAM, body);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Message(){}
	
	/**
	 * return wether msg is using json format 
	 */
	public boolean isJson(String msg) {
		try {
			new JSONObject(msg);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}
	
	public JSONObject convertStringToJSon(String msg) {
		JSONObject res = null;
		try {
			res = new JSONObject(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return res;
	}
	
	public boolean isIncomingSMS(JSONObject msg) {
		try {
			return msg.get(ACTION_PARAM).equals(RECEIVE_SMS_ACTION);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

}