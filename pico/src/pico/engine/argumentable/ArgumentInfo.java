package pico.engine.argumentable;

import pico.ArgumentType;

public final class ArgumentInfo {
	private Class<?> parameterType;
	private String parameterName;
	private ArgumentType argumentType;
	private boolean isRequestBody;
	private String argumentName;
	private String defaultValue;
	
	public ArgumentInfo(String parameterName, Class<?> parameterType, String argumentName,
			ArgumentType argumentType, boolean isRequestBody, String defaultValue) {
		this.parameterName = parameterName;
		this.parameterType = parameterType;
		this.argumentName = argumentName;
		this.argumentType = argumentType;
		this.isRequestBody = isRequestBody;
		this.defaultValue = defaultValue;
	}

	public Class<?> getParameterType() {
		return parameterType;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public ArgumentType getArgumentType() {
		return argumentType;
	}
	
	public String getArgumentName() {
		return argumentName;
	}
	
	public boolean isRequestBody() {
		return isRequestBody;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}
	
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	
	public void setArgumentType(ArgumentType argumentType) {
		this.argumentType = argumentType;
	}
	
	public void setArgumentName(String argumentName) {
		this.argumentName = argumentName;
	}
	
	public void setRequestBody(boolean isRequestBody) {
		this.isRequestBody = isRequestBody;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public String getName() {
		if (argumentName != null && argumentName.length() > 0)
			return argumentName;
		return parameterName;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("{");
		buf.append("parameterName=").append(parameterName);
		buf.append(",parameterType=").append(parameterType);
		buf.append(",argumentName=").append(argumentName);
		buf.append(",argumentType=").append(argumentType);
		buf.append(",isRequestBody=").append(isRequestBody);
		buf.append(",defaultValue=").append(defaultValue);
		buf.append("}");
		return buf.toString();
	}
}
