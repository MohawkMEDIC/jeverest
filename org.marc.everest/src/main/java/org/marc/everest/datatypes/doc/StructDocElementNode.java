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

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Represents an element
 * @author Justin
 *
 */
public class StructDocElementNode extends StructDocNode {

	// The namespace URI
	private String m_namespaceUri;
	
	/**
	 * Structured document element node
	 */
	public StructDocElementNode() { 
		super();
		this.m_namespaceUri = "urn:hl7-org:v3";
	}
	
	/**
	 * Creates a node with the specified name
	 */
	public StructDocElementNode(String name) {
		super(name, null);
		this.m_namespaceUri = "urn:hl7-org:v3";
	}

	/**
	 * Creates a new structured document with a text node representing the text
	 */
	public StructDocElementNode(String name, String value) {
		super(name, null);
		this.m_children.add(new StructDocTextNode(value));
		
	}
	
	/**
	 * Add a comment to this element returning the new comment
	 * @return The added comment
	 */
	public StructDocCommentNode addComment(String commentText)
	{
		StructDocCommentNode retVal = new StructDocCommentNode(commentText);
		this.m_children.add(retVal);
		return retVal;
	}
	
	/**
	 * Add an attribute with the specified name and value
	 * @return The added attribute
	 */
	public StructDocAttributeNode addAttribute(String name, String value)
	{
		StructDocAttributeNode retVal = new StructDocAttributeNode(name, value);
		this.m_children.add(retVal);
		return retVal;
	}
	
	/**
     * Add an element with the specified name
     * @return A pointer to the new element for chaining calls
	 */
    public StructDocElementNode addElement(String name)
    {
        StructDocElementNode retVal = new StructDocElementNode(name);
        retVal.m_namespaceUri = this.m_namespaceUri;
        this.m_children.add(retVal);
        return retVal;
    }

    /**
     * Add an element with the specified name and value
     * @param name The name of the element
     * @param value The initial value of the element
     * @return A pointer to the new element for chaining calls
     */
    public StructDocElementNode addElement(String name, String value)
    {
        StructDocElementNode retVal = new StructDocElementNode(name, value);
        retVal.m_namespaceUri = this.m_namespaceUri;
        this.m_children.add(retVal);
        return retVal;
    }

    /**
     * Add an element with the specified name and children
     * @param name The name of the element
     * @param children Child nodes to be made part of the element
     * @return A pointer to the new element for chaining calls
     */
    public StructDocElementNode addElement(String name, StructDocNode ... children)
    {
        StructDocElementNode sde = new StructDocElementNode(name);
        sde.m_namespaceUri = this.m_namespaceUri;
        for (StructDocNode c : children)
            sde.m_children.add(c);
        this.m_children.add(sde);
        return sde;
    }
    

    /**
     * Add text node
     * @return A pointer to the new text node for chaining calls
     */
    public StructDocTextNode addText(String text)
    {
        StructDocTextNode retVal = new StructDocTextNode(text);
        this.m_children.add(retVal);
        return retVal;
    }

    
    /**
     * Get the namespace of this element
     */
    public String getNamespaceUri() {
		return m_namespaceUri;
	}

    /**
     * Sets the namespace of this element
     */
	public void setNamespaceUri(String value) {
		this.m_namespaceUri = value;
	}

	/**
     * Read XML
     */
	@Override
	public void readXml(XMLStreamReader reader) {

		if(reader.getEventType() != XMLStreamReader.START_ELEMENT)
			throw new IllegalStateException("Invalid state, myst be in Element");
		this.m_name = reader.getLocalName();
		this.m_namespaceUri = reader.getNamespaceURI();
		
		int depth = 0;
		
		// Attributes
		for(int i = 0; i < reader.getAttributeCount(); i++)
			if(!reader.getAttributeName(i).equals("xmlns") && !reader.getAttributePrefix(i).equals("xmlns"))
				this.addAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
		
		// Elements
		try {
			while(!(reader.next() == XMLStreamReader.END_ELEMENT && depth == 0 && reader.getLocalName().equals(this.m_name)))
			{
				switch(reader.getEventType())
				{
					case XMLStreamReader.COMMENT:
						this.addComment(reader.getText());
						break;
					case XMLStreamReader.CHARACTERS:
					case XMLStreamReader.CDATA:
						if(reader.isWhiteSpace())
							continue;
						this.addText(reader.getText()).setCdata(reader.getEventType() == XMLStreamReader.CDATA);
						break;
					case XMLStreamReader.START_ELEMENT:
						StructDocElementNode node = new StructDocElementNode();
						node.readXml(reader);
						this.m_children.add(node);
						break;
				}
			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Write XML
	 */
	@Override
	public void writeXml(XMLStreamWriter writer) throws XMLStreamException {
		// TODO Auto-generated method stub
		writer.writeStartElement(this.m_namespaceUri, this.m_name);
		
		List<StructDocNode> attrs = new ArrayList<StructDocNode>(),
				nonAttr = new ArrayList<StructDocNode>();
		for(StructDocNode nd : this.m_children)
			if(nd instanceof StructDocAttributeNode)
				attrs.add(nd);
			else
				nonAttr.add(nd);
		for(StructDocNode nd : attrs)
			nd.writeXml(writer);
		for(StructDocNode nd : nonAttr)
			nd.writeXml(writer);
		writer.writeEndElement();
	}

	@Override
	public boolean equals(Object other) {
		// TODO Auto-generated method stub
		Boolean equals = super.equals(other);
		if(equals && other != null && other instanceof StructDocElementNode)
			equals &= ((StructDocElementNode)other).m_namespaceUri.equals(this.m_namespaceUri);
		return equals;
	}

	/**
	 * Represent as a string
	 */
	@Override
	public String toString() {
		StringBuilder retVal = new StringBuilder();
				
		List<StructDocNode> attrs = new ArrayList<StructDocNode>(),
				nonAttr = new ArrayList<StructDocNode>();
		for(StructDocNode nd : this.m_children)
			if(nd instanceof StructDocAttributeNode)
				attrs.add(nd);
			else
				nonAttr.add(nd);
		
		// Attrs
		retVal.append(String.format("<%s xmlns=\"%s\" ", this.m_name, this.m_namespaceUri));
		for(StructDocNode nd : attrs)
		{
			retVal.append(nd.toString());
			retVal.append(" ");
		}
		
		retVal.append(">");
		
		// Sub elements
		for(StructDocNode nd : nonAttr)
			retVal.append(nd.toString());
		
		retVal.append(String.format("</%s>", this.m_name));
		return retVal.toString();
	}

	/**
	 * Represent this as a plain string
	 */
	@Override
	public String toPlainString() {
		StringBuilder retVal = new StringBuilder();
		if(this.m_name.equals("td"))
			retVal.append(" ");
		else if(this.m_name.equals("br") || this.m_name.equals("p") || this.m_name.equals("tr"))
			retVal.append("\r\n");
		else if(this.m_name.equals("li"))
			retVal.append("-");
		// Sub elements
		for(StructDocNode nd : this.m_children)
			if(!(nd instanceof StructDocAttributeNode))
				retVal.append(nd.toPlainString());

		if(this.m_name.equals("td"))
			retVal.append(";");
		else if(this.m_name.equals("li"))
			retVal.append("\r\n");

		return retVal.toString();
	}

	
	

}
