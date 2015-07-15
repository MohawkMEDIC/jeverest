/* 
 * Copyright 2008-2013 Mohawk College of Applied Arts and Technology
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 * 
 * User: Justin Fyfe
 * Date: 10-25-2012
 */
package org.marc.everest.formatters;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;

import javax.xml.bind.DatatypeConverter;

import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.ED;
import org.marc.everest.datatypes.ST;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.interfaces.IGraphable;

/**
 * A class of conversion functions
 */
public final class Convert {

	/**
	 * Convert space separated items into a string
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IGraphable> SET<T> toSet(String value, Class<T> type, Type rawDestType)
	{
		String[] values = value.split("\\s");
        SET<T> retVal = new SET<T>();
        Type castType = type;
        if(rawDestType instanceof ParameterizedType)
        	castType = ((ParameterizedType)rawDestType).getActualTypeArguments()[0];
        			
        for(String v : values)
            retVal.add((T)FormatterUtil.fromWireFormat(v, castType));
        return retVal;
	}

	/**
	 * Convert to bigdecimal
	 */
	public static BigDecimal toBigDecimal(String bigd)
	{
		return new BigDecimal(bigd);
	}
	
	/**
	 * Convert to boolean
	 */
	public static BL toBoolean(String value)
	{
		return new BL(DatatypeConverter.parseBoolean(value));
	}
	
	/**
	 * Convert a string to a coded simple
	 */
	public static <T> CS<T> toCodedSimple(String value, Class<T> type)
	{
		return new CS<T>((T)FormatterUtil.fromWireFormat(value, type));
	}

	/**
	 * Convert a string to a coded simple
	 */
	public static CS<String> toCodedSimple(String value)
	{
		return new CS<String>(value);
	}
	
	/**
	 * Convert an SD to an ED
	 */
	public static ED toEd(ST value)
	{
		try {
			return value.toEd();
		} catch (UnsupportedEncodingException e) {
			throw new FormatterException("Cannot convert binary data to string", e);
		}
	}
}
