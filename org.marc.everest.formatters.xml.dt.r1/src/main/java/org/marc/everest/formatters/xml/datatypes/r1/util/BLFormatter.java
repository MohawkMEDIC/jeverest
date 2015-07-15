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

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.BL;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;

/**
 * Represents a formatter that is capable of formatting and parsing BL instances
 * in R1
 */
public class BLFormatter extends ANYFormatter {

	/**
	 * Graph object o onto s
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		// Base graph
		super.graph(s, o, context, result);
		
		BL instance = (BL)o;
		if(instance.isNull())
			return; // don't format null BL
		
		// format value
		try
		{
			if(instance.getValue() != null)
				s.writeAttribute("value", DatatypeConverter.printBoolean(instance.getValue()));
		}
		catch(XMLStreamException e)
		{
			throw new FormatterException("Could not format BL instance", e);
		}
	}

	/**
	 * Parse an object from the stream
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		BL retVal = super.parse(s, context, result, BL.class);
		
		// Parse data
		if(s.getAttributeValue(null, "value") != null)
			retVal.setValue(DatatypeConverter.parseBoolean(s.getAttributeValue(null, "value")));
		
		// validate
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/**
	 * Get the type that this formatter can format
	 */
	@Override
	public String getHandlesType() {
		return "BL";
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.addAll(Arrays.asList(new String[] { "value" }));
		return retVal;
	}

}
