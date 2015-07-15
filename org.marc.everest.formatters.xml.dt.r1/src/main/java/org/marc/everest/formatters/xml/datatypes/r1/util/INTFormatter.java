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

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.INT;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.ResultDetailType;

/**
 * Formatter for the INT datatype
 */
public class INTFormatter extends PDVFormatter {

	/**
	 * Graph the INT to the wire
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		super.graph(s, o, context, result);
		INT instance = (INT)o;
		// Unsupported properties
        if (instance.getExpression() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "INT", "expression", s.toString()));
        if (instance.getOriginalText() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "INT", "originalText", s.toString()));
        if (instance.getUncertainty() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "INT", "uncertainty", s.toString()));
        if (instance.getUncertaintyType() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "INT", "uncertaintyType", s.toString()));
        if (instance.getUncertainRange() != null)
            result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "INT", "uncertaintyRange", s.toString()));
	}

	/**
	 * Get the type that this formatter handles
	 */
	@Override
	public String getHandlesType() {
		return "INT";
	}

	/**
	 * Parse the instance from the wire
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		INT retVal = super.parse(s, context, result, INT.class);
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

}
