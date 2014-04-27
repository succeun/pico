package hi.pico;

import hi.pico.engine.ControllerEngine;
import hi.pico.engine.ControllerMapper;
import hi.pico.engine.MethodMapper;

import java.util.ArrayList;
import java.util.List;

public class ControllerContext {
	private Class<?> controllerClass;
	private String controllerName;
	private String controllerClassName;
	private boolean isSingletonable = false;
	private String methodName;
	private ControllerEngine engine;
	private ControllerMapper controllerMapper;
	private List<MethodMapper> invokedMethodMappers = new ArrayList<MethodMapper>();
	
	public ControllerContext(ControllerEngine engine, ControllerMapper controllerMapper, Class<?> controllerClass, String controllerName,
			String controllerClassName, boolean isSingletonable,
			String methodName) {
		super();
		this.engine = engine;
		this.controllerMapper = controllerMapper;
		this.controllerClass = controllerClass;
		this.controllerName = controllerName;
		this.controllerClassName = controllerClassName;
		this.isSingletonable = isSingletonable;
		this.methodName = methodName;
	}
	
	public ControllerEngine getControllerEngine() {
		return engine;
	}
	
	public ControllerMapper getControllerMapper() {
		return controllerMapper;
	}
	
	public void addInvokedMethodMapper(MethodMapper invokedMethodMapper) {
		invokedMethodMappers.add(invokedMethodMapper);
	}
	
	public List<MethodMapper> getInvokedMethodMappers() {
		return invokedMethodMappers;
	}
	
	public Class<?> getControllerClass() {
		return controllerClass;
	}
	
	public String getControllerName() {
		return controllerName;
	}
	
	public String getControllerClassName() {
		return controllerClassName;
	}
	
	public boolean isSingletonable() {
		return isSingletonable;
	}

	public String getMethodName() {
		return methodName;
	}
}
