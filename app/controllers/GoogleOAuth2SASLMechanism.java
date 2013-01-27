package controllers;

import java.io.IOException;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.Base64;

import play.Logger;

/**
 * class that implements the sasl mechanism to authenticate a user with google oauth 2.0
 */
public class GoogleOAuth2SASLMechanism extends SASLMechanism {

	public final static String GOOGLE_SASL_MECHANISM = "X-OAUTH2";
	private String accessToken;

	public GoogleOAuth2SASLMechanism(SASLAuthentication saslAuthentication) {
		super(saslAuthentication);
	}

	@Override
	public void authenticate(String username, String pass, String host) throws IOException, XMPPException {
		throw new XMPPException("Google doesn't support password authentication in OAuth2.");
	}

	@Override
	public void authenticate(String username, String hostname, CallbackHandler cbh) throws IOException, XMPPException {
		this.authenticationId = username;
		this.hostname = hostname;

		// retrieve the access token with the callback handler
		final TextInputCallback[] cbs = { new TextInputCallback("accessToken") };
		try {
			cbh.handle(cbs);
		} catch (final UnsupportedCallbackException e) {
			throw new IOException("UnsupportedCallback", e);
		}
		accessToken = cbs[0].getText();

		if (accessToken != null) {
			authenticate();
		}
	}

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
		return GOOGLE_SASL_MECHANISM;
	}
}
