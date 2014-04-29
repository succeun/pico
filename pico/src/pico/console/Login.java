package pico.console;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.ServletException;

import pico.WebController;
import pico.WebMethod;
import pico.console.templates.ConsoleRoot;
import pico.engine.util.RSAAuth;
import pico.view.View;
import pico.view.Views;

@WebController
public class Login extends Console {
	@WebMethod
	public View login() throws ServletException {
		// ����Ű, ����Ű ���� �� ����
		RSAAuth.encrypt(req);

		req.setAttribute("message", "");
		return forward("/Login/login.ftl");
	}

	@WebMethod
	public View loginok() throws ServletException, IOException {
		// ����Ű�� ��ȣȭ�� �����͸� ����Ű�� ��ȣȭ�Ͽ� ��ȯ
		Map<String, String> values = RSAAuth.decrypt(req, "password");
		String password = values.get("password");

		if (getConsolePassword().equals(password)) {
			session.setAttribute("auth", "true");
			String url = req.getParameter("url");
			if (url != null && url.length() > 0) {
				url = URLDecoder.decode(url, "euc-kr");
				url = replace(url);
				return Views.redirect(url);
			} else {
				return Views.redirect("../Main/main");
			}
		} else {
			req.setAttribute("message", "��й�ȣ�� Ʋ�Ƚ��ϴ�.");
			return forward("/Login/login.ftl");
		}
	}

	private String replace(String value) {
		if (value == null) {
			return null;
		} else {
			for (int i = 0; i < chars.length; i++) {
				value = value.replace(chars[i][0], chars[i][1]);
			}
			return value;
		}
	}

	private static String[][] chars = new String[][] {
			new String[] { "&amp;", "&" }, new String[] { "&lt;", "<" },
			new String[] { "&gt;", ">" }, new String[] { "&quot;", "\"" } };
}
