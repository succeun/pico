package pico;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Before {
	/**
     * Does not intercept these actions
     */
    String[] unless() default {};	// 해당 문자열의 메소드 콜이 아니라면,
    String[] only() default {};		// 해당 문자열의 메소드 콜이 라면,

    /**
     * Interceptor priority (0 is high priority)
     */
    int priority() default 0;
    
    String description() default "";
}
