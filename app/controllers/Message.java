package controllers;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

public class Message {

	public static void sendMessage() {
		ConnectionConfiguration config;
		config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		XMPPConnection client = new XMPPConnection(config);

		try {
			client.connect();
		} catch (XMPPException e) {
			System.out.println("Error while connecting" + e);
		}

		try {
			client.login("uneadressemaildetest@gmail.com", "pourfaciliterledev");
		} catch (XMPPException e) {
			System.out.println("Error while login" + e);
		}

		Presence presence = new Presence(Presence.Type.available);
		client.sendPacket(presence);

		Chat chat = client.getChatManager().createChat("matsuhar@gmail.com", null);

		if (chat != null) {
			try {
				chat.sendMessage("coucou");
			} catch (XMPPException e) {
				System.out.println("Error while sending message" + e);
			}
		}
	}

}
