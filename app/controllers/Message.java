package controllers;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.Base64;

public class Message {

	private String accessToken;
	private final String SERVICE = "talk.google.com";
	private final int PORT = 5222;
	private final String SERVER = "gmail.com";

	private final static String SASL_MECHANISM = "X-OAUTH2";

	private final class SASLGoogleOAuth2Mechanism extends SASLMechanism {

		private String clientID;
		private String clientSecret;
		private String accessToken;

		public SASLGoogleOAuth2Mechanism(SASLAuthentication saslAuthentication) {
			super(saslAuthentication);
		}

		@Override
		public void authenticate(String username, String host, CallbackHandler cbh) throws IOException, XMPPException {
			// Set the authenticationID as the username, since they must be the
			// same in this case.
			this.authenticationId = username;
			this.hostname = host;

			final TextInputCallback[] cbs = { new TextInputCallback("clientID"), new TextInputCallback("clientSecret"),
					new TextInputCallback("accessToken") };
			try {
				cbh.handle(cbs);
			} catch (final UnsupportedCallbackException e) {
				throw new IOException("UnsupportedCallback", e);
			}
			clientID = cbs[0].getText();
			clientSecret = cbs[1].getText();
			accessToken = cbs[2].getText();

			authenticate();
		}

		@Override
		protected void authenticate() throws IOException, XMPPException {
			final String raw = "\0" + this.authenticationId + "\0" + this.accessToken;
			final String authenticationText = Base64.encodeBytes(raw.getBytes("UTF-8"), Base64.DONT_BREAK_LINES);
			// Send the authentication to the server
			getSASLAuthentication().send(new Packet() {
				@Override
				public String toXML() {
					return "<auth mechanism=\"X-OAUTH2\"" + " auth:service=\"oauth2\"" + " xmlns:auth=\"http://www.google.com/talk/protocol/auth\""
							+ " xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" + authenticationText + "</auth>";
				}
			});
		}

		@Override
		protected String getName() {
			return SASL_MECHANISM;
		}
	}

	static {
		SASLAuthentication.registerSASLMechanism(SASL_MECHANISM, SASLGoogleOAuth2Mechanism.class);
		SASLAuthentication.supportSASLMechanism(SASL_MECHANISM);
	}

	public Message(String accessToken) {
		this.accessToken = accessToken;
	}

	public void login() {
		ConnectionConfiguration config = new ConnectionConfiguration(SERVICE, PORT, SERVER);
		XMPPConnection client = new XMPPConnection(config);
		config.setSASLAuthenticationEnabled(true);
		try {
			client.connect();
			// client.login("uneadressemaildetest@gmail.com",
			// "pourfaciliterledev");
			//client.getSASLAuthentication().authenticate(arg0, arg1, arg2);
			//client.login("matsuhar", accessToken);
		} catch (XMPPException e) {
			System.out.println("Error while connecting" + e);
		}
		/*
		 * Presence presence = new Presence(Presence.Type.available);
		 * client.sendPacket(presence);
		 * 
		 * //Chat chat =
		 * client.getChatManager().createChat("matsuhar@gmail.com", null); Chat
		 * chat =
		 * client.getChatManager().createChat("uneadressemaildetest@gmail.com",
		 * null);
		 * 
		 * if (chat != null) { try { chat.sendMessage("coucou"); } catch
		 * (XMPPException e) { System.out.println("Error while sending message"
		 * + e); } }
		 */
	}
}
