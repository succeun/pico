package pico.console;

import javax.servlet.ServletException;

import pico.WebController;
import pico.WebMethod;
import pico.console.templates.ConsoleRoot;
import pico.view.View;
import pico.view.Views;

@WebController
public class Main extends Console {
	@WebMethod
	public View main() throws ServletException {
		return forward("/Main/main.ftl");
    }
}