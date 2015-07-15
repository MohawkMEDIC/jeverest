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
 * Date: 23-10-2012
 */
package org.marc.everest.xml;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Represents a writer that can keep track of its current state
 */
public class XMLStateStreamWriter implements XMLStreamWriter {

	// Backing field for underlying stream
	private XMLStreamWriter m_underlyingStream;
	// Backing field for current path
	private Stack<QNameFlushable> m_currentPath = new Stack<QNameFlushable>();
	// Attribute buffer 
	private Queue<IXMLBufferedNode> m_attributeBuffer = new LinkedBlockingQueue<IXMLBufferedNode>();
	
	/**
	 * Creates a new instance of the XMLStateStreamWriter
	 * @param underlyingStream
	 */
	public XMLStateStreamWriter(XMLStreamWriter underlyingStream) {
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
	 * Closes this instance of hte XMLStateStreamWraper
	 */
	public void close() throws XMLStreamException {
		this.m_underlyingStream.close();
	}

	/**
	 * Flushes the underlying stream
	 */
	public void flush() throws XMLStreamException {
		this.m_underlyingStream.flush();
	}

	/**
	 * Get the namespace context
	 */
	public NamespaceContext getNamespaceContext() {
		return this.m_underlyingStream.getNamespaceContext();
	}

	/**
	 * Get the prefix for the specified URI
	 */
	public String getPrefix(String uri) throws XMLStreamException {
		return this.m_underlyingStream.getPrefix(uri);
	}

	/**
	 * Gets the value of the specified property from the underlying stream
	 */
	public Object getProperty(String name) {

		return this.m_underlyingStream.getProperty(name);
	}

	/**
	 * Sets the default namespace of the writer
	 */
	public void setDefaultNamespace(String uri) throws XMLStreamException {
		this.m_underlyingStream.setDefaultNamespace(uri);
	}

	/**
	 * Sets the namespace context
	 */
	public void setNamespaceContext(NamespaceContext context)
			throws XMLStreamException {
		this.m_underlyingStream.setNamespaceContext(context);
	}

	/**
	 * Sets the prefix to the specified uri
	 */
	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		this.m_underlyingStream.setPrefix(prefix, uri);
	}

	/**
	 * Writes an attribute to the underlying XMLWriterStream
	 */
	public void writeAttribute(String localName, String value)
			throws XMLStreamException {
		this.m_attributeBuffer.add(new XmlBufferedAttribute(localName, null, value, null));
	}

	/**
	 * Writers an attribute in the specfied namespace to the underlying stream
	 */
	public void writeAttribute(String namespaceURI, String localName,
			String value) throws XMLStreamException {
		this.m_attributeBuffer.add(new XmlBufferedAttribute(localName, namespaceURI, value, null));
	}

	/**
	 * Writes an attribute with the specified prefix and name to the 
	 * underlying stream
	 */
	public void writeAttribute(String prefix, String namespaceURI,
			String localName, String value) throws XMLStreamException {
		this.m_attributeBuffer.add(new XmlBufferedAttribute(localName, namespaceURI, value, prefix));
	}

	/**
	 * Writes cDATA data to the stream
	 */
	public void writeCData(String data) throws XMLStreamException {
		if(this.m_currentPath.size() > 0) // Flush
			this.flushAttributes(true);
		
		this.m_underlyingStream.writeCData(data);
	}

	/**
	 * Writes raw characters to the underlying stream
	 */
	public void writeCharacters(char[] text, int start, int len)
			throws XMLStreamException {
		if(this.m_currentPath.size() > 0) // Flush
			this.flushAttributes(true);
		
		this.m_underlyingStream.writeCharacters(text, start, len);
	}

	/**
	 * Writes the raw string to the underlying stream
	 */
	public void writeCharacters(String text) throws XMLStreamException {
		if(this.m_currentPath.size() > 0) // Flush
			this.flushAttributes(true);
		
		this.m_underlyingStream.writeCharacters(text);
	}

	/**
	 * Writes a comment to the underlying stream
	 */
	public void writeComment(String data) throws XMLStreamException {
		if(this.m_currentPath.size() > 0) // Flush
			this.flushAttributes(true);
		
		this.m_underlyingStream.writeComment(data);
	}

