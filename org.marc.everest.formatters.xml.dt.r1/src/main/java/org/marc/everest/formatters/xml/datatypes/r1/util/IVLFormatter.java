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
 * Date: 03-21-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.PQ;
import org.marc.everest.datatypes.SetOperator;
import org.marc.everest.datatypes.generic.IVL;
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
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.NotSupportedChoiceResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Represents a class that can format / graph IVL 
 */
public class IVLFormatter extends PDVFormatter {

	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		
		// Graph base properties
		super.graph(s, o, context, result);
		
		IVL<?> instance = (IVL<?>)o;
		if(instance.isNull()) return; // no need to format
		
		// Warn developer of any unsupported properties
		 if (instance.getOriginalText() != null)
             result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "IVL", "originalText", s.toString()));
         if (instance.getLowInclusive() != null || instance.getHighInclusive() != null)
             result.addResultDetail(new ResultDetail(ResultDetailType.WARNING,
                 "The properties 'lowInclusive' and 'highInclusive' will be used as low/@inclusive and high/@inclusive attributes for R1 formatting", s.toString(), null));

         try
         {
	         // Operator
	         if(instance.getOperator() != null)
	        	 s.writeAttribute("operator", FormatterUtil.toWireFormat(instance.getOperator()));
	         
	         // Valid combinations of data
	         if(instance.getLow() != null && instance.getHigh() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "low", instance.getLow(), context, instance.getLowInclusive()).getDetails());
	        	 result.addResultDetail(this.writeElementUtil(s, "high", instance.getHigh(), context, instance.getHighInclusive()).getDetails());
	        	 
	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "low, high, value can't be represented together in an IVL data type in R1. The data has been formatted but may be invalid", s.toString(), null));
                 if (instance.getWidth() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "high, width, low can't be represented together in an IVL data type in R1. The data has been formatted but may be invalid", s.toString(), null));
	         }
	         else if(instance.getLow() != null && instance.getWidth() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "low", instance.getLow(), context, instance.getLowInclusive()).getDetails());
	        	 result.addResultDetail(this.writeElementUtil(s, "width", instance.getWidth(), context, null).getDetails());

	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "low, width, value can't be represented together in an IVL data type in R1. The data has been formatted but may be invalid", s.toString(), null));
                 if (instance.getHigh() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "high, width, low can't be represented together in an IVL data type in R1. The data has been formatted but may be invalid", s.toString(), null));
	         }
	         else if(instance.getHigh() != null && instance.getWidth() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "width", instance.getWidth(), context, null).getDetails());
	        	 result.addResultDetail(this.writeElementUtil(s, "high", instance.getHigh(), context, instance.getHighInclusive()).getDetails());

	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "low, width, value can't be represented together in an IVL data type in R1. The data has been formatted but may be invalid", s.toString(), null));
                 if (instance.getLow() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "high, width, low can't be represented together in an IVL data type in R1. The data has been formatted but may be invalid", s.toString(), null));
         	 }
	         else if(instance.getLow() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "low", instance.getLow(), context, instance.getLowInclusive()).getDetails());
	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "low and value can't be represented together in an IVL data type in R1. The data has been formatted but may be invalid", s.toString(), null));
	         }
	         else if(instance.getHigh() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "high", instance.getHigh(), context, instance.getHighInclusive()).getDetails());
	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "high and value can't be represented together in an IVL data type in R1. The data has been formatted but may be invalid", s.toString(), null));
	         }
	         else if(instance.getWidth() != null)
	         {
	        	 result.addResultDetail(this.writeElementUtil(s, "width", instance.getWidth(), context, null).getDetails());
	        	 if (instance.getValue() != null)
	                    result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "width and value can't be represented together in an IVL data type in R1. The data has been formatted but may be invalid", s.toString(), null));
	         }
	         else if(instance.getValue() != null)
        	 {
	        	 result.addResultDetail(new NotSupportedChoiceResultDetail(ResultDetailType.WARNING, "Though XML ITS supports it, use of the IVL 'value' attribute should be avoided. The data has been serialized but may be uninterpretable by anoher system", s.toString(), null)); 
        	 }
	         else
                 result.addResultDetail(new ResultDetail(ResultDetailType.ERROR,
                     "Can't create a valid representation of IVL using the supplied data for data types R1. Valid IVL must have [low & [high | width]] | [high & width] | high | value to satisfy data type R1 constraints",
                     s.toString(), null));

         }
         catch(XMLStreamException e)
         {
        	 throw new FormatterException("Cannot graph IVL type", e);
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
	
	/**
	 * Get the type this handles
	 */
	@Override
	public String getHandlesType() {
		return "IVL";
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.addAll(Arrays.asList(new String[] {
				"operator",
				"low",
				"high",
				"width",
				"lowClosed",
				"highClosed"
		}));
		return retVal;
	}

	/**
	 * Parse and object from the command prompt
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		// Preserve Operator
		SetOperator operator = null;
		if(s.getAttributeValue(null, "operator") != null)
			operator = FormatterUtil.fromWireFormat(s.getAttributeValue(null, "operator"), SetOperator.class);
		
		IVL retVal = super.parse(s, context, result, IVL.class);

		if(operator != null)
			retVal.setOperator(operator);
		
		// Operator attribute
		// Append warning when value is used
		//if(s.getAttributeValue(null, "value") != null)
		//	result.addResultDetail(new NotSupportedChoiceResultDetail(ResultDetailType.WARNING, "Though the XML ITS supports it, use of the IVL 'value' attribute should be avoided", s.toString(), null));
		
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
						
						// Low
						if(s.getLocalName().equals("low"))
						{
							// Inclusive?
							if(s.getAttributeValue(null, "inclusive") != null)
								retVal.setLowInclusive(FormatterUtil.fromWireFormat(s.getAttributeValue(null, "inclusive"), Boolean.class));
							
							FormatterElementContext childContext = context.findChildContextFromName("low", PropertyType.NONSTRUCTURAL, IVL.class);
							if(childContext.getOwnerType() instanceof TypeVariable)
								childContext.setOwnerType(childContext.getActualTypeArgument((TypeVariable<?>)childContext.getOwnerType()));
							IFormatterParseResult hostResult = this.getHost().parse(s, childContext);
							result.addResultDetail(hostResult.getDetails());
							retVal.setLow((IAny)hostResult.getStructure());
						}
						// Event
						else if(s.getLocalName().equals("high"))
						{
							// Inclusive?
							if(s.getAttributeValue(null, "inclusive") != null)
								retVal.setHighInclusive(FormatterUtil.fromWireFormat(s.getAttributeValue(null, "inclusive"), Boolean.class));
							
							FormatterElementContext childContext = context.findChildContextFromName("high", PropertyType.NONSTRUCTURAL, IVL.class);
							if(childContext.getOwnerType() instanceof TypeVariable)
								childContext.setOwnerType(childContext.getActualTypeArgument((TypeVariable<?>)childContext.getOwnerType()));
							IFormatterParseResult hostResult = this.getHost().parse(s, childContext);
							result.addResultDetail(hostResult.getDetails());
							retVal.setHigh((IAny)hostResult.getStructure());
						}
						else if(s.getLocalName().equals("width"))
						{
							IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("width", PropertyType.NONSTRUCTURAL, IVL.class));
							result.addResultDetail(hostResult.getDetails());
							retVal.setWidth((PQ)hostResult.getStructure());
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
				throw new FormatterException("Could not parse IVL type", e);
			}
		}// if
		
		// validate
		super.validate(retVal, s.toString(), result);
		return retVal;
	}
	
}
