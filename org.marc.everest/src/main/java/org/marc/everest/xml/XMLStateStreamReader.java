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
 * Date: 27-11-2012
 */
package org.marc.everest.xml;

import java.util.Stack;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Represents a XMLStreamReader implementation which can keep track of its own state 
 */
public class XMLStateStreamReader implements XMLStreamReader {

	// Backing field for underlying stream
	private XMLStreamReader m_underlyingStream;
	// Backing field for current path
	private Stack<QName> m_currentPath = new Stack<QName>();
		
	/**
	 * Creates a new instance of the XMLStateStreamWriter
	 * @param underlyingStream
	 */
	public XMLStateStreamReader(XMLStreamReader underlyingStream) {
		m_underlyingStream = underlyingStream;
	}

	/**
	 * Get the current element, null if at the start of the writer
	 */
	public QName getCurrentElement() {
		if(this.m_currentPath.size() == 0)
			return null;
		return this.m_currentPath.peek();
	}
	
	/**
	 * Gets the current path to the element in the Everest ns#localName format
	 */
	public String getCurrentElementPath()
	{
        StringBuilder sb = new StringBuilder();
        QName[] xqa = this.m_currentPath.toArray(new QName[]{});
        for(int i = xqa.length - 1; i >= 0; i--)
            sb.insert(0, String.format("/%s#%s", xqa[i].getNamespaceURI(), xqa[i].getLocalPart()));
        return sb.toString();		
	}
	
	/**
	 * Get an XPath to the current element
	 * @return
	 */
	public String getCurrentElementXPath() 
	{
        StringBuilder sb = new StringBuilder();
        QName[] xqa = this.m_currentPath.toArray(new QName[]{});
        for(int i = xqa.length - 1; i >= 0; i--)
        	sb.insert(0, String.format("/*[namespace-uri() = '%s' and local-name() = '%s']", xqa[i].getNamespaceURI(), xqa[i].getLocalPart()));
        return sb.toString();		
		
	}
	
	/**
	 * Close the underlying reader
	 */
	@Override
	public void close() throws XMLStreamException {
		this.m_underlyingStream.close();
	}

	/**
	 * Get attribute count
	 */
	@Override
	public int getAttributeCount() {
		return this.m_underlyingStream.getAttributeCount();
	}

	/**
	 * Get an attribute local name
	 */
	@Override
	public String getAttributeLocalName(int index) {
		return this.m_underlyingStream.getAttributeLocalName(index);
	}

	/**
	 * GEt an attribute name
	 */
	@Override
	public QName getAttributeName(int index) {
		return this.m_underlyingStream.getAttributeName(index);
	}

	/**
	 * GEt attribute namespace
	 */
	@Override
	public String getAttributeNamespace(int index) {
		return this.m_underlyingStream.getAttributeNamespace(index);
	}

	/**
	 * Get an attribute's prefix
	 */
	@Override
	public String getAttributePrefix(int index) {
		return this.m_underlyingStream.getAttributePrefix(index);
	}

	/**
	 * Get the attribute type
	 */
	@Override
	public String getAttributeType(int index) {
		return this.m_underlyingStream.getAttributeType(index);
	}

	/**
	 * Get attribute value
	 */
	@Override
	public String getAttributeValue(int index) {
		return this.m_underlyingStream.getAttributeValue(index);
	}

	/**
	 * Get attribute value
	 */
	@Override
	public String getAttributeValue(String namespaceURI, String localName) {
		return this.m_underlyingStream.getAttributeValue(namespaceURI, localName);
	}

	/**
	 * Get character encoding scheme
	 */
	@Override
	public String getCharacterEncodingScheme() {
		return this.m_underlyingStream.getCharacterEncodingScheme();
	}

	/**
	 * Get element text name
	 */
	@Override
	public String getElementText() throws XMLStreamException {
		String retVal =  this.m_underlyingStream.getElementText();
		this.updateStack();		
		return retVal;

	}

	/**
	 * Get encoding
	 */
	@Override
	public String getEncoding() {
		return this.m_underlyingStream.getEncoding();
	}

	/**
	 * Get the event type
	 */
	@Override
	public int getEventType() {
		return this.m_underlyingStream.getEventType();
	}

	/**
	 * Get the local name of the current element
	 */
	@Override
	public String getLocalName() {
		
		return this.m_underlyingStream.getLocalName();
	}

	/**
	 * Get the location
	 */
	@Override
	public Location getLocation() {
		return this.m_underlyingStream.getLocation();
	}

	/**
	 * Get the name of the current element
	 */
	@Override
	public QName getName() {
		return this.m_underlyingStream.getName();
	}

	/**
	 * Get the namespace context
	 */
	@Override
	public NamespaceContext getNamespaceContext() {
		return this.m_underlyingStream.getNamespaceContext();
	}

	/**
	 * Get the number of namespaces
	 */
	@Override
	public int getNamespaceCount() {
		return this.m_underlyingStream.getNamespaceCount();
	}

	/**
	 * Get the namespace prefix for the given index
	 */
	@Override
	public String getNamespacePrefix(int index) {
		return this.m_underlyingStream.getNamespacePrefix(index);
	}