	/**
	 * Writes the default namespace to the stream 
	 */
	public void writeDefaultNamespace(String namespaceURI)
			throws XMLStreamException {
		this.m_attributeBuffer.add(new XmlBufferedNamespaceDecl(null, namespaceURI));
	}

	/**
	 * Writes the DTD data to the underlying stream
	 */
	public void writeDTD(String dtd) throws XMLStreamException {
		this.m_underlyingStream.writeDTD(dtd);
	}

	/**
	 * Writes an empty element to the underlying stream
	 */
	public void writeEmptyElement(String localName) throws XMLStreamException {
		this.m_underlyingStream.writeEmptyElement(localName);
	}

	/**
	 * Writes an empty element in the specified namespace URI to the stream
	 */
	public void writeEmptyElement(String namespaceURI, String localName)
			throws XMLStreamException {
		this.m_underlyingStream.writeEmptyElement(namespaceURI, localName);
	}

	/**
	 * Writes an empty element with the specified prefix to the stream
	 */
	public void writeEmptyElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		this.m_underlyingStream.writeEmptyElement(prefix, localName, namespaceURI);
	}

	/**
	 * Writes the end of document (closes any start tags)
	 */
	public void writeEndDocument() throws XMLStreamException {
		
		// Clear the stack 
		if(this.m_currentPath.size() > 0)
			while(this.m_currentPath.pop() != null);
		
		this.m_underlyingStream.writeEndDocument();
	}

	/**
	 * Writes an end element to the underlying stream
	 */
	public void writeEndElement() throws XMLStreamException {
		
		// Does this have attributes waiting to be flushed?
		this.flushAttributes(false); // Write out an empty element
		//this.m_underlyingStream.writeEndElement();
		
		// Pop the current element off the element stack
		this.m_currentPath.pop();
	}

	/**
	 * Writes a reference to the specfied entity name
	 */
	public void writeEntityRef(String name) throws XMLStreamException {
		this.m_underlyingStream.writeEntityRef(name);
	}

	/**
	 * Writes the specified namespace to the underlying stream
	 */
	public void writeNamespace(String prefix, String namespaceURI)
			throws XMLStreamException {
		this.m_attributeBuffer.add(new XmlBufferedNamespaceDecl(prefix, namespaceURI));
	}

	/**
	 * Writes a processing instruction to the prolog
	 */
	public void writeProcessingInstruction(String target)
			throws XMLStreamException {
		this.m_underlyingStream.writeProcessingInstruction(target);
	}

	/**
	 * Writes a processing instruction with the specified target and data
	 */
	public void writeProcessingInstruction(String target, String data)
			throws XMLStreamException {
		this.m_underlyingStream.writeProcessingInstruction(target, data);
	}

	/**
	 * Writes the start document element (XML prolog) to the stream
	 */
	public void writeStartDocument() throws XMLStreamException {
		if(this.m_currentPath.size() == 0)
			this.m_underlyingStream.writeStartDocument(); // Write the start document
		
	}

	/**
	 * Writes the start document element to the stream
	 */
	public void writeStartDocument(String version) throws XMLStreamException {
		if(this.m_currentPath.size() == 0)
			this.m_underlyingStream.writeStartDocument(version);
	}

	/**
	 * Writes the start document element to the stream
	 */
	public void writeStartDocument(String encoding, String version)
			throws XMLStreamException {
		if(this.m_currentPath.size() == 0)
			this.m_underlyingStream.writeStartDocument(encoding, version);
	}

	/**
	 * Writes a start element to the stream
	 */
	public void writeStartElement(String localName) throws XMLStreamException {
		
			// Check the attribute stack, is there any attributes in it?
		//if(this.m_attributeBuffer.size() > 0)
		if(this.m_currentPath.size() > 0) // Flush
			this.flushAttributes(true);
		
		//this.m_underlyingStream.writeStartElement(localName);
		
		// Lookup the QName and construct
		this.m_currentPath.push(new QNameFlushable(this.getNamespaceContext().getNamespaceURI(""), localName));
	}
	
	/**
	 * Write a start element in the specified namespace URI
	 */
	public void writeStartElement(String namespaceURI, String localName)
			throws XMLStreamException {
		
		// Check the attribute stack, is there any attributes in it?
		if(this.m_currentPath.size() > 0) // Flush
			this.flushAttributes(true);

//		this.m_underlyingStream.writeStartElement(namespaceURI, localName);
		
		// add to path
		this.m_currentPath.push(new QNameFlushable(namespaceURI, localName));
	}

	/**
	 * Write a start element with specified namespace uri, prefix and local name
	 */
	public void writeStartElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
