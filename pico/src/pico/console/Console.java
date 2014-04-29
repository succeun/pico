package pico.console;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pico.Before;
import pico.Parameter;
import pico.WebController;
import pico.WebMethod;
import pico.console.templates.ConsoleRoot;
import pico.engine.ControllerEngine;
import pico.view.FreeMarkerRenderer;
import pico.view.Redirect;
import pico.view.Renderer;
import pico.view.View;

@WebController
public class Console {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static String DEFAULT_PASSWORD = "1111";
	private static String consolePassword = DEFAULT_PASSWORD;
	
	/**
	 * 콘솔 암호를 설정한다.
	 * @param pwd
	 */
	public static void setConsolePassword(String pwd) {
		if (pwd != null) {
			consolePassword = pwd;
		}
	}
	
	public static String getConsolePassword() {
		return consolePassword;
	}
	
	protected static Renderer renderer = new FreeMarkerRenderer(ConsoleRoot.class);
	
	protected ServletContext context;
	protected HttpServletRequest req;
	protected HttpServletResponse res;
	protected HttpSession session;
	protected ControllerEngine engine;
	
	@Before
	public void onBeforeWithEngine(ControllerEngine engine) throws Exception {
		this.engine = engine;
	}
	
	@Before
	public void onBefore(ServletContext context, HttpServletRequest req, 
			HttpServletResponse res, String methodName) throws Exception {	
		res.setContentType("text/html");
		req.setCharacterEncoding("euc-kr");
		res.setCharacterEncoding("euc-kr");
		
		this.context = context;
		this.req = req;
		this.res = res;
		this.session = req.getSession();
	}
	
	// 인증 검사를 하지 말아야 할 메소드 제외
	@Before(unless={"login", "loginok"})
	public View onAuth(ServletContext context, HttpServletRequest req, 
			HttpServletResponse res, String methodName) throws Exception {	
        String tmp = (String) session.getAttribute("auth");
        if (!"true".equals(tmp)) {
            String query = req.getQueryString();
            String url = req.getRequestURI() + ((query != null) ? "?" + query : "");
            return new Redirect("../Login/login?" + new Parameter("url", url).toURLString("euc-kr"));
        } else { 
        	getSidebar();
        	return null;
        }
	}
	
	private void getSidebar() {
        Category root = new Category(req);
            new Category(req, root, "Home", Main.class,  "main");

            Category category = new Category(req, root, "Server");
            	new Category(req, category, "Config", MServer.class, "config");
            	new Category(req, category, "Thread", MServer.class, "thread");
            	new Category(req, category, "Memory", MServer.class, "memory");
            	
            category = new Category(req, root, "Engine");
            	new Category(req, category, "Engine Config", MEngine.class, "config");
            
            category = new Category(req, root, "Cron");
            	new Category(req, category, "Job List", MCron.class, "list");

        PluginManager.PluginEntry[] wentries = PluginManager.getPluginEntries();
        if (wentries != null && wentries.length > 0) {
        category = new Category(req, root, "Plugins");
            for (int i = 0; i < wentries.length;i++) {
                String id = wentries[i].getId();
                Category submenu = new Category(req, category, id);

                Plugin plugin = wentries[i].getPlugin();
                Iterator<Entry<String, String>> entries = plugin.getActions().entrySet().iterator();
                while (entries.hasNext()) {
                	Entry<String, String> entry = entries.next();
                    new Category(req, submenu, entry.getKey(), MPlugin.class, "action", "id=" + id + "&action=" + entry.getValue());
                }
            }
        }
        
        Category selectCategory = root.getByRequestURL(req);

        // 선택된 메뉴의 패스 기억
        String selectPath = "";
        if (selectCategory != null) {
            selectCategory.setSelect();
            selectPath = selectCategory.getPath(true);
            session.setAttribute("selectCategory", selectPath);
        } else {
            String str = (String) session.getAttribute("selectCategory");
            if (str != null)
                selectPath = str;
        }

        req.setAttribute("sidebar", root);
        req.setAttribute("selectPath", selectPath);
    }
	
	@WebMethod
	public View resource(String path) throws IOException {
		URL url = ConsoleRoot.class.getResource(path);
		logger.debug("Request Resource: {}", path);
		return renderer.resource(url);
	}
}
