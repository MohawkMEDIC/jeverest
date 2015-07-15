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
 * Date: 02-11-2012
 */
package org.marc.everest.xml;

/**
 * Buffered node to workaround the ugly Java XmlStreamWriter output 
 */
class XmlBufferedAttribute implements IXMLBufferedNode {

	// Name
	private String m_nodeName;
	// Namespace
	private String m_nodeNamespaceURI;
	// Value
	private String m_nodeValue;
	// Prefix
	private String m_nodePrefix;
	
	/**
	 * Creates a new instance of the buffered attribute
	 */
	public XmlBufferedAttribute(String nodeName, String nodeNamespaceURI,
			String nodeValue, String nodePrefix) {
		super();
		this.m_nodeName = nodeName;
		this.m_nodeNamespaceURI = nodeNamespaceURI;
		this.m_nodeValue = nodeValue;
		this.m_nodePrefix = nodePrefix;
	}

	/**
	 * Gets the name of the node
	 */
	public String getNodeName() {
		return m_nodeName;
	}

	/**
	 * Gets the namespace uri of the attribute
	 */
	public String getNodeNamespaceURI() {
		return m_nodeNamespaceURI;
	}

	/**
	 * Get the value of the attribute
	 */
	public String getNodeValue() {
		return m_nodeValue;
	}

	/**
	 * Get the prefix of the attribute
	 */
	public String getNodePrefix() {
		return m_nodePrefix;
	}

	
	

}
