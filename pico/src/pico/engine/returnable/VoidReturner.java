package pico.engine.returnable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;

public class VoidReturner extends BaseReturner {

	public boolean isAvailable(Class<?> paramType) {
		return (void.class.isAssignableFrom(paramType));
	}

	public boolean render(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext,
			Object obj) throws Exception {
		return false;
	}

}