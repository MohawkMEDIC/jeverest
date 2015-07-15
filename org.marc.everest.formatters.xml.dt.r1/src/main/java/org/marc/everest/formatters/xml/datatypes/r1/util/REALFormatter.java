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
 * Date: 02-15-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.REAL;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.ResultDetailType;

/**
 * Represents a formatter that can parse and graph REAL instances
 */
public class REALFormatter extends PDVFormatter {

	/**
	 * Graph the real to the wire
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		// We don't use base.graph() here because we want to control the value property
        ANYFormatter baseFormatter = new ANYFormatter();

        baseFormatter.graph(s, o, context, result);
        REAL instance = (REAL)o;

        if (instance.isNull()) return; // Don't graph anymore

        // Precision
        try {
        	if (instance.getValue() != null && instance.getPrecision() != 0)
				s.writeAttribute("value", String.format(String.format("%%.%df", instance.getPrecision()), instance.getValue()));
			else if (instance.getValue() != null)
	            s.writeAttribute("value", instance.getValue().toString());
    	} catch (XMLStreamException e) {
			throw new FormatterException("Cannot format REAL");
		}
        // Unsupported properties
        if (instance.getExpression() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "REAL", "expression", s.toString()));
        if (instance.getOriginalText() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "REAL", "originalText", s.toString()));
        if (instance.getUncertainty() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "REAL", "uncertainty", s.toString()));
        if (instance.getUncertaintyType() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "REAL", "uncertaintyType", s.toString()));
        if (instance.getUncertainRange() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "REAL", "uncertainRange", s.toString()));

	}

	/**
	 * Get the type this handles
	 */
	@Override
	public String getHandlesType() {
		return "REAL";
	}

	/**
	 * Parse the real instance
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
        REAL retVal = super.parse(s, context, result, REAL.class);

        // Precision is not supported in R1, but is still useful to have so 
        // we will report the precision of the data that was on the wire
        String valStr = s.getAttributeValue(null, "value");
        if (valStr != null && valStr.contains("."))
            retVal.setPrecision(valStr.length() - valStr.indexOf(".") - 1);
        else
            retVal.setPrecision(0);

        
        // Validate
        super.validate(retVal, s.toString(), result);

        return retVal;
	}

	
}
