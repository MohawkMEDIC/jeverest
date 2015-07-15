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
 * Date: 12-20-2012
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.AD;
import org.marc.everest.datatypes.ADXP;
import org.marc.everest.datatypes.AddressPartType;
import org.marc.everest.datatypes.GTS;
import org.marc.everest.datatypes.PostalAddressUse;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Represents a formatter helper that is capable of formatting and parsing an 
 * R1 AD datatype object
 */
public class ADFormatter extends ANYFormatter {

	// Mappings to / from R1 element names to part types
	private Map<AddressPartType, String> s_mappings = new HashMap<AddressPartType, String>();
	private Map<String, AddressPartType> s_reverseMapping = new HashMap<String, AddressPartType>();
	
	/**
	 * Address formatter ctor
	 */
	public ADFormatter()
	{
		// Add mappings
		if(s_mappings.size() == 0)
		{
	       s_mappings.put(AddressPartType.AdditionalLocator, "additionalLocator");
	       s_mappings.put(AddressPartType.AddressLine, "streetAddressLine");
	       s_mappings.put(AddressPartType.BuildingNumber, "houseNumber");
	       s_mappings.put(AddressPartType.Delimiter, "delimeter");
	       s_mappings.put(AddressPartType.Country, "country");
	       s_mappings.put(AddressPartType.State, "state");
	       s_mappings.put(AddressPartType.County, "county");
	       s_mappings.put(AddressPartType.City, "city");
	       s_mappings.put(AddressPartType.PostalCode, "postalCode");
	       s_mappings.put(AddressPartType.StreetAddressLine, "streetAddressLine");
	       s_mappings.put(AddressPartType.BuildingNumberNumeric, "houseNumberNumeric");
	       s_mappings.put(AddressPartType.Direction, "direction");
	       s_mappings.put(AddressPartType.StreetName, "streetName");
	       s_mappings.put(AddressPartType.StreetNameBase, "streetNameBase");
	       s_mappings.put(AddressPartType.StreetType, "streetNameType");
	       s_mappings.put(AddressPartType.UnitIdentifier, "unitID");
	       s_mappings.put(AddressPartType.UnitDesignator, "unitType");
	       s_mappings.put(AddressPartType.CareOf, "careOf");
	       s_mappings.put(AddressPartType.CensusTract, "censusTract");
	       s_mappings.put(AddressPartType.DeliveryAddressLine, "deliveryAddressLine");
	       s_mappings.put(AddressPartType.DeliveryInstallationType, "deliveryInstallationType");
	       s_mappings.put(AddressPartType.DeliveryInstallationArea, "deliveryInstallationArea");
	       s_mappings.put(AddressPartType.DeliveryInstallationQualifier, "deliveryInstallationQualifier");
	       s_mappings.put(AddressPartType.DeliveryMode, "deliveryMode");
	       s_mappings.put(AddressPartType.DeliveryModeIdentifier, "deliveryModeIdentifier");
	       s_mappings.put(AddressPartType.BuildingNumberSuffix, "buildingNumberSuffix");
	       s_mappings.put(AddressPartType.PostBox, "postBox");
	       s_mappings.put(AddressPartType.Precinct, "precinct");
	       
	       // Reverse mapping
	       for(AddressPartType k : s_mappings.keySet())
	    	   s_reverseMapping.put(s_mappings.get(k), k);
		}
	}
	
	/**
	 * Graph o onto s
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {

		// Call super graph
		super.graph(s, o, context, result);
		
		// cast instance
		AD instance = (AD)o;
		if(instance.isNull())
			return; // no need to format a null AD
		
		try
		{
			// Attributes first
			if(instance.getUse() != null)
				s.writeAttribute("use", FormatterUtil.toWireFormat(instance.getUse()));
			if(instance.isNotOrdered())
				s.writeAttribute("isNotOrdered", FormatterUtil.toWireFormat(instance.isNotOrdered()));
			
			// Parts (these are discretely named objects)
			if(instance.getPart() != null)
				for(ADXP part : instance.getPart())
				{
					String eName = s_mappings.get(part.getPartType());
					if(eName == null)
						throw new MessageValidationException(String.format("Can't represent address part type of %s", part.getPartType()));
					
					s.writeStartElement(DatatypeFormatter.NS_HL7, eName);
					ADXPFormatter adFormatter = new ADXPFormatter();
					adFormatter.graph(s, part, context, result);
					s.writeEndElement(); // adxp name
				}
			
			// Usable period
			if(instance.getUseablePeriod() != null)
			{
				s.writeStartElement(DatatypeFormatter.NS_HL7, "useablePeriod");
				IFormatterGraphResult hostResult = this.getHost().graph(s, instance.getUseablePeriod(), context.findChildContextFromName("useablePeriod", PropertyType.NONSTRUCTURAL));
				result.addResultDetail(hostResult.getDetails());
				s.writeEndElement();
			}
		}
		catch(XMLStreamException e)
		{
			throw new FormatterException("Could not format AD instance", e);
		}
	}

	/**
	 * Parse an object from the stream
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		AD retVal = super.parse(s, context, result, AD.class);
		
		// Parse data out
		String useValue = s.getAttributeValue(null, "use"),
				isNotOrderedValue = s.getAttributeValue(null, "isNotOrdered");
		
		
		// Use value is not null so get it and convert it using the method signature for getUse
		if(useValue != null)
			retVal.setUse((SET<CS<PostalAddressUse>>)FormatterUtil.fromWireFormat(useValue, context.findChildContextFromName("use", PropertyType.STRUCTURAL).getGetterMethod().getGenericReturnType()));
		if(isNotOrderedValue != null)
			retVal.setIsNotOrdered((Boolean)FormatterUtil.fromWireFormat(isNotOrderedValue, Boolean.class));
		
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

						// Canadian extension for useable period
						if(s.getLocalName().equals("useablePeriod"))
						{
							FormatterElementContext childContext = context.findChildContextFromName("useablePeriod", PropertyType.NONSTRUCTURAL, AD.class);
							IFormatterParseResult hostResult = this.getHost().parse(s, childContext);
							result.addResultDetail(hostResult.getDetails());
							retVal.setUseablePeriod((GTS)result.getStructure());
						}
						// Another mapping
						else if(s_reverseMapping.containsKey(s.getLocalName()))
						{
							ADXPFormatter adxpFormatter = new ADXPFormatter();
							adxpFormatter.setHost(this.getHost());
							ADXP part = (ADXP)adxpFormatter.parse(s, context, result);
							part.setPartType(s_reverseMapping.get(s.getLocalName()));
							retVal.getPart().add(part);
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
				throw new FormatterException("Could not parse AD type", e);
			}

		}

		// Validate
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/**
	 * Get the type this handles
	 */
	@Override
	public String getHandlesType() {
		return "AD";
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = Arrays.asList(new String[] {
				"use",
				"isNotOrdered",
				"part",
				"useablePeriod"
		});
		retVal.addAll(super.getSupportedProperties());
		return retVal;
	}

}
