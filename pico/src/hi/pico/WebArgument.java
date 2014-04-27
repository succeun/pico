package hi.pico;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface WebArgument {
	ArgumentType type() default ArgumentType.ANY;
	/**
	 * 요청 Body에서 읽을지 여부 결정
	 * true일경우, name 항목은 무시 됨
	 * @return boolean
	 */
	boolean body() default false;
	String name() default "";
	String[] defaultValue() default {};
}
