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
 * Date: 05-22-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.AD;
import org.marc.everest.datatypes.ADXP;
import org.marc.everest.datatypes.GTS;
import org.marc.everest.datatypes.PostalAddressUse;
import org.marc.everest.datatypes.TEL;
import org.marc.everest.datatypes.TelecommunicationsAddressUse;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * A class that can format to/from TEL instances 
 */
public class TELFormatter extends ANYFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		super.graph(s, o, context, result);
		
		TEL instance = (TEL)o;
		try
		{
			if(instance.isNull()) return; // no need to format the telecommunications address data
			
			if(instance.getValue() != null)
				s.writeAttribute("value", instance.getValue());
			if(instance.getUse() != null && instance.getUse().size() > 0)
				s.writeAttribute("use", FormatterUtil.toWireFormat(instance.getUse()));
			if(instance.getCapabilities() != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING,"TEL",  "capabilities", s.toString()));
			
			// Elements
			if(instance.getUseablePeriod() != null)
			{
				s.writeStartElement(DatatypeFormatter.NS_HL7, "useablePeriod");
				IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getUseablePeriod(), context.findChildContextFromName("useablePeriod", PropertyType.NONSTRUCTURAL));
				result.addResultDetail(hostResult.getDetails());
				s.writeEndElement();
			}
		}
		catch(Exception e)
		{
			throw new FormatterException("Unable to represent TEL instance", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		TEL retVal = super.parse(s, context, result, TEL.class);
		
		// Parse data out
		String useValue = s.getAttributeValue(null, "use"),
				valueValue = s.getAttributeValue(null, "value");
		
		
		// Use value is not null so get it and convert it using the method signature for getUse
		if(useValue != null)
			retVal.setUse((SET<CS<TelecommunicationsAddressUse>>)FormatterUtil.fromWireFormat(useValue, context.findChildContextFromName("use", PropertyType.STRUCTURAL).getGetterMethod().getGenericReturnType()));
		retVal.setValue(valueValue);
		
		// Content / Elements
		if(!s.isEndElement())
		{
			try
			{
				int sDepth = 0;
				String sName = s.getLocalName();
				s.next();
				while(!(s.getEventType() == XMLStreamReader.END_ELEMENT && sDepth == 0 && s.getLocalName().equals(sName)))
				{
					String oldName = s.getLocalName();
					try
					{
						
						// Canadian extension for useable period
						if(s.getLocalName().equals("useablePeriod"))
						{
							IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("useablePeriod", PropertyType.NONSTRUCTURAL, AD.class));
							result.addResultDetail(hostResult.getDetails());
							retVal.setUsablePeriod((GTS)result.getStructure());
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
						if(oldName.equals(s.getLocalName())) s.next();
					}
				}	
			}
			catch(XMLStreamException e)
			{
				throw new FormatterException("Could not parse TEL type", e);
			}

		}

		// Validate
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "TEL";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("value");
		retVal.add("use");
		retVal.add("useablePeriod");
		return retVal;
	}

	

}
