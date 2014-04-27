package hi.pico.console;

import hi.pico.engine.ControllerEngine;
import hi.pico.engine.MethodMapper;
import hi.pico.engine.util.ClassUtil;
import hi.pico.view.View;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class Plugin {
	private Map<String, MethodMapper> mappers = null;
	private Map<String, String> actions = new HashMap<String, String>();
	
	private synchronized void initMethods() {
		if (mappers == null) {
			mappers = new HashMap<String, MethodMapper>();
			List<Method> methods = ClassUtil.findAllAnnotatedMethods(getClass(), PluginMethod.class);
			for (Method method : methods) {
				PluginMethod pm = method.getAnnotation(PluginMethod.class);
				String name = pm.name();
				String description = pm.description();
				boolean visiableMenu = pm.visiableMenu();
				
				method.setAccessible(true);
				mappers.put(method.getName(), new MethodMapper(method, description));
				
				if (visiableMenu) {
					name = (name == null || name.length() <= 0) ? method.getName() : name;
					actions.put(name, method.getName());
				}
			}
		}
    }
	
	/**
	 * 이름을 반환한다.
	 * @return 이름
	 */
	public abstract String getName();
	
	/**
     * 초기화 작업을 수행한다.
     */
    public abstract void init();

    /**
     * 파괴 작업을 수행한다.
     */
    public abstract void destroy();
	
	/**
     * 관리를 위한 Action의 메뉴명과 메소드명의 쌍을 반환한다.
     * @return Action  메뉴명과 메소드명의 맵
     */
    public Map<String, String> getActions() {
    	initMethods();
    	return actions;
    }
    
    /**
     * 해당하는 Action을 수행하여, freemarker 템플릿 HTML을 반환한다.
     * @param action 수행할 Action
     * @param engine ControllerEngine
     * @param req Request
     * @param res Response
     * @return 수행후 화면상에 HTML 내용
     * @throws Exception 
     */
    public View perform(String action, ControllerEngine engine, 
    		HttpServletRequest req, HttpServletResponse res,
    		ServletContext context, HttpSession session) throws Exception {
		MethodMapper methodMapper = mappers.get(action);
		
		Object obj = methodMapper.invoke(this, engine, req, res, context, session);
		if (obj != null && !(obj instanceof PluginView)) {
			methodMapper.render(context, req, res, null, obj);
			return null;
		} else {
			return (View) obj;
		}
    }
    
	public String getURIQueryString(HttpServletRequest req) {
		return req.getRequestURI() + "?" +req.getQueryString();
	}
}
