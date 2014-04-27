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
	 * ��û Body���� ������ ���� ����
	 * true�ϰ��, name �׸��� ���� ��
	 * @return boolean
	 */
	boolean body() default false;
	String name() default "";
	String[] defaultValue() default {};
}
