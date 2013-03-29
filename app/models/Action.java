package models;

public enum Action {
	SEND_SMS("send-sms-action"), RECEIVE_SMS("receive-sms-action");

	private final String text;

	private Action(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public static Action fromString(String text) {
		if (text != null) {
			for (Action action : Action.values()) {
				if (text.equals(action.text))
					return action;
			}
		}
		return null;
	}
}
