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
package hi.pico.commons.beans;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * � �������̽��� ������ Ŭ������ ���Ͽ�, � Ư�� �޼ҵ���� �����,
 * �α׿� ���� Ư�� ������ �߰��ϰ��� �Ҷ� �Ǵ� �ణ�� ������ �ʿ�� �Ҷ�,
 * �� Ŭ������ ����ϰų� �ؾ� ������, �̰��� �̿��ϸ�, ���� ������ Ǯ���ִ�
 * AOP�� ���� ������ �� �ִ�.
 * ��, ���������� ��ü���� ���α׷����� Cross-cutting concern�� �������� ���ø����̼ǿ� ���ս�Ų
 * �ݸ�, AOP�� �������� Cross-cutting concern�� �ٷ�ٰ� ǥ���� �� �ִ�.
 * <pre>
 * ObjectProxy proxy = ObjectProxy.newInstance(new LogInvokingHandler());
 * Testable obj = new TestObject();
 * obj = (Testable) proxy.newInstance(obj);
 * System.out.println(obj.test("1234"));
 * </pre>
 * �� �ڵ�� Ư�� ��ü�� ������ �������̽��� �޼ҵ带 �����Ҷ����� �α׸� �ﵵ�� AOP�� ������ ���̴�.
 * TestObject ��ü�� ������ �ۼ����� �ʰ� �α׸� ���� �� �ִ� ������ �־�� ���´�.
 * [����]
 * ���� ���� ���α׷���(Aspect-oriented programming : AOP)�� ���α׷��ӵ��� �α�� ���� �Ϲ�����
 * å�� ������ �Ѿ�� ������ Ⱦ���� ������ ���ȭ�ϵ��� ���ִ� ���ο� ���α׷��� ����̴�.
 * @author Eun Jeong-Ho, silver@intos.biz
 * @since 2005. 6. 16.
 */
public final class ObjectProxy implements InvocationHandler
{
	/**
	 * {@link InvokingHandler InvokingHandler}�� ����� {@link ObjectProxy ObjectProxy}�� ��ȯ�Ѵ�.
	 * @param invokingHandler {@link InvokingHandler InvokingHandler}
	 * @return {@link ObjectProxy ObjectProxy}
	 */
	public static ObjectProxy newInstance(InvokingHandler invokingHandler)
	{
		return new ObjectProxy(invokingHandler);
	}

    /**
	 * {@link Method#invoke Method.invoke()}�� ���� �޼ҵ� ����� �Ͼ
	 * �θ��� Throwable, �� ������ �Ǵ� Throwable�� ��ȯ�Ѵ�.
	 * @param t - Throwable
	 * @return ���� ������ �Ǵ� Throwable
	 */
	public static Throwable unwrapThrowable(Throwable t)
	{
		Throwable t2 = t;
		while (true)
		{
			if (t2 instanceof InvocationTargetException)
			{
				t2 = ((InvocationTargetException) t).getTargetException();
			}
			else if (t instanceof UndeclaredThrowableException)
			{
				t2 = ((UndeclaredThrowableException) t).getUndeclaredThrowable();
			}
			else
			{
				return t2;
			}
		}
	}

    private InvokingHandler handler;

	/**
	 * Ư�� �޼ҵ带 ����� ȣ���� {@link InvokingHandler InvokingHandler}�� �̿��Ͽ�
	 * �����Ѵ�.
	 * @param handler InvokingHandler, ���� null �̶��, �⺻������ {@link LogInvokingHandler LogInvokingHandler}�� ����Ѵ�.
	 */
	private ObjectProxy(InvokingHandler handler)
	{
		super();
		if (handler == null)
			handler = new LogInvokingHandler();
		this.handler = handler;
	}

	/**
	 * ��ϵ� {@link InvokingHandler InvokingHandler}�� ��ȯ�Ѵ�.
	 * @return {@link InvokingHandler InvokingHandler}
	 */
	public InvokingHandler getHandler()
	{
		return handler;
	}

	/**
	 * �־��� ��ü�� �޼ҵ带 Argument�� �����Ͽ� �����Ų��.
	 * @param proxy ��ü
	 * @param method ������ �޼ҵ�
	 * @param args Arguments
	 * @return �����
	 * @throws Throwable
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		Object result;
		try
		{
			result = handler.invoke(method, args);
		}
		catch (Throwable t)
		{
			throw unwrapThrowable(t);
		}

		return result;
	}

	/**
	 * Proxy�� ��ü�� �޾�, AOP�� ����� ��ü�� ��ȯ�Ѵ�.
	 * obj�� ������ �������̽��� �̿��Ͽ�, ����ȯ �Ͽ� ��� �ϱ� �ٶ���.
	 * @param obj Object
	 * @return Object, ������ �������̽��� �� ��ȯ�Ͽ� �����
	 */
	public Object newInstance(Object obj)
	{
    	return newInstance(obj, obj.getClass().getInterfaces());
    }

	/**
	 * Proxy�� ��ü�� �޾�, AOP�� ����� ��ü�� ��ȯ�Ѵ�.
	 * obj�� ������ �������̽��� �̿��Ͽ�, ����ȯ �Ͽ� ��� �ϱ� �ٶ���.
     * ��ȯ�ϰ����ϴ� �������̽��� �ڿ� Ŭ���� �迭�� �Ѱ��ش�.
	 * ������ ������, ���࿡ ����� �������̽��� ClassLoader�� �ٸ� ��쿡��
	 * �� �޼ҵ带 ����Ͽ�, ������־�� �Ѵ�.
	 * <pre>
	 * Object obj = proxy.newInstance(conn, new Class[]{Connection.class});
	 * </pre>
	 * @param obj Object
	 * @param interfaces ��ȯ�ϰ����ϴ� �������̽���
	 * @return Object, ������ �������̽��� �� ��ȯ�Ͽ� �����
	 */
	public Object newInstance(Object obj, Class<?>[] interfaces)
	{
		ClassLoader cl = obj.getClass().getClassLoader();
        return newInstance(obj, interfaces, cl);
    }

    public Object newInstance(Object obj, Class<?>[] interfaces, ClassLoader loader)
	{
    	handler.setTargetObject(obj);
		// ���⼭ ��ȯ�Ǵ� ��ü�� �������̽��� ���� �����ϴ�.
    	return Proxy.newProxyInstance(loader, interfaces, this);
	}
}