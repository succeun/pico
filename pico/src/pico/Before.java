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
    String[] unless() default {};	// �ش� ���ڿ��� �޼ҵ� ���� �ƴ϶��,
    String[] only() default {};		// �ش� ���ڿ��� �޼ҵ� ���� ���,

    /**
     * Interceptor priority (0 is high priority)
     */
    int priority() default 0;
    
    String description() default "";
}
