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
	 * �̸��� ��ȯ�Ѵ�.
	 * @return �̸�
	 */
	public abstract String getName();
	
	/**
     * �ʱ�ȭ �۾��� �����Ѵ�.
     */
    public abstract void init();

    /**
     * �ı� �۾��� �����Ѵ�.
     */
    public abstract void destroy();
	
	/**
     * ������ ���� Action�� �޴���� �޼ҵ���� ���� ��ȯ�Ѵ�.
     * @return Action  �޴���� �޼ҵ���� ��
     */
    public Map<String, String> getActions() {
    	initMethods();
    	return actions;
    }
    
    /**
     * �ش��ϴ� Action�� �����Ͽ�, freemarker ���ø� HTML�� ��ȯ�Ѵ�.
     * @param action ������ Action
     * @param engine ControllerEngine
     * @param req Request
     * @param res Response
     * @return ������ ȭ��� HTML ����
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
