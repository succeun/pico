package pico.engine;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pico.After;
import pico.ApplicationStart;
import pico.Before;
import pico.Catch;
import pico.ControllerContext;
import pico.CronMethod;
import pico.Finally;
import pico.MethodType;
import pico.MultipartConfig;
import pico.MultipartRequest;
import pico.NotFoundMethodException;
import pico.ServiceServlet;
import pico.WebController;
import pico.WebField;
import pico.WebMethod;
import pico.engine.util.ClassUtil;

/**
 * @author Eun Jeong-Ho, succeun@gmail.com
 * @version 2012. 3. 2
 */
public class ControllerMapper {
	private final Logger logger = LoggerFactory.getLogger(ServiceServlet.class);
	private String controllerName;
	private Class<?> controllerClass;
	private String controllerClassName;
	private boolean isSingletonable = false;
	private Object singletonController;
	private Object lock = new Object();
	
	private Map<String, MethodMapper> methodMappers = new HashMap<String, MethodMapper>();
	
	private Map<MethodMapper, MethodTypeAndUrls> methodMapperAndMethodTypeAndUrls = 
												new HashMap<MethodMapper, MethodTypeAndUrls>();
	
	private Map<String, MethodMapper> crons = new HashMap<String, MethodMapper>();
	
	private List<MethodMapper> applicationStarts = new ArrayList<MethodMapper>();
	private List<MethodMapper> befores = new ArrayList<MethodMapper>();
	private List<MethodMapper> afters = new ArrayList<MethodMapper>();
	private List<MethodMapper> catches = new ArrayList<MethodMapper>();
	private List<MethodMapper> allFinally = new ArrayList<MethodMapper>();
	
	private List<Field> resourceFields = new ArrayList<Field>();
	private MultipartConfig multipartConfig;
	
	public ControllerMapper(Class<?> controllerClass) throws IllegalAccessException, InstantiationException, ServletException {
		this.controllerClass = controllerClass;
		isSingletonable = controllerClass.getAnnotation(WebController.class).singleton();

		controllerClassName = this.controllerClass.getName();
		this.controllerName = controllerClassName.substring(controllerClassName.lastIndexOf(".") + 1);
		
		if (controllerClass.isAnnotationPresent(MultipartConfig.class)) {
			multipartConfig = controllerClass.getAnnotation(MultipartConfig.class);
		}
		
		initMethodMeppers();
	}

	public Class<?> getControllerClass() {
		return controllerClass;
	}

	public String getControllerName() {
		return controllerName;
	}

	private Object newController() throws IllegalAccessException, InstantiationException, ServletException {
		return controllerClass.newInstance();
	}

	private Object getController() throws IllegalAccessException, InstantiationException, ServletException {
		Object controller;

		if (isSingletonable) {
			if (singletonController == null) {
				synchronized (lock) {
					if (singletonController == null) {
						singletonController = newController();
					}
				}
			}
			controller = singletonController;
		} else {
			controller = newController();
		}

		return controller;
	}

