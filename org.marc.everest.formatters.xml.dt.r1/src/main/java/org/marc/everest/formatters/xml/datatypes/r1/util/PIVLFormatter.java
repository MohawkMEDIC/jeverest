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
 * Date: 07-21-2014 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.INT;
import org.marc.everest.datatypes.PQ;
import org.marc.everest.datatypes.SetOperator;
import org.marc.everest.datatypes.generic.CalendarCycle;
import org.marc.everest.datatypes.generic.IVL;
import org.marc.everest.datatypes.generic.PIVL;
import org.marc.everest.datatypes.generic.RTO;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.R1FormatterCompatibilityMode;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.NotSupportedChoiceResultDetail;
import org.marc.everest.resultdetails.PropertyValuePropagatedResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Periodic interval processor
 * @author Justin Fyfe
 *
 */
public class PIVLFormatter extends SXCMFormatter  {

	/**
	 * Graph the PIVL
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		super.graph(s, o, context, result);
		
		PIVL<?> instance = (PIVL<?>)o;
		if(instance.isNull()) return; // no need to format
		
		// Warn developer of any unsupported properties
        if (instance.getValue() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "value", "PIVL", s.toString()));
        if (instance.getCount() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "count", "PIVL", s.toString()));

        try {
	        // Operator
            if (instance.getAlignment() != null)
                s.writeAttribute("alignment", FormatterUtil.toWireFormat(instance.getAlignment()));
            if (instance.getInstitutionSpecified() != null)
                s.writeAttribute("institutionSpecified", FormatterUtil.toWireFormat(instance.getInstitutionSpecified()));

            // Phase
            if(instance.getPhase() != null)
            {
				// Get child context
				s.writeStartElement(DatatypeFormatter.NS_HL7, "phase");
				IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getPhase(), context.findChildContextFromName("phase", PropertyType.NONSTRUCTURAL));
				result.setCode(hostResult.getCode());
				result.addResultDetail(hostResult.getDetails());
				s.writeEndElement();
            }

            // Frequency not supported by UV R1
            if(instance.getFrequency() != null)
            {
            	// JF - Frequency is not supported by UV R1
                if (result.getCompatibilityMode() == R1FormatterCompatibilityMode.Canadian)
                {
    				s.writeStartElement(DatatypeFormatter.NS_HL7, "frequency");
    				IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getFrequency(), context.findChildContextFromName("frequency", PropertyType.NONSTRUCTURAL));
    				result.setCode(hostResult.getCode());
    				result.addResultDetail(hostResult.getDetails());
    				s.writeEndElement();
                }
                else
                {
                    PQ periodValue = instance.getFrequency().getDenominator().divide(instance.getFrequency().getNumerator());
                    result.addResultDetail(new PropertyValuePropagatedResultDetail(ResultDetailType.WARNING, "frequency", "period", periodValue, s.toString()));
    				s.writeStartElement(DatatypeFormatter.NS_HL7, "period");
    				IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getPeriod(), context.findChildContextFromName("period", PropertyType.NONSTRUCTURAL));
    				result.setCode(hostResult.getCode());
    				result.addResultDetail(hostResult.getDetails());
    				s.writeEndElement();
                }
            }
            else if(instance.getPeriod() != null)
            {
				s.writeStartElement(DatatypeFormatter.NS_HL7, "period");
				IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getPeriod(), context.findChildContextFromName("period", PropertyType.NONSTRUCTURAL));
				result.setCode(hostResult.getCode());
				result.addResultDetail(hostResult.getDetails());
				s.writeEndElement();
            	
            }

		} catch (XMLStreamException e) {
			throw new FormatterException("Unable to format instance", e);
		}

		 

	}

	/**
	 * Parse the instance
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		PIVL<?> retVal = super.parseSxcm(s, context, result, PIVL.class);
		
		// Operator attribute
		// Append warning when value is used
		if(s.getAttributeValue(null, "value") != null)
			result.addResultDetail(new NotSupportedChoiceResultDetail(ResultDetailType.WARNING, "Though the XML ITS supports it, use of the IVL 'value' attribute should be avoided", s.toString(), null));
		if(s.getAttributeValue(null, "institutionSpecified") != null)
			retVal.setInstitutionSpecified(FormatterUtil.fromWireFormat(s.getAttributeValue(null, "institutionSpecified"), Boolean.class));
		if(s.getAttributeValue(null, "alignment") != null)
			retVal.setAlignment(FormatterUtil.fromWireFormat(s.getAttributeValue(null, "alignment"), CalendarCycle.class));
		
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

					//DatatypeFormatter.nextElementEvent(s);
					
					String oldName = s.getLocalName();
					try
					{
						if(s.getEventType() == XMLStreamReader.END_ELEMENT) continue;
						
						// Frequency
						if(s.getLocalName().equals("frequency"))
						{
							IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("frequency", PropertyType.NONSTRUCTURAL, PIVL.class));
							result.addResultDetail(hostResult.getDetails());
							retVal.setFrequency((RTO<INT,PQ>)hostResult.getStructure());
						}
						// Phase
						else if(s.getLocalName().equals("phase"))
						{
							IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("phase", PropertyType.NONSTRUCTURAL, PIVL.class));
							result.addResultDetail(hostResult.getDetails());
							retVal.setPhase((IVL)hostResult.getStructure());
						}
						// Period
						else if(s.getLocalName().equals("period"))
						{
							IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("period", PropertyType.NONSTRUCTURAL, PIVL.class));
							result.addResultDetail(hostResult.getDetails());
							retVal.setPeriod((PQ)hostResult.getStructure());
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
				throw new FormatterException("Could not parse PIVL type", e);
			}
		}// if
		
		// validate
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/**
	 * Get the type this formatter supports
	 */
	@Override
	public String getHandlesType() {
		// TODO Auto-generated method stub
		return "PIVL";
	}

	/**
	 * Gets supported properties 
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.addAll(Arrays.asList(new String[] {
				"alignment",
				"phase",
				"period",
				"institutionSpecified"
		}));
		return retVal;
	}

}
