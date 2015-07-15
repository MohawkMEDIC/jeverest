/* 
 * Copyright 2012 Mohawk College of Applied Arts and Technology
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
 * Date: 12-21-2012 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;

/**
 * Represents a formatter that can parse a CS instance 
 */
public class CSFormatter extends ANYFormatter {

	/**
	 * Graph an object to the stream
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		super.graph(s, o, context, result);
		
		CS<?> instance = (CS<?>)o;
		
		try
		{
			if(instance.getCode() != null && !instance.isNull())
				s.writeAttribute("code", FormatterUtil.toWireFormat(instance.getCode()));
		}
		catch(XMLStreamException e)
		{
			throw new FormatterException("Could not graph CS instance", e);
		}
		
	}

	/**
	 * Parse a CS instance
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		CS<?> retVal = this.parseCodifiedValue(s, context, result, CS.class);
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/**
	 * Parse into a specialization of CS
	 */
	protected <T extends CS<?>> T parseCodifiedValue(XMLStreamReader s, FormatterElementContext context, DatatypeFormatterParseResult result, Class<T> returnType)
	{
		T retVal = super.parse(s, context, result, returnType);
		
		// Parse data out
		if(s.getAttributeValue(null, "code") != null)
		{
			if(s.getLocalName().equals("interpretationCode"))
				String.format("","");
			// Get the bound code domain
			Type boundCodeDomain = String.class;
			if(context.getOwnerType() instanceof ParameterizedType)
			{
				boundCodeDomain = ((ParameterizedType)context.getOwnerType()).getActualTypeArguments()[0];
				if(boundCodeDomain instanceof TypeVariable<?>)
					boundCodeDomain = context.getActualTypeArgument((TypeVariable<?>)boundCodeDomain);
				else if(boundCodeDomain instanceof WildcardType)
					boundCodeDomain = String.class;
			}
			retVal.setCode(FormatterUtil.fromWireFormat(s.getAttributeValue(null, "code"), boundCodeDomain));
		}
		
		return retVal;

	}
	
	/**
	 * Get the type this formatter handles
	 */
	@Override
	public String getHandlesType() {
		return "CS";
	}


	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.addAll(Arrays.asList(new String[] { "code" }));
		return retVal;
	}

}
