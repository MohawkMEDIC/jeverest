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
 * Date: 07-08-2014 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.doc.StructDocAttributeNode;
import org.marc.everest.datatypes.doc.StructDocElementNode;
import org.marc.everest.datatypes.doc.StructDocNode;
import org.marc.everest.datatypes.SD;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.IDatatypeFormatter;

/**
 * A formatter implementing necessary routines to parse / graph an SD
 * @author Justin
 *
 */
public class SDFormatter extends ANYFormatter implements IDatatypeFormatter {

	/**
	 * Graph this instance
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {

		SD instance_sd = (SD)o;
		super.graph(s, o, context, result);
		if(o == null || instance_sd.isNull())
			return;
		
		try {
		
			// Attributes
			if(instance_sd.getMediaType() != null)
					s.writeAttribute("mediaType", instance_sd.getMediaType());
			if(instance_sd.getLanguage() != null)
				s.writeAttribute("language", instance_sd.getLanguage());
			if(instance_sd.getStyleCode() != null)
				s.writeAttribute("styleCode", instance_sd.getStyleCode());
			if(instance_sd.getId() != null)
				s.writeAttribute("ID", instance_sd.getId());
			
			if(instance_sd.getContent() != null)
				for(StructDocNode nd : instance_sd.getContent())
					nd.writeXml(s);
		} catch (XMLStreamException e) {
			throw new FormatterException("Could not graph SD instance", e);
		}
		
	}

	/**
	 * Parse an instance
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		String pathName = s.toString(); 
		
		SD retVal = super.parse(s, context, result, SD.class);
		
		if(s.getAttributeValue(null, "styleCode") != null)
			retVal.setStyleCode(s.getAttributeValue(null, "styleCode"));
		if(s.getAttributeValue(null, "mediaType") != null)
			retVal.setMediaType(s.getAttributeValue(null, "mediaType"));
		if(s.getAttributeValue(null, "language") != null)
			retVal.setLanguage(s.getAttributeValue(null, "language"));
		if(s.getAttributeValue(null, "ID") != null)
			retVal.setId(s.getAttributeValue(null, "ID"));
		
		if(!s.isEndElement())
		{
			StructDocElementNode tNode = new StructDocElementNode();
			tNode.readXml(s);
			for(StructDocNode cNode : tNode.getChildren())
				if(!(cNode instanceof StructDocAttributeNode))
					retVal.getContent().add(cNode);
		}
		
		// Validate
		super.validate(retVal, pathName, result);
		return retVal;
	}

	/**
	 * Get the type this formatter handles
	 */
	@Override
	public String getHandlesType() {
		return "SD";
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("content");
		retVal.add("mediaType");
		retVal.add("language");
		retVal.add("styleCode");
		retVal.add("ID");
		return retVal;
	}

}
