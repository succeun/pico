package hi.pico.engine.message;

public class Message {
	public static final String ERROR = "ERROR";
	public static final String WARN = "WARN";
	public static final String INFO = "INFO";
	private String code;
	private TypeLevel type;
	private String message;
	private ShowLevel show;

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public TypeLevel getType() {
		return this.type;
	}

	public void setType(TypeLevel type) {
		this.type = type;
	}

	public String getMessage() {
		return this.message;
	}

	public String getFullMessage() {
		return "[" + this.code + "] " + this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ShowLevel getShow() {
		return this.show;
	}

	public void setShow(ShowLevel show) {
		this.show = show;
	}

	@Override
	public Message clone() {
		Message newMsg = new Message();
		newMsg.setCode(this.code);
		newMsg.setMessage(this.message);
		newMsg.setType(this.type);
		newMsg.setShow(this.show);
		return newMsg;
	}
}
