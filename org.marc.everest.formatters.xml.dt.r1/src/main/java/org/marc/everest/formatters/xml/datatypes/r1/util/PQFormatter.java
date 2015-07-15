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
 * Date: 03-21-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.PQ;
import org.marc.everest.datatypes.PQR;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
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
 * Represents a formatter that can serialize to/from PQ instances
 */
public class PQFormatter extends PDVFormatter {

	/**
	 * Graph element o onto s
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		// We want to control Value so ... we'll call the ANY Formatter
		ANYFormatter baseFormatter = new ANYFormatter();
		baseFormatter.graph(s, o, context, result);
		
		PQ instance = (PQ)o;
		if(instance.isNull()) return; // null don't graph
		
		 if (instance.getCodingRationale() != null)
             result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "PQ", "codingRationale", s.toString()));
         if (instance.getExpression() != null)
             result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "PQ", "expression", s.toString()));
         if (instance.getOriginalText() != null)
             result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "PQ", "originalText", s.toString()));
         if (instance.getUncertainty() != null)
             result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "PQ", "uncertainty", s.toString()));
         if (instance.getUncertaintyType() != null)
             result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "PQ", "uncertaintyType", s.toString()));
         if (instance.getUncertainRange() != null)
             result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "PQ", "uncertainRange", s.toString()));
         
         try
         {
	         if (instance.getUnit() != null)
	             s.writeAttribute("unit", instance.getUnit());
	         //System.out.println(String.format("%%f.%d", instance.getPrecision()));
			 if (instance.getValue() != null)
	            s.writeAttribute("value", instance.getValue().toString());
	         
	         // Translations
	         if(instance.getTranslation() != null)
	        	 for(PQR trans : instance.getTranslation())
	        	 {
	        		 s.writeStartElement(DatatypeFormatter.NS_HL7, "translation");
	 	            IFormatterGraphResult hostResult = this.getHost().graph(s, trans, context.findChildContextFromName("translation", PropertyType.NONSTRUCTURAL, PQ.class));
		            result.addResultDetail(hostResult.getDetails());
	        		 s.writeEndElement();
	        	 }
         }
         catch(XMLStreamException e)
         {
        	 throw new FormatterException("Could not format PQ instance", e);
         }
	}

	/**
	 * Parse an object from s 
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		PQ retVal = super.parse(s, context, result, PQ.class);
		
		// Unit?
		retVal.setUnit(s.getAttributeValue(null, "unit"));
		
		// Value string
		String valueString = s.getAttributeValue(null, "value");
		if(valueString != null)
			retVal.setValue(new BigDecimal(valueString));
		if(valueString != null && valueString.contains("."))
			retVal.setPrecision(valueString.length() - valueString.indexOf("."));
		else
			retVal.setPrecision(0);
		
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
						if(s.getEventType() == XMLStreamReader.END_ELEMENT) continue;

						// Translation
						if(s.getLocalName().equals("translation"))
						{
							IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("translation", PropertyType.NONSTRUCTURAL, PQ.class));
							result.addResultDetail(hostResult.getDetails());
							retVal.setTranslation((SET<PQR>)hostResult.getStructure());
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
				throw new FormatterException("Could not parse PQ type", e);
			}
		}// if
		
		// validate
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.addAll(Arrays.asList(new String[] {
				"unit",
				"precision",
				"translation"
		}));
		return retVal;
	}

	/**
	 * Get the type this formatter handles
	 */
	@Override
	public String getHandlesType() {
		return "PQ";
	}

	
	
}
