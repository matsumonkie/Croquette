package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import models.GoogleContacts;
import models.UserEmail;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.F.Promise;
import play.libs.WS.Response;
import play.libs.Json;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.mainView;

import com.google.common.base.Optional;
import com.google.gdata.data.contacts.ContactEntry;

public class Application extends Controller {

	public static void p(String str) {
		System.out.println(str);
	}

	public static Result test() {
		Logger.info(">>>start");

		Logger.info(">>>end");
		return ok("coucou toi!");
	}

	public static Result index() {
		// Message.sendMessage();
		Optional<UUID> userUUID = GoogleAuth.getUserUUID();
		if (userUUID.isPresent()) {
			Optional<String> accessToken = Sessions.getUserToken(userUUID.get());

			if (accessToken.isPresent()) {
				UserEmail email = new UserEmail();
				String userEmail = email.getUserEmailAddress(accessToken.get());

				Contacts contacts = new Contacts(accessToken.get());
				List<String> names = contacts.getContacts();
				return ok(mainView.render("test", names));
			}
		}
		return ok("error");
	}

	public static WebSocket<JsonNode> chat() {
		return new WebSocket<JsonNode>() {

			public void onReady(WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {

				in.onMessage(new Callback<JsonNode>() {
					public void invoke(JsonNode event) {
						Logger.info("new event + " + event);
						out.write(event);
					}
				});

				in.onClose(new Callback0() {
					public void invoke() {
						Logger.info("Disconnected");
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
}
