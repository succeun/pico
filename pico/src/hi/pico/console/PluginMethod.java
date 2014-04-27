package hi.pico.console;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PluginMethod {
	/**
	 * ���̵� �޴��� �޴��� ���� �̸��̸�, 
	 * ���� Plugin ��ü������ �ش� �̸��� �����Ͽ��� �Ѵ�.
	 * @return
	 */
	String name() default "";
	/**
	 * ������ ��ȯ�Ѵ�.
	 * @return
	 */
	String description() default "";
	/**
	 * ���̵� �޴��� �޴��� ������ ���� ��ȯ
	 * @return
	 */
	boolean visiableMenu() default true;
}
