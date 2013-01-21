package controllers;

import java.util.Collection;
import java.util.UUID;

import models.Contact;
import models.Contacts;
import models.UserEmail;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.cache.Cache;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.Controller;
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
		Optional<UUID> optUserUUID = User.getUserUUID();

		// user was on this website earlier 
		if (optUserUUID.isPresent()) {
			UUID userUUID = optUserUUID.get();
			Logger.info("-> user already logged in");
			Optional<String> optAccessToken = Sessions.getUserToken(userUUID);
			
			// user grant access to his contacts
			if (optAccessToken.isPresent()) {
				String accessToken = optAccessToken.get();
				
				String emailAddress = getUserEmailAddress(userUUID, accessToken);
				Collection<Contact> contacts = Sessions.getUserContacts(userUUID);
				
				XMPPConnectionHandler con = new XMPPConnectionHandler(emailAddress, accessToken);
				con.setAuthenticationConf();
				con.setPresenceAvailable();
				con.sendMessage("coucou");
				
				return ok(mainView.render("main", contacts));
			}
		}
		return redirect("/authenticate");
	}

	
	public static WebSocket<JsonNode> chat() {
		return new WebSocket<JsonNode>() {

			public void onReady(WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {

				in.onMessage(new Callback<JsonNode>() {
					public void invoke(JsonNode event) {
						Logger.info(" -> New SMS : " + event);
						out.write(event);
					}
				});

				in.onClose(new Callback0() {
					public void invoke() {
						Logger.info(" -> Websocket closed");
					}
				});
			}
		};
	}

	
	public static void sendMsg(WebSocket.Out<JsonNode> out, String author, String content) {
		ObjectNode event = Json.newObject();
		event.put("author", author);
		event.put("content", content);

		out.write(event);
	}

	
	/*
	 * sign off the user by cleaning caches, user sessions 
	 * and disconnecting user from its gmail account
	 */
	public static Result SignOff() {
		Optional<UUID> userUUID = User.getUserUUID();
		Sessions.removeUserData(userUUID.get());

		session().clear();
		
		Logger.info("-> user logged out");
		return redirect("/authenticate");
	}
}
