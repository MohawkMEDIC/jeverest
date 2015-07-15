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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.generic.QSET;
import org.marc.everest.datatypes.generic.SXPR;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.interfaces.IDatatypeStructureFormatter;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.interfaces.IStructureFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedResultDetail;

/**
 * Formats an instance of QSET onto the wire
 * @author fyfej
 *
 */
public class QSETFormatter extends SXPRFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.SXPRFormatter#graph(javax.xml.stream.XMLStreamWriter, java.lang.Object, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult)
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		 
		try
		{
			result.addResultDetail(new NotImplementedResultDetail(ResultDetailType.WARNING, String.format("%s cannot be graphed by the R1 formatter directly, processing as SXPR", this.getHandlesType()), s.toString(), null));
			SXPR<?> sxprInstance = ((QSET<?>)o).translateToSXPR();
			super.graph(s, sxprInstance, context, result);
		}
		catch(Exception e)
		{
			throw new FormatterException("Could not format QSET instance", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.SXPRFormatter#parse(javax.xml.stream.XMLStreamReader, org.marc.everest.formatters.FormatterElementContext, org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult)
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		SXPR<?> sxprInstance = (SXPR<?>)super.parse(s, context, result);
		return sxprInstance.translateToQSET();
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.SXPRFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {
		return "QSET";
	}

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.SXPRFormatter#getSupportedProperties()
	 */
	@Override
	public List<String> getSupportedProperties() {
		return new ANYFormatter().getSupportedProperties();
	}

	
	
}
