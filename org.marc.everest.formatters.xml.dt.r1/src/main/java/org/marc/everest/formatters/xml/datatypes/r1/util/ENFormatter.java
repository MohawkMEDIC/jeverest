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
 * Date: 01-15-2013 
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
import org.marc.everest.datatypes.ADXP;
import org.marc.everest.datatypes.EN;
import org.marc.everest.datatypes.ENXP;
import org.marc.everest.datatypes.EntityNamePartQualifier;
import org.marc.everest.datatypes.EntityNamePartType;
import org.marc.everest.datatypes.EntityNameUse;
import org.marc.everest.datatypes.GTS;
import org.marc.everest.datatypes.TS;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.IVL;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.datatypes.interfaces.IPredicate;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.MessageValidationException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.XsiType;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.NotSupportedChoiceResultDetail;
import org.marc.everest.resultdetails.ResultDetail;


/**
 * Entity Name formatter for EN and/or PN instances 
 */
public class ENFormatter extends ANYFormatter {

	// Mappings to / from R1 element names to part types
	private Map<EntityNamePartType, String> s_mappings = new HashMap<EntityNamePartType, String>();
	private Map<String, EntityNamePartType> s_reverseMapping = new HashMap<String, EntityNamePartType>();
	
	/**
	 * ENformatter ctor
	 */
	public ENFormatter()
	{
		// Add mappings
		if(s_mappings.size() == 0)
		{
	       s_mappings.put(EntityNamePartType.Given, "given");
	       s_mappings.put(EntityNamePartType.Delimiter, "delimeter");
	       s_mappings.put(EntityNamePartType.Family, "family");
	       s_mappings.put(EntityNamePartType.Prefix, "prefix");
	       s_mappings.put(EntityNamePartType.Suffix, "suffix");
	       
	       // Reverse mapping
	       for(EntityNamePartType k : s_mappings.keySet())
	    	   s_reverseMapping.put(s_mappings.get(k), k);
		}
	}

