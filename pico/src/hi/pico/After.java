package hi.pico;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface After {
	/**
     * Does not intercept these actions
     */
    String[] unless() default {};
    String[] only() default {};

    /**
     * Interceptor priority (0 is high priority)
     */
    int priority() default 0;
    
    String description() default "";
}
