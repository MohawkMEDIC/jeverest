/* 
 * Copyright 2013 Mohawk College of Applied Arts and Technology
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
 * Date: 05-15-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.PQ;
import org.marc.everest.datatypes.SetOperator;
import org.marc.everest.datatypes.generic.IVL;
import org.marc.everest.datatypes.generic.SXCM;
import org.marc.everest.datatypes.generic.SXPR;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.datatypes.interfaces.ISetComponent;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Formats an instance of SXPR onto the wire
 * @author fyfej
 *
 */
public class SXPRFormatter extends ANYFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {

		// Format base
		super.graph(s, o, context, result);
		
		SXPR<? extends IAny> instance = (SXPR<? extends IAny>)o;
		// Null?
		if(instance.isNull()) return; // no need to graph a null instance
		
		try
		{
			if(instance.getOperator() != null)
				s.writeAttribute("operator", FormatterUtil.toWireFormat(instance.getOperator()));
			
			// Elements
			int iCount = 0;
			for(ISetComponent<? extends IAny> component : instance)
			{
				s.writeStartElement(DatatypeFormatter.NS_HL7, "comp");

				// Always emit the XSI:TYPE
				s.writeAttribute(DatatypeFormatter.NS_XSI, "type", FormatterUtil.createXsiTypeName(component));
				
				// First element should have no operator
				if(iCount == 0)
					if(component instanceof SXCM<?>)
					{
						SXCM<?> operatorNode = (SXCM<?>)component;
						if(operatorNode.getOperator() != null)
							result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "Operator won't be represented in the first object in SXPR", s.toString(), null));
						operatorNode.setOperator(null);
					}
				
				IFormatterGraphResult hostResult = this.getHost().graph(s, component);
				result.addResultDetail(hostResult.getDetails());
				
				s.writeEndElement();
				iCount++;
			}
		}
		catch(Exception e)
		{
			throw new FormatterException("Unable to format SXPR instance", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		SXPR<?> retVal = super.parse(s, context, result, SXPR.class);
		
		if(retVal.isNull())
			return retVal;
		else
		{
			if(s.getAttributeValue(null, "operator") != null)
				retVal.setOperator(FormatterUtil.fromWireFormat(s.getAttributeValue(null, "operator"), SetOperator.class));
			
			// Content / Elements
			if(!s.isEndElement())
			{
				try
				{
					int sDepth = 0;
					String sName = s.getLocalName();
					DatatypeFormatter.nextElementEvent(s);
					while(!(s.getEventType() == XMLStreamReader.END_ELEMENT && sDepth == 0 && s.getLocalName().equals(sName)))
					{

						String oldName = s.getLocalName();
						try
						{
							
							if(s.getEventType() == XMLStreamReader.END_ELEMENT) continue;

							// Component
							if(s.getLocalName().equals("comp"))
							{
								IFormatterParseResult hostResult = this.getHost().parse(s, context.findChildContextFromName("comp", PropertyType.NONSTRUCTURAL));
								result.addResultDetail(hostResult.getDetails());
								retVal.add((SXCM)hostResult.getStructure());
							}
							else
								result.addResultDetail(new NotImplementedElementResultDetail(ResultDetailType.WARNING, s.getLocalName(), s.getNamespaceURI(), s.toString(), null));
						}
						catch(MessageValidationException e)
						{
							result.addResultDetail(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), s.toString(), e));
						}
						/**/
						finally
						{
							if(oldName.equals(s.getLocalName())) DatatypeFormatter.nextElementEvent(s);
						}
					}	
				}
				catch(XMLStreamException e)
				{
					throw new FormatterException("Could not parse SXPR type", e);
				}
			}// if
			
			super.validate(retVal, s.toString(), result);
			return retVal;
		}
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return super.getHandlesType();
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		return super.getSupportedProperties();
	}

	
}