	/**
	 * Format an instance of EN to the wire
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		EN instance = (EN)o;

        // Do a base format
        super.graph(s, instance, context, result);

        // Null flavor
        if (instance.isNull())
            return;

        try
        {
        	
	        // use
	        if (instance.getUse() != null)
	            s.writeAttribute("use", FormatterUtil.toWireFormat(instance.getUse()));
	
	        // parts
	        if(instance.getParts() != null)
	            for(ENXP part : instance.getParts())
	            {
	                EntityNamePartType pt = part.getType() == null ? null : part.getType().toEnumeratedVocabulary();
	                SET<CS<EntityNamePartQualifier>> qualifiers = new SET<CS<EntityNamePartQualifier>>();
	                if (part.getQualifier() != null)
	                    for (CS<EntityNamePartQualifier> qlf : part.getQualifier())
	                        qualifiers.add((CS<EntityNamePartQualifier>)qlf.shallowCopy());
	
	                // Title part type?
	                if (pt == EntityNamePartType.Title)
	                {
	                	qualifiers.add(new CS<EntityNamePartQualifier>(new EntityNamePartQualifier("TITLE", null)));
	                    pt = null;
	                }
	
	                // Possible to match qualifier to a part tpye if none specified!
	                if (!qualifiers.isEmpty())
	                {
	                    CS<EntityNamePartQualifier> pfx = qualifiers.find(new IPredicate<CS<EntityNamePartQualifier>>() {
							
							@Override
							public boolean match(CS<EntityNamePartQualifier> i) {
								return i.getCode().equals(EntityNamePartQualifier.Prefix);
							}
						}),
	                        sfx = qualifiers.find(new IPredicate<CS<EntityNamePartQualifier>>() {
								
								@Override
								public boolean match(CS<EntityNamePartQualifier> i) {
									return i.getCode().equals(EntityNamePartQualifier.Suffix);
								}
							});
	                    if (pfx != null)
	                    {
	                        pt = EntityNamePartType.Prefix;
	                        qualifiers.remove(pfx);
	                    }
	                    else if (sfx != null)
	                    {
	                        pt = EntityNamePartType.Suffix;
	                        qualifiers.remove(sfx);
	                    }
	                }
	
	                // Part type is not set so do it inline
	                if (pt == null)
	                {
	                    if (!qualifiers.isEmpty())
	                        result.addResultDetail(new NotSupportedChoiceResultDetail(ResultDetailType.WARNING, "Part has qualifier but is not being rendered as a type element, qualifier will be dropped", s.toString(), null));
	                    s.writeCharacters(part.getValue());
	                }
	                else if (s_mappings.containsKey(pt))
	                {
	                    ENXP prt = (ENXP)part.shallowCopy();
	                    prt.setType(new CS<EntityNamePartType>(pt));
	                    prt.setQualifier(qualifiers);
	                    s.writeStartElement(DatatypeFormatter.NS_HL7, s_mappings.get(pt));
	                    ENXPFormatter enFormatter = new ENXPFormatter();
	                    enFormatter.graph(s, prt, context, result);
	                    s.writeEndElement();
	                }
	                else
	                    throw new MessageValidationException(String.format("Can't represent entity name part '{0}' in datatypes R1 at '{1}'", pt, s.toString()));
	
	            }
	
	        // Bug: 2102 - Graph the validTime element. Since the HXIT
	        // class in R2 already has validTimeLow and validTimeHigh 
	        // what we'll do is map these attributes to the validTime element
	        if (instance.getValidTimeLow() != null || instance.getValidTimeHigh() != null)
	        {
	            IVL<TS> validTime = new IVL<TS>(instance.getValidTimeLow(), instance.getValidTimeHigh());
	            s.writeStartElement(DatatypeFormatter.NS_HL7, "validTime");
				XsiType genType = new XsiType(IVL.class);
				genType.getTypeArguments().add(TS.class);
				FormatterElementContext fakeContext = new FormatterElementContext(IVL.class, null);
				fakeContext.setOwnerType(genType);
	            IFormatterGraphResult hostResult = this.getHost().graph(s, validTime, fakeContext);
	            result.addResultDetail(hostResult.getDetails());
	            s.writeEndElement(); // valid time
	        }
        }
        catch(XMLStreamException e)
        {
        	result.addResultDetail(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), s.toString(), e));
        }
    }

	/**
	 * Parse an object from the wire format
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		return this.parse(s, context, result, EN.class);
	}
	
	/**
	 * Parse an EN/PN/TN or ON from wire formats
	 */
	@Override
	public <T extends IAny> T parse(XMLStreamReader s, FormatterElementContext context, DatatypeFormatterParseResult result, Class<T> clazz)
	{
		
		EN retVal = (EN)super.parse(s, context, result, clazz);
		
		// Parse use
		if(s.getAttributeValue(null, "use") != null)
			retVal.setUse((SET<CS<EntityNameUse>>)FormatterUtil.fromWireFormat(s.getAttributeValue(null, "use"), context.findChildContextFromName("use", PropertyType.STRUCTURAL).getOwnerType(), false));
		
		// Content / Elements
		if(!s.isEndElement())
		{
			try
			{
				int sDepth = 0;
				String sName = s.getLocalName();
				s.next();
				while(!(s.getEventType() == XMLStreamReader.END_ELEMENT && sDepth == 0 && s.getLocalName().equals(sName)))
				{

					if ((s.getEventType() == XMLStreamReader.CHARACTERS ||
                            s.getEventType() == XMLStreamReader.CDATA) && s.getText().trim().length() > 0)
					{
                        retVal.getParts().add(new ENXP(s.getText()));
                        s.next();
                        continue;
					}
					
					// Skip anything other than elements
					while(s.hasNext() && s.getEventType() != XMLStreamReader.START_ELEMENT && s.getEventType() != XMLStreamReader.END_ELEMENT)
						s.next();

					// HACK: Exit condition
					if(s.getEventType() == XMLStreamReader.END_ELEMENT && sDepth == 0 && s.getLocalName().equals(sName))
						break;
					
					String oldName = s.getLocalName();
					try
					{
						if(s.getEventType() == XMLStreamReader.END_ELEMENT) continue;

						
						// Canadian extension for useable period
						if(s.getLocalName().equals("validTime"))
						{
							XsiType genType = new XsiType(IVL.class);
							genType.getTypeArguments().add(TS.class);
							FormatterElementContext eleContext = new FormatterElementContext(IVL.class, null);
							eleContext.setOwnerType(genType);
							IFormatterParseResult hostResult = this.getHost().parse(s, eleContext);
							result.addResultDetail(hostResult.getDetails());
							
							// Process valid time
							IVL<TS> validTime = (IVL<TS>)hostResult.getStructure();
							retVal.setValidTimeHigh(validTime.getHigh());
							retVal.setValidTimeLow(validTime.getLow());
						}
						// Another mapping
						else if(s_reverseMapping.containsKey(s.getLocalName()))
						{
							ENXPFormatter adxpFormatter = new ENXPFormatter();
							adxpFormatter.setHost(this.getHost());
							ENXP part = (ENXP)adxpFormatter.parse(s, context, result);
							part.setType(new CS<EntityNamePartType>(s_reverseMapping.get(s.getLocalName())));
							retVal.getParts().add(part);
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
			catch(XMLStreamException e)
			{
				throw new FormatterException("Could not parse EN type", e);
			}

		}
		
		super.validate(retVal, s.toString(), result);
		return (T) retVal;
	}

	/**
	 * Get the type that this handles
	 * @return
	 */
	@Override
	public String getHandlesType() {
		return "EN";
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.addAll(Arrays.asList("part", "use", "validTimeLow", "validTimeHigh"));
		return retVal;
	}
	
}
