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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * 
 * @author Eun Jeong-Ho, succeun@gmail.com
 * @version 2005. 9. 30. 
 */
public class StringConvertor extends Convertor
{
	private static final DecimalFormat DEFAULT_NUMBER_FORMAT = new DecimalFormat("#.####################");
	
	public StringConvertor()
	{
		register(String.class);
	}
	/**
	 * °´Ã¼¸¦ º¯È¯ÇÑ´Ù.
	 * @param obj °´Ã¼
	 * @return º¯È¯µÈ °´Ã¼
	 */
	@Override
	public Object convert(Object obj)
	{
		if (obj instanceof char[])
			return String.valueOf((char[])obj);
		if (obj instanceof byte[])
			return String.valueOf(obj);
		if (obj instanceof Date)
			return ConvertorUtil.getDateTime((Date) obj)+"";
		if (obj instanceof BigDecimal)
			return DEFAULT_NUMBER_FORMAT.format(obj);
		else
			return obj.toString();
	}

}