	private void initMethodMeppers() throws ServletException {
		{
			List<Method> methods = ClassUtil.findAllAnnotatedMethods(controllerClass, CronMethod.class);
	        for (Method method : methods) {
	        	String description = method.getAnnotation(CronMethod.class).description();
	        	method.setAccessible(true);
	        	MethodMapper methodMapper = new MethodMapper(this, method, description);
	        	crons.put(methodMapper.getMethodName(), methodMapper);
	        	logger.info(controllerClassName + "#" + method.getName() + " in CronMethod");
	        }
		}
		
		{
			List<Method> methods = ClassUtil.findAllAnnotatedMethods(controllerClass, ApplicationStart.class);
	        Collections.sort(methods, new Comparator<Method>() {
	            public int compare(Method m1, Method m2) {
	            	ApplicationStart start1 = m1.getAnnotation(ApplicationStart.class);
	            	ApplicationStart start2 = m2.getAnnotation(ApplicationStart.class);
	                return start1.priority() - start2.priority();
	            }
	        });
	        for (Method method : methods) {
	        	String description = method.getAnnotation(ApplicationStart.class).description();
	        	method.setAccessible(true);
	        	applicationStarts.add(new MethodMapper(this, method, description));
	        	logger.info(controllerClassName + "#" + method.getName() + " in ApplicationStart");
	        }
		}
		
		{
			List<Method> methods = ClassUtil.findAllAnnotatedMethods(controllerClass, Before.class);
	        Collections.sort(methods, new Comparator<Method>() {
	            public int compare(Method m1, Method m2) {
	                Before before1 = m1.getAnnotation(Before.class);
	                Before before2 = m2.getAnnotation(Before.class);
	                return before1.priority() - before2.priority();
	            }
	        });
	        for (Method method : methods) {
	        	String description = method.getAnnotation(Before.class).description();
	        	method.setAccessible(true);
	        	befores.add(new MethodMapper(this, method, description));
	        	logger.info(controllerClassName + "#" + method.getName() + " in Before");
	        }
		}
        
		{
			List<Method> methods = ClassUtil.findAllAnnotatedMethods(controllerClass, After.class);
	        Collections.sort(methods, new Comparator<Method>() {
	            public int compare(Method m1, Method m2) {
	                After after1 = m1.getAnnotation(After.class);
	                After after2 = m2.getAnnotation(After.class);
	                return after1.priority() - after2.priority();
	            }
	        });
	        for (Method method : methods) {
	        	String description = method.getAnnotation(After.class).description();
	        	method.setAccessible(true);
	        	afters.add(new MethodMapper(this, method, description));
	        	logger.info(controllerClassName + "#" + method.getName() + " in After");
	        }
		}
        
		{
			List<Method> methods = ClassUtil.findAllAnnotatedMethods(controllerClass, Catch.class);
	        Collections.sort(methods, new Comparator<Method>() {
	            public int compare(Method m1, Method m2) {
	                Catch catch1 = m1.getAnnotation(Catch.class);
	                Catch catch2 = m2.getAnnotation(Catch.class);
	                return catch1.priority() - catch2.priority();
	            }
	        });
	        for (Method method : methods) {
	        	String description = method.getAnnotation(Catch.class).description();
	        	method.setAccessible(true);
	        	catches.add(new MethodMapper(this, method, description));
	        	logger.info(controllerClassName + "#" + method.getName() + " in Catch");
	        }
		}
        
		{
			List<Method> methods = ClassUtil.findAllAnnotatedMethods(controllerClass, Finally.class);
	        Collections.sort(methods, new Comparator<Method>() {
	            public int compare(Method m1, Method m2) {
	                Finally finally1 = m1.getAnnotation(Finally.class);
	                Finally finally2 = m2.getAnnotation(Finally.class);
	                return finally1.priority() - finally2.priority();
	            }
	        });
	        for (Method method : methods) {
	        	String description = method.getAnnotation(Finally.class).description();
	        	method.setAccessible(true);
	        	allFinally.add(new MethodMapper(this, method, description));
	        	logger.info(controllerClassName + "#" + method.getName() + " in Finally");
	        }
		}
		
		{
			List<Method> methods = ClassUtil.findAllAnnotatedMethods(controllerClass, WebMethod.class);
			for (Method method : methods) {
				WebMethod wm = method.getAnnotation(WebMethod.class);
				String[] urls = wm.url();
				MethodType[] methodTypes = wm.method();
				String description = wm.description();
				
				MethodMapper methodMapper = new MethodMapper(this, method, description);
				methodMappers.put(methodMapper.getMethodName(), methodMapper);
				if (urls != null && urls.length > 0) {
					List<String> urlList = new ArrayList<String>();
					for (String url : urls) {
						if (url != null && url.length() > 0)
							urlList.add(url);
					}
					if (urlList.size() > 0) {
						MethodTypeAndUrls methodTypeAndUrls = new MethodTypeAndUrls(urlList.toArray(new String[]{}), methodTypes);
						methodMapperAndMethodTypeAndUrls.put(methodMapper, methodTypeAndUrls);
						logger.info(controllerClassName + "#" + method.getName() + " in WebMethod : " + methodTypeAndUrls);
					}
				} else {
					logger.info(controllerClassName + "#" + method.getName() + " in WebMethod.");
				}
			}
		}
		
		{
			resourceFields = ClassUtil.findAllAnnotatedFields(controllerClass, WebField.class);
		}
	}
	
	private static class MethodTypeAndUrls {
		public String[] urls = null;
		public MethodType[] methodTypes = null;
		
		public MethodTypeAndUrls(String[] urls, MethodType[] methodTypes) {
			this.urls = urls;
			this.methodTypes = methodTypes; 
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (urls != null && urls.length > 0) {
				sb.append("Urls: ");
				sb.append(urls[0]);
				for (int i = 1; i < urls.length; i++)
					sb.append(", ").append(urls[i]);
			}
			
			if (methodTypes != null && methodTypes.length > 0) {
				sb.append(", Types: ");
				sb.append(methodTypes[0]);
				for (int i = 1; i < methodTypes.length; i++)
					sb.append(", ").append(methodTypes[i]);
			}
			
			return sb.toString(); 
		}
	}

