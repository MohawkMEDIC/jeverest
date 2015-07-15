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

/**
 * Represents a datatype formatter that can represent an ADXP instance
 */
public class ADXPFormatter extends ANYFormatter {

	// Backing field for structure formatter
	private IXmlStructureFormatter m_host;
	
	/**
	 * Graph the item to the stream
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {

		// Graph ANY attributes
		super.graph(s, o, context, result);
		
		ADXP instance = (ADXP)o;
		if(instance.isNull())
			return; // No need to graph a null item
		
		// Format types
		try
		{
			if(instance.getPartType() != null && result.getCompatibilityMode() != R1FormatterCompatibilityMode.ClinicalDocumentArchitecture)
				s.writeAttribute("partType", FormatterUtil.toWireFormat(instance.getPartType()));
			if(instance.getCode() != null)
			{
				if(result.getCompatibilityMode() == R1FormatterCompatibilityMode.Canadian)
					s.writeAttribute("code", instance.getCode());
				else
					result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ADXP", "code", s.toString()));
			}
			if(instance.getValue() != null)
				s.writeCharacters(instance.getValue());
		}
		catch(XMLStreamException e)
		{
			throw new FormatterException("Can't format the ADXP instance", e);
		}
		// Unsupported properties
		if(instance.getCodeSystem() != null)
			result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ADXP", "codeSystem", s.toString()));
		if(instance.getCodeSystemVersion() != null)
			result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ADXP", "codeSystemVersion", s.toString()));
	}

	/**
	 * Parse an object from the stream
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		// Parse the result
		ADXP retVal = super.parse(s, context, result, ADXP.class);
		
		if(!s.isEndElement())
		{
			String tAttributeValue = s.getAttributeValue(null, "code");
			if(tAttributeValue != null && result.getCompatibilityMode() == R1FormatterCompatibilityMode.Canadian)
				retVal.setCode(tAttributeValue);
			else if(tAttributeValue != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ADXP", "code", s.toString()));
			
			try {
				retVal.setValue(s.getElementText());

			} catch (XMLStreamException e) {
				throw new FormatterException("Can't process the contents of the ADXP instance", e);
			}
		}
		
		return retVal;
	}

	/**
	 * Get the handler type
	 */
	@Override
	public String getHandlesType() {
		return "ADXP";
	}

	/**
	 * Gets or sets the host
	 */
	@Override
	public IXmlStructureFormatter getHost() {
		return this.m_host;
	}

	/**
	 * Set the host
	 */
	@Override
	public void setHost(IXmlStructureFormatter host) {
		this.m_host = host;
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
