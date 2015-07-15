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
 * Date: 11-19-2012 (not at 11:00)
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.EN;
import org.marc.everest.datatypes.NullFlavor;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.IDatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.R1FormatterCompatibilityMode;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.DatatypeFlavorValidationResultDetail;

public class ANYFormatter implements IDatatypeFormatter {

	/**
	 * Ths host of this formatter
	 */
	protected IXmlStructureFormatter m_host;
	
	/**
	 * Graph o onto s
	 * @throws XMLStreamException 
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		try
		{
			if(!(o instanceof ANY))
				throw new IllegalArgumentException("Cannot format o");
	
			// Cast to ANY
			ANY instance = (ANY)o;
			
			// Render
			if(instance.isNull())
				s.writeAttribute("nullFlavor", FormatterUtil.toWireFormat(instance.getNullFlavor()));
			else if(instance.getFlavorId() != null)
			{
				if(result.getCompatibilityMode().equals(R1FormatterCompatibilityMode.Canadian))
					s.writeAttribute("specializationType", instance.getFlavorId());
				else
					result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ANY", "Flavor", s.toString()));
			}
			
			// validate datatype
			if(result.getValidateConformance() && !instance.validate())
			{
				for(IResultDetail dtl : instance.validateEx())
				{
					dtl.setLocation(s.toString());
					result.addResultDetail(dtl);
				}
				result.setCode(ResultCodeType.Rejected);
			}
				
			// validate flavor
			if(instance.getFlavorId() != null && result.getValidateConformance() && !FormatterUtil.validateFlavor(instance.getFlavorId().toUpperCase(), instance))
			{
				result.addResultDetail(new DatatypeFlavorValidationResultDetail(ResultDetailType.WARNING, instance.getClass().getName(), instance.getFlavorId(), s.toString()));
				if(result.getCode().equals(ResultCodeType.Accepted))
					result.setCode(ResultCodeType.AcceptedNonConformant);
			}
			 // Warn if items can't be represented in R1
	        if (instance.getControlActExt() != null)
	            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ANY", "controlActExt", s.toString()));
	        if(instance.getControlActRoot() != null)
	            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ANY", "controlActRoot", s.toString()));
	        if (instance.getValidTimeHigh() != null && !(o instanceof EN))
	            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ANY", "validTimeHigh", s.toString()));
	        if (instance.getValidTimeLow() != null && !(o instanceof EN))
	            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ANY", "validTimeLow", s.toString()));
	        if (instance.getUpdateMode() != null)
	            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ANY", "updateMode", s.toString()));
		}
		catch(XMLStreamException e)
		{
			throw new FormatterException("Could not format instance", e);
		}
	}

	/**
	 * Parse into the specified type
	 */
	public <T extends IAny> T parse(XMLStreamReader s, FormatterElementContext context, DatatypeFormatterParseResult result, Class<T> instanceType)
	{
		try {
			// Construct
			IAny retVal = instanceType.newInstance();
			
			// Parse nullFlavor and specialization type
			if(s.getAttributeValue(null, "nullFlavor") != null)
				((ANY)retVal).setNullFlavor((NullFlavor)FormatterUtil.fromWireFormat(s.getAttributeValue(null, "nullFlavor"), NullFlavor.class));
			else if(s.getAttributeValue(null, "specializationType") != null && result.getCompatibilityMode() == R1FormatterCompatibilityMode.Canadian)
				((ANY)retVal).setFlavorId(s.getAttributeValue(null, "specializationType"));
			
			return (T)retVal;
		} catch (Exception e) {
			throw new FormatterException("Could not instantiate type", e);
		}
		
	}
	
	/**
	 * Parse the type 
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		ANY instance = this.parse(s, context, result, ANY.class)
				;
        // Read the NullFlavor, and Specialization data from the wire
        if (result.getValidateConformance() && !instance.validate())
            for (IResultDetail r : instance.validateEx())
            {
                r.setLocation(s.toString());
                result.addResultDetail(r);
            }

        // Disabled for test
        // Validate flavor... 
        IResultDetail[] flavor;
        if (instance.getFlavorId() != null && result.getValidateConformance() && FormatterUtil.validateFlavor(instance.getFlavorId().toUpperCase(), instance) == false)
            result.addResultDetail(new DatatypeFlavorValidationResultDetail(ResultDetailType.WARNING, instance.getClass().getName(), instance.getFlavorId(), s.toString()));

        return instance;
	}

	/**
	 * Copy all base attributes
	 */
	public void copyBaseAttributes(ANY src, ANY dest)
	{
		dest.setControlActExt(src.getControlActExt());
		dest.setControlActRoot(src.getControlActRoot());
		dest.setFlavorId(src.getFlavorId());
		dest.setNullFlavor(src.getNullFlavor());
		dest.setUpdateMode(src.getUpdateMode());
		dest.setValidTimeHigh(src.getValidTimeHigh());
		dest.setValidTimeLow(src.getValidTimeLow());
	}
	/**
	 * Get the type this handles
	 */
	@Override
	public String getHandlesType() {
		return "ANY";
	}

	/**
	 * Get the host
	 */
	@Override
	public IXmlStructureFormatter getHost() {
		return this.m_host;
	}

	/**
	 * Sets the host
	 */
	@Override
	public void setHost(IXmlStructureFormatter host) {
		this.m_host = host;
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		return Arrays.asList(new String[] { "nullFlavor" });
	}

	/**
	 * Validate an instance
	 */
	public void validate(ANY instance, String path,	DatatypeFormatterParseResult result) {
		
		// Don't validate null
		if(instance == null)
			return;
		
		// Validate Core DT
         if (result.getValidateConformance() && !instance.validate())
        	 for(IResultDetail dtl : instance.validateEx())
        	 {
        		 dtl.setLocation(path);
        		 result.addResultDetail(dtl);
        	 }

         // Validate flavor... 
         if (instance.getFlavorId() != null && result.getValidateConformance() && FormatterUtil.validateFlavor(instance.getFlavorId().toUpperCase(), instance) == false)
             result.addResultDetail(new DatatypeFlavorValidationResultDetail(ResultDetailType.WARNING, instance.getClass().getName(), instance.getFlavorId(), path));
		
	}

	/**
	 * Parse element data 
	 */
	public <T> T parseElement(String name, XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result, Class<T> clazz) {
		IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName(name, PropertyType.NONSTRUCTURAL));
		result.addResultDetail(hostResult.getDetails());
		return (T)hostResult.getStructure();
	}

}
