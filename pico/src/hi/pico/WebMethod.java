package hi.pico;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebMethod {
	String[] url() default {};
	MethodType[] method() default MethodType.All;
	String description() default "";
}
