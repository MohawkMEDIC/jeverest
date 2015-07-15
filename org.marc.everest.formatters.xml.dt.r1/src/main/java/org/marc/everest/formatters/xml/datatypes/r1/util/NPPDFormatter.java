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
 * Date: 04-04-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.List;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.generic.NPPD;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.datatypes.generic.UVP;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;

/**
 * Represents a class that can graph to/from NPPD instances
 */
public class NPPDFormatter extends COLLFormatter {

	/**
	 * Graphs the object to the wire
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {

		context.setOwnerType(((NPPD)o).getClass().getGenericSuperclass());
		super.graph(s, o, context, result);
	}

	/**
	 * Parse the object
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		return super.parse(s, context, result);
	}

	/**
	 * Handles type
	 */
	@Override
	public String getHandlesType() {
		return "NPPD";
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		return super.getSupportedProperties();
	}


	
}
