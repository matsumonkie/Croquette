package controllers;

import java.util.UUID;

import models.Conversation;
import models.Conversations;
import models.XMPPMessage;
import models.Message.Action;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import play.Logger;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.mainView;

import com.google.common.base.Optional;

public class Application extends Controller {

	public static Result test() {
		Logger.info(">>>start");

		Logger.info(">>>end");
		return ok("coucou toi!");
	}

	public static Result index() {
		if (User.getUserUUID().isPresent()) {
			return ok(mainView.render("main"));
		}
		return redirect("/authenticate");
	}

	public static WebSocket<JsonNode> chat() {
		// we need the http context in the websocket handler
		final Context ctx = ctx();

		return new WebSocket<JsonNode>() {

			private XMPPConnectionHandler con; 
			
			public void onReady(WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {

				in.onMessage(new Callback<JsonNode>() {
					public void invoke(JsonNode event) {
						Logger.info(" -> New SMS : " + event);
						out.write(event);
						sendMsgTest(con);
					}
				});

				in.onClose(new Callback0() {
					public void invoke() {
						Logger.info(" -> Websocket closed");
						// we do not want to keep the listeners since we are going to create new ones
						con.deleteAliveListeners();
					}
				});

				handleIncomingMsg(out);
			}

			/*
			 * 
			 */
			private void handleIncomingMsg(WebSocket.Out<JsonNode> out) {
				// restore http context
				Context.current.set(ctx);
				UUID userUUID = User.getUserUUID().get();
				// init the xmpp connection
				//XMPPConnectionHandler con = 
						con = initXMPPConnectionHandler(userUUID);
				// get the chat so that we can create a xmpp listener
				Chat chat = con.getChat();
				saveNewMessage(out, userUUID, chat);
				
				/*for(int i = 0; i<10; i++) {
				sendMsgTest(con);				
				}*/
			}

			/**
			 * create the xmmp connection for the given user
			 */
			private XMPPConnectionHandler initXMPPConnectionHandler(UUID userUUID) {
				String emailAddress = Sessions.getUserEmailAddress(userUUID).get();
				String accessToken = Sessions.getUserToken(userUUID).get();

				XMPPConnectionHandler connection = new XMPPConnectionHandler(emailAddress, accessToken);
				connection.init();
				return connection;
			}
			
			private void sendMsgTest(XMPPConnectionHandler con) {
				models.Message msg = new models.Message(Action.RECEIVE_SMS, "0695617776", "matsuhar@gmail.com", "coucou");
				con.sendMessage(msg);
			}

			/**
			 *  
			 */
			private void saveNewMessage(final WebSocket.Out<JsonNode> out, final UUID userUUID, Chat chat) {
				chat.addMessageListener(new MessageListener() {
					@Override
					public void processMessage(Chat arg0, Message msg) {
						Logger.info("msg = " + msg.getBody());
						XMPPMessage xmppMsg = new XMPPMessage(msg);
						// if message received is originally an sms, handle it!
						if (xmppMsg.isSMSMessage()) {
							String authorPhoneNumber = xmppMsg.getAuthorPhoneNumber();
							// find corresponding conversation
							Conversations conversations = Sessions.getUserConversations(userUUID);
							Conversation conversation = conversations.getConversation(authorPhoneNumber);

							models.Message message = new models.Message(xmppMsg);
							conversation.addMessage(message);
							
							//finally notify the client a new sms has arrived
							notifyNewMessage(out, message);
						}
					}
				});
			}
			
			/**
			 * 
			 */
			public void notifyNewMessage(WebSocket.Out<JsonNode> out, models.Message newMsg) {
				out.write(newMsg.asJson());
			}

		};
	}

	/*
	 * sign off the user by cleaning caches, user sessions and disconnecting
	 * user from its gmail account
	 */
	public static Result SignOff() {
		Optional<UUID> userUUID = User.getUserUUID();
		Sessions.removeUserData(userUUID.get());
		
		session().clear();

		Logger.info("-> user logged out");
		return redirect("/authenticate");
	}
}
