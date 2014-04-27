package hi.pico.view;

import hi.pico.ControllerContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Redirect extends View {
	private String location;
	private int code = HttpServletResponse.SC_FOUND;

	public Redirect(String location) {
		this.location = location;
	}

	public Redirect(String location, boolean permanent) {
		this.location = location;
		if (permanent)
			this.code = HttpServletResponse.SC_MOVED_PERMANENTLY;
	}

	public Redirect(String location, int code) {
		this.location = location;
		this.code = code;
	}

	public String getLocation() {
		return location;
	}

	public int getCode() {
		return code;
	}

	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		res.sendRedirect(location);
		if (code != HttpServletResponse.SC_FOUND)
			res.sendError(code);
	}
}
