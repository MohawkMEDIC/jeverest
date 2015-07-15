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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.datatypes.generic.CE;
import org.marc.everest.datatypes.generic.CV;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;

/**
 * Represents a formatter that can parse a CE
 */
public class CEFormatter extends CVFormatter {

	/**
	 * Graph object onto the wire
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		// Super graph
		super.graph(s, o, context, result);
		
		CE<?> instance = (CE<?>)o;
		
		try
		{
			// Format coded simple even if null ...
			if(instance.getTranslation() != null)
			{
				// Get child context
				s.writeStartElement(DatatypeFormatter.NS_HL7, "translation");
				IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getTranslation(), context.findChildContextFromName("translation", PropertyType.NONSTRUCTURAL));
				result.setCode(hostResult.getCode());
				result.addResultDetail(hostResult.getDetails());
				s.writeEndElement();
			}
		}
		catch(XMLStreamException e)
		{
			throw new FormatterException("Can't format CE instance", e);
		}
		
	}

	/**
	 * Parse an object from the stream
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		CE<?> retVal = super.parseCodifiedValue(s, context, result, CE.class);
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/**
	 * Handle the CE types ... 
	 */
	@Override
	protected boolean parseElementDataIntoInstance(CV<?> retVal,
			XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		if(s.getLocalName().equals("translation"))
		{
			SETFormatter setFormatter = new SETFormatter();
            setFormatter.setHost(this.getHost());
            if (retVal instanceof CE<?>)
                ((CE<?>)retVal).setTranslation((SET<CD<?>>)setFormatter.parse(s, context.findChildContextFromName("translation", PropertyType.NONSTRUCTURAL, CE.class), result)); // Parse LIST
            else
                result.addResultDetail(new NotImplementedElementResultDetail(ResultDetailType.WARNING, s.getLocalName(), s.getNamespaceURI(), s.toString(), null));
            return true;
		}
		else
			return super.parseElementDataIntoInstance(retVal, s, context, result);
	}

	/**
	 * Get the type this formatter helper handles
	 */
	@Override
	public String getHandlesType() {
		return "CE";
	}

	/**
	 * Get the supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.addAll(Arrays.asList(new String[] {
				"translation"
		}));
		return retVal;
	}

	
}
