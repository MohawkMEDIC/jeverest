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
 * Date: 12-04-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.ED;
import org.marc.everest.datatypes.EncapsulatedDataCompression;
import org.marc.everest.datatypes.EncapsulatedDataIntegrityAlgorithm;
import org.marc.everest.datatypes.EncapsulatedDataRepresentation;
import org.marc.everest.datatypes.ST;
import org.marc.everest.datatypes.TEL;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.R1FormatterCompatibilityMode;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

public class STFormatter extends ANYFormatter {

	/**
	 * Graph object onto the wire
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		super.graph(s, o, context, result);

		try {
			ST instance = (ST)o;
			// Get rid of these attributes
            if (result.getCompatibilityMode() != R1FormatterCompatibilityMode.ClinicalDocumentArchitecture)
            {
                // In R1 data types an ST is a restriction of an ED with these attributes fixed
                s.writeAttribute("mediaType", "text/plain");
                s.writeAttribute("representation", "TXT");
            }

            // Language
            if (instance.getLanguage() != null)
                s.writeAttribute("language", instance.getLanguage());
            
            // Content
            s.writeCharacters(instance.getValue());

            // Translation
            if (instance.getTranslation() != null)
                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ST", "translation", s.toString()));
            
		} catch (Exception e) {
			throw new FormatterException("Could not graph ST instance", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.EDFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		// Parse into retval
		ST retVal = super.parse(s, context, result, ST.class);
		
		// Parse data
		// Now parse our data out... Attributes
        if (s.getAttributeValue(null,"language") != null)
            retVal.setLanguage(s.getAttributeValue(null,"language"));
        
        // Process elements
        StringBuilder innerContent = new StringBuilder();
		if(!s.isEndElement())
		{
			try
			{
				int sDepth = 0;
				String sName = s.getLocalName();
				s.next();
				
				while(!(s.getEventType() == XMLStreamReader.END_ELEMENT && sDepth == 0 && s.getLocalName().equals(sName)))
				{
					if(s.getEventType() == XMLStreamReader.CHARACTERS || 
							s.getEventType() == XMLStreamReader.CHARACTERS)
					{
						innerContent.append(s.getText());
						s.next();
					}
					else
					{
						result.addResultDetail(new NotImplementedElementResultDetail(ResultDetailType.WARNING, s.getLocalName(), s.getNamespaceURI(), s.toString(), null));
						FormatterUtil.readRawXmlInput(s); // Consume data and throw
					}
				}	
			}
			catch(XMLStreamException e)
			{
				throw new FormatterException("Could not parse ED type", e);
			}
		}
		
		if(!innerContent.toString().isEmpty())
			retVal.setValue(innerContent.toString());
		
		// Validate
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.EDFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "ST";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.EDFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("value");
		retVal.add("language");
		return retVal;
	}

	
	
}
