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
 * Date: 01-27-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.ENXP;
import org.marc.everest.datatypes.TN;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.InsufficientRepetitionsResultDetail;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Represents a trivial name formatter that is capable of parsing and formatting 
 * a trivial name
 */
public class TNFormatter extends ANYFormatter {

	/**
	 * Graph an object to the wire
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		 TN tn = (TN)o;
         super.graph(s, o, context, result);

         if (tn.getParts().size() > 0 && tn.getNullFlavor() == null)
			try {
				s.writeCharacters(tn.getPart(0).getValue());
			} catch (XMLStreamException e) {
				throw new FormatterException("Could not format TN instance");
			}
         if (tn.getParts().size() > 1)
             result.addResultDetail(new InsufficientRepetitionsResultDetail(ResultDetailType.WARNING,
                 "TN is only permitted to have one part",
                 s.toString(), null));
	}

	/**
	 * Parse an object from the xml stream
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		TN tn = super.parse(s, context, result, TN.class);

        // Parse the mixed content and add it to the list.
        if (!s.isEndElement())
        {
            String oldName = s.getLocalName();
            ENXP tnPart = new ENXP("");
            try
            {
	            while (s.next() != XMLStreamReader.END_ELEMENT && s.getLocalName() != oldName)
	            {
	                if (s.getEventType() == XMLStreamReader.CHARACTERS || s.getEventType() == XMLStreamReader.CDATA)
	                    tnPart.setValue(tnPart.getValue() + s.getTextStart());
	                else if (s.getEventType() == XMLStreamReader.START_ELEMENT)
	                    result.addResultDetail(new NotImplementedElementResultDetail(ResultDetailType.WARNING,
	                        s.getLocalName(),
	                        s.getNamespaceURI(),
	                        s.toString(), null));
	            }
            }
            catch(Exception e)
            {
            	result.addResultDetail(new ResultDetail(ResultDetailType.ERROR, "Couldn't parse TN instance", s.toString(), e));
            }
            tn.getParts().add(tnPart);
        }

        return tn;	
    }

	/**
	 * Get the type handled by this formatter
	 */
	@Override
	public String getHandlesType() {
		return "TN";
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("part");
		return retVal;
	}

	
	
}
