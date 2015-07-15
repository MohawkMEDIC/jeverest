/* 
 * Copyright 2008-2014 Mohawk College of Applied Arts and Technology
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
 * Date: 07-03-2014
 */
package org.marc.everest.datatypes.doc;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Represents an XML comment
 */
public class StructDocCommentNode extends StructDocNode {

	/**
	 * Creates a new comment node
	 */
	public StructDocCommentNode() { super(); }
	/**
	 * Creates a new comment node with the specified content
	 * @param text The text of the comment
	 */
	public StructDocCommentNode(String text) { super(null, text); }
	
	/**
	 * Read the xml
	 */
	@Override
	public void readXml(XMLStreamReader reader) {
		if(reader.getEventType() != XMLStreamReader.COMMENT)
			throw new IllegalStateException("Invalid State, must be in Comment");
		this.m_value = reader.getText();

	}

	/**
	 * Write XML
	 */
	@Override
	public void writeXml(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeComment(this.m_value);
	}
	/**
	 * Represent this node as a plain string (that would be displayed)
	 */
	@Override
	public String toPlainString() {
		return "";
	}

}
