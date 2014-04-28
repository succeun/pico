package pico;

import javax.servlet.ServletException;

@SuppressWarnings("serial")
public class NotFoundMethodException extends ServletException {
	private String methodName;
	private String className;

	public NotFoundMethodException(String className, String methodName) {
		super("Not found method: " + className + "#" + methodName);
		
		this.className = className;
		this.methodName = methodName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getMethodName() {
		return methodName;
	}
}
