package hi.pico.engine;

import hi.pico.MethodType;
import hi.pico.ServiceServlet;
import hi.pico.WebController;
import hi.pico.console.Plugin;
import hi.pico.console.PluginManager;
import hi.pico.engine.url.UrlInfo;
import hi.pico.engine.url.UrlResolver;
import hi.pico.engine.util.ClassFinder;
import hi.pico.engine.util.ClassUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eun Jeong-Ho, silver@hihds.com
 * @version 2010. 8. 1
 */
public final class ControllerEngine {
	private final Logger logger = LoggerFactory.getLogger(ServiceServlet.class);
	
	private Map<String, Class<?>> controllerClasses;
	private Map<Class<?>, ControllerMapper> controllerMappers;
    private UrlResolver urlResolver;
    private String[] controlPackageNames;
	private ServletConfig config;

    public void destory() {
    	controllerClasses = null;
    	controllerMappers = null;
    }

    public ControllerEngine(ServletConfig config,
    						String[] controlPackageNames, 
    						UrlResolver urlResolver) throws ServletException {
    	this.config = config;
    	this.controlPackageNames = controlPackageNames;
    	this.urlResolver = urlResolver;
        load();
    }

    public void reload() throws ServletException {
        destory();
        load();
    }
    
    public UrlResolver getURLResolver() {
    	return this.urlResolver;
    }

    private void load() throws ServletException {
    	controllerClasses = new HashMap<String, Class<?>>();
    	controllerMappers = Collections.synchronizedMap(new HashMap<Class<?>, ControllerMapper>());
    	
    	try {
			SchedulerManager.init();
		} catch (SchedulerException e) {
			throw new ServletException(e);
		}
	    
	    registClass();
    }
    
    /**
     * Controller 클래스를 기반으로, ControllerMapper를 반환한다.
     * @param controllerClass
     * @return ControllerMapper
     * @throws ServletException
     */
    public ControllerMapper getMapper(Class<?> controllerClass) throws ServletException {
        return controllerMappers.get(controllerClass);
    }

    public ControllerMapper[] getControllerMappers() {
        return controllerMappers.values().toArray(new ControllerMapper[0]);
    }
    
    private void registClass() throws ServletException {
		try {
			List<File> classpaths = new ArrayList<File>();
			classpaths.add(new File(config.getServletContext().getRealPath("/WEB-INF/classes")));
			File lib = new File(config.getServletContext().getRealPath("/WEB-INF/lib"));
			File[] jars = lib.listFiles();
			for (File jar : jars) {
				if (jar.getName().endsWith(".jar") || jar.getName().endsWith(".zip")) {
					classpaths.add(jar);
				}
			}
			
			List<Class<?>> ctrlClasses = new ArrayList<Class<?>>();
			List<Class<?>> pluginClasses = new ArrayList<Class<?>>();
			
			List<Class<?>> classes = new ClassFinder(classpaths).findSubclasses(controlPackageNames);
			for (Class<?> ctrlClass : classes) {
				if (ctrlClass.isAnnotationPresent(WebController.class)) {
					ctrlClasses.add(ctrlClass);
				}
				
				if (ctrlClass != Plugin.class && Plugin.class.isAssignableFrom(ctrlClass)) {
					pluginClasses.add(ctrlClass);
				}
			}
			
			for (Class<?> cls : pluginClasses) {
				PluginManager.register(cls);
			}
			
			for (Class<?> cls : ctrlClasses) {
				controllerClasses.put(cls.getName(), cls);
				ControllerMapper ctrlmapper = new ControllerMapper(cls);
                controllerMappers.put(cls, ctrlmapper);
			}
			
			{
				Iterable<ControllerMapper> ctrlmappers = controllerMappers.values();
				for(ControllerMapper ctrlmapper : ctrlmappers) {
					ctrlmapper.startCrons(this, config);
				}
			}
			
			if (config != null) {
				Iterable<ControllerMapper> ctrlmappers = controllerMappers.values();
				for(ControllerMapper ctrlmapper : ctrlmappers) {
					ctrlmapper.startup(this, config);
				}
			}
		} catch (InstantiationException e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		}
	}
    
    private Class<?> getControllerClass(String fullName) throws ClassNotFoundException {
        Class<?> cls = (Class<?>) controllerClasses.get(fullName);
        if (cls == null) {
            cls = ClassUtil.loadClass(fullName);
            if (cls.isAnnotationPresent(WebController.class)) {
            	controllerClasses.put(fullName, cls);
            } else {
            	cls = null;
            }
        }
        return cls;
    }

    public void invoke(MethodType methodType, ServletContext context, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	try
        {
    		MethodMapper methodMapper = getURLMapping(methodType, req);
    		if (methodMapper != null) {
    			methodMapper.getControllerMapper().invoke(this, context, req, res, methodMapper.getMethodName());
    		} else {
    			UrlInfo info = urlResolver.resolve(context, req, res);
    			
	            if (info != null) {
	            	String methodName = info.getMethodName();
	                String classFullName = info.getClassFullName();
	
	                if (classFullName != null && classFullName.length() > 0) {
	                    Class<?> ctrlClass = getControllerClass(classFullName);
	                    if (ctrlClass != null) {
	                        ControllerMapper ctrlmapper = getMapper(ctrlClass);
	                        ctrlmapper.invoke(this, context, req, res, methodName);
	                    } else {
	                    	throw new ServletException("Can't access. " + classFullName); 
	                    }
	                } else {
	                    throw new ServletException("Not understand url in [ex) /pakagename/classname/methodname]: " + req.getRequestURI());
	                }
	            } else {
	            	res.sendError(HttpServletResponse.SC_NOT_FOUND);
	            }
    		}
        } catch (ClassNotFoundException e) {
            throw new ServletException(e);
        }
    }
    
    public MethodMapper getURLMapping(MethodType inMethodType, HttpServletRequest req) throws ServletException {
    	// FIXME: 전체 컨트롤을 탐색하는것이 아니라, 해당 URL 정보에서 찾는 것이 빠를듯
    	Iterable<Map.Entry<Class<?>, ControllerMapper>> itr = controllerMappers.entrySet();
    	for (Map.Entry<Class<?>, ControllerMapper> entry : itr) {
    		MethodMapper methodMapper = entry.getValue().getURLMapping(inMethodType, req);
    		if (methodMapper != null) {
    			return methodMapper;
    		}
    	}
    	return null;
    }
}