	public MethodMapper[] getMethodMappers() {
		return methodMappers.values().toArray(new MethodMapper[0]);
	}

	public MethodMapper getMethodMapper(String methodName) {
		return methodMappers.get(methodName);
	}

	public void invoke(ControllerEngine engine, ServletContext context, HttpServletRequest req, HttpServletResponse res,
			String methodName) throws ServletException, IOException {
		Object controller = null;
		MethodMapper methodMapper = null;
		boolean isRendering = false;
		ControllerContext controllerContext = new ControllerContext(engine, this, controllerClass, controllerName, controllerClassName, isSingletonable, methodName);
		
		try {	
			controller = getController();
			
			if (multipartConfig != null && req.getMethod().equals("POST") && req.getContentType().startsWith("multipart/form-data")) {
				long reqMax = multipartConfig.maxRequestSize();
				long fileMax = multipartConfig.maxFileSize();
				int fileSizeThreshold = multipartConfig.fileSizeThreshold();
				String location = multipartConfig.location();
				String prefix = "@";	// 이것으로 시작하면, getRealPath을 기준으로 상대로 접근함
				if (location != null && location.startsWith(prefix)) {
					location = context.getRealPath(location.substring(prefix.length()));
				}
				req = new MultipartRequest(context, req, location, reqMax, fileSizeThreshold, System.getProperty("java.io.tmpdir"));
				((MultipartRequest)req).doUpload(location, fileMax);
			}
			
			setResourceFields(context, controllerContext, req, res, controller);

			methodMapper = getMethodMapper(methodName);
			if (methodMapper != null) {
				isRendering = handleBefores(context, controllerContext, req, res, methodName, controller, isRendering);
				
				isRendering = invokeMethod(context, controllerContext, req, res, methodMapper, controller, isRendering);
				
				isRendering = handleAfters(context, controllerContext, req, res, methodName, controller, isRendering);
			} else {
				throw new NotFoundMethodException(controllerClassName, methodName);
			}
		} catch (InvocationTargetException e) {
			try {
				isRendering = doException(context, controllerContext, req, res, methodName, controller, e.getCause(), isRendering);
			} catch (Exception inner) {
				if (inner instanceof ServletException) {
					throw (ServletException) inner;
				} else {
					throw new ServletException("Exception while doing @Catch", inner);
				}
			}
		} catch (NotFoundMethodException e) {
			try {
				isRendering = doException(context, controllerContext, req, res, methodName, controller, e, isRendering);
			} catch (Exception inner) {
				if (inner instanceof ServletException) {
					throw (ServletException) inner;
				} else {
					throw new ServletException("Exception while doing @Catch", inner);
				}
			}
		} catch (Exception e) {
			try {
				isRendering = doException(context, controllerContext, req, res, methodName, controller, e, isRendering);
			} catch (Exception inner) {
				if (inner instanceof ServletException) {
					throw (ServletException) inner;
				} else {
					throw new ServletException("Exception while doing @Catch", inner);
				}
			}
		} finally {
			try {
				isRendering = handleFinallies(context, controllerContext, req, res, methodName, controller, isRendering);
			} catch (Exception inner) {
				if (inner instanceof ServletException) {
					throw (ServletException) inner;
				} else {
					throw new ServletException("Exception while doing @Finally", inner);
				}
			}
		}
	}

	private boolean invokeMethod(ServletContext context, ControllerContext controllerContext, HttpServletRequest req,
			HttpServletResponse res, MethodMapper methodMapper, Object controller, boolean isRendering) throws Exception {
		if (!isRendering) {
			controllerContext.addInvokedMethodMapper(methodMapper);	// 메인 메소드 수행 이력 저장
			Object obj = methodMapper.invoke(controller, context, controllerContext, req, res);
			if (obj != null) {
				isRendering = methodMapper.render(context, req, res, controllerContext, obj);
			}
		}
		return isRendering;
	}

