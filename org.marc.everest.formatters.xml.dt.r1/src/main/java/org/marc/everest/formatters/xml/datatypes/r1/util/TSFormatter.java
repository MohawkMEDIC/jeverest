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
 * Date: 05-26-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.List;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.TS;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;

/**
 * Represents a formatter that can graph/parse TS instances
 * @author fyfej
 *
 */
public class TSFormatter extends ANYFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		 // Get an instance ref
        TS instance_ts = (TS)o;

        // Do a base format
        super.graph(s, o, context, result);

        // Null flavor
        if (((ANY)o).isNull())
            return;
        
        // Timestamp
        if (instance_ts.getValue() != null)
        	try
	        {
	            s.writeAttribute("value", instance_ts.getValue());
	        }
	        catch(Exception e)
	        {
	        	throw new FormatterException("Could not format TS instance", e);
	        }
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		// Parse TS
        TS retVal = super.parse(s, context, result, TS.class);

        // Now parse our data out... Attributes
        String sValue = s.getAttributeValue(null, "value");
        if(sValue != null)
        	retVal.setValue(sValue);

        // Validate
        super.validate(retVal, s.toString(), result);


        return retVal;
    }

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "TS";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("value");
		return retVal;
	}

	
	
	
}
