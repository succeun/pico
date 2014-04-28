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
 * java.lang.reflect.* �� �ӵ������� ������� �ذ��ϰ��� �ϴ� Class
 * {@link java.lang.Class#getFields Class.getFileds()}�� ���������� cache�� ���ؼ� ������� �ذ��Ѵ�.<br>
 * [��������]<br>
 * IBM�� JDK�� ���, �ʵ带 �����ö�, ������ �Ųٷ� ��ȯ�� �ȴ�. ����,
 * JAVA Vendor�� �Ǻ��Ͽ�, ���������� ������ �� �ְ� ����ȭ ��Ų��.
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
	 * Ư�� ������ ��� �ʵ尡 �������� ������ ����ȭ �����ش�.
	 * @param fields ����ȭ�� Field �迭
	 * @return ����ȭ�� Field �迭
	 */
	public static Field[] normalize(Field[] fields)
	{
		// IBM�� JVM�� ���, �ʵ���� ������ �Ų� ��ȯ�ȴ�.
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
	 * {@link java.lang.Class#getFields Class#getFields()}�� ���� ���� {@link java.lang.reflect.Field Field}�� �����Ѵ�.
	 * <b>public</b> fields �� ��ȯ�Ѵ�.
	 * @param c �ʵ带 �̾Ƴ����� �ϴ� Class
	 * @return Field �迭
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
	 * {@link java.lang.Class#getDeclaredFields Class#getDeclaredFields()}�� ���� ���� {@link java.lang.reflect.Field Field}�� �����Ѵ�.
	 * <b>public, protected, default(package) access, private</b> fields ��� ��ȯ�Ѵ�.
	 * @param c �ʵ带 �̾Ƴ����� �ϴ� Class
	 * @return Field �迭
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
	 * �͸��� ������Ʈ�� �޼ҵ带 �����Ų��.<br>
	 * Mathod��ü�� invoke�� ���� ���� �������.
	 *
	 * ReflectionUtil.invoke( obj, "println", new Object[]{new String("haha")} );
	 *
	 * @param obj �����Ű���� �ϴ� Object ��ü
	 * @param methodName �޼ҵ��
	 * @param params �޼ҵ忡 �Ѱ��� Object �迭
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
	 * <code>Method</code> �߿��� public �̸鼭 �ش� ���� ���� <code>Method</code>�� ��ȯ�Ѵ�.
	 * @param methods �޼ҵ� �迭
	 * @param name ã�� �޼ҵ��
	 * @return �޼ҵ� ��ü
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
