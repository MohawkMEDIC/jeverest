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
 * Date: 05-26-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.PropertyType;
import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.NullFlavor;
import org.marc.everest.datatypes.generic.URG;
import org.marc.everest.datatypes.generic.UVP;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.datatypes.interfaces.IPrimitiveDataValue;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Represents a formatter that can graph/parse uncertainty value probabilistic (UVP) to the wire
 * @author fyfej
 *
 */
public class UVPFormatter extends ANYFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		// Graph this UVP to the stream
        super.graph(s, o, context, result);
        
        // Add probability 
        if (((ANY)o).isNull())
            return; // No need for this

        UVP instance = (UVP)o;

        try
        {
	        // Output XSI:TYPE
        	if(context.getOwnerClazz().equals(UVP.class) || context.getOwnerClazz().equals(URG.class))
        		s.writeAttribute("xsi", DatatypeFormatter.NS_XSI, "type", FormatterUtil.createXsiTypeName(o));
	
	        if (instance.getProbability() != null)
	            s.writeAttribute("probability", FormatterUtil.toWireFormat(instance.getProbability()));
	        
	        // Graph the value
	        IAny anyValue = instance.getValue();
	        if (anyValue == null)
	            return;
	
	        IFormatterGraphResult hostResult = this.getHost().graph(s, anyValue, context.findChildContextFromName("value", PropertyType.NONSTRUCTURAL));
	        result.addResultDetail(hostResult.getDetails());
        }
        catch(Exception e)
        {
        	throw new FormatterException("Unable to graph object of type UVP", e);
        }
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		
		UVP retVal = super.parse(s, context, result, UVP.class);

		try
		{
			String probabilityString = s.getAttributeValue(null, "probability"),
					xsiTypeString = s.getAttributeValue(DatatypeFormatter.NS_XSI, "type");
			
			// Get the value type
			Type valueType = context.getOwnerType();
			if(xsiTypeString != null)
				valueType = FormatterUtil.parseXsiTypeName(xsiTypeString);
			// Get the inner binding type
			if(valueType instanceof ParameterizedType)
				valueType = ((ParameterizedType)valueType).getActualTypeArguments()[0]; 
			
			// Now parse the probability
			if(probabilityString != null)
				retVal.setProbability(FormatterUtil.fromWireFormat(probabilityString, Float.class));
			
			// Now parse value
			FormatterElementContext valueContext = context.findChildContextFromName("value", PropertyType.NONSTRUCTURAL);
			valueContext.setOwnerType(valueType);
			IFormatterParseResult hostResult = this.getHost().parse(s, valueContext);
			ANY valueValue = (ANY)hostResult.getStructure();
			retVal.setValue(valueValue);
			result.addResultDetail(hostResult.getDetails());
			
			// Move null flavors from value to the root
			retVal.setNullFlavor(valueValue.getNullFlavor());
			retVal.setFlavorId(valueValue.getFlavorId());
			valueValue.setNullFlavor((NullFlavor)null);
			valueValue.setFlavorId(null);
			
			// Validate
			super.validate(retVal, s.toString(), result);
		}
		catch(Exception e)
		{
			result.addResultDetail(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), s.toString(), e));
		}
		
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "UVP";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("probability");
		return retVal;
	}

	
	
}
