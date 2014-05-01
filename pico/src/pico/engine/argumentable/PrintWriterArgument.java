/**
 * 
 */
package pico.engine.argumentable;

import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;

public class PrintWriterArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (PrintWriter.class.isAssignableFrom(paramType));
	}
	
	public Object getArgument(int index, ArgumentInfo argumentInfo, ControllerContext controllerContext, 
			ServletConfig config, ServletContext context, HttpServletRequest req, HttpServletResponse res, Throwable ex) throws Exception {
        return res.getWriter();
    }
}