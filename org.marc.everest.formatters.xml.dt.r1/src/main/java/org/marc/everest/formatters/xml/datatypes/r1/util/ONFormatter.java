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
 * Date: 01-27-2012
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.EN;
import org.marc.everest.datatypes.EntityNamePartType;
import org.marc.everest.datatypes.ON;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.VocabularyIssueResultDetail;

/**
 * Represents a class that can format ON instances
 */
public class ONFormatter extends ENFormatter {

	/**
	 * Graph object to the wire
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		// Validate and remove any incriminating data
        ON instance = (ON)o;
        
        instance = new ON(instance.getUse(), instance.getParts());
        instance.setNullFlavor(((IAny)o).getNullFlavor());

            for(int i = instance.getParts().size() - 1; i >= 0; i--)
                if (instance.getPart(i).getType() != null &&
                (instance.getPart(i).getType().getCode() == EntityNamePartType.Family ||
                    instance.getPart(i).getType().getCode() == EntityNamePartType.Given))
                {
                    result.addResultDetail(new VocabularyIssueResultDetail(ResultDetailType.WARNING,
                        String.format("Part name '%s' in ON instance will be removed. ON Parts cannot have FAM or GIV parts", instance.getPart(i).toString()),
                        s.toString(),
                        null));
                    instance.getParts().remove(i);
                }
        super.graph(s, instance, context, result);	
	}

	/**
	 * 
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {

		ON retVal = ON.fromEN((EN)super.parse(s, context, result));
        
        // Remove non-allowed parts
            for (int i = retVal.getParts().size() - 1; i >= 0; i--)
                if (retVal.getPart(i).getType() != null && (
                retVal.getPart(i).getType().getCode() == EntityNamePartType.Family ||
                    retVal.getPart(i).getType().getCode() == EntityNamePartType.Given))
                {
                    result.addResultDetail(new VocabularyIssueResultDetail(ResultDetailType.WARNING,
                        String.format("Part name '%s' in ON instance will be removed. ON Parts cannot have FAM or GIV parts", retVal.getPart(i)),
                        s.toString(),
                        null));
                    retVal.getParts().remove(i);
                }

        return retVal;
	}

	/**
	 * Get the type that this handles
	 */
	@Override
	public String getHandlesType() {
		return "ON";
	}

	
	
}
