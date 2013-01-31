package controllers;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.codehaus.jackson.JsonNode;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

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
	// Create a packet filter to listen for new messages from a particular
	// user. We use an AndFilter to combine two other filters.
	private PacketFilter filter;

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
	 * will create and init all the correct settings to be able to listen to
	 * incoming message
	 */
	public void init() {
		setAuthenticationConf();
		setPresenceAvailable();
		createChat();
	}

	/**
	 * remove every created listeners
	 */
	public void deleteAliveListeners() {
		for (MessageListener listener : chat.getListeners()) {
			chat.removeMessageListener(listener);
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

	/**
	 * just listen to message coming from us
	 */
	public void createChat() {
		chat = client.getChatManager().createChat(login, null);
	}

	public Chat getChat() {
		return chat;
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

	public void sendMessage(models.Message msg) {
		sendMessage(msg.asJson().toString());
	}
}
