package pico.console;

import javax.servlet.ServletException;

import pico.WebController;
import pico.WebMethod;
import pico.view.View;

@WebController
public class Main extends Console {
	@WebMethod
	public View main() throws ServletException {
		return renderer.forward(LAYOUT, "/Main/main.ftl");
    }
}