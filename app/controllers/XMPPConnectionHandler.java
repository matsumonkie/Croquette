package controllers;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.codehaus.jackson.JsonNode;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import org.json.JSONObject;

import play.Logger;
import play.mvc.WebSocket;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class XMPPConnectionHandler {

	private String accessToken;
	private String login;
	private final String SERVICE = "talk.google.com";
	private final int PORT = 5222;
	private final String SERVER = "gmail.com";
	private XMPPConnection client;
	private Chat chat = null;

	private static final Config conf = ConfigFactory.load();
	private static final boolean XMPP_DEBUG_ENABLE = conf.getBoolean("xmpp_debug_enable");

	public XMPPConnectionHandler(String login, String accessToken) {
		this.login = login;
		this.accessToken = accessToken;
	}

	// register Google sasl mechanism and use it as default SASL mechanism for
	// every authentication
	static {
		SASLAuthentication.registerSASLMechanism(GoogleOAuth2SASLMechanism.GOOGLE_SASL_MECHANISM, GoogleOAuth2SASLMechanism.class);
		// 0 means google sasl mechanism is the prefered one
		SASLAuthentication.supportSASLMechanism(GoogleOAuth2SASLMechanism.GOOGLE_SASL_MECHANISM, 0);
	}

	/**
	 * create a callbackhandler to retrieve the access token in the
	 * SASLMechanism
	 */
	private class TokenCallbackHandler implements CallbackHandler {

		private String accessToken;

		TokenCallbackHandler(String accessToken) {
			this.accessToken = accessToken;
		}

		@Override
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (Callback cb : callbacks) {
				if (cb instanceof TextInputCallback) {
					TextInputCallback token = (TextInputCallback) cb;
					token.setText(accessToken);
				} else {
					throw new UnsupportedCallbackException(cb, "The submitted Callback is unsupported");
				}
			}
		}
	}

	/**
	 * set the configuration for authentication, we will use gtalk with SASL
	 * authentication
	 */
	public void setAuthenticationConf() {
		ConnectionConfiguration config = new ConnectionConfiguration(SERVICE, PORT, SERVER);
		config.setDebuggerEnabled(XMPP_DEBUG_ENABLE);
		// gtalk is using SASL authentication
		config.setSASLAuthenticationEnabled(true);
		client = new XMPPConnection(config);
		try {
			client.connect();
			TokenCallbackHandler token = new TokenCallbackHandler(accessToken);
			String response = client.getSASLAuthentication().authenticate(login, accessToken, token);
			Logger.info("user logged in : " + response);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public void setPresenceAvailable() {
		Presence presence = new Presence(Presence.Type.available);
		client.sendPacket(presence);
	}

	public void createChat() {
		chat = client.getChatManager().createChat(login, null);
		//chat = client.getChatManager().createChat("uneadressemaildetest@gmail.com", null);
	}

	public void listenChat() {
		if (chat != null) {
			chat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat arg0, Message arg1) {
					Logger.info("msg reçu : " + arg1.getBody());
				}
			});
		}
	}

	/**
	 * listen for new xmpp messages. If incoming xmpp message is originally an 
	 * SMS, write it to the websocket  
	 * @param out
	 */
	public void writeIncomingMsg (final WebSocket.Out<JsonNode> out) {
		if (chat != null) {
			chat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat arg0, Message msgReceived) {
					String body = msgReceived.getBody();
					models.Message msg = new models.Message();
					if (msg.isJson(body)) {
						JSONObject jsonObject = msg.convertStringToJSon(body);
						if (msg.isIncomingSMS(jsonObject)) {
							Logger.info("incoming message : " + body);
							JSonNode node = new JSonNode();
							out.write(jsonObject.);						
						}
					}
				}
			});
		}
	}

	public void sendMessage(String msg) {
		if (chat != null) {
			try {
				chat.sendMessage(msg);
			} catch (XMPPException e) {
				System.out.println("Error while sending message" + e);
			}
		}

	}
}
