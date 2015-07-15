/* 
 * Copyright 2013 Mohawk College of Applied Arts and Technology
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
 * Date: 02-15-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.PQ;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.datatypes.interfaces.IPrimitiveDataValue;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.IDatatypeFormatter;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Represents a generic formatter which can format and parse PDV instances
 */
public class PDVFormatter extends ANYFormatter {

	/**
	 * Graph object to the stream
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		super.graph(s, o, context, result);
		
		IPrimitiveDataValue<?> pv = (IPrimitiveDataValue<?>)o;
		
		if(pv.isNull())
			return; // no further graphing
		
		// Graph the value property
		if(pv.getValue() instanceof  ANY)
		{
			IDatatypeFormatter formatter = DatatypeFormatter.getFormatter(pv.getValue().getClass());
			formatter.setHost(this.getHost());
			formatter.graph(s, pv.getValue(), context, result);
		}
		else if(pv.getValue() != null)
		{
			try {
				s.writeAttribute("value", FormatterUtil.toWireFormat(pv.getValue()));
			} catch (XMLStreamException e) {
				throw new FormatterException("Could not format value", e);
			}
		}
	}

	
	/**
	 * Parse the PDV instance
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IAny> T parse(XMLStreamReader s,
			FormatterElementContext context,
			DatatypeFormatterParseResult result, Class<T> instanceType) {
		
		T retVal = super.parse(s, context, result, instanceType);

		IPrimitiveDataValue pdv = (IPrimitiveDataValue)retVal;
		try
		{
			if(s.getAttributeValue(null, "value") != null)
			{
				FormatterElementContext childContext = context.findChildContextFromName("value", PropertyType.STRUCTURAL, retVal.getClass());
				Type parameterType = childContext.getOwnerType();
				if(parameterType instanceof TypeVariable)
				{
					parameterType = childContext.getActualTypeArgument((TypeVariable<?>)parameterType);
					childContext.setOwnerType(parameterType);
				}

				// Hack: Parse as value
				if(ANY.class.isAssignableFrom(FormatterUtil.getClassForType(parameterType)))
				{
					childContext.setIgnoreTypeCasting(true);
					IFormatterParseResult hostResult = this.getHost().parse(s, childContext);
					pdv.setValue(hostResult.getStructure());
				}
				else
				// Hack : This is overridden in XSI:Type
					pdv.setValue(FormatterUtil.fromWireFormat(s.getAttributeValue(null, "value"), parameterType ));
			}
		}
		catch(Exception e)
		{
			result.addResultDetail(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), s.toString(), e));
		}
		
		return retVal;
	}



	/**
	 * Get the type this formatter handles
	 */
	@Override
	public String getHandlesType() {
		return "PDV";
	}

	/**
	 * Get a list of supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("value");
		return retVal;
	}

	
	
}
