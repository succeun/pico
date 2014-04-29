/*
 * �� �ҽ��ڵ��� ���۱��� Eun JeongHo(����ȣ)���� �ִ�.
 * �� �ڵ�� �������� ������� �������� ������ �� ������,
 * ������� �����Ǿ��� ���,
 * ������ ���, ����Ʈ ��û�� �޾ƾ� �ϸ�,
 * ������ ���, ���� ��� �Ѵ�.
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
	 * �ش� ���� int������ �����´�.
	 * @param obj Object
	 * @return int ��
	 */
	public static Integer cloneInt(Integer obj)
	{
		return new Integer(obj.intValue());
	}

	/**
	 * �ش� ���� byte������ �����´�.
	 * @param obj Object
	 * @return byte ��
	 */
	public static Byte cloneByte(Byte obj)
	{
		return new Byte(obj.byteValue());
	}

	/**
	 * �ش� ���� char������ �����´�.
	 * @param obj Object
	 * @return char ��
	 */
	public static Character cloneChar(Character obj)
	{
		return new Character(obj.charValue());
	}

	/**
	 * �ش� ���� int������ �����´�.
	 * @param obj Object
	 * @return short ��
	 */
	public static Short cloneShort(Short obj)
	{
		return new Short(obj.shortValue());
	}

	/**
	 * �ش� ���� long������ �����´�.
	 * @param obj Object
	 * @return long ��
	 */
	public static Long cloneLong(Long obj)
	{
		return new Long(obj.longValue());
	}

	/**
	 * �ش� ���� double������ �����´�.
	 * @param obj Object
	 * @return double ��
	 */
	public static Double cloneDouble(Double obj)
	{
		return new Double(obj.doubleValue());
	}

	/**
	 * �ش� ���� float������ �����´�.
	 * @param obj Object
	 * @return double ��
	 */
	public static Float cloneFloat(Float obj)
	{
		return new Float(obj.floatValue());
	}

	/**
	 * �ش� ���� ���ڿ��� ��ȯ�Ѵ�.
	 * @param obj Object
	 * @return ���ڿ� key�� ���� null �̸� ���ڿ��� ��ȯ�Ѵ�.
	 */
	public static String cloneStr(Object obj)
	{
		return obj.toString();
	}

	/**
	 * �ش� ���� boolean������ �����´�.
	 * @param obj Object
	 * @return boolean ��
	 */
	public static Boolean cloneBoolean(Boolean obj)
	{
		return (obj.booleanValue()) ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * �ش� ���� {@link java.util.Date Date}������ �����´�.
	 * @param obj Object
	 * @return Date ��
	 */
	public static Date cloneDate(Date obj)
	{
		return new Date(obj.getTime());
	}

	/**
	 * �ش� ���� {@link java.math.BigInteger BigInteger}������ �����´�.
	 * @param obj Object
	 * @return Date ��
	 */
	public static BigInteger cloneBigInteger(BigInteger obj)
	{
		return new BigInteger(obj.toByteArray());
	}

	/**
	 * �ش� ���� {@link java.math.BigInteger BigInteger}������ �����´�.
	 * @param obj Object
	 * @return Date ��
	 */
	public static BigDecimal cloneBigDecimal(BigDecimal obj)
	{
		return new BigDecimal(obj.toString());
	}

	/**
	 * ��ü�� ���ϴ� Ÿ���� Ŭ������ ��ü�� ��ȯ�Ѵ�.
     * ���ϴ� Ÿ����, Primitive �Ǵ� Stirng, BigInteger, BigDecimal�� �����Ѵ�.
	 * @param obj Object
	 * @return Object ��ü
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
     * �迭�̳�, Collection�� ���ϴ� Ÿ���� Class�� �迭�� ��ȯ �޴´�.
     * ���ϴ� Ÿ����, Primitive �Ǵ� Stirng, BigInteger, BigDecimal�� �����Ѵ�.
     * @param clazz ��ȯ�ް��� �ϴ� �迭�� Ŭ����
     * @param obj ��ȯ�ϰ��� �ϴ� ��ü
     * @return Ŭ������ ������ �迭
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
			String obj = "������";
			String obj1 = cloneStr(obj);
			System.out.println( (obj.equals(obj1)) ? "true" : "false :" + obj1);
		}

		{
			String[] obj = new String[]{"������", "1", "2"};
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
