package hi.pico.console;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PluginMethod {
	/**
	 * 사이드 메뉴등 메뉴상에 보일 이름이며, 
	 * 동일 Plugin 객체에서는 해당 이름이 유일하여야 한다.
	 * @return
	 */
	String name() default "";
	/**
	 * 설명을 반환한다.
	 * @return
	 */
	String description() default "";
	/**
	 * 사이드 메뉴등 메뉴상에 보일지 여부 반환
	 * @return
	 */
	boolean visiableMenu() default true;
}
