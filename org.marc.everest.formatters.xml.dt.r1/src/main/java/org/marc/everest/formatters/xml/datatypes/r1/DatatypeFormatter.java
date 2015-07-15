/* 
 * Copyright 2011-2012 Mohawk College of Applied Arts and Technology
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
 * Date: 11-09-2012
 */
package org.marc.everest.formatters.xml.datatypes.r1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.Structure;
import org.marc.everest.datatypes.GTS;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IDatatypeStructureFormatter;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.interfaces.IStructureFormatter;
import org.marc.everest.formatters.interfaces.IValidatingStructureFormatter;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.util.ANYFormatter;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotImplementedResultDetail;
import org.marc.everest.resultdetails.NotSupportedChoiceResultDetail;
import org.marc.everest.resultdetails.ResultDetail;
import org.marc.everest.util.ClassEnumerator;

/**
 * Represents a datatype formatter that has the capability to format data type instances
 * according to the data types r1 specification
 */
public class DatatypeFormatter implements IXmlStructureFormatter,
		IDatatypeStructureFormatter, IValidatingStructureFormatter {

	// Backing field for the host 
	private IStructureFormatter m_host;
	// Backing field for helper formatters
	private static Map<String, Class<? extends IDatatypeFormatter>> s_helperFormatters = new HashMap<String, Class<? extends IDatatypeFormatter>>();
	// Backing field for supported types
	private static List<String> s_supportedTypes = new ArrayList<String>();
	// Backing field for compatibility mode
	private R1FormatterCompatibilityMode m_compatibilityMode = R1FormatterCompatibilityMode.Universal;
	// Backing field for validate conformance
	private boolean m_validateConformance = true;
	// Namespace declaration for HL7
	public static final String NS_HL7 = "urn:hl7-org:v3";
	// Namespace declaration for XSI
	public static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	
	// Supported names
	private static final String[] s_unsupportedNames = new String[] {
		"COLL",
		"QSET"
	};
	
	/**
	 * Get compatibility mode
	 */
	public R1FormatterCompatibilityMode getCompatibilityMode() {
		return this.m_compatibilityMode;
	}

	/**
	 * Set compatibility mode
	 */
	public void setCompatibilityMode(R1FormatterCompatibilityMode compatibilityMode) {
		this.m_compatibilityMode = compatibilityMode;
	}

	/**
	 * Datatype formatter constructor
	 */
	public DatatypeFormatter() 
	{
		this.setValidateConformance(true);
		if(s_helperFormatters.size() == 0)
			synchronized (s_helperFormatters) {
				if(s_helperFormatters.size() != 0) return; // check 
				
				// Get all classes in this package
				BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/FormatterClasses")));
				String className = null,
						packageName = "org.marc.everest.formatters.xml.datatypes.r1.util";
				try
				{
				while ((className = reader.readLine()) != null)
				{
					// Try to instantiate the helper
					try {
						Class<?> clazz = Class.forName(String.format("%s.%s", packageName, className));
						IDatatypeFormatter fmtr = (IDatatypeFormatter)clazz.newInstance();
						s_helperFormatters.put(fmtr.getHandlesType(), fmtr.getClass());
						s_supportedTypes.add(fmtr.getHandlesType());
					} catch (Exception e) {
						; // ignore exceptions
					}
				}
				}
				catch(Exception e)
				{; // ignore
				}
				
				if(s_helperFormatters.size() == 0)
					throw new FormatterException("Could not initialize formatter context");
			}
	}
	
	/**
	 * Constructs a new formatter with the specified compatibility mode
	 */
	public DatatypeFormatter(R1FormatterCompatibilityMode compatibilityMode)
	{
		this();
		this.setCompatibilityMode(compatibilityMode);
	}
	
	/**
	 * Get the graph aides for this formatter
	 */
	@Override
	public ArrayList<IStructureFormatter> getGraphAides() {
		return null; // Not supported
	}

	/**
	 * Get the host formatter of this formatter
	 */
	@Override
	public IStructureFormatter getHost() {
		return this.m_host;
	}

	/**
	 * Set the host formatter of this formatter
	 */
	@Override
	public void setHost(IStructureFormatter value) {
		this.m_host = value;
	}

	/**
	 * Get all structures that this formatter handles
	 */
	@Override
	public List<String> getHandledStructures() {
		return s_supportedTypes;
	}

	/**
	 * Graph object o onto stream s
	 */
	@Override
	public IFormatterGraphResult graph(OutputStream s, IGraphable o) {
		 return new DatatypeFormatterGraphResult(this.m_compatibilityMode, this.m_validateConformance, ResultCodeType.Rejected, Arrays.asList(new IResultDetail[] {
	                new NotImplementedResultDetail(ResultDetailType.ERROR, "Can't use the datatypes R1 formatter on a stream", null)
	            }));
	}

	/**
	 * Parse object from stream s
	 */
	@Override
	public IFormatterParseResult parse(InputStream s) {
	 	return new DatatypeFormatterParseResult(this.m_compatibilityMode, this.m_validateConformance, ResultCodeType.Rejected, Arrays.asList(new IResultDetail[] {
	                new NotImplementedResultDetail(ResultDetailType.ERROR, "Can't use the datatypes R1 formatter on a stream", null)
        })); 
	}

	/**
	 * Close the formatter
	 */
	@Override
	public void close()
	{
		// No special logic required
	}
	
	/**
	 * Get formatter based on type
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static IDatatypeFormatter getFormatter(Class<?> type)
	{
		// Get the struct attribute
        IDatatypeFormatter formatter = null;
        Class<?> cType = type;

        // Find a structure attribute to format... 
        while (formatter == null)
        {
            Structure sta = cType.getAnnotation(Structure.class);

            // Find the object that we want to render
            Class<?> formatterType = null;
            if (s_helperFormatters.containsKey(sta.name()))
				try {
					formatter = s_helperFormatters.get(sta.name()).newInstance();
				} catch(Exception e)
				{
					throw new FormatterException("Could not create formatter helpder", e); 
				}

            // Swap cType
            cType = cType.getSuperclass();
            if (cType == null) return null; // Not available
        }

        return formatter;
	}
	
	/**
	 * Get supported properties
	 */
	@Override
	public Iterable<String> getSupportedPropertyNames(Class<?> type) {
		
		try {
			IDatatypeFormatter fmtr = getFormatter(type);
			if(fmtr == null)
				return null;
			return fmtr.getSupportedProperties();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Parse an object from the xr
	 */
	@Override
	public IFormatterParseResult parse(XMLStreamReader xr) {
		
		return this.parse(xr, null);
	}

	/**
	 * Graph an object to xw
	 */
	@Override
	public IFormatterGraphResult graph(XMLStreamWriter xw, IGraphable o) {
		FormatterElementContext context = new FormatterElementContext(o, null, null);
		return this.graph(xw, o, context);
	}


	/**
	 * Gets a value which indicates whether this formatter validates conformance
	 */
	@Override
	public boolean getValidateConformance() {
		return this.m_validateConformance;
	}

	/**
	 * Sets a value which indiciates whether this formatter validates conformance
	 */
	@Override
	public void setValidateConformance(boolean value) {
		this.m_validateConformance = value;
	}

	/**
	 * Graphs a datatype instance o onto xw using the specified context 
	 */
	@Override
	public IFormatterGraphResult graph(XMLStreamWriter xw, IGraphable o, FormatterElementContext ctx) {
		if(o== null)
			return new DatatypeFormatterGraphResult(this.m_compatibilityMode, this.m_validateConformance, ResultCodeType.Accepted);
		
		try
		{
			IDatatypeFormatter fmtr = getFormatter(o.getClass());
			
			if(fmtr == null)
				return new DatatypeFormatterGraphResult(this.m_compatibilityMode, this.m_validateConformance, ResultCodeType.NotAvailable, Arrays.asList(new IResultDetail[] {
						new NotImplementedResultDetail(ResultDetailType.ERROR, String.format("Could not find formatter for '%s'", o.getClass().getName()))
				}));
			
			fmtr.setHost(this.getHost() instanceof IXmlStructureFormatter ? (IXmlStructureFormatter)this.getHost() : this);
			DatatypeFormatterGraphResult result = new DatatypeFormatterGraphResult(this.m_compatibilityMode, this.m_validateConformance);
			fmtr.graph(xw, o, ctx, result);
			return result;
		}
		catch(Exception e)
		{
			return new DatatypeFormatterGraphResult(this.m_compatibilityMode, this.m_validateConformance, ResultCodeType.Error, 
					Arrays.asList(
							new IResultDetail[] { 
									new ResultDetail(ResultDetailType.ERROR, e.getMessage(), xw.toString(), e)
							}
							));
		}
	}

	/**
	 * Parses a datatype instance from xr using the specified context
	 */
	@Override
	public IFormatterParseResult parse(XMLStreamReader xr, FormatterElementContext ctx) {
		
		// Get the struct attribute
        IDatatypeFormatter formatter = null;
        
        if(ctx == null)
        	return new DatatypeFormatterParseResult(this.getCompatibilityMode(), this.getValidateConformance(), ResultCodeType.AcceptedNonConformant, null);
        
        Type cType = ctx.getOwnerType() ;
        if(cType == Object.class)
        	return new DatatypeFormatterParseResult(this.getCompatibilityMode(), this.getValidateConformance(), ResultCodeType.AcceptedNonConformant, Arrays.asList(
        				new IResultDetail[] {
        						new UnsupportedDatatypeR1PropertyResultDetail(ResultDetailType.ERROR, "NULL", ctx.getPropertyAnnotation().name(), xr.toString())
        				}
        			));
        
        Class<?> cClass = FormatterUtil.getClassForType(cType, ctx);

        // Set host for parse
        DatatypeFormatterParseResult result = new DatatypeFormatterParseResult(this.getCompatibilityMode(), this.getValidateConformance());
        

        // Don't check for XSI type if the type is a GTS or iterable
        // This is because the XSI type on these classes will mislead the formatter selector
        if (FormatterUtil.hasInterface(cClass, Iterable.class) && !Modifier.isAbstract(cClass.getModifiers()))
            formatter = this.getFormatter(cClass);
        else
        {
        	 // Force processing as an XSI:Type
            if (xr.getAttributeValue(NS_XSI, "type") != null && !ctx.getIgnoreTypeCasting())
            {
            	String xsiType = xr.getAttributeValue(DatatypeFormatter.NS_XSI, "type");
        		// Is there a namespace prefix?
        		if(xsiType != null && xsiType.contains(":"))
        		{
        			String pfx = xsiType.substring(0, xsiType.indexOf(":"));
        			if(xr.getNamespaceURI(pfx).equals(DatatypeFormatter.NS_HL7))
        				xsiType = xsiType.substring(xsiType.indexOf(":") + 1);
        			else
        				result.addResultDetail(new NotSupportedChoiceResultDetail(ResultDetailType.WARNING, String.format("Cannot xsi:type is not in the HL7 namespace '%s'", xsiType), xr.toString(), null));
        		}
        		else if(xsiType != null && !xr.getNamespaceURI().equals(DatatypeFormatter.NS_HL7)) // not an HL7 namespace
        		{
        			result.addResultDetail(new NotSupportedChoiceResultDetail(ResultDetailType.ERROR, String.format("Cannot process xsi:type '%s'", xsiType), xr.toString(), null));
        			xsiType = null;
        		}
        		
                cType = FormatterUtil.parseXsiTypeName(xsiType);
                cClass = FormatterUtil.getClassForType(cType);
                ctx.setOwnerType(cType);
                
            }
            
            formatter = this.getFormatter(cClass);
        }

        if (formatter == null)
            return null;


        formatter.setHost((IXmlStructureFormatter)(this.getHost() == null ? this : this.getHost()));

        // Parse
        result.setStructure((IGraphable)formatter.parse(xr, ctx, result));
        
        return result;
	}

	/**
	 * Move the stream to the next element event
	 * @throws XMLStreamException 
	 */
	public static void nextElementEvent(XMLStreamReader s) throws XMLStreamException {
		int retVal = s.next();
		while(retVal != XMLStreamReader.START_ELEMENT && retVal != XMLStreamReader.END_ELEMENT && s.hasNext())
			retVal = s.next();
		
	}

}
