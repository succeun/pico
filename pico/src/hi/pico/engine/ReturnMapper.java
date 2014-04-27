package hi.pico.engine;


import hi.pico.ControllerContext;
import hi.pico.engine.returnable.ViewReturner;
import hi.pico.engine.returnable.VoidReturner;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ReturnMapper {
	protected static List<Returnable> returnables = new LinkedList<Returnable>();
	
	static {
		returnables.add(new ViewReturner());
		returnables.add(new VoidReturner());
	}
	
	public static void regist(Class<? extends Returnable> clazz) throws ServletException {
		try {
			Returnable parametable = (Returnable)clazz.newInstance();
			returnables.add(0, parametable);
		} catch (InstantiationException e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		}
	}
	
    public static ReturnMapper createReturnMapper(MethodMapper methodMapper) {
        return new ReturnMapper(methodMapper);
    }

    protected MethodMapper methodMapper;
    protected Returnable wrapper;
    protected Class<?> returnType;

    private ReturnMapper(MethodMapper methodMapper) {
        this.methodMapper = methodMapper;
        returnType = methodMapper.getMethod().getReturnType();
       	wrapper = getReturnable(returnType);
    }

	private Returnable getReturnable(Class<?> returnType) {
		try {
			for (Returnable returnable : returnables) {
				if (returnable.isAvailable(returnType)) {
					return (Returnable) returnable.getClass().newInstance(); 
				}
			}
		} catch (InstantiationException ignored) {
		} catch (IllegalAccessException ignored) {
		}
		return null;
	}
    
	public boolean render(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext, Object obj) throws Exception {
		Returnable returnable = wrapper;
		if (obj != null && returnable == null)
			returnable = getReturnable(obj.getClass());
		
		if (returnable != null) 
			return returnable.render(context, req, res, controllerContext, obj);
		
		return false;
    }
}
