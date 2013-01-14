package controllers;

import java.util.Collection;
import java.util.UUID;

import models.Contact;
import models.Contacts;
import models.UserEmail;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
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
		Optional<UUID> userUUID = GoogleAuth.getUserUUID();

		if (userUUID.isPresent()) {
			Optional<String> accessToken = Sessions.getUserToken(userUUID.get());

			if (accessToken.isPresent()) {
				UserEmail email = new UserEmail();
				String userEmail = email.getUserEmailAddress(accessToken.get());

				Contacts contacts = new Contacts(accessToken.get());
				Collection<Contact> names = contacts.getContacts();
				return ok(mainView.render("main", names));
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
