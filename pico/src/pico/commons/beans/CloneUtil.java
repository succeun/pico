/*
 * 이 소스코드의 저작권은 Eun JeongHo(은정호)에게 있다.
 * 이 코드는 제작자의 허락없이 무단으로 배포될 수 없으며,
 * 허락없이 배포되었을 경우,
 * 여성의 경우, 데이트 신청을 받아야 하며,
 * 남성의 경우, 술을 사야 한다.
 */
package pico.commons.beans;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * 
 * @author Eun Jeong-Ho, succeun@gmail.com
 * @since 2005. 8. 5.  
 */
public class CloneUtil
{
	/**
	 * 해당 값을 int형으로 가져온다.
	 * @param obj Object
	 * @return int 값
	 */
	public static Integer cloneInt(Integer obj)
	{
		return new Integer(obj.intValue());
	}

	/**
	 * 해당 값을 byte형으로 가져온다.
	 * @param obj Object
	 * @return byte 값
	 */
	public static Byte cloneByte(Byte obj)
	{
		return new Byte(obj.byteValue());
	}

	/**
	 * 해당 값을 char형으로 가져온다.
	 * @param obj Object
	 * @return char 값
	 */
	public static Character cloneChar(Character obj)
	{
		return new Character(obj.charValue());
	}

	/**
	 * 해당 값을 int형으로 가져온다.
	 * @param obj Object
	 * @return short 값
	 */
	public static Short cloneShort(Short obj)
	{
		return new Short(obj.shortValue());
	}

	/**
	 * 해당 값을 long형으로 가져온다.
	 * @param obj Object
	 * @return long 값
	 */
	public static Long cloneLong(Long obj)
	{
		return new Long(obj.longValue());
	}

	/**
	 * 해당 값을 double형으로 가져온다.
	 * @param obj Object
	 * @return double 값
	 */
	public static Double cloneDouble(Double obj)
	{
		return new Double(obj.doubleValue());
	}

	/**
	 * 해당 값을 float형으로 가져온다.
	 * @param obj Object
	 * @return double 값
	 */
	public static Float cloneFloat(Float obj)
	{
		return new Float(obj.floatValue());
	}

	/**
	 * 해당 값을 문자열로 반환한다.
	 * @param obj Object
	 * @return 문자열 key에 값이 null 이면 빈문자열을 반환한다.
	 */
	public static String cloneStr(Object obj)
	{
		return obj.toString();
	}

