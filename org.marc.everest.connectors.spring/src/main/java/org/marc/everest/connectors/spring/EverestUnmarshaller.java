/**
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
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.marc.everest.annotations.Structure;
import org.marc.everest.exceptions.ConnectorException;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;
import org.marc.everest.interfaces.IGraphable;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

/**
 * A spring Unmarshaller that leverages the Everest Framework
 */
public class EverestUnmarshaller implements Unmarshaller {

	// The formatter to be used
	private IXmlStructureFormatter m_formatter;
	
	// Graph results
	private HashMap<Object,IFormatterParseResult> m_parseResults = new HashMap<Object, IFormatterParseResult>(); 
	
	/**
	 * Get a graph result
	 */
	public synchronized IFormatterParseResult popParseResult(Object context)
	{
		IFormatterParseResult res = this.m_parseResults.get(context);
		if(res != null)
			this.m_parseResults.remove(context);
		return res;
	}
	
	/**
	 * Creates a new instance of the Everest unmarshaller
	 * @param fmtr
	 */
	public EverestUnmarshaller(IXmlStructureFormatter fmtr)
	{
		this.m_formatter = fmtr;
	}
	
	/**
	 * Determine if the specified class is supported
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

	/**
	 * Unmarshal the object from the source
	 */
	@Override
	public Object unmarshal(Source src) throws IOException, XmlMappingException {
		InputStream ins = null;
		if(src instanceof StreamSource)
			ins = ((StreamSource)src).getInputStream();
		else 
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Result outputTarget = new StreamResult(outputStream);
			try {
				TransformerFactory.newInstance().newTransformer().transform(src, outputTarget);
			} catch(Exception e)
			{
				throw new ConnectorException("Couldn't transform source to input stream for formatter");
			}
			ins = new ByteArrayInputStream(outputStream.toByteArray());
		}
		
		// HACK: I know this looks odd, why don't I just return the IFormatterParseResult right?
		// Well, I want to keep this consistent with the Marshaller, also the IFormatterParseResult isn't
		// really what is intended to be passed back.
		IFormatterParseResult retVal = this.m_formatter.parse(ins);
		this.m_parseResults.put(retVal.getStructure(), retVal);
		return retVal.getStructure();
	}

}