	/**
	 * Get the namespace URI of the current element
	 */
	@Override
	public String getNamespaceURI() {
		return this.m_underlyingStream.getNamespaceURI();
	}

	/**
	 * Get the namespace prefix for the given prefix
	 */
	@Override
	public String getNamespaceURI(String prefix) {
		return this.m_underlyingStream.getNamespaceURI(prefix);
	}

	/**
	 * Get the namespace prefix at the specified index
	 */
	@Override
	public String getNamespaceURI(int index) {
		return this.m_underlyingStream.getNamespaceURI(index);
	}

	/**
	 * Get processor data
	 */
	@Override
	public String getPIData() {
		return this.m_underlyingStream.getPIData();
	}

	/**
	 * Get PI target
	 */
	@Override
	public String getPITarget() {
		return this.m_underlyingStream.getPITarget();
	}

	/**
	 * Get the prefix of the current element
	 */
	@Override
	public String getPrefix() {
		return this.m_underlyingStream.getPrefix();
	}

	/**
	 * Get a reader property
	 */
	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return this.m_underlyingStream.getProperty(name);
	}

	/**
	 * Get the inner text of this node
	 */
	@Override
	public String getText() {
		return this.m_underlyingStream.getText();
	}

	/**
	 * Get the text as a series of bytes
	 */
	@Override
	public char[] getTextCharacters() {
		return this.m_underlyingStream.getTextCharacters();
	}

	/**
	 * Get the text characters as a buffer at the specified index with the specified length
	 */
	@Override
	public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
			throws XMLStreamException {
		return this.m_underlyingStream.getTextCharacters(sourceStart, target, targetStart, length);
	}

	/**
	 * Get the length of text in this element
	 */
	@Override
	public int getTextLength() {
		return this.m_underlyingStream.getTextLength();
	}

	/**
	 * Get the start element of the current text reader
	 */
	@Override
	public int getTextStart() {
		return this.m_underlyingStream.getTextStart();
	}

	/**
	 * Get the XML version
	 */
	@Override
	public String getVersion() {
		return this.m_underlyingStream.getVersion();
	}

	/**
	 * True if the current node has a name
	 */
	@Override
	public boolean hasName() {
		return this.m_underlyingStream.hasName();
	}

	/**
	 * True if this current reader can be advanced
	 */
	@Override
	public boolean hasNext() throws XMLStreamException {
		return this.m_underlyingStream.hasNext();
	}

	/**
	 * True if this current node has text
	 */
	@Override
	public boolean hasText() {
		return this.m_underlyingStream.hasText();
	}

	/**
	 * True if the current node has an attribute
	 */
	@Override
	public boolean isAttributeSpecified(int index) {
		return this.m_underlyingStream.isAttributeSpecified(index);
	}

	/**
	 * Returns true if the node is characters
	 */
	@Override
	public boolean isCharacters() {
		return this.m_underlyingStream.isCharacters();
	}

	/**
	 * Returns true if the node is an end element
	 */
	@Override
	public boolean isEndElement() {
		return this.m_underlyingStream.isEndElement();
	}

	/**
	 * True if the node is standalone
	 */
	@Override
	public boolean isStandalone() {
		return this.m_underlyingStream.isStandalone();
	}

	/**
	 * True if this node is a start element
	 */
	@Override
	public boolean isStartElement() {
		return this.m_underlyingStream.isStartElement();
	}

	/**
	 * True if this element is whitespace
	 */
	@Override
	public boolean isWhiteSpace() {
		return this.m_underlyingStream.isWhiteSpace();
	}

	/**
	 * Advance to the next node
	 */
	@Override
	public int next() throws XMLStreamException {
		
		// Advance the reader
		int retVal = this.m_underlyingStream.next();
		while(retVal != XMLStreamReader.START_ELEMENT && retVal != XMLStreamReader.END_ELEMENT && retVal != XMLStreamReader.CDATA && retVal != XMLStreamReader.CHARACTERS)
			retVal = this.m_underlyingStream.next();
		this.updateStack();		
		return retVal;
	}

	/**
	 * Advance to the next tag
	 */
	@Override
	public int nextTag() throws XMLStreamException {
		// Advance the reader
		int retVal = this.m_underlyingStream.nextTag();
		this.updateStack();
		return retVal;
	}

	/**
	 * Test if the current name, type and namespace match the current context
	 */
	@Override
	public void require(int type, String namespaceURI, String localName)
			throws XMLStreamException {
		this.m_underlyingStream.require(type, namespaceURI, localName);
	}

	/**
	 * Standalone set
	 */
	@Override
	public boolean standaloneSet() {
		return this.m_underlyingStream.standaloneSet();
	}

	/**
	 * Update the current element stack
	 */
	private void updateStack()
	{
		// Stack
		if(this.isStartElement())
			this.m_currentPath.push(this.getName());
		if(this.isEndElement())
			this.m_currentPath.pop();
	}
	
	/**
	 * Get current element path
	 */
	@Override
	public String toString() {
		return this.getCurrentElementXPath();
	}
}
