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
 * Buffered namespace decl
 * @author fyfej
 *
 */
class XmlBufferedNamespaceDecl implements IXMLBufferedNode {

	// Namespace
	private String m_nodeName;
	// Uri
	private String m_nodeValue;
	
	/**
	 * Creates buffered namespace declaration 
	 * @param namespacePrefix
	 * @param namespaceUri
	 */
	public XmlBufferedNamespaceDecl(String namespacePrefix, String namespaceUri) {
		super();
		this.m_nodeName = namespacePrefix;
		this.m_nodeValue = namespaceUri;
	}

	/**
	 * Gets the node name
	 */
	@Override
	public String getNodeName() {
		return this.m_nodeName;
	}

	/**
	 * Gets the node value
	 */
	@Override
	public String getNodeValue() {
		return this.m_nodeValue;
	}

	

}
