package hi.pico.console;

import hi.pico.WebController;
import hi.pico.WebMethod;
import hi.pico.view.View;

import javax.servlet.ServletException;

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
