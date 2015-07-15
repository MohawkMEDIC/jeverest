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
 * Represents a text node
 * @author Justin
 *
 */
public class StructDocTextNode extends StructDocNode {

	// True if the node is cdata
	private Boolean m_isCdata = false;

	/**
	 * Creates a structured doc text node
	 */
	public StructDocTextNode() { super(); }
	/**
	 * Creates a structured doc text node with specified text
	 */
	public StructDocTextNode(String text) { super(null, text); }

	/**
	 * Get whether the node is CDATA
	 * @return
	 */
	public Boolean isCdata() { return this.m_isCdata; }
	/**
	 * Set if the node is CDATA
	 */
	public void setCdata(Boolean value) { this.m_isCdata = value; }
	
	/**
	 * Read XML
	 */
	@Override
	public void readXml(XMLStreamReader reader) {
		if (reader.getEventType() != XMLStreamReader.CHARACTERS &&
                reader.getEventType() != XMLStreamReader.CDATA)
                throw new IllegalStateException("Invalid state, must be in Text");
        this.m_value = reader.getText();
        this.m_isCdata = reader.getEventType() == XMLStreamReader.CDATA;
	}

	/**
	 * Write XML
	 */
	@Override
	public void writeXml(XMLStreamWriter writer) throws XMLStreamException {
		if (this.m_isCdata)
            writer.writeCData(this.m_value);
        else
            writer.writeCharacters(this.m_value);	
	}
	@Override
	public String toString() {

		if(this.m_isCdata)
			return String.format("<![CDATA[%s]]>", this.m_value);
		else
			return this.m_value.replace("&", "&amp;").replace("<", "&lt;");
		
	}
	/**
	 * Represent this text node as plain string
	 */
	@Override
	public String toPlainString() {
		return this.m_value;
	}

	
}
