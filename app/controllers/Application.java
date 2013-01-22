package controllers;

import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

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
		Optional<UUID> optUserUUID = User.getUserUUID();

		// user was on this website earlier
		if (optUserUUID.isPresent()) {
			UUID userUUID = optUserUUID.get();
			Logger.info("-> user already logged in");
			Optional<String> optAccessToken = Sessions.getUserToken(userUUID);

			// user grant access to his contacts
			if (optAccessToken.isPresent()) {
				return ok(mainView.render("main"));
			}
		}
		return redirect("/authenticate");
	}

	
	public static WebSocket<JsonNode> chat() {
		// we need the http context in the websocket handler
		final Context ctx = ctx();

		return new WebSocket<JsonNode>() {

			/*
			 * create a chat and write every new message from it to the websocket 
			 */
			public void handleIncomingXMPPMsg(WebSocket.Out<JsonNode> out) {
				// restore http context
				Context.current.set(ctx);
				
				UUID userUUID = User.getUserUUID().get();
				String emailAddress = Sessions.getUserEmailAddress(userUUID).get();
				String accessToken = Sessions.getUserToken(userUUID).get();

				XMPPConnectionHandler con = new XMPPConnectionHandler(emailAddress, accessToken);
				con.setAuthenticationConf();
				con.setPresenceAvailable();
				con.createChat();
				con.writeIncomingMsg(out);
			}
			
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
				
				handleIncomingXMPPMsg(out);
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
