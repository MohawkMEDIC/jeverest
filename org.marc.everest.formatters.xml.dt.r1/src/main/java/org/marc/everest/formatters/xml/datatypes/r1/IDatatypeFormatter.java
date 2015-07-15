/* 
 * Copyright 2011-2012 Mohawk College of Applied Arts and Technology
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
 * Date: 11-09-2012
 */
package org.marc.everest.formatters.xml.datatypes.r1;

import java.util.List;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;

/**
 * Interface for helper formatters
 */
public interface IDatatypeFormatter {

	/**
	 * Graph object o onto s
	 */
	void graph(XMLStreamWriter s, Object o, FormatterElementContext context, DatatypeFormatterGraphResult result);

	/**
	 * Parse an object from s
	 */
	Object parse(XMLStreamReader s, FormatterElementContext context, DatatypeFormatterParseResult result);

	/**
	 * The type that this formatter handles
	 */
	String getHandlesType();

	/**
	 * Gets the host formatter
	 */
	IXmlStructureFormatter getHost();
	
	/**
	 * Sets the host formatter
	 */
	void setHost(IXmlStructureFormatter host);
	
	/**
	 * Get supported properties
	 */
	List<String> getSupportedProperties();
	
}
