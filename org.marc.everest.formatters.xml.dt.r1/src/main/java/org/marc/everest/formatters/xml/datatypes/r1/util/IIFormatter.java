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
 * Date: 11-11-2012 (not at 11:00)
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.IdentifierScope;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.R1FormatterCompatibilityMode;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.ResultDetailType;

/**
 * A formatter which can represent an II instance on the wire
 * @author Justin
 *
 */
public class IIFormatter extends ANYFormatter {

	/**
	 * Graph this object
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {

		if(!(o instanceof II))
			throw new IllegalArgumentException("Cannot format object");
		
		// Graph super elements
		super.graph(s, o, context, result);
		
		II instance = (II)o;
		try
		{
			// Handle II Graphing if no null flavor
			if(instance.isNull())
				return;
			if(instance.getRoot() != null) // root element
				s.writeAttribute("root", instance.getRoot());
			if(instance.getExtension() != null) // extension element
				s.writeAttribute("extension", instance.getExtension());
			if(instance.getAssigningAuthorityName() != null) // assigning authority element
				s.writeAttribute("assigningAuthorityName", instance.getAssigningAuthorityName());
			if(instance.getDisplayable() != null) // displayable
				s.writeAttribute("displayable", FormatterUtil.toWireFormat(instance.getDisplayable()));
	
			// Use is not permitted in R1, instead we use scope
			if(instance.getScope() != null)
			{
				if(result.getCompatibilityMode().equals(R1FormatterCompatibilityMode.Canadian))
					switch(instance.getScope())
					{
						case VersionIdentifier:
							s.writeAttribute("use", "VER");
							break;
						case BusinessIdentifier:
							s.writeAttribute("use", "BUS");
							break;
						default:
							result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "II", "Scope", s.toString()));
							break;
					}
				else
					result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "II", "Scope", s.toString()));
			}
			
			// Not supported properties
			if(instance.getIdentifierName() != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "II", "IdentifierName", s.toString()));
			if(instance.getReliability() != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "II", "Reliability", s.toString()));
		}
		catch(XMLStreamException e)
		{
			throw new FormatterException("Could not format II instance", e);
		}
	}

	/**
	 * Parse this item
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		// Parse base attributes
		II retVal = super.parse(s, context, result, II.class);
		
		// Now parse our data out... Attributes
        if (s.getAttributeValue(null, "root") != null)
            retVal.setRoot(s.getAttributeValue(null, "root"));
        if (s.getAttributeValue(null, "extension") != null)
            retVal.setExtension(s.getAttributeValue(null, "extension"));
        if (s.getAttributeValue(null, "displayable") != null)
            retVal.setDisplayable(DatatypeConverter.parseBoolean(s.getAttributeValue(null, "displayable")));
        if (s.getAttributeValue(null, "use") != null)
        {
            if(s.getAttributeValue(null, "use").equals("VER"))
                    retVal.setScope(IdentifierScope.VersionIdentifier);
            else if(s.getAttributeValue(null, "use").equals("BUS"))
                    retVal.setScope(IdentifierScope.BusinessIdentifier);
        }

        // Validate
        super.validate(retVal, s.toString(), result);
        return retVal;
	}

	/**
	 * Get the type this handles
	 */
	@Override
	public String getHandlesType() {
		return "II";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {

		List<String> retVal = Arrays.asList(new String[]
				{
					"root",
					"extension",
					"displayable",
					"use"
				});
		retVal.addAll(super.getSupportedProperties());
		return retVal;
	}

	
	
}
