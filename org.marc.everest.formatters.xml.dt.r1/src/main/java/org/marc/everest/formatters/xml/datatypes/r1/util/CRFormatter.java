/* 
 * Copyright 2011-2013 Mohawk College of Applied Arts and Technology
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
 * Date: 01-03-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.datatypes.generic.CR;
import org.marc.everest.datatypes.generic.CV;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Represents a formatter that can parse and graph the CR datatype
 */
public class CRFormatter extends ANYFormatter {

	/**
	 * Graph a CR instance to the wire
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		super.graph(s, o, context, result);
		
		CR<?> instance = (CR<?>)o;
		
		if(instance.isNull())
			return; // Don't format if nullflavor is set
		
		// Graph structural attributes
		try {
			if(instance.isInverted() != null)
				s.writeAttribute("inverted", DatatypeConverter.printBoolean(instance.isInverted()));
			
			// Non structural elements
			if(instance.getName() != null)
			{
				s.writeStartElement(DatatypeFormatter.NS_HL7, "name");
				IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getName(), context.findChildContextFromName("name", PropertyType.NONSTRUCTURAL, CR.class));
				result.addResultDetail(hostResult.getDetails());
				s.writeEndElement();
			}
			if(instance.getValue() != null)
			{
				s.writeStartElement(DatatypeFormatter.NS_HL7, "value");
				IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getValue(), context.findChildContextFromName("value", PropertyType.NONSTRUCTURAL, CR.class));
				result.addResultDetail(hostResult.getDetails());
				s.writeEndElement();
			}
		} catch (XMLStreamException e) {
			throw new FormatterException("Could not format CR instance", e);
		}
					
	}

	/**
	 * Parse an object from the stream
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		CR<?> retVal = super.parse(s, context, result, CR.class);
		
		// Parse
		if(!retVal.isNull())
		{
			// Inverted
			if(s.getAttributeValue(null, "inverted") != null)
				retVal.setInverted(DatatypeConverter.parseBoolean(s.getAttributeValue(null, "inverted")));
			
			// Process elements
			if(!s.isEndElement())
			{
				try
				{
					int sDepth = 0;
					String sName = s.getLocalName();
					DatatypeFormatter.nextElementEvent(s);
					while(!(s.getEventType() == XMLStreamReader.END_ELEMENT && sDepth == 0 && s.getLocalName().equals(sName)))
					{
						
						String oldName = s.getLocalName();
						try
						{

							if(s.getLocalName().equals("name"))
							{
								IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("name", PropertyType.NONSTRUCTURAL, CR.class));
								result.addResultDetail(hostResult.getDetails());
								retVal.setName((CV<?>)hostResult.getStructure());
							}
							else if(s.getLocalName().equals("value"))
							{
								IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("value", PropertyType.NONSTRUCTURAL, CR.class));
								result.addResultDetail(hostResult.getDetails());
								retVal.setValue((CD<?>)hostResult.getStructure());
								
							}
							else
								result.addResultDetail(new NotImplementedElementResultDetail(ResultDetailType.WARNING, s.getLocalName(), s.getNamespaceURI(), s.toString(), null));
							
						}
						catch(MessageValidationException e)
						{
							result.addResultDetail(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), s.toString(), e));
						}
						/**/
						finally
						{
							if(oldName.equals(s.getLocalName())) DatatypeFormatter.nextElementEvent(s);
						}
					}	
				}
				catch(XMLStreamException e)
				{
					throw new FormatterException("Could not parse CR type", e);
				}

			}
		}
		
		// Validate
		super.validate(retVal, s.toString(), result);
		
		return retVal;
			
	}

	/**
	 * Get the type that this formatter helper handles
	 */
	@Override
	public String getHandlesType() {
		return "CR";
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = Arrays.asList(new String[] {
				"name",
				"value",
				"inverted"
		}); 
		retVal.addAll(super.getSupportedProperties());
		return retVal;
	}

	
	
}
