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
 * Date: 05-22-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.SetOperator;
import org.marc.everest.datatypes.generic.RTO;
import org.marc.everest.datatypes.generic.SXCM;
import org.marc.everest.datatypes.generic.SXPR;
import org.marc.everest.datatypes.interfaces.IQuantity;
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
 * Represents a formatter that can graph and parse RTO instances from the wire
 * @author fyfej
 *
 */
public class RTOFormatter extends ANYFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		// Graph the parent context
		super.graph(s, o, context, result);
		RTO<?,?> instance = (RTO<?,?>)o;
		
		try
		{
			
			// Check for non-supported properties
			if(instance.getExpression() != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "RTO", "expression", s.toString()));
			if(instance.getOriginalText() != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "RTO", "originalText",  s.toString()));
			if (instance.getUncertainty() != null)
                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "RTO", "uncertainty", s.toString()));
            if (instance.getUncertaintyType() != null)
                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "RTO", "uncertaintyType", s.toString()));
            if (instance.getUncertainRange() != null)
                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "RTO", "uncertainRange", s.toString()));
            if (instance.getValue() != null)
                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "RTO", "value", s.toString()));
            
            if(instance.getNumerator() != null)
            {
            	s.writeStartElement(DatatypeFormatter.NS_HL7, "numerator");
            	// graph the host
            	IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getNumerator(), context.findChildContextFromName("numerator", PropertyType.NONSTRUCTURAL));
            	result.addResultDetail(hostResult.getDetails());
            	s.writeEndElement();
            }
			if(instance.getDenominator() != null)
			{
            	s.writeStartElement(DatatypeFormatter.NS_HL7, "numerator");
            	// graph the host
            	IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getDenominator(), context.findChildContextFromName("denominator", PropertyType.NONSTRUCTURAL));
            	result.addResultDetail(hostResult.getDetails());
            	s.writeEndElement();
			}
		}
		catch(Exception e)
		{
			throw new FormatterException("CAnnot format the RTO instance to the wire.", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		RTO retVal = super.parse(s, context, result, RTO.class);
		
		if(retVal.isNull())
			return retVal;
		else
		{

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

							if(s.getLocalName().equals("numerator"))
							{
								IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("numerator", PropertyType.NONSTRUCTURAL));
								result.addResultDetail(hostResult.getDetails());
								retVal.setNumerator((IQuantity)hostResult.getStructure());
							}
							else if(s.getLocalName().equals("denominator"))
							{
								IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("denominator", PropertyType.NONSTRUCTURAL));
								result.addResultDetail(hostResult.getDetails());
								retVal.setDenominator((IQuantity)hostResult.getStructure());
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
					throw new FormatterException("Could not parse RTO type", e);
				}
			}// if
			
			super.validate(retVal, s.toString(), result);
			return retVal;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "RTO";
	}

	

}
