package hi.pico.view;

import hi.pico.ControllerContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Text extends AbstractData {
	private String contentType;
	private String text;

	public Text(String text) {
		this(null, text);
	}

	public Text(String contentType, String text) {
		this.contentType = contentType;
		this.text = text;
	}

	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		this.req = req;
		this.res = res;
		if (contentType == null || contentType.length() == 0)
			contentType = "text/plain";
		write(contentType, text);
	}
}
