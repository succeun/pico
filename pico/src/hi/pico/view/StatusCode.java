package hi.pico.view;

import hi.pico.ControllerContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatusCode extends View {
	private int code = HttpServletResponse.SC_FOUND;

	public StatusCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		res.setStatus(code);
	}
}
