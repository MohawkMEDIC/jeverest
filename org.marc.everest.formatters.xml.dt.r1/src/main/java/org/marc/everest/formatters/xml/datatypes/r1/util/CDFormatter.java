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
 * Date: 01-05-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.datatypes.generic.CE;
import org.marc.everest.datatypes.generic.CR;
import org.marc.everest.datatypes.generic.CV;
import org.marc.everest.datatypes.generic.LIST;
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
 * Represents a formatter that is capable of formatting CD instances
 */
public class CDFormatter extends CEFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.CEFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		// Super graph
		super.graph(s, o, context, result);
		
		CD<?> instance = (CD<?>)o;
		
		try
		{
			// Format coded simple even if null ...
			if(instance.getQualifier() != null)
			{
				// Get child context
				s.writeStartElement(DatatypeFormatter.NS_HL7, "qualifier");
				IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getQualifier(), context.findChildContextFromName("qualifier", PropertyType.NONSTRUCTURAL));
				result.setCode(hostResult.getCode());
				result.addResultDetail(hostResult.getDetails());
				s.writeEndElement();
			}
		}
		catch(XMLStreamException e)
		{
			throw new FormatterException("Can't format CD instance", e);
		}
				
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.CEFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "CD";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.CEFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("qualifier");
		return retVal;
	}

	
	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.CEFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		CD<?> retVal = super.parseCodifiedValue(s, context, result, CD.class);
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.CEFormatter#parseElementDataIntoInstance(org.marc.everest.datatypes.generic.CV, javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	protected boolean parseElementDataIntoInstance(CV<?> retVal,
			XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		// Parse qualifier
		if(s.getLocalName().equals("qualifier"))
		{
			LISTFormatter setFormatter = new LISTFormatter();
            setFormatter.setHost(this.getHost());
            if (retVal instanceof CD<?>)
                ((CD<?>)retVal).setQualifier((LIST<CR<?>>)setFormatter.parse(s, context.findChildContextFromName("qualifier", PropertyType.NONSTRUCTURAL, CD.class), result)); // Parse LIST
            else
                result.addResultDetail(new NotImplementedElementResultDetail(ResultDetailType.WARNING, s.getLocalName(), s.getNamespaceURI(), s.toString(), null));
            return true;
		}
		else
			return super.parseElementDataIntoInstance(retVal, s, context, result);
	}


}