//		this.m_underlyingStream.writeStartElement(prefix, localName, namespaceURI);
		
		// Check the attribute stack, is there any attributes in it?
		//if(this.m_attributeBuffer.size() > 0)
		if(this.m_currentPath.size() > 0) // Flush
			this.flushAttributes(true);

		// add to path
		if(prefix != null)
			this.m_currentPath.push(new QNameFlushable(namespaceURI, localName, prefix));
		else
			this.m_currentPath.push(new QNameFlushable(namespaceURI, localName));
	}

	/**
	 * Flush attributes
	 */
	private void flushAttributes(boolean isStartElement) throws XMLStreamException
	{
		
		QNameFlushable elementName = this.m_currentPath.peek();

		if(isStartElement && elementName.wasFlushed()) return;

		
		// Write start element or empty element based on content
		if(isStartElement)
		{
			// Peek up
			elementName.setHasInnerElements();
			if(elementName.getNamespaceURI() == "" || elementName.getNamespaceURI() == null)
				m_underlyingStream.writeStartElement(elementName.getLocalPart());
			else
			{
				if(this.getPrefix(elementName.getNamespaceURI()) == null)
					m_underlyingStream.writeStartElement(elementName.getLocalPart());
				else
				{
					String pfx = this.getNamespaceContext().getPrefix(elementName.getNamespaceURI());
					if(pfx == null || pfx.isEmpty())
						m_underlyingStream.writeStartElement(elementName.getLocalPart());
					else
						m_underlyingStream.writeStartElement(pfx, elementName.getLocalPart(),  elementName.getNamespaceURI());
				}
			}
		}
		else
		{
			if(elementName.hasInnerElements())
				m_underlyingStream.writeEndElement();
			else
			{
				if(elementName.getNamespaceURI() == "" || elementName.getNamespaceURI() == null)
					m_underlyingStream.writeEmptyElement(elementName.getLocalPart());
				else
				{
					String pfx = this.getNamespaceContext().getPrefix(elementName.getNamespaceURI());
					if(pfx == null || pfx.isEmpty())
						m_underlyingStream.writeEmptyElement(elementName.getLocalPart());
					else
						m_underlyingStream.writeEmptyElement(pfx, elementName.getLocalPart(), elementName.getNamespaceURI());
				}
			}
		}	
		
		elementName.setFlushed();
		
		// Flush the attributes
		while(this.m_attributeBuffer.size() > 0)
		{
			IXMLBufferedNode buf = this.m_attributeBuffer.poll();
			
			if(buf instanceof XmlBufferedAttribute)
			{
				XmlBufferedAttribute bufAtt = (XmlBufferedAttribute)buf;
				if((bufAtt.getNodePrefix() == null || bufAtt.getNodePrefix() == "") &&
						(bufAtt.getNodeNamespaceURI() == null || bufAtt.getNodeNamespaceURI() == ""))
					this.m_underlyingStream.writeAttribute(bufAtt.getNodeName(), bufAtt.getNodeValue());
				else if(bufAtt.getNodePrefix() == null || bufAtt.getNodePrefix() == "")
					this.m_underlyingStream.writeAttribute(bufAtt.getNodeNamespaceURI(), bufAtt.getNodeName(), bufAtt.getNodeValue());
				else
					this.m_underlyingStream.writeAttribute(bufAtt.getNodePrefix(), bufAtt.getNodeNamespaceURI(), bufAtt.getNodeName(), bufAtt.getNodeValue());
			}
			else if(buf instanceof XmlBufferedNamespaceDecl)
			{
				if(buf.getNodeName() == null)
					this.m_underlyingStream.writeDefaultNamespace(buf.getNodeValue());
				else
					this.m_underlyingStream.writeNamespace(buf.getNodeName(), buf.getNodeValue());
			}
		}
	}

	/**
	 * Get current element path
	 */
	@Override
	public String toString() {
		return this.getCurrentElementXPath();
	}
	
	
}
