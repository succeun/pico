package hi.pico.engine;

import hi.pico.ControllerContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Returnable {
	public boolean isAvailable(Class<?> paramType);
	/**
	 * 
	 * @param context
	 * @param req
	 * @param res
	 * @param controllerContext
	 * @param obj
	 * @return ·»´õ¸µ ¿©ºÎ
	 * @throws Exception
	 */
	public boolean render(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext, Object obj) throws Exception;
}
