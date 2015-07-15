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
 * Date: 12-20-2012
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.ADXP;
import org.marc.everest.datatypes.ENXP;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IStructureFormatter;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.IDatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.R1FormatterCompatibilityMode;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Represents a datatype formatter that can represent an ENXP instance
 */
public class ENXPFormatter extends ANYFormatter {
	
	/**
	 * Graph the item to the stream
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {

        ENXP instance = (ENXP)o;

        // Start with part type and code attributes
        super.graph(s, o, context, result);

        if (instance.isNull())
            return;
        
        // Now format our data
        try
        {
	        if (instance.getType() != null && result.getCompatibilityMode() != R1FormatterCompatibilityMode.ClinicalDocumentArchitecture)
	            s.writeAttribute("partType", FormatterUtil.toWireFormat(instance.getType()));
	        if (instance.getQualifier() != null && !instance.getQualifier().isEmpty())
	            s.writeAttribute("qualifier", FormatterUtil.toWireFormat(instance.getQualifier()));
	        if (instance.getCode() != null && result.getCompatibilityMode() != R1FormatterCompatibilityMode.Canadian)
	            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ENXP", "code", s.toString()));
	        else if(instance.getCode() != null)
	            s.writeAttribute("code", instance.getCode());
	        if (instance.getValue() != null)
	            s.writeCharacters(instance.getValue());
	        if (instance.getCodeSystem() != null) // Warn if there is no way to represent this in R1
	            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ENXP", "codeSystem", s.toString()));
	        if(instance.getCodeSystemVersion() != null)
	            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ENXP","codeSystemVersion",  s.toString()));
        }
        catch(Exception e)
        {
        	result.addResultDetail(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), s.toString(), e));
        }
	}

	/**
	 * Parse an object from the stream
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		// Parse the result
		ENXP retVal = super.parse(s, context, result, ENXP.class);
		
		if(!s.isEndElement())
		{
			String tAttributeValue = s.getAttributeValue(null, "code");
			if(tAttributeValue != null && result.getCompatibilityMode() == R1FormatterCompatibilityMode.Canadian)
				retVal.setCode(tAttributeValue);
			else if(tAttributeValue != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ENXP", "code", s.toString()));
			
			try {
				retVal.setValue(s.getElementText());
				
			} catch (XMLStreamException e) {
				throw new FormatterException("Can't process the contents of the ENXP instance", e);
			}
		}
		
		return retVal;
	}

	/**
	 * Get the handler type
	 */
	@Override
	public String getHandlesType() {
		return "ENXP";
	}

	/**
	 * Get a list of supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = Arrays.asList(new String[]
				{
					"partType",
					"code",
					"value"
				});
		retVal.addAll(super.getSupportedProperties());
		return retVal;
	}

}
