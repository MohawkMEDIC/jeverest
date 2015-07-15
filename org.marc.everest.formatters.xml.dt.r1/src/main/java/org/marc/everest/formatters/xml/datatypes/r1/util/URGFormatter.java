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
 * Date: 05-25-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.generic.URG;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotSupportedChoiceResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Uncertainty range formatter
 */
public class URGFormatter extends UVPFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.UVPFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		// Format UVP to the wire
		super.graph(s, o, context, result);
		
		// Null?
		URG instance = (URG)o;
		if(instance.isNull()) return;
		
		// Output values
		try
		{
			if(instance.getOriginalText() != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "URG", "originalText", s.toString()));
			if(instance.getLowInclusive() != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "URG", "lowInclusive", s.toString()));
			if(instance.getHighInclusive() != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "URG", "highInclusive", s.toString()));
			
			// Graph like an IVL
            // Why is this duplicated from IVL you ask?
            // Wouldn't it be easier to create a method that graphs IInterval you ask?
            // The reason is simple, URG has some slightly different rules, for example each component is not written as 
            // an IVXB<T> (i.e. no low/high closed)
	         if(instance.getLow() != null && instance.getHigh() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "low", instance.getLow(), context, instance.getLowInclusive()).getDetails());
	        	 result.addResultDetail(this.writeElementUtil(s, "high", instance.getHigh(), context, instance.getHighInclusive()).getDetails());
	        	 
	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "low, high, value can't be represented together in an URG data type in R1. The data has been formatted but may be invalid", s.toString(), null));
                if (instance.getWidth() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "high, width, low can't be represented together in an URG data type in R1. The data has been formatted but may be invalid", s.toString(), null));
	         }
	         else if(instance.getLow() != null && instance.getWidth() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "low", instance.getLow(), context, instance.getLowInclusive()).getDetails());
	        	 result.addResultDetail(this.writeElementUtil(s, "width", instance.getWidth(), context, null).getDetails());

	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "low, width, value can't be represented together in an URG data type in R1. The data has been formatted but may be invalid", s.toString(), null));
                if (instance.getHigh() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "high, width, low can't be represented together in an URG data type in R1. The data has been formatted but may be invalid", s.toString(), null));
	         }
	         else if(instance.getHigh() != null && instance.getWidth() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "width", instance.getWidth(), context, null).getDetails());
	        	 result.addResultDetail(this.writeElementUtil(s, "high", instance.getHigh(), context, instance.getHighInclusive()).getDetails());

	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "low, width, value can't be represented together in an URG data type in R1. The data has been formatted but may be invalid", s.toString(), null));
                if (instance.getLow() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "high, width, low can't be represented together in an URG data type in R1. The data has been formatted but may be invalid", s.toString(), null));
        	 }
	         else if(instance.getLow() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "low", instance.getLow(), context, instance.getLowInclusive()).getDetails());
	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "low and value can't be represented together in an URG data type in R1. The data has been formatted but may be invalid", s.toString(), null));
	         }
	         else if(instance.getHigh() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "high", instance.getHigh(), context, instance.getHighInclusive()).getDetails());
	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "high and value can't be represented together in an URG data type in R1. The data has been formatted but may be invalid", s.toString(), null));
	         }
	         else if(instance.getWidth() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "width", instance.getWidth(), context, null).getDetails());
	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "width and value can't be represented together in an URG data type in R1. The data has been formatted but may be invalid", s.toString(), null));
	         }
	         else
                result.addResultDetail(new ResultDetail(ResultDetailType.ERROR,
                    "Can't create a valid representation of IVL using the supplied data for data types R1. Valid URG must have [low & [high | width]] | [high & width] | high | value to satisfy data type R1 constraints",
                    s.toString(), null));
		}
		catch(Exception e)
		{
			throw new FormatterException("Could not format URG instance", e);
		}
		
		
	}

	/**
	 * Write the element to the wire
	 */
	private IFormatterGraphResult writeElementUtil(XMLStreamWriter s, String elementName, IGraphable value, FormatterElementContext context, Object inclusive) throws XMLStreamException
	{
		s.writeStartElement(DatatypeFormatter.NS_HL7, elementName);
		
		if(inclusive != null)
			s.writeAttribute("inclusive", ((Boolean)inclusive).toString());
		
		IFormatterGraphResult retVal = this.getHost().graph(s,  value, context.findChildContextFromName(elementName, PropertyType.NONSTRUCTURAL));
		s.writeEndElement();
		return retVal;
	}
	
	
	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.UVPFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		return super.parse(s, context, result);
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.UVPFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "URG";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.UVPFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		return super.getSupportedProperties();
	}

	
	
}