	private void setResourceFields(ServletContext context, ControllerContext controllerContext, HttpServletRequest req, HttpServletResponse res, 
			Object controller) throws IllegalArgumentException, IllegalAccessException, FileUploadException {
		for (Field field : resourceFields) {
			field.setAccessible(true);
			Class<?> paramType = field.getType();
			if (MultipartRequest.class.isAssignableFrom(paramType)) {
				if (!(req instanceof MultipartRequest))
					field.set(controller, new MultipartRequest(context, req));
				else
					field.set(controller, (MultipartRequest)req);
			} else if (HttpServletRequest.class.isAssignableFrom(paramType)
					|| ServletRequest.class.isAssignableFrom(paramType)) {
				field.set(controller, req);
			} else if (HttpServletResponse.class.isAssignableFrom(paramType)
					|| ServletResponse.class.isAssignableFrom(paramType)) {
				field.set(controller, res);
			} else if (HttpSession.class.isAssignableFrom(paramType)) {
				field.set(controller, req.getSession());
			} else if (ServletContext.class.isAssignableFrom(paramType)) {
				field.set(controller, context);
			} else if (ControllerEngine.class.isAssignableFrom(paramType)) {
				field.set(controller, controllerContext.getControllerEngine());
			} else if ( ControllerContext.class.isAssignableFrom(paramType)) {
				field.set(controller, controllerContext);
			}
		}
	}
	
