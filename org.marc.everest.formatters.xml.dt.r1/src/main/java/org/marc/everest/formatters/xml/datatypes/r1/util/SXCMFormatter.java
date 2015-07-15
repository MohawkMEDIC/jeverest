package org.marc.everest.formatters.xml.datatypes.r1.util;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.datatypes.SetOperator;
import org.marc.everest.datatypes.generic.IVL;
import org.marc.everest.datatypes.generic.SXCM;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterGraphResult;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatterParseResult;

/**
 * Formatter for SXCM
 */
public class SXCMFormatter extends PDVFormatter {

	/**
	 * Graph
	 */
	@Override
	public void graph(XMLStreamWriter s, Object o,
			FormatterElementContext context, DatatypeFormatterGraphResult result) {
		// TODO Auto-generated method stub
		super.graph(s, o, context, result);
		
		SXCM<?> instance = (SXCM<?>)o;
		
		if(instance.isNull()) return; // no need to format
		
        // Operator
        if(instance.getOperator() != null)
			try {
				s.writeAttribute("operator", FormatterUtil.toWireFormat(instance.getOperator()));
			} catch (XMLStreamException e) {
				 throw new FormatterException("Cannot graph SXCM type", e);
			}

	}

	/**
	 * Parse
	 */
	public <T extends SXCM> T parseSxcm(XMLStreamReader s,
			FormatterElementContext context,
			DatatypeFormatterParseResult result, Class<T> instanceType) {

		// Preserve Operator
		SetOperator operator = null;
		if(s.getAttributeValue(null, "operator") != null)
			operator = FormatterUtil.fromWireFormat(s.getAttributeValue(null, "operator"), SetOperator.class);
		
		T retVal = super.parse(s, context, result, instanceType);

		if(operator != null)
			retVal.setOperator(operator);
		
		return retVal;
	}

	/**
	 * Handles type
	 */
	@Override
	public String getHandlesType() {
		return "SXCM";
		
	}

	/**
	 * Parse 
	 */
	@Override
	public Object parse(XMLStreamReader s, FormatterElementContext context,
			DatatypeFormatterParseResult result) {
		// TODO Auto-generated method stub
		return this.parseSxcm(s, context, result, SXCM.class);
	}

	/**
	 * Supported property
	 */
	@Override
	public List<String> getSupportedProperties() {
		List<String> retVal = super.getSupportedProperties();
		retVal.add("operator");
		return retVal;
	}
	

	
}
