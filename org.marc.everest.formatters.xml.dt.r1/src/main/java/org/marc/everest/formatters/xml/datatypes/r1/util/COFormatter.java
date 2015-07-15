/* 
 * Copyright 2011-2013 Mohawk College of Applied Arts and Technology
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
 * Date: 01-03-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.CO;
import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.R1FormatterCompatibilityMode;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.ResultDetail;

/**
 * Represetns a formatter that is capable of formatting a CO instance
 */
public class COFormatter extends CDFormatter {

	/**
	 * Graph object to the stream
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		// null
		if(o == null)
			return;

		CO instance = (CO)((ANY)o).shallowCopy();
		instance.setCode((CD<String>)(instance.getCode() == null ? new CD<String>() : instance.getCode()).shallowCopy());

		// Both null flavor and flavor id are propogated for R1 from the container CO into 
		// the CO.code property as the R1 CO is nothing more than a CV with ordinal value
		if(instance.isNull())
		{
			if(instance.getCode().isNull()) // Code is null so propogate
				result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "NullFlavor from CO instance will overwrite NullFlavor from CO.Code property", s.toString(), null));
			instance.getCode().setNullFlavor(instance.getNullFlavor());
		}
		else if(instance.getFlavorId() != null)
		{
			if(instance.getCode().getFlavorId() != null)
				result.addResultDetail(new ResultDetail(ResultDetailType.WARNING, "FlavorId from CO instance will overwrite FlavorId from CO.Code property", s.toString(), null));
			instance.getCode().setFlavorId(instance.getFlavorId());
		}
		
		// Graph
		super.graph(s, instance.getCode(), context, result);
		
		// Append details for not supported properties
		if(instance.getValue() != null)
		{
			if(result.getCompatibilityMode().equals(R1FormatterCompatibilityMode.ClinicalDocumentArchitecture))
				try {
					s.writeAttribute("value", instance.getValue().toString());
				} catch (XMLStreamException e) {
					throw new FormatterException("Could not format CO instance", e);
				}
			else
				result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "CO", "value", s.toString()));

		}
		if(instance.getCode() != null && instance.getCode().getQualifier() != null)
			result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "CO", "code.qualifier", s.toString()));
		if(instance.getCode() != null && instance.getCode().getTranslation() != null)
			result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "CO", "code.translation", s.toString()));
		
	}

	/**
	 * Parse an object from the wire
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {

		// Value?
		String valueString = s.getAttributeValue(null, "value");
		CD<String> retValCode = super.parseCodifiedValue(s, context, result, CD.class);
		CO retVal = new CO();
		retVal.setCode(retValCode);
		
		if(valueString != null)
			retVal.setValue(new BigDecimal(valueString));
		
		// Propogate the code up to the CO instance
		if(retVal.getCode() != null)
			super.copyBaseAttributes(retVal.getCode(), retVal);
		
		super.validate(retVal, s.toString(), result);
		return retVal;
	}

	/**
	 * Get the type this handles
	 */
	@Override
	public String getHandlesType() {
		return "CO";
	}

	/**
	 * Get supported properties
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = new ANYFormatter().getSupportedProperties();
		retVal.add("code");
		return retVal;
	}

	
	
}