	public MethodMapper getURLMapping(MethodType inMethodType, HttpServletRequest req) throws ServletException {
		//String pathInfo = req.getPathInfo();	// 서블릿 매핑의 컨텍스트를 포함하지 못하며, test.hi 같은 파일명 처리 못함 null 반환함 
		String pathInfo = req.getRequestURI();	// URL의 Full 경로 제공
		if (pathInfo != null && pathInfo.length() > 0) {
			Iterable<Map.Entry<MethodMapper, MethodTypeAndUrls>> itr =  methodMapperAndMethodTypeAndUrls.entrySet();
			for (Map.Entry<MethodMapper, MethodTypeAndUrls> entry : itr) {
				MethodMapper methodMapper = entry.getKey();
				MethodTypeAndUrls item = entry.getValue();
				for (MethodType type : item.methodTypes) {
					if (type == MethodType.All || type == inMethodType) {
						for (String url : item.urls) {
							//Logger.getLogger().debug(controllerClassName + "#" + methodMapper.getMethodName() + " have url: " + url);
							Map<String, String> values = UriTemplateParser.parseUriPattern(url, pathInfo);
							if (values != null) {
								Iterable<Map.Entry<String, String>> ita = values.entrySet();
								for (Map.Entry<String, String> value : ita) {
									req.setAttribute(value.getKey(), value.getValue());
								}
								return methodMapper;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public void startCrons(ControllerEngine engine, ServletConfig config) throws ServletException {
		try {
			Iterable<Map.Entry<String, MethodMapper>> itr =  crons.entrySet();
			for (Map.Entry<String, MethodMapper> entry : itr) {
				String methodName = entry.getKey();
				MethodMapper cron = entry.getValue();
				ControllerContext controllerContext = new ControllerContext(engine, this, controllerClass, controllerName, controllerClassName, isSingletonable, methodName);
				SchedulerManager.createJob(config, controllerContext, controllerClass, getController(), cron);
				
				logger.info(controllerClassName + "#" + cron.getMethodName() + " cron is started.");
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public Object startup(ControllerEngine engine, ServletConfig config) throws ServletException {
		Object result = null;
		Object controller = null;
		try {
			controller = getController();
			
			for (MethodMapper start : applicationStarts) {
				ControllerContext controllerContext = new ControllerContext(engine, this, controllerClass, controllerName, controllerClassName, isSingletonable, start.getMethodName());
				start.invoke(controller, config, config.getServletContext(), controllerContext, null, null);
			    logger.info(controllerName + "#" + start.getMethodName() + " is started.");
			}
		} catch (InvocationTargetException e) {
			throw new ServletException(e);
		} catch (NotFoundMethodException e) {
			throw new ServletException(e);
		} catch (Exception e) {
			throw new ServletException(e);
		}

		return result;
	}
	
	private boolean handleBefores(ServletContext context, ControllerContext controllerContext, HttpServletRequest req,
			HttpServletResponse res, String methodName, Object controller, boolean isRendering) throws Exception {
		for (MethodMapper before : befores) {
		    String[] unless = before.getMethod().getAnnotation(Before.class).unless();
		    String[] only = before.getMethod().getAnnotation(Before.class).only();
		    boolean skip = false;
		    for (String un : only) {
		        if (un.equals(methodName)) {
		            skip = false;
		            break;
		        } else {
		            skip = true;
		        }
		    }
		    for (String un : unless) {
		        if (un.equals(methodName)) {
		            skip = true;
		            break;
		        }
		    }
		    if (!skip) {
		    	before.getMethod().setAccessible(true);
		    	Object obj = before.invoke(controller, context, controllerContext, req, res);
		    	if (!isRendering && obj != null) {
                	boolean isRnd = before.render(context, req, res, controllerContext, obj);
    		    	if (isRnd)
    		    		return true;
		    	}
		    }
		}
		return false;
	}
	
	private boolean handleAfters(ServletContext context, ControllerContext controllerContext, HttpServletRequest req,
			HttpServletResponse res, String methodName, Object controller, boolean isRendering) throws Exception {
		for (MethodMapper after : afters) {
		    String[] unless = after.getMethod().getAnnotation(After.class).unless();
		    String[] only = after.getMethod().getAnnotation(After.class).only();
		    boolean skip = false;
		    for (String un : only) {
		        if (un.equals(methodName)) {
		            skip = false;
		            break;
		        } else {
		            skip = true;
		        }
		    }
		    for (String un : unless) {
		        if (un.equals(methodName)) {
		            skip = true;
		            break;
		        }
		    }
		    if (!skip) {
		    	after.getMethod().setAccessible(true);
		    	Object obj = after.invoke(controller, context, controllerContext, req, res);
		    	if (!isRendering && obj != null) {
                	isRendering = after.render(context, req, res, controllerContext, obj);
    		    	if (isRendering)
    		    		return isRendering;
		    	}
		    }
		}
		return false;
	}
	
	private boolean handleFinallies(ServletContext context, ControllerContext controllerContext, HttpServletRequest req,
			HttpServletResponse res, String methodName, Object controller, boolean isRendering) throws Exception {
		for (MethodMapper aFinally : allFinally) {
            String[] unless = aFinally.getMethod().getAnnotation(Finally.class).unless();
            String[] only = aFinally.getMethod().getAnnotation(Finally.class).only();
            boolean skip = false;
            for (String un : only) {
                if (un.equals(methodName)) {
                    skip = false;
                    break;
                } else {
                    skip = true;
                }
            }
            for (String un : unless) {
                if (un.equals(methodName)) {
                    skip = true;
                    break;
                }
            }
            if (!skip) {
                aFinally.getMethod().setAccessible(true);
                Object obj = aFinally.invoke(controller, context, controllerContext, req, res);
                if (!isRendering && obj != null) {
                	isRendering = aFinally.render(context, req, res, controllerContext, obj);
    		    	if (isRendering)
    		    		return isRendering;
		    	}
            }
        }
		return false;
	}

	private boolean doException(ServletContext context, ControllerContext controllerContext, HttpServletRequest req, HttpServletResponse res, 
			String methodName, Object controller, Throwable e, boolean isRendering) throws Exception {
		if (catches.size() > 0) {
			try {
				boolean isInvoked = false;
				for (MethodMapper mCatch : catches) {
                    Class<?>[] exceptions = mCatch.getMethod().getAnnotation(Catch.class).value();
                    // 어노테이션에 정의된  Exception이 없다면, 파라미터를 분석하여, 호출 가능여부를 확인 
                    if (exceptions.length == 0) {
                    	List<Class<?>> exceptionParams = new ArrayList<Class<?>>();
                    	Class<?>[] params = mCatch.getMethod().getParameterTypes();
                    	for (Class<?> param : params) {
                    		if (Exception.class.isAssignableFrom(param)) {
                    			exceptionParams.add(param);
                    		}
                    	}
                    	if (exceptionParams.size() > 0) {
                    		exceptions = exceptionParams.toArray(new Class[0]);
                    	} else {
                    		exceptions = new Class[]{Exception.class};
                    	}
                    }
                    // 파라미터에 정의된 Exception이 모두 발생한 Exception으로 변환 가능한지 확인이 되야 호출 할수 있다.
                    // 하나라도 타입 변환이 안된다면, "java.lang.IllegalArgumentException: argument type mismatch" 발생
                    int isSuccess = 0;
                    for (Class<?> exception : exceptions) {
                        if (exception.isInstance(e)) {
                        	isSuccess++;
                        }
                    }
                    
                    if (isSuccess == exceptions.length) {
                    	mCatch.getMethod().setAccessible(true);
	                    Object obj = mCatch.invoke(controller, null, context, controllerContext, req, res, e);
	                    isInvoked = true;
	                    
	                    if (!isRendering && obj != null) {
	                    	isRendering = mCatch.render(context, req, res, controllerContext, obj);
	        		    	if (isRendering)
	        		    		return isRendering;
	    		    	}
                    }
                }
				if (!isInvoked)
					throw new ServletException(e);
				
			} catch (Exception ex) {
				throw new ServletException(ex);
			}
		} else {
			throw new ServletException(e);
		}
		return false;
	}
}
