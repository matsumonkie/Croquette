package models;

import java.util.Observer;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;

import play.Logger;

public class XMPPHandler implements MessageListener {

		private static Chat chat;
		private static XMPPConnection client = null;

		public XMPPHandler() {
			setXMPPConf();
			if (client != null) {
				connect();
				login();
				setPresenceAvailable();
				initChat();
			}
		}

		private static void setXMPPConf() {
			ConnectionConfiguration config = null;
//			config = new ConnectionConfiguration("talk.google.com", "gmail.com", "5222");
			client = new XMPPConnection(config);
		}

		private static void connect() {
			try {
				client.connect();
			} catch (XMPPException e) {
				Logger.error("Error while connecting", e);
			}
			Logger.info("client connected");
		}

		private static void login() {
			try {
				client.login("uneadressemaildetest@gmail.com", "pourfaciliterledev");
			} catch (XMPPException e) {
				Logger.error("Error while login", e);
			}
			Logger.info("client logged");
		}

		private static void setPresenceAvailable() {
			Presence presence = new Presence(Presence.Type.available);
			client.sendPacket(presence);
		}

		public static void sendMessage(String msg) {
			if (chat != null) {
				try {
					chat.sendMessage(msg);
				} catch (XMPPException e) {
					Logger.error("Error while sending message", e);
				}
			}
		}

		public static void sendMessage(Message msg) {
			if (chat != null) {
				try {
					chat.sendMessage(msg);
				} catch (XMPPException e) {
					Logger.error("Error while sending message", e);
				}
			}
		}

		public void initChat() {
			chat = client.getChatManager().createChat("matsuhar@gmail.com", this);
		}

		@Override
		public void processMessage(Chat arg0, Message arg1) {
			
		}

	}
