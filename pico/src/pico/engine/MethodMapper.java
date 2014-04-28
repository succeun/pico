package pico.engine;


import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;

/**
 * @author Eun Jeong-Ho, silver@intos.biz
 * @version 2006. 4. 10
 */
public class MethodMapper
{
    private ControllerMapper controllerMapper;
    private Method method;
    private ArgumentMapper argumentMapper;
    private ReturnMapper returnMapper;
    private long callCount;  // ȣ��Ƚ��
    private double averageProcessingTime;  // ��սð�
    private String description;	// ����
    
    public MethodMapper(Method method, String description) {
        this(null, method, description);
    }
    
    public MethodMapper(ControllerMapper controllerMapper, Method method) {
    	this(controllerMapper, method, "");
    }
    
    public MethodMapper(ControllerMapper controllerMapper, Method method, String description) {
        this.controllerMapper = controllerMapper;
        this.method = method;
        this.description = description;
        argumentMapper = ArgumentMapper.createArgumentMapper(this);
        returnMapper = ReturnMapper.createReturnMapper(this);
    }

    public ControllerMapper getControllerMapper() {
        return controllerMapper;
    }

    public String getMethodName() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }
    
    public String getDescription() {
    	return description;
    }
    
    public ArgumentMapper getArgumentMapper() {
    	return argumentMapper;
    }

    /**
     * �� ȣ��Ƚ���� ��ȯ�Ѵ�.
     * @return �� ȣ��Ƚ��
     */
    public long getCallCount() {
        return callCount;
    }

    /**
     * ��� ����ð��� ��ȯ�Ѵ�.
     * @return ��� ����ð�
     */
    public long getAverageProcessingTime() {
        return (long) averageProcessingTime;
    }
    
    public Object invoke(Object controller, Object ... objects) throws Exception {
        callCount++;
        long start = System.currentTimeMillis();
        try {
            return method.invoke(controller, argumentMapper.getArguments(objects));
        } finally {
            long elapsedTime = System.currentTimeMillis() - start;
            averageProcessingTime = (averageProcessingTime * (callCount - 1) + elapsedTime) / callCount;
        }
    }
    
    public boolean render(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext, Object obj) throws Exception {
    	return returnMapper.render(context, req, res, controllerContext, obj);
    }
}