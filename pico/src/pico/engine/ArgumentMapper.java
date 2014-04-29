package pico.engine;


import pico.ArgumentType;
import pico.WebArgument;
import pico.engine.argumentable.ArgumentInfo;
import pico.engine.argumentable.BeanArgument;
import pico.engine.argumentable.ControllerContextArgument;
import pico.engine.argumentable.ControllerEngineArgument;
import pico.engine.argumentable.FieldArgument;
import pico.engine.argumentable.HttpServletRequestArgument;
import pico.engine.argumentable.HttpServletResponseArgument;
import pico.engine.argumentable.HttpSessionArgument;
import pico.engine.argumentable.MapArgument;
import pico.engine.argumentable.MultipartRequestArgument;
import pico.engine.argumentable.PrintWriterArgument;
import pico.engine.argumentable.ServletConfigArgument;
import pico.engine.argumentable.ServletContextArgument;
import pico.engine.argumentable.ThrowableArgument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

/**
 * @author Eun Jeong-Ho, silver@intos.biz
 * @version 2006. 4. 12
 */
public final class ArgumentMapper {
	protected static List<Argumentable> argumentables = new LinkedList<Argumentable>();
	
	static {
		argumentables.add(new MapArgument());
		argumentables.add(new FieldArgument());
		argumentables.add(new MultipartRequestArgument());
		argumentables.add(new HttpServletRequestArgument());
		argumentables.add(new HttpServletResponseArgument());
		argumentables.add(new HttpSessionArgument());
		argumentables.add(new ServletContextArgument());
		argumentables.add(new ServletConfigArgument());
		argumentables.add(new PrintWriterArgument());
		argumentables.add(new ThrowableArgument());
		argumentables.add(new ControllerContextArgument());
		argumentables.add(new ControllerEngineArgument());
		argumentables.add(new BeanArgument());
	}
	
	public static void regist(Class<? extends Argumentable> clazz) throws ServletException {
		try {
			Argumentable argumentable = (Argumentable)clazz.newInstance();
			argumentables.add(0, argumentable);
		} catch (InstantiationException e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		}
	}
	
    public static ArgumentMapper createArgumentMapper(MethodMapper methodMapper) {
        return new ArgumentMapper(methodMapper);
    }

    protected MethodMapper methodMapper;
    protected List<Argumentable> wrappers;
    protected List<ArgumentInfo> argumentInfos = new LinkedList<ArgumentInfo>();

    private ArgumentMapper(MethodMapper methodMapper) {
        this.methodMapper = methodMapper;
        
        Method method = methodMapper.getMethod();
        Paranamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());
        
        String[] parameterNames = new String[0];
        try {
        	// throws ParameterNamesNotFoundException if not found
        	parameterNames = paranamer.lookupParameterNames(method); 
        	// will return null if not found
        	//parameterNames = paranamer.lookupParameterNames(method, false); 
        } catch(ParameterNamesNotFoundException e) {
        	String msg = methodMapper.getControllerMapper().getControllerClass().getName() + "#" + method.getName()
        				+ "에서 해당 메소드의 Agrument를 분석할 수 없습니다. Class는 반드시 디버그 정보(lines,vars,source) 포함하여 컴파일 하여야 합니다.";
        	throw new RuntimeException(msg, e);
        }
        
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes != null && parameterTypes.length > 0) {
            wrappers = new ArrayList<Argumentable>();

            for (Class<?> parameterType : parameterTypes) {
                wrappers.add( getArgumentable(parameterType) );
            }
        }
        
        String[] argumentNames = new String[parameterNames.length];
        ArgumentType[] argumentTypes = new ArgumentType[parameterNames.length];
        boolean[] isBodys = new boolean[parameterNames.length];
        String[] defaultValues = new String[parameterNames.length];
        
        // 파라미터마다 여러개의 어노테이션을 보유할 수 있으므로 2차원 배열 반환함
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
        	Annotation[] annotations = parameterAnnotations[i];
        	for (Annotation annotation : annotations) {
                if (annotation instanceof WebArgument) {
                	argumentNames[i] = ((WebArgument) annotation).name();
                	argumentTypes[i] = ((WebArgument) annotation).type();
                	isBodys[i] = ((WebArgument) annotation).body();
                	String[] values = ((WebArgument) annotation).defaultValue();
                	if (values != null && values.length > 0)
                		defaultValues[i] = values[0];
                }
            }
        }
        
        for (int i = 0; i < parameterNames.length; i++) {
        	argumentInfos.add(new ArgumentInfo(parameterNames[i], parameterTypes[i],
        			argumentNames[i], argumentTypes[i], isBodys[i], defaultValues[i]));
        }
    }

	private Argumentable getArgumentable(Class<?> paramType) {
		try {
			for (Argumentable argumentable : argumentables) {
				if (argumentable.isAvailable(paramType)) {
					return (Argumentable) argumentable.getClass().newInstance(); 
				}
			}
		} catch (InstantiationException ignored) {
		} catch (IllegalAccessException ignored) {
		}
		return null;
	}
    
    /**
     * 요청으로 부터 메소드에 전달할 파라미터의 값을 변환하여 가져온다.
     */
    public Object[] getArguments(Object ... values) throws Exception {
        Object[] objects = null;
        if (wrappers != null && wrappers.size() > 0) {
        	int size = wrappers.size();
    		objects = new Object[size];
    		for(int i = 0; i < size; i++) {
    			objects[i] = wrappers.get(i).getArgument(i, argumentInfos.get(i), values);
    		}
        }
        return objects;
    }
}