	/**
	 * 해당 값을 boolean형으로 가져온다.
	 * @param obj Object
	 * @return boolean 값
	 */
	public static Boolean cloneBoolean(Boolean obj)
	{
		return (obj.booleanValue()) ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * 해당 값을 {@link java.util.Date Date}형으로 가져온다.
	 * @param obj Object
	 * @return Date 값
	 */
	public static Date cloneDate(Date obj)
	{
		return new Date(obj.getTime());
	}

	/**
	 * 해당 값을 {@link java.math.BigInteger BigInteger}형으로 가져온다.
	 * @param obj Object
	 * @return Date 값
	 */
	public static BigInteger cloneBigInteger(BigInteger obj)
	{
		return new BigInteger(obj.toByteArray());
	}

	/**
	 * 해당 값을 {@link java.math.BigInteger BigInteger}형으로 가져온다.
	 * @param obj Object
	 * @return Date 값
	 */
	public static BigDecimal cloneBigDecimal(BigDecimal obj)
	{
		return new BigDecimal(obj.toString());
	}

	/**
	 * 객체를 원하는 타입의 클래스의 객체로 반환한다.
     * 원하는 타입은, Primitive 또는 Stirng, BigInteger, BigDecimal을 지원한다.
	 * @param obj Object
	 * @return Object 객체
	 */
	public static Object clone(Object obj)
	{
		if (obj == null)
			return null;

		Class<?> clazz = obj.getClass();
		if (!clazz.isArray())
		{
			if (String.class.isAssignableFrom(clazz))
				return cloneStr(obj);
			else if  (Short.class.isAssignableFrom(clazz) || short.class.isAssignableFrom(clazz))
				return cloneShort((Short)obj);
			else if  (Character.class.isAssignableFrom(clazz) || char.class.isAssignableFrom(clazz))
				return cloneChar((Character)obj);
			else if  (Byte.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz))
				return cloneByte((Byte)obj);
			else if  (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz))
				return cloneInt((Integer)obj);
			else if  (Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz))
				return cloneLong((Long)obj);
			else if  (Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz))
				return cloneFloat((Float)obj);
			else if  (Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz))
				return cloneDouble((Double)obj);
			else if  (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz))
				return cloneBoolean((Boolean)obj);
			else if  (BigInteger.class.isAssignableFrom(clazz))
				return cloneBigInteger((BigInteger)obj);
			else if  (BigDecimal.class.isAssignableFrom(clazz))
				return cloneBigDecimal((BigDecimal)obj);
			else if  (Date.class.isAssignableFrom(clazz))
				return cloneDate((Date)obj);
			//else if  (Class.class)
			else
				return obj;
		}
		else
		{
			if (String[].class.isAssignableFrom(clazz)
                || Short[].class.isAssignableFrom(clazz) || short[].class.isAssignableFrom(clazz)
                || Character[].class.isAssignableFrom(clazz) || char[].class.isAssignableFrom(clazz)
                || Byte[].class.isAssignableFrom(clazz) || byte[].class.isAssignableFrom(clazz)
                || Integer[].class.isAssignableFrom(clazz) || int[].class.isAssignableFrom(clazz)
                || Long[].class.isAssignableFrom(clazz) || long[].class.isAssignableFrom(clazz)
                || Float[].class.isAssignableFrom(clazz) || float[].class.isAssignableFrom(clazz)
                || Double[].class.isAssignableFrom(clazz) || double[].class.isAssignableFrom(clazz)
                || Boolean[].class.isAssignableFrom(clazz) || boolean[].class.isAssignableFrom(clazz)
                || BigInteger[].class.isAssignableFrom(clazz)
                || BigDecimal[].class.isAssignableFrom(clazz)
				|| Date.class.isAssignableFrom(clazz))
				return getArray(clazz.getComponentType(),  obj);
			else
				return obj;
		}
	}

	/**
     * 배열이나, Collection을 원하는 타입의 Class의 배열로 반환 받는다.
     * 원하는 타입은, Primitive 또는 Stirng, BigInteger, BigDecimal을 지원한다.
     * @param clazz 반환받고자 하는 배열의 클래스
     * @param obj 변환하고자 하는 객체
     * @return 클래스로 구성된 배열
     */
    private static Object getArray(Class<?> clazz, Object obj)
	{
		Class<?> cls = obj.getClass();
		if (cls.isArray())
		{
			int len = Array.getLength(obj);
            Object newObj = Array.newInstance(clazz, len);
			for (int i = 0; i < len; i++)
				Array.set(newObj, i, clone(Array.get(obj, i)));
			return newObj;
		}
		else if (obj instanceof Collection<?>)
		{
			Collection<?> list = (Collection<?>) obj;
			Object newObj = Array.newInstance(clazz, list.size());
			Iterator<?> itr = list.iterator();
			int i = 0;
			while (itr.hasNext())
			{
				Object item = clone(itr.next());
				Array.set(newObj, i++, item);
			}
			return newObj;
		}
		else
        {
            Object newObj = Array.newInstance(clazz, 1);
            Array.set(newObj, 0, clone(obj));
            return newObj;
        }
	}

	@SuppressWarnings("null")
	public static void main(String[] args)
	{
		{
			BigDecimal obj = new BigDecimal("12.4");
			BigDecimal obj1 = cloneBigDecimal(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
		{
			BigInteger obj = new BigInteger("124");
			BigInteger obj1 = cloneBigInteger(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
		{
			Boolean obj = Boolean.TRUE;
			Boolean obj1 = cloneBoolean(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
		{
			Byte obj = new Byte((byte)1);
			Byte obj1 = cloneByte(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
		{
			Character obj = new Character((char)1);
			Character obj1 = cloneChar(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
		{
			Date obj = new Date();
			Date obj1 = cloneDate(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
		{
			Double obj = new Double(1);
			Double obj1 = cloneDouble(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
        {
			Float obj = new Float(1);
			Float obj1 = cloneFloat(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
		{
			Integer obj = new Integer(1);
			Integer obj1 = cloneInt(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
		{
			Long obj = new Long(1);
			Long obj1 = cloneLong(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
		{
			Short obj = new Short((short)1);
			Short obj1 = cloneShort(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}
		{
			String obj = "하하하";
			String obj1 = cloneStr(obj);
			System.out.println( (obj.equals(obj1)) ? "true" : "false :" + obj1);
		}

		{
			String[] obj = new String[]{"하하하", "1", "2"};
			String[] obj1 = (String[]) clone(obj);
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}

		{
			String obj = null;
			String obj1 = (String) clone(obj);
			System.out.println( (obj.equals(obj1)) ? "true" : "false :" + obj1);
		}

		{
			int[] obj = new int[]{1, 2, 3};
			int[] obj1 = (int[]) clone(obj);
			System.out.println(obj1.getClass().getName());
			System.out.println( (obj == obj1) ? "true" : "false :" + obj1);
		}

	}
}
