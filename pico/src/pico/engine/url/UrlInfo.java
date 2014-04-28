package pico.engine.url;

public class UrlInfo {
	private String classFullName;
	private String methodName;
	public UrlInfo(String classFullName, String methodName) {
		this.classFullName = classFullName;
		this.methodName = methodName;
	}
	
	public String getClassFullName() {
		return classFullName;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public void setClassFullName(String classFullName) {
		this.classFullName = classFullName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	
}
