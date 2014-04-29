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
package pico.commons.beans.conversion;

import java.util.Date;

/**
 * 
 * @author Eun Jeong-Ho, succeun@gmail.com
 * @version 2005. 9. 30. 
 */
public class IntegerConvertor extends Convertor
{
	public IntegerConvertor()
	{
    	register(Integer.class);
	}

	/**
	 * ��ü�� ��ȯ�Ѵ�.
	 * @param obj ��ü
	 * @return ��ȯ�� ��ü
	 */
	@Override
	public Object convert(Object obj)
	{
		if (obj instanceof String)
			return ("".equals(obj)) ? new Integer(0) : Integer.valueOf((String) obj);
		else if (obj instanceof Number)
			return new Integer(((Number) obj).intValue());
		else if (obj instanceof Boolean)
			return new Integer((((Boolean) obj).booleanValue()) ? 1 : 0);
		else if (obj instanceof Date)
			return new Integer(ConvertorUtil.getDate((Date) obj));
		else
			return new Integer(0);
	}

}
