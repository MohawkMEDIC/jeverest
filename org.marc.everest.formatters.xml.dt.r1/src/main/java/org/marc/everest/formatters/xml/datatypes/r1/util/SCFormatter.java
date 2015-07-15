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
 * Date: 05-22-2013
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.List;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.*;
import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.formatters.xml.datatypes.r1.UnsupportedDatatypeR1PropertyResultDetail;
import org.marc.everest.interfaces.ResultDetailType;

/**
 * Represents a formatter that is capable of formatting an ST instance to the wire
 * @author fyfej
 *
 */
public class SCFormatter extends STFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.STFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		
		// Serialize CS
        SC cs = (SC)o;

        try
        {
        	
	        // Serialize ST 
	        if (((ANY)o).isNull())
	        	return;
	        else if(cs.getCode() != null && !cs.getCode().isNull())
	        {
	            if (cs.getCode().getCode() != null)
	                s.writeAttribute("code", FormatterUtil.toWireFormat(cs.getCode()));
	            if (cs.getCode().getCodeSystem() != null)
	                s.writeAttribute("codeSystem", FormatterUtil.toWireFormat(cs.getCode().getCodeSystem()));
	            if (cs.getCode().getCodeSystemName() != null)
	                s.writeAttribute("codeSystemName", FormatterUtil.toWireFormat(cs.getCode().getCodeSystemName()));
	            if (cs.getCode().getCodeSystemVersion() != null)
	                s.writeAttribute("codeSystemVersion", FormatterUtil.toWireFormat(cs.getCode().getCodeSystemVersion()));
	            if (cs.getCode().getDisplayName() != null)
	                s.writeAttribute("displayName", FormatterUtil.toWireFormat(cs.getCode().getDisplayName()));
	            
	            // Not supported properties
	            if (cs.getCode().getValueSet() != null)
	                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "SC", "code.valueSet", s.toString()));
	            if (cs.getCode().getValueSetVersion() != null)
	                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "SC", "code.valueSetVersion",  s.toString()));
	            if(cs.getCode().getCodingRationale() != null)
	                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "SC", "code.codingRationale", s.toString()));
	            if (cs.getCode().getOriginalText() != null)
	                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "SC", "code.originalText",  s.toString()));
	            if (cs.getCode().getQualifier() != null)
	                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "SC", "code.qualifier", s.toString()));
	            if (cs.getCode().getTranslation() != null)
	                result.addResultDetail(new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.WARNING, "SC", "code.translation",  s.toString()));
	        }
	
	        ST st = (ST)o;
	        super.graph(s, o, context, result);
        }
        catch(Exception e)
        {
        	throw new FormatterException("Could not format SC instance", e);
        }
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.STFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		// The SC to return.
        SC sc = super.parse(s, context, result, SC.class);

        if (sc.isNull())
            return sc;

        if (s.getAttributeValue(null, "code") != null || s.getAttributeValue(null, "codeSystem") != null ||
            s.getAttributeValue(null, "codeSystemVersion") != null || s.getAttributeValue(null, "codeSystemName") != null ||
            s.getAttributeValue(null, "displayName") != null)
        {
            sc.setCode(new CD<String>());
            sc.getCode().setCodeSystem(s.getAttributeValue(null, "codeSystem"));
            sc.getCode().setCodeSystemVersion(s.getAttributeValue(null, "codeSystemVersion"));
            sc.getCode().setCodeSystemName(s.getAttributeValue(null, "codeSystemName"));
            sc.getCode().setDisplayName(s.getAttributeValue(null, "displayName"));
        }
        else if (s.getAttributeValue(null, "code") != null)
            sc.getCode().setCode(FormatterUtil.fromWireFormat(s.getAttributeValue(null, "code"), CS.class));
        
        // Read the ST parts
        ST st = (ST)super.parse(s, context, result);
        sc.setLanguage(st.getLanguage());
        sc.setValue(st.getValue());

        return sc;
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.STFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "SC";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.STFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
        retVal.add("code");
        return retVal;
	}

}
