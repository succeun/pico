package pico.engine.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassUtil {
	public static Class<?>[] getClasses(String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		File directory = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = '/' + packageName.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(packageName + " (" + directory
					+ ") does not appear to be a valid package");
		}
		if (directory.exists()) {
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".class")) {
					classes.add(Class.forName(packageName + '.' + files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(packageName + " does not appear to be a valid package");
		}
		Class<?>[] classesArray = new Class[classes.size()];
		classes.toArray(classesArray);
		return classesArray;
	}
	
	public static Class<?>[] getClasses(File directory) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		getClasses(classes, directory, directory);
		Class<?>[] classesA = new Class[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}
	
	public static Class<?>[] getClasses(List<Class<?>> classes, File directory, File root) throws ClassNotFoundException {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				getClasses(classes, files[i], root);
			} else {
				String filename = files[i].getName();
				
				String rootPath = root.getAbsolutePath();
				String filePath = files[i].getAbsolutePath();
				
				String tmp = filePath.substring(rootPath.length() + 1).replaceAll("/|\\\\", ".");
				if (filename.endsWith(".class")) {
					classes.add(Class.forName(tmp.substring(0, tmp.length() - 6)));
				}
			}
		}
		Class<?>[] classesA = new Class[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}
	
	public static Class<?> loadClass(String className) throws ClassNotFoundException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		    
		if (loader != null)
			return Class.forName(className, false, loader);
		else
			return Class.forName(className);
	}
	
	/**
     * Find all annotated method from a class
     * @param clazz The class
     * @param annotationType The annotation class
     * @return A list of method object
     */
    public static List<Method> findAllAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationType) {
        if( clazz == null ) {
            return new ArrayList<Method>(0);
        }

        // have to resolve it.
        List<Method> methods = new ArrayList<Method>();
        // get list of all annotated methods on this class..
        for(Method method : findAllAnnotatedMethods(clazz)) {
            if (method.isAnnotationPresent(annotationType)) {
                methods.add(method);
            }
        }
        return methods;
    }

    /**
     * Find all annotated method from a class
     * @param clazz The class
     * @return A list of method object
     */
    public static List<Method> findAllAnnotatedMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<Method>();
        
        while (clazz != null && !clazz.equals(Object.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getAnnotations().length > 0) {
                    methods.add(method);
                }
            }
            
            clazz = clazz.getSuperclass();
        }

        return methods;
    }
    
    /**
     * Find all annotated field from a class
     * @param clazz The class
     * @param annotationType The annotation class
     * @return A list of field object
     */
    public static List<Field> findAllAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationType) {

        if( clazz == null ) {
            return new ArrayList<Field>(0);
        }

        // have to resolve it.
        List<Field> fields = new ArrayList<Field>();
        // get list of all annotated methods on this class..
        for(Field field : findAllAnnotatedFields(clazz)) {
            if (field.isAnnotationPresent(annotationType)) {
                fields.add(field);
            }
        }
        return fields;
    }
    
    /**
     * Find all annotated field from a class
     * @param clazz The class
     * @return A list of field object
     */
    public static List<Field> findAllAnnotatedFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        
        while (clazz != null && !clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getAnnotations().length > 0) {
                    fields.add(field);
                }
            }
            
            clazz = clazz.getSuperclass();
        }

        return fields;
    }
	
}
