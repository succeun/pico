package hi.pico.console;

import hi.pico.WebController;
import hi.pico.WebMethod;
import hi.pico.view.View;

import javax.servlet.ServletException;

@WebController
public class Main extends Console {
	@WebMethod
	public View main() throws ServletException {
		return renderer.forward(LAYOUT, "/Main/main.ftl");
    }
}