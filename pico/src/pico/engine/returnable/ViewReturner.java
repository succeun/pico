package pico.engine.returnable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;
import pico.view.View;

public class ViewReturner extends BaseReturner {

	public boolean isAvailable(Class<?> paramType) {
		return (View.class.isAssignableFrom(paramType));
	}

	public boolean render(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext,
			Object obj) throws Exception {
		if (obj != null) {
			((View) obj).render(context, req, res, controllerContext);
			return true;
		}
		
		return false;
	}

}
