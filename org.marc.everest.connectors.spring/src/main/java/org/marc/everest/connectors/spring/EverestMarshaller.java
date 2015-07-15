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
 * Date: 12-14-2012
 */
package org.marc.everest.connectors.spring;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.marc.everest.annotations.Structure;
import org.marc.everest.exceptions.ConnectorException;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;
import org.marc.everest.interfaces.IGraphable;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Represents a OXM marshaller that can be used with the Spring Framework
 */
public class EverestMarshaller implements Marshaller {

	// The structure formatter
	private IXmlStructureFormatter m_formatter;

	// Graph results
	private HashMap<Object,IFormatterGraphResult> m_graphResults = new HashMap<Object, IFormatterGraphResult>(); 
	
	/**
	 * Get a graph result
	 */
	public synchronized IFormatterGraphResult popGraphResult(Object context)
	{
		IFormatterGraphResult res = this.m_graphResults.get(context);
		if(res != null)
			this.m_graphResults.remove(context);
		return res;
	}
	
	/**
	 * Creates a new instance of the Marshaller with the specified formatter
	 */
	public EverestMarshaller(IXmlStructureFormatter formatter)
	{
		this.m_formatter = formatter;
	}
	
	/**
	 * Marhsal the object to the result
	 */
	@Override
	public void marshal(Object obj, Result res) throws IOException, XmlMappingException {
		
		// Marshal the object
		if(obj instanceof IGraphable)
		{
			if(res instanceof StreamResult)
				this.m_graphResults.put(obj, this.m_formatter.graph(((StreamResult)res).getOutputStream(), (IGraphable)obj));
			else // Not a stream result we need to transform what Everest can handle (a stream) to SAX/DOM
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				this.m_graphResults.put(obj, this.m_formatter.graph(bos, (IGraphable)obj));
				StreamSource streamSrc = new StreamSource(new ByteArrayInputStream(bos.toByteArray()));
				try {
					TransformerFactory.newInstance().newTransformer().transform(streamSrc, res);
				} catch(Exception e)
				{
					throw new ConnectorException("Couldn't marshal object to specified result");
				}
				
			}
			
			
		}
		else
			throw new ConnectorException("Can't serialize object as it does not implement IGraphable");
	}

	/**
	 * Return true if the marshaller supports the class
	 */
	@Override
	public boolean supports(Class<?> t) {
		
		// Must be IGraphable and must have structure annotation
		if(!FormatterUtil.hasInterface(t, IGraphable.class))
			return false;
		
		Structure struct = t.getAnnotation(Structure.class);
		return this.m_formatter.getHandledStructures().contains("*") ||
				this.m_formatter.getHandledStructures().contains(struct.name());
		
	}

}
