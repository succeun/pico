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
 * 어떤 인터페이스를 구현한 클래스에 대하여, 어떤 특정 메소드들을 수행시,
 * 로그와 같은 특정 동작을 추가하고자 할때 또는 약간의 수정이 필요로 할때,
 * 그 클래스를 상속하거나 해야 하지만, 이것을 이용하면, 위에 문제를 풀어주는
 * AOP를 쉽게 구현할 수 있다.
 * 즉, 이전까지의 객체지향 프로그래밍은 Cross-cutting concern을 정적으로 어플리케이션에 결합시킨
 * 반면, AOP는 동적으로 Cross-cutting concern를 다룬다고 표현할 수 있다.
 * <pre>
 * ObjectProxy proxy = ObjectProxy.newInstance(new LogInvokingHandler());
 * Testable obj = new TestObject();
 * obj = (Testable) proxy.newInstance(obj);
 * System.out.println(obj.test("1234"));
 * </pre>
 * 위 코드는 특정 객체가 구현한 인터페이스의 메소드를 실행할때마다 로그를 찍도록 AOP를 구현한 것이다.
 * TestObject 객체를 새로이 작성하지 않고도 로그를 찍을 수 있는 구문을 넣어둔 형태다.
 * [참고]
 * 영역 지향 프로그래밍(Aspect-oriented programming : AOP)은 프로그래머들이 로깅과 같이 일반적인
 * 책임 구분을 넘어서는 행위인 횡단적 사항을 모듈화하도록 해주는 새로운 프로그래밍 기법이다.
 * @author Eun Jeong-Ho, silver@intos.biz
 * @since 2005. 6. 16.
 */
public final class ObjectProxy implements InvocationHandler
{
	/**
	 * {@link InvokingHandler InvokingHandler}를 등록한 {@link ObjectProxy ObjectProxy}를 반환한다.
	 * @param invokingHandler {@link InvokingHandler InvokingHandler}
	 * @return {@link ObjectProxy ObjectProxy}
	 */
	public static ObjectProxy newInstance(InvokingHandler invokingHandler)
	{
		return new ObjectProxy(invokingHandler);
	}

    /**
	 * {@link Method#invoke Method.invoke()}와 같이 메소드 실행시 일어난
	 * 부모의 Throwable, 즉 원인이 되는 Throwable를 반환한다.
	 * @param t - Throwable
	 * @return 실제 원인이 되는 Throwable
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
	 * 특정 메소드를 실행시 호출할 {@link InvokingHandler InvokingHandler}를 이용하여
	 * 생성한다.
	 * @param handler InvokingHandler, 만일 null 이라면, 기본적으로 {@link LogInvokingHandler LogInvokingHandler}를 등록한다.
	 */
	private ObjectProxy(InvokingHandler handler)
	{
		super();
		if (handler == null)
			handler = new LogInvokingHandler();
		this.handler = handler;
	}

	/**
	 * 등록된 {@link InvokingHandler InvokingHandler}를 반환한다.
	 * @return {@link InvokingHandler InvokingHandler}
	 */
	public InvokingHandler getHandler()
	{
		return handler;
	}

	/**
	 * 주어진 객체의 메소드를 Argument를 전달하여 실행시킨다.
	 * @param proxy 객체
	 * @param method 실행할 메소드
	 * @param args Arguments
	 * @return 결과값
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
	 * Proxy할 객체를 받아, AOP가 적용된 객체를 반환한다.
	 * obj에 구현된 인터페이스를 이용하여, 형변환 하여 사용 하기 바란다.
	 * @param obj Object
	 * @return Object, 구현한 인터페이스로 형 변환하여 사용함
	 */
	public Object newInstance(Object obj)
	{
    	return newInstance(obj, obj.getClass().getInterfaces());
    }

	/**
	 * Proxy할 객체를 받아, AOP가 적용된 객체를 반환한다.
	 * obj에 구현된 인터페이스를 이용하여, 형변환 하여 사용 하기 바란다.
     * 변환하고자하는 인터페이스를 뒤에 클래스 배열로 넘겨준다.
	 * 주의할 점으로, 만약에 사용할 인터페이스의 ClassLoader가 다를 경우에는
	 * 이 메소드를 사용하여, 등록해주어야 한다.
	 * <pre>
	 * Object obj = proxy.newInstance(conn, new Class[]{Connection.class});
	 * </pre>
	 * @param obj Object
	 * @param interfaces 변환하고자하는 인터페이스들
	 * @return Object, 구현한 인터페이스로 형 변환하여 사용함
	 */
	public Object newInstance(Object obj, Class<?>[] interfaces)
	{
		ClassLoader cl = obj.getClass().getClassLoader();
        return newInstance(obj, interfaces, cl);
    }

    public Object newInstance(Object obj, Class<?>[] interfaces, ClassLoader loader)
	{
    	handler.setTargetObject(obj);
		// 여기서 반환되는 객체는 인터페이스로 접근 가능하다.
    	return Proxy.newProxyInstance(loader, interfaces, this);
	}
}