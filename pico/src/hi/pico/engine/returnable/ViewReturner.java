package hi.pico.engine.returnable;

import hi.pico.ControllerContext;
import hi.pico.view.View;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
