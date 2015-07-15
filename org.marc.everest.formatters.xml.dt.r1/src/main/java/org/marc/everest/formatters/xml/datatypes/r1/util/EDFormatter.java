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
 * Date: 01-04-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.ED;
import org.marc.everest.datatypes.EncapsulatedDataCompression;
import org.marc.everest.datatypes.EncapsulatedDataIntegrityAlgorithm;
import org.marc.everest.datatypes.EncapsulatedDataRepresentation;
import org.marc.everest.datatypes.TEL;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.ResultDetail;
import org.marc.everest.xml.XMLStateStreamWriter;

/**
 * Represents a formatter that can parse and graph ED instances in R1 format
 * @author fyfej
 *
 */
public class EDFormatter extends ANYFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {

		if(o == null) return;

		// Graph ANY attributes
		super.graph(s, o, context, result);
		
		// get instance
		ED instance = (ED)o;
		if(instance.isNull())
			return;
		
		try {
			// Attributes
			if(instance.getRepresentation() != null)
				s.writeAttribute("representation", instance.getRepresentation().getCode());
			if(instance.getMediaType() != null)
				s.writeAttribute("mediaType", instance.getMediaType());
			if(instance.getLanguage() != null)
				s.writeAttribute("language", instance.getLanguage());
			if(instance.getCompression() != null)
				s.writeAttribute("compression", instance.getCompression().getCode());
			if(instance.getIntegrityCheck() != null)
				s.writeAttribute("integrityCheck", DatatypeConverter.printBase64Binary(instance.getIntegrityCheck()));
			if(instance.getDescription() != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ED", "description", s.toString()));
			if(instance.getIntegrityCheckAlgorithm() != null)
				s.writeAttribute("integrityCheckAlgorithm", instance.getIntegrityCheckAlgorithm().getCode());
			
			// Elements
			if(instance.getReference() != null)
			{
				s.writeStartElement(DatatypeFormatter.NS_HL7, "reference");
				result.addResultDetail(this.getHost().graph(s,  instance.getReference(), context.findChildContextFromName("reference", PropertyType.NONSTRUCTURAL, ED.class)).getDetails());
				s.writeEndElement();
			}
			if(instance.getThumbnail() != null)
			{
				s.writeStartElement(DatatypeFormatter.NS_HL7, "thumbnail");
				result.addResultDetail(this.getHost().graph(s,  instance.getThumbnail(), context.findChildContextFromName("thumbnail", PropertyType.NONSTRUCTURAL, ED.class)).getDetails());
				s.writeEndElement();
			}
			if(instance.getTranslation() != null)
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "ED", "translation", s.toString()));
			
			// Encoding 
			if(instance.getData() != null && instance.getData().length > 0)
			{
				if(instance.getRepresentation() == EncapsulatedDataRepresentation.Base64 || instance.getCompression() != null)
					s.writeCharacters(DatatypeConverter.printBase64Binary(instance.getData()));
				else if(instance.getRepresentation() == EncapsulatedDataRepresentation.Text)
					s.writeCharacters(new String(instance.getData(), Charset.forName("UTF8")));
				else 
					try
					{
						FormatterUtil.writeRawXmlOutput(instance.getData(), s);
					}
					catch(FormatterException ex)
					{
						result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "Couldn't write XML data, encoded as text", s.toString(), ex));
						s.writeCharacters(new String(instance.getData(), Charset.forName("UTF8")));
					}
			}
		} catch (XMLStreamException e) {
			throw new FormatterException("Can't graph ED instance");
		}
		
	}

	

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {

		// Parse into retval
		ED retVal = super.parse(s, context, result, ED.class);
		
		// Parse data
		// Now parse our data out... Attributes
        if (s.getAttributeValue(null, "representation") != null)
            retVal.setRepresentation(FormatterUtil.fromWireFormat(s.getAttributeValue(null,"representation"), EncapsulatedDataRepresentation.class));
        if (s.getAttributeValue(null,"mediaType") != null)
            retVal.setMediaType(s.getAttributeValue(null,"mediaType"));
        if (s.getAttributeValue(null,"language") != null)
            retVal.setLanguage(s.getAttributeValue(null,"language"));
        if (s.getAttributeValue(null,"compression") != null)
            retVal.setCompression(FormatterUtil.fromWireFormat(s.getAttributeValue(null,"compression"), EncapsulatedDataCompression.class));
        if (s.getAttributeValue(null,"integrityCheckAlgorithm") != null)
        	retVal.setIntegrityCheckAlgorithm(FormatterUtil.fromWireFormat(s.getAttributeValue(null, "integrityCheckAlgorithm"), EncapsulatedDataIntegrityAlgorithm.class));
        if (s.getAttributeValue(null,"integrityCheck") != null)
            retVal.setIntegrityCheck(DatatypeConverter.parseBase64Binary(s.getAttributeValue(null,"integrityCheck")));
        // Process elements
        StringBuilder innerContent = new StringBuilder();
		if(!s.isEndElement())
		{
			try
			{
				int sDepth = 0;
				String sName = s.getLocalName();
				s.next();
				while(!(s.getEventType() == XMLStreamReader.END_ELEMENT && sDepth == 0 && s.getLocalName().equals(sName)))
				{
					if(s.getEventType() == XMLStreamReader.CHARACTERS || s.getEventType() == XMLStreamReader.CDATA)
					{
						if(s.getEventType() == XMLStreamReader.CDATA || !s.getText().trim().isEmpty())
							innerContent.append(s.getText());
						s.next();
					}
					else
					{
						String oldName = s.getLocalName();
						try
						{
							if(s.getEventType() == XMLStreamReader.END_ELEMENT) continue;

	
							if(s.getLocalName().equals("thumbnail"))
								retVal.setThumbnail(super.parseElement("thumbnail", s, context, result, ED.class));
							else if(s.getLocalName().equals("reference"))
								retVal.setReference(super.parseElement("reference", s, context, result, TEL.class));
							else if(!(s.getEventType() == XMLStreamReader.END_ELEMENT && sDepth == 0 && s.getLocalName() != sName) &&
									s.getEventType() == XMLStreamReader.START_ELEMENT)
							{
								retVal.setRepresentation(EncapsulatedDataRepresentation.Xml);
								innerContent.append(FormatterUtil.readRawXmlInput(s));
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
							if(oldName.equals(s.getLocalName())) s.next();
						}
					}
				}	
			}
			catch(XMLStreamException e)
			{
				throw new FormatterException("Could not parse ED type", e);
			}
		}
		
		// Set inner content?
		if(innerContent.length() > 0)
		{
			if(retVal.getRepresentation() == EncapsulatedDataRepresentation.Base64)
				retVal.setData(DatatypeConverter.parseBase64Binary(innerContent.toString()));
			else
				retVal.setData(innerContent.toString());
		}
		
		// Validate integrity check
		try {
			if(!retVal.validateIntegrityCheck())
			{
				if(retVal.getCompression() == null || !retVal.unCompress().validateIntegrityCheck())
					result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "ED failed integrity check", s.toString(), null));
			}
		} catch (Exception e) {
			result.addResultDetail(new ResultDetail(ResultDetailType.ERROR, "Could not perform integrity check on ED", s.toString(), e));
		}
		
		// Validate
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	
	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "ED";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.addAll(Arrays.asList(
				new String[] {
						"representation",
						"mediaType",
						"compression",
						"language",
						"integrityCheck",
						"integrityCheckAlgorithm",
						"reference",
						"thumbnail",
						"data"
				}
				));
		return retVal;
	}



}
