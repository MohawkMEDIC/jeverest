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
 * Represents an abstract node found within a structured document
 */
public abstract class StructDocNode {

	// Child nodes
	protected List<StructDocNode> m_children;
	// Name
	protected String m_name;
	// Value
	protected String m_value;
	
	/**
	 * Structured document node
	 */
	public StructDocNode() 
	{
		this.m_children = new ArrayList<StructDocNode>();
	}
	
	/**
	 * Creates a new structured document node with the specified name and value
	 */
	public StructDocNode(String name, String value) {
		this();
		this.m_name = name;
		this.m_value = value;
	}

	/**
	 * Gets the local name of the node
	 */
	public String getName() {
		return this.m_name;
	}

	/**
	 * Set the name of the node
	 */
	public void setName(String value) {
		this.m_name = value;
	}

	/**
	 * Get the value of the node
	 */
	public String getValue() {
		return this.m_value;
	}

	/**
	 * Set the value of the node
	 */
	public void setValue(String value) {
		this.m_value = value;
	}

	/**
	 * Get children nodes
	 * @return
	 */
	public List<StructDocNode> getChildren() {
		return this.m_children;
	}
	
	/**
	 * Read XML from the specified stream
	 */
	public abstract void readXml(XMLStreamReader reader);

	/**
	 * Write XML to the specified stream
	 */
	public abstract void writeXml(XMLStreamWriter writer) throws XMLStreamException;

	/**
	 * Equals method
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null)
            return false;

        Boolean equals = other.getClass().equals(this.getClass());
        if(!equals) return false;
        
        StructDocNode otherNode = (StructDocNode)other;
        equals &= otherNode.m_name.equals(this.m_name);
        equals &= otherNode.m_value.equals(this.m_value);
        if (this.m_children != null && otherNode.m_children != null &&
            this.m_children.size() == otherNode.m_children.size())
        {
        	
        	// For elements and text
        	List<StructDocNode> thisNonAttr = new ArrayList<StructDocNode>(),
        			otherNonAttr = new ArrayList<StructDocNode>();
        	
        	// Attributes in any order
            for(StructDocNode child : this.m_children)
            	if(child instanceof StructDocAttributeNode)
            		equals &= otherNode.m_children.contains(child);
            	else
            		thisNonAttr.add(child);
            for(StructDocNode child : otherNode.m_children)
            	if(!(child instanceof StructDocAttributeNode))
            		otherNonAttr.add(child);
            // Elements and text in specific order 
            for (int i = 0; i < thisNonAttr.size(); i++)
                equals &= (thisNonAttr.get(i) == null && otherNonAttr.get(i) == null) ^ (thisNonAttr.get(i).equals(otherNonAttr.get(i)));
            
        }
        else
            equals = false;
        return equals;
	}

	/**
	 * Find a child node based on its name
	 */
	public <T> T findChild(String name,
			Class<? extends StructDocNode> nodeType) {
		for(StructDocNode nd : this.m_children)
			if(name.equals(nd.getName()) && nodeType.equals(nd.getClass()))
				return (T) nd;
		return null;
	}

	/**
	 * Find node by ID
	 */
	public StructDocNode findNodeById(String id) {
		StructDocAttributeNode idAttribute = this.findChild("ID", StructDocAttributeNode.class);
		if(idAttribute != null && idAttribute.getValue().equals(id)) 
			return this;
		for(StructDocNode nd : this.m_children)
		{
			StructDocNode res = nd.findNodeById(id);
			if(res != null) 
				return res;
		}
		return null;
	}

	/**
	 * Represent this as a plain string
	 * @return
	 */
	public abstract String toPlainString();
	
	
}
