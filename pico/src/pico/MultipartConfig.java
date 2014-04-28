package pico;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MultipartConfig {

    /**
     * The directory location where files will be stored
     */
    String location() default "";

    /**
     * The maximum size allowed for uploaded files.
     * 
     * <p>The default is <tt>-1L</tt>, which means unlimited.
     */
    long maxFileSize() default -1L;

    /**
     * The maximum size allowed for <tt>multipart/form-data</tt>
     * requests
     * 
     * <p>The default is <tt>-1L</tt>, which means unlimited.
     */
    long maxRequestSize() default -1L;

    /**
     * The size threshold after which the file will be written to disk
     */
    int fileSizeThreshold() default 0;
}
