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
 * Date: 01-15-2012
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import javax.xml.stream.XMLStreamReader;

import org.marc.everest.datatypes.PN;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;

/**
 * A formatter for the PN type
 */
public class PNFormatter extends ENFormatter {

	/**
	 * Parse the object
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		return super.parse(s,  context, result, PN.class);
	}

	/**
	 * Get the type this handles
	 */
	@Override
	public String getHandlesType() {
		return "PN";
	}

	
}
