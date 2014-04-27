package hi.pico.console;

import hi.pico.WebController;
import hi.pico.WebMethod;
import hi.pico.engine.util.RSAAuth;
import hi.pico.view.View;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.ServletException;

@WebController
public class Login extends Console {
	@WebMethod
	public View login() throws ServletException
    {
		// 공개키, 개인키 생성 및 보관
		RSAAuth.encrypt(req);
		
		req.setAttribute("message", "");
        return renderer.forward("/Login/login.ftl");
    }
	
	@WebMethod
    public View loginok() throws ServletException, IOException
    {
        // 공개키로 암호화된 데이터를 개인키로 복호화하여 반환  
		Map<String, String> values = RSAAuth.decrypt(req, "password");
		String password = values.get("password");
        
        if (getConsolePassword().equals(password)) {
        	session.setAttribute("auth", "true");
            String url = req.getParameter("url");
            if (url != null && url.length() > 0) {
            	url = URLDecoder.decode(url, "euc-kr");
            	url = replace(url);
            	return renderer.redirect(url);
            } else {
            	return renderer.redirect("../Main/main");
            }
        } else {
            req.setAttribute("message", "비밀번호가 틀렸습니다.");
            return renderer.forward("/Login/login.ftl");
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
			new String[] {"&amp;", "&"},
			new String[] {"&lt;", "<"},
			new String[] {"&gt;", ">"},
			new String[] {"&quot;", "\""}
	};
}
