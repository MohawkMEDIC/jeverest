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
 * Represents an attribute
 */
public class StructDocAttributeNode extends StructDocNode {

	/**
	 * Creates a new instance of the StructureDocumentAttributeNode
	 */
	public StructDocAttributeNode() { super(); }
	/**
	 * Creates a new instance of the StructuredDocumentAttribute node with specified name and value
	 * @param name The name of the attribute
	 * @param value The value of the attribute
	 */
	public StructDocAttributeNode(String name, String value) { super(name, value); }
	
	/**
	 * Read XML
	 */
	@Override
	public void readXml(XMLStreamReader reader) {
		if(reader.getEventType() != XMLStreamReader.ATTRIBUTE)
			throw new IllegalStateException("Invalid State, must be in Attribute");
		this.m_name = reader.getLocalName();
		this.m_value = reader.getText();
	}

	/**
	 * Write the attribute
	 */
	@Override
	public void writeXml(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeAttribute(this.m_name, this.m_value);

	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s=\"%s\" ", this.m_name, this.m_value);
	}
	/**
	 * Represent this node as a plain string
	 */
	@Override
	public String toPlainString() {
		return "";
	}
	
	

}
