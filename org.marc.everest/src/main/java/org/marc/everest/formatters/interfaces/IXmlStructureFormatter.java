/* 
 * Copyright 2008-2011 Mohawk College of Applied Arts and Technology
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
 * Date: 07-21-2011
 */
package org.marc.everest.formatters.interfaces;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.interfaces.IGraphable;

/**
 * Represents a class that can graph and parse instances from 
 * an Xml based stream
 */
public interface IXmlStructureFormatter extends IStructureFormatter {

	/**
	 * Parse data from the specified XMLStream reader and create 
	 * a structure from the stream.
	 * @param xr The XmlReader to from which to read data 
	 * @return An IFormatterParseResult that contains the parsed structure data
	 */
	IFormatterParseResult parse(XMLStreamReader xr);
	/**
	 * Graph data in the specified object to the specified XmlStreamWriter
	 * @param xw The XmlStreamWriter to graph data to
	 * @param o The object to graph to the XmlStreamWriter
	 * @return A IFormatterGraphResult that contains the details of the format operation
	 */
	IFormatterGraphResult graph(XMLStreamWriter xw, IGraphable o);

	/**
	 * Graph an object to xw using the specified context
	 */
	IFormatterGraphResult graph(XMLStreamWriter xw, IGraphable o, FormatterElementContext ctx);
	
	/**
	 * Parse data from the specified object from the specified context
	 */
	IFormatterParseResult parse(XMLStreamReader xr, FormatterElementContext ctx);
	
}
