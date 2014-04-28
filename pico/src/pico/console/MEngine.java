package pico.console;

import javax.servlet.ServletException;

import pico.WebController;
import pico.WebMethod;
import pico.view.View;

@WebController
public class MEngine extends Console {
	@WebMethod
	public View config() throws ServletException
    {
        req.setAttribute("version", "0.1 Beta");
        String realPath = context.getRealPath("/");
        req.setAttribute("realPath", realPath.replaceAll("\\\\+", "/").replaceAll("/+", "/"));

        return renderer.forward(LAYOUT, "/MEngine/config.ftl");
    }
	
	@WebMethod
    public void reload() throws ServletException
    {
        engine.reload();
    }
}
