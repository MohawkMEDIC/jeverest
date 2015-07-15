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
import org.marc.everest.datatypes.ED;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.CV;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Represents a class that can format and parse CV instances
 */
public class CVFormatter extends CSFormatter {

	/**
	 * Graph an object onto the stream
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		// Graph CS attributes
		super.graph(s, o, context, result);
		
		// Additional data
		CV<?> instance = (CV<?>)o;
		
		try
		{
			// Format additional attributes
			if(instance.getCodeSystem() != null && !instance.getCodeSystem().isEmpty())
				s.writeAttribute("codeSystem", instance.getCodeSystem());
			if(instance.getCodeSystemName() != null && !instance.getCodeSystemName().isEmpty())
				s.writeAttribute("codeSystemName", instance.getCodeSystemName());
			if(instance.getCodeSystemVersion() != null && !instance.getCodeSystemVersion().isEmpty())
				s.writeAttribute("codeSystemVersion", instance.getCodeSystemVersion());
			if(instance.getDisplayName() != null && !instance.getDisplayName().isEmpty())
				s.writeAttribute("displayName", instance.getDisplayName());
			
			if(instance.getOriginalText() != null)
			{
				EDFormatter edFormatter = new EDFormatter();
				edFormatter.setHost(this.getHost());
				s.writeStartElement(DatatypeFormatter.NS_HL7, "originalText");
				edFormatter.graph(s, instance.getOriginalText(), context, result);
				s.writeEndElement();
			}
		}
		catch(XMLStreamException e)
		{
			throw new FormatterException("Couldn't format CV instance", e);
		}
	}

	/**
	 * Parse object 
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		CV<?> retVal = this.parseCodifiedValue(s, context, result, CV.class);
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/**
	 * Parse codified value with CV attributes	
	 */
	@Override
	protected <T extends CS<?>> T parseCodifiedValue(XMLStreamReader s,
			FormatterElementContext context,
			DatatypeFormatterParseResult result, Class<T> returnType) {
		
		CV<?> retVal = (CV<?>)super.parseCodifiedValue(s, context, result, returnType);
		
		if(s.getAttributeValue(null, "codeSystem") != null)
			retVal.setCodeSystem(s.getAttributeValue(null, "codeSystem"));
		if(s.getAttributeValue(null, "codeSystemName") != null)
			retVal.setCodeSystemName(s.getAttributeValue(null, "codeSystemName"));
		if(s.getAttributeValue(null, "codeSystemVersion") != null)
			retVal.setCodeSystemVersion(s.getAttributeValue(null, "codeSystemVersion"));
		if(s.getAttributeValue(null, "displayName") != null)
			retVal.setDisplayName(s.getAttributeValue(null, "displayName"));
		
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
						
						if(s.getEventType() == XMLStreamReader.END_ELEMENT) continue;

						if(!this.parseElementDataIntoInstance(retVal, s, context, result))
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
				throw new FormatterException("Could not parse AD type", e);
			}

		}
		return (T)retVal;
	}

	/**
	 * Parse element data into an instance
	 */
	protected boolean parseElementDataIntoInstance(CV<?> retVal,
			XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		// Process element
		if(s.getLocalName().equals("originalText"))
		{
			EDFormatter fmtr = new EDFormatter();
			fmtr.setHost(this.getHost());
			retVal.setOriginalText((ED)fmtr.parse(s, context.findChildContextFromName("originalText", PropertyType.NONSTRUCTURAL), result));
			return true;
		}
		
		return false;
	}

	/**
	 * Get the type this handles
	 */
	@Override
	public String getHandlesType() {
		return "CV";
	}

	/**
	 * Get the supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.addAll(Arrays.asList(new String[] {
				"codeSystem",
				"codeSystemName",
				"codeSystemVersion",
				"displayName",
				"originalText"
		}));
		return retVal;
	}

	
}
