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
 * Date: 01-15-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.PQ;
import org.marc.everest.datatypes.SetOperator;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.DomainTimingEvent;
import org.marc.everest.datatypes.generic.EIVL;
import org.marc.everest.datatypes.generic.IVL;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Represents a formatter that is capable of formatting to/from EIVL format
 */
public class EIVLFormatter extends SXCMFormatter {

	/**
	 * Graph the object o onto the stream writer s
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {

		super.graph(s, o, context, result);
        
        // Now graph the attributes
		EIVL<?> instance = (EIVL<?>)o;
		
		try
		{
	        // Append the attributes to the writer
	        if (instance.isNull())
	            return; // Nothing to report
	
	        // Write elements
	        if (instance.getEvent() != null)
	        {
	            s.writeStartElement(DatatypeFormatter.NS_HL7, "event");
	            IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getEvent(), context.findChildContextFromName("event", PropertyType.NONSTRUCTURAL, EIVL.class));
	            result.addResultDetail(hostResult.getDetails());
	            s.writeEndElement();
	        }
	        if (instance.getOffset() != null)
	        {
	            s.writeStartElement(DatatypeFormatter.NS_HL7, "offset");
	            IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getOffset(), context.findChildContextFromName("offset", PropertyType.NONSTRUCTURAL, EIVL.class));
	            result.addResultDetail(hostResult.getDetails());
	            s.writeEndElement();
	        }
		}
		catch(XMLStreamException e)
		{
			result.addResultDetail(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), s.toString(), e));
		}
	
	}

	/**
	 * Parse an object from the wire
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		EIVL<?> retVal = super.parseSxcm(s, context, result, EIVL.class);
		
		// Content / Elements
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

						// Offset
						if(s.getLocalName().equals("offset"))
						{
							IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("offset", PropertyType.NONSTRUCTURAL, EIVL.class));
							result.addResultDetail(hostResult.getDetails());
							retVal.setOffset((IVL<PQ>)hostResult.getStructure());
						}
						// Event
						else if(s.getLocalName().equals("event"))
						{
							IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("event", PropertyType.NONSTRUCTURAL, EIVL.class));
							result.addResultDetail(hostResult.getDetails());
							retVal.setEvent((CS<DomainTimingEvent>)hostResult.getStructure());
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
				throw new FormatterException("Could not parse EIVL type", e);
			}
		}// if
		
		// validate
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/**'
	 * Get the type handled by this formatter helper
	 */
	@Override
	public String getHandlesType() {
		return "EIVL";
	}

	/**
	 * Get supported properties
	 * @return
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.addAll(Arrays.asList(
				"event",
				"offset"
				));
		return retVal;
	}
	
	
	
}
