/*
 * Copyright (c) 2005, Jeong-Ho Eun
 * All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 */
package pico.commons.beans;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * java.lang.reflect.* 의 속도문제를 어느정도 해결하고자 하는 Class
 * {@link java.lang.Class#getFields Class.getFileds()}의 느린문제를 cache를 통해서 어느정도 해결한다.<br>
 * [수정사항]<br>
 * IBM의 JDK의 경우, 필드를 가져올때, 순서가 거꾸로 반환이 된다. 따라서,
 * JAVA Vendor를 판별하여, 정상적으로 동작할 수 있게 정형화 시킨다.
 * @author Eun Jeong-Ho, silver@intos.biz
 * @since 2004. 1. 26.
 */
public final class ReflectionUtil
{

	private final static Map<Class<?>, Field[]> fHash = Collections.synchronizedMap(new HashMap<Class<?>, Field[]>());   // Field hashing
	private final static Map<Class<?>, Field[]> dfHash = Collections.synchronizedMap(new HashMap<Class<?>, Field[]>());   // Field hashing

	private static String JAVA_VENDOR;

	static
	{
		Properties p = System.getProperties();
		JAVA_VENDOR = p.getProperty("java.vendor").toLowerCase();
	}

	/**
	 * 특정 벤더의 경우 필드가 뒤집히는 현상을 정형화 시켜준다.
	 * @param fields 정형화할 Field 배열
	 * @return 정형화된 Field 배열
	 */
	public static Field[] normalize(Field[] fields)
	{
		// IBM의 JVM의 경우, 필드들의 순서가 거꾸 반환된다.
		if (JAVA_VENDOR.equals("ibm corporation"))
		{
			Field[] newfields = new Field[fields.length];

			for (int i = 0; i < newfields.length; i++)
				newfields[i] = fields[fields.length - i - 1];

			return newfields;
		}
		return fields;
	}

	/**
	 * {@link java.lang.Class#getFields Class#getFields()}를 통해 나온 {@link java.lang.reflect.Field Field}를 보관한다.
	 * <b>public</b> fields 만 반환한다.
	 * @param c 필드를 뽑아내고자 하는 Class
	 * @return Field 배열
	 */
	public static Field[] getFields(Class<?> c)
	{
		Field[] fields = (Field[]) fHash.get(c);

		if (fields == null)
		{
			fields = normalize(c.getFields());
			fHash.put(c, fields);
		}

		return fields;
	}


	/**
	 * {@link java.lang.Class#getDeclaredFields Class#getDeclaredFields()}를 통해 나온 {@link java.lang.reflect.Field Field}를 보관한다.
	 * <b>public, protected, default(package) access, private</b> fields 모두 반환한다.
	 * @param c 필드를 뽑아내고자 하는 Class
	 * @return Field 배열
	 */
	public static Field[] getDeclaredFields(Class<?> c)
	{
		Field[] fields = (Field[]) dfHash.get(c);

		if (fields == null)
		{
			fields = normalize(c.getDeclaredFields());
			dfHash.put(c, fields);
		}

		return fields;
	}


	/**
	 * 익명의 오브젝트의 메소드를 실행시킨다.<br>
	 * Mathod객체의 invoke를 쓰기 쉽게 만들었다.
	 *
	 * ReflectionUtil.invoke( obj, "println", new Object[]{new String("haha")} );
	 *
	 * @param obj 실행시키고자 하는 Object 객체
	 * @param methodName 메소드명
	 * @param params 메소드에 넘겨질 Object 배열
	 */
	public static Object invoke(Object obj, String methodName, Object[] params)
			throws InvocationTargetException, IllegalAccessException
	{
		try
		{
			Method[] methods = obj.getClass().getMethods();

			Method method = getMethod(methods, methodName);

			Object result = null;

			if (method != null)
			{
				result = method.invoke(obj, params);
			}
			else
			{
				throw new IllegalAccessException("Not exist the method. Method Name: [" + methodName + "]");
			}

			return result;
		}
		catch (InvocationTargetException e)
		{
			throw e;
		}
		catch (IllegalAccessException e)
		{
			throw e;
		}
	}

	/**
	 * <code>Method</code> 중에서 public 이면서 해당 명을 가진 <code>Method</code>를 반환한다.
	 * @param methods 메소드 배열
	 * @param name 찾을 메소드명
	 * @return 메소드 객체
	 */
	public static Method getMethod(Method[] methods, String name)
	{
		Method method = null;
		for (int i = 0; i < methods.length; i++)
		{
			method = methods[i];
			if (!Modifier.isPublic(method.getModifiers()))
				continue;
			if (method.getName().equals(name))
				return method;
		}
		return null;
	}
}
