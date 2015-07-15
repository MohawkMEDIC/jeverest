/* 
 * Copyright 2012 Mohawk College of Applied Arts and Technology
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
 * Date: 12-20-2012 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.generic.COLL;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.IDatatypeFormatter;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.ResultDetail;
import org.marc.everest.xml.XMLStateStreamWriter;

/**
 * Represents a formatter that can format any COLL instance
 */
public class COLLFormatter implements IDatatypeFormatter {

	// The host formatter
	private IXmlStructureFormatter m_hostFormatter;
	
	/**
	 * Graph the collection of objects
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {

		QName currentElementName = null;
		
		// Is there an element in the stack that we can read?
		if(s instanceof XMLStateStreamWriter)
			currentElementName = ((XMLStateStreamWriter)s).getCurrentElement();
		else
		{
			result.addResultDetail(new ResultDetail(ResultDetailType.ERROR, "Can't represent collections unless formatting to XMLStateStreamWriter", s.toString(), null));
			return;
		}
		

		Iterable<?> instance = (Iterable<?>)o;
		int count = 0;
		Iterator<?> iterator = instance.iterator();
		Class<?> genericParameter = null;
		// Generic parameter ... hmm.. this is interesting in Java
		// What we're going to do is get the purported generic type from the getter
		if(context.getGetterMethod() != null && context.getGetterMethod().getGenericReturnType() instanceof ParameterizedType)
		{
			ParameterizedType genReturnType = (ParameterizedType)context.getGetterMethod().getGenericReturnType();
			genericParameter = FormatterUtil.getClassForType(genReturnType.getActualTypeArguments()[0], context);
		}
		else // Can't determine, so... we'll use IGraphable which will force all items to output XSI Type
			genericParameter = IGraphable.class;
		
		// Iterate through elements
		while(iterator.hasNext())
		{
			try
			{
				if(count != 0)
					s.writeStartElement(currentElementName.getNamespaceURI(), currentElementName.getLocalPart());
				
				// Output XSI type if needed (when the item does not match the collection type
				Object current = iterator.next();
				if(!genericParameter.equals(current.getClass()))
				{
					String xsiTypeName = FormatterUtil.createXsiTypeName(current);
					s.writeAttribute(DatatypeFormatter.NS_XSI, "type", xsiTypeName);
				}
				
				// Format
				FormatterElementContext fakeItemContext = new FormatterElementContext(current, null, context);
				
				IFormatterGraphResult hostResult = this.getHost().graph(s, (IGraphable)current, fakeItemContext);
				result.setCode(hostResult.getCode());
				result.addResultDetail(hostResult.getDetails());
				
				if(!iterator.hasNext()) break; // Don't write end element for the last item as this is done by the host
				s.writeEndElement();
			}
			catch(XMLStreamException e)
			{
				throw new FormatterException(e.getMessage(), e);
			}
			finally
			{
				count++;
			}
		}
	}

	/**
	 * Parse an object from the stream
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		// COLL is an abstract type so parse as a set
		return this.parse(s, context, result, SET.class);
	}

	/**
	 * Construct the type
	 */
	protected <T extends COLL> T parse(XMLStreamReader s, FormatterElementContext context, DatatypeFormatterParseResult result, Class<T> instanceType)
	{
		
		try
		{
			T retVal = instanceType.newInstance();
			QName currentElementName = s.getName();
			
			while(s.getLocalName().equals(currentElementName.getLocalPart()) && s.hasNext())
			{
				if(s.getEventType() == XMLStreamReader.START_ELEMENT)
				{
					// Create a fake context
					Type typeParse = IGraphable.class;
					if(context.getOwnerType() instanceof ParameterizedType)
					{
						ParameterizedType pt = (ParameterizedType)context.getOwnerType();
						typeParse = pt.getActualTypeArguments()[0];
					}
					FormatterElementContext fakeElementContext = new FormatterElementContext(typeParse, null);
					fakeElementContext.setParentContext(context);
					
					IFormatterParseResult hostResult = this.getHost().parse(s, fakeElementContext);
					result.setCode(hostResult.getCode());
					result.addResultDetail(hostResult.getDetails());
					retVal.add(hostResult.getStructure());
				}
				
				// Read until the next element if the next element doesn't match then break out and do not continue
		
				// First, have we hit an end element that matches the name?
				if(s.getEventType() == XMLStreamReader.END_ELEMENT &&
						!s.getLocalName().equals(currentElementName.getLocalPart()))
						return retVal;
				DatatypeFormatter.nextElementEvent(s);
				while(s.getEventType() != XMLStreamReader.START_ELEMENT && s.hasNext())
				{
					if (s.getEventType() == XMLStreamReader.END_ELEMENT &&
                    	!s.getLocalName().equals(currentElementName.getLocalPart()))
                    	break;
					DatatypeFormatter.nextElementEvent(s);
				}
			}
			
			return retVal;
		}
		catch(Exception e)
		{
			throw new FormatterException("Couldn't instantiate type", e);
		}
	}
	
	/**
	 * Get the type that this item handles
	 */
	@Override
	public String getHandlesType() {
		return "COLL";
	}

	/**
	 * Gets the host formatter
	 */
	@Override
	public IXmlStructureFormatter getHost() {
		return this.m_hostFormatter;
	}

	/**
	 * Sets the host formatter
	 */
	@Override
	public void setHost(IXmlStructureFormatter host) {
		this.m_hostFormatter = host;
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		return Arrays.asList(new String[] { "item" });
	}

}
