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
 * Date: 05-15-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.PQR;
import org.marc.everest.datatypes.generic.CV;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;

public class PQRFormatter extends CVFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.CVFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		// Since PQR is a CV , we can use the CD formatter to graph basic attributes onto the stream
		PQR instance = (PQR)o;
		
		if(!instance.isNull())
		{
			try
			{
			if (instance.getValue() != null && instance.getPrecision() != 0)
				s.writeAttribute("value", String.format(String.format("%%f.%f", instance.getPrecision()), instance.getValue()));
			else if (instance.getValue() != null)
	            s.writeAttribute("value", instance.getValue().toString());
			}
			catch(Exception e)
			{
				throw new FormatterException("Cannot graph type PQR", e);
			}
		}
		super.graph(s, o, context, result);
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.CVFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		String valStr = s.getAttributeValue(null, "value");
		
		// Create return value
		PQR retVal = super.parseCodifiedValue(s, context, result, PQR.class);
		
		retVal.setValue(new BigDecimal(valStr));
		
		// Validate
		super.validate(retVal, s.toString(), result);
		
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.CVFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "PQR";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.CVFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("value");
		return retVal;
	}

	

}
