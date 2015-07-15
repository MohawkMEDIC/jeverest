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
 * Date: 01-16-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.List;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.GTS;
import org.marc.everest.datatypes.NullFlavor;
import org.marc.everest.datatypes.TS;
import org.marc.everest.datatypes.generic.QSET;
import org.marc.everest.datatypes.interfaces.ISetComponent;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.IDatatypeFormatter;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.MandatoryElementMissingResultDetail;
import org.marc.everest.resultdetails.NotSupportedChoiceResultDetail;
import org.marc.everest.resultdetails.PropertyValuePropagatedResultDetail;

/**
 * Represents a formatter that can parse and graph GTS instances
 */
public class GTSFormatter extends ANYFormatter {

	/**
	 * Graph the GTS instance to the wire
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		// Propogate anything from HULL to the root instances
		GTS instance = (GTS)o;
		
		if(instance.getHull() != null)
		{
			if(instance.getHull().getNullFlavor() != null)
			{
				instance.setNullFlavor(instance.getNullFlavor() == null ? instance.getHull().getNullFlavor() : instance.getNullFlavor());
				((ANY)instance.getHull()).setNullFlavor((NullFlavor)null);
				result.addResultDetail(new PropertyValuePropagatedResultDetail(ResultDetailType.WARNING, "hull.nullFlavor", "nullFlavor", instance.getNullFlavor(), s.toString()));
			}
			if(instance.getHull().getFlavorId() != null)
			{
				instance.setFlavorId(instance.getFlavorId() == null ? instance.getHull().getFlavorId() : instance.getFlavorId());
				((ANY)instance.getHull()).setFlavorId(null);
				result.addResultDetail(new PropertyValuePropagatedResultDetail(ResultDetailType.WARNING, "hull.flavorId", "flavorId", instance.getFlavorId(), s.toString()));
			}
		}
		
		// Graph super attributes (ANY)
		super.graph(s, o, context, result);
		
		if(instance.isNull())
			return;
		else if(instance.getHull() == null)
		{
			result.addResultDetail(new MandatoryElementMissingResultDetail(ResultDetailType.ERROR, "Cannot graph a GTS with no Hull", s.toString(), null));
			return;
		}

		try
		{
			// is this a QS?\
			ISetComponent<TS> hull = instance.getHull();
			if(hull instanceof QSET<?>)
					hull = ((QSET<TS>)hull).translateToSXPR();
			
			 String xsiTypeName = FormatterUtil.createXsiTypeName(hull);
			 if(xsiTypeName.equals("SXPR"))
				 xsiTypeName = "SXPR_TS"; // HACK: Type erasure makes doing this programmatically impossible
			 
	         s.writeAttribute(DatatypeFormatter.NS_XSI, "type", xsiTypeName);
	         // Output the formatting
	         IFormatterGraphResult hostResult = this.getHost().graph(s, (IGraphable)hull, context.findChildContextFromName("hull", PropertyType.NONSTRUCTURAL));
	         result.addResultDetail(hostResult.getDetails());			
		}
		catch(Exception e)
		{
			throw new FormatterException("Cannot format this instance of GTS");
		}
	}

	/**
	 * Parse an object from the wire
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		// Parse
        GTS retVal = super.parse(s, context, result, GTS.class);

        // Is there any need to continue?
        if (retVal.isNull())
            return retVal;

        // Now determine the type of GTS
        String typeName = s.getAttributeValue(DatatypeFormatter.NS_XSI, "type");

        IGraphable hullCandidate = (IGraphable) this.getHost().parse(s, context.findChildContextFromName("hull", PropertyType.NONSTRUCTURAL));
        if(!(hullCandidate instanceof ISetComponent<?>))
        {
        	result.addResultDetail(new NotSupportedChoiceResultDetail(ResultDetailType.ERROR, String.format("Cannot parse a GTS Hull of type '%s'", typeName), s.toString(), null));
        	return retVal;
        }
        retVal.setHull((ISetComponent<TS>)hullCandidate);
        
        // Correct the flavor, the flavor of the hull becomes the flavor of the object
        retVal.setFlavorId(retVal.getFlavorId() != null ? retVal.getFlavorId() : retVal.getHull().getFlavorId());
        ((ANY)retVal.getHull()).setFlavorId(null);
        retVal.setNullFlavor(retVal.getNullFlavor() != null ? retVal.getNullFlavor() : retVal.getHull().getNullFlavor());
        ((ANY)retVal.getHull()).setNullFlavor((NullFlavor)null);

        // Set the details
        
        return retVal;	
    }

	/**
	 * Get the type this formatter handles
	 */
	@Override
	public String getHandlesType() {
		return "GTS";
	}

	/**
	 * Get the properties this supports
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("hull");
		return retVal;
	}

	
	
	
}
