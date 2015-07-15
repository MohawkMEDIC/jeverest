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
 * Date: 08-31-2011
 */
package org.marc.everest.formatters.xml.its1;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.Interaction;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.interfaces.INormalizable;
import org.marc.everest.datatypes.interfaces.ISetComponent;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.ObjectDisposedException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.formatters.interfaces.IFormatterParseResult;
import org.marc.everest.formatters.interfaces.IStructureFormatter;
import org.marc.everest.formatters.interfaces.IValidatingStructureFormatter;
import org.marc.everest.formatters.interfaces.IXmlStructureFormatter;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.NotSupportedChoiceResultDetail;
import org.marc.everest.resultdetails.ResultDetail;
import org.marc.everest.util.ClassEnumerator;
import org.marc.everest.xml.XMLStateStreamReader;
import org.marc.everest.xml.XMLStateStreamWriter;


/**
 * A formatter instance that has the capability to graph
 * and parse instances to/from the HL7v3 XML ITS 1.0 
 * specification
 */
public class XmlIts1Formatter implements IStructureFormatter, IXmlStructureFormatter, IValidatingStructureFormatter {

	// backing field for create required elements
	private boolean m_createRequiredElements = false;
	// set to true when the object's dispose method has been called
	private boolean m_disposed = false;
	// A list of graph aides that are set for this instance of the structure formatter
	private ArrayList<IStructureFormatter> m_graphAides = new ArrayList<IStructureFormatter>();
	// backing field for the host property
	private IStructureFormatter m_host;
	// Namespace declaration for HL7
	static final String NS_HL7 = "urn:hl7-org:v3";
	// Namespace declaration for XSI
	static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	// Validate conformance
	private boolean m_validateConformance = true;
	// already loaded has maps
	private Map<String, Class<?>> m_rootNameMaps = new HashMap<String, Class<?>>();
	// Reflection formatter instance
	private ReflectionFormatter m_reflectFormatter;
	// Prefix
	private String m_prefix = "hl7";
	
	/**
	 * Creates a new instance of the XML ITS 1 formatter
	 */
	public XmlIts1Formatter() {
		this.m_reflectFormatter = new ReflectionFormatter();
		this.m_reflectFormatter.setHost(this);
	}
	
	/**
	 * Gets the prefix used for the HL7 namespace
	 * @return
	 */
	public String getElementPrefix() {
		return this.m_prefix;
	}
	
	/**
	 * Sets the element prefix
	 * @param prefix
	 */
	public void setElementPrefix(String prefix)
	{
		this.m_prefix = prefix;
	}
	
	/**
	 * Gets a value which indicates whether the formatter should create required elements
	 * @return
	 */
	public boolean getCreateRequiredElements() {
		return m_createRequiredElements;
	}

	/**
	 * Sets a value which indicates whether the formatter should automatically create required elements
	 */
	public void setCreateRequiredElements(boolean value) {
		this.m_createRequiredElements = value;
	}

	/**
	 * Tear down the XmlIts1Formatter instance and 
	 * mark the object as disposed.
	 */
	@Override
	public void close() {
		// TODO Add tear down here
		this.m_disposed = true;
	}

	/**
	 * Helper method that will throw an exception if the object has been disposed
	 */
	private void throwIfDisposed() throws ObjectDisposedException
	{
		if(this.m_disposed)
			throw new ObjectDisposedException("XmlIts1Formatter");
	}

	/**
	 * Performs provisioning steps on all assigned graph aides
	 */
	private void provisionGraphAides() throws ObjectDisposedException
	{
		throwIfDisposed();
		for(IStructureFormatter aide : this.m_graphAides)
			aide.setHost(this);
	}
	
	/**
	 * Parse an instance from the specified XmlStreamReader
	 * @param xr The XMLStreamReader to parse data from
	 * @return The formatter result of the parse operation
	 * @throws ParseException 
	 */
	@Override
	public IFormatterParseResult parse(XMLStreamReader xr) throws ObjectDisposedException {
		throwIfDisposed();
		
		// Force use XML State Stream Reader
		if(!(xr instanceof XMLStateStreamReader))
			xr = new XMLStateStreamReader(xr);
		
		// Read
		try {
			
			// Go to an element
			while(!xr.isStartElement() && xr.hasNext())
				xr.next();
			
			// Sanity check namespaceURI
			if(!xr.hasNext() || !xr.getNamespaceURI().equals(XmlIts1Formatter.NS_HL7))
				throw new FormatterException(String.format("Can't parse '%s' from namespace '%s'. The data does not appear to be HL7v3 data", xr.getName(), xr.getNamespaceURI()));
			
			// Mapped type
			Class<?> mappedClass;
			for(int i = 0; i < 2; i++)
			{
				mappedClass = m_rootNameMaps.get(xr.getLocalName());
				if(mappedClass != null)
					return this.parse(xr, new FormatterElementContext(mappedClass, null));
				else if(i == 0) // first go-around build root maps
				{
					// Load all GPMR packages from the classpath
					List<Class<?>> candidateClasses = ClassEnumerator.loadGpmrPackages();
					for(Class<?> candidateClass : candidateClasses)
					{
						synchronized (m_rootNameMaps) {
							Structure struct = candidateClass.getAnnotation(Structure.class);
							if(struct != null && (struct.isEntryPoint() || struct.structureType().equals(StructureType.INTERACTION)) &&
									xr.getLocalName().equals(struct.name()) && !m_rootNameMaps.containsKey(struct.name()))
								m_rootNameMaps.put(struct.name(), candidateClass);
						}
					}
				}
			}

			// Couldn't find a class
			return new XmlIts1FormatterParseResult(
					ResultCodeType.Rejected, 
					Arrays.asList(new IResultDetail[] {
							new ResultDetail(ResultDetailType.ERROR, String.format("Could not find a type to de-serialize '%s' into", xr.getLocalName()), xr.toString(), null)
					})
				);
			
		} catch (XMLStreamException e) {
			throw new FormatterException(e.getMessage(), e);
		}
	}

	/**
	 * Adds the specified class to the list of cached classes in this formatter. This can reduce the
	 * amount of time it takes to first deserialize a class
	 */
	public void addCachedClass(Class<? extends IGraphable> cacheType)
	{
		Structure struct = cacheType.getAnnotation(Structure.class);
		if(struct == null)
			throw new FormatterException("Cannot cache the specified type as it is missing the structure attribute");
		else if(m_rootNameMaps.containsKey(struct.name()))
			throw new FormatterException("Already cached class information");
		m_rootNameMaps.put(struct.name(), cacheType);
	}
	
	/**
	 * Graphs object o to the XmlStreamWriter xw
	 * @param xw The XmlStreamWriter to graph data to
	 * @param o The object that is to be graphed
	 * @return The result of the graph operation
	 */
	@Override
	public IFormatterGraphResult graph(XMLStreamWriter xw, IGraphable o) throws ObjectDisposedException {

		throwIfDisposed();
		FormatterElementContext context = new FormatterElementContext(o, null, null);
		return this.graph(xw, o, context);
		
	}

	/**
	 * An internal helper function that performs the graphing operation
	 */
	private void graphObjectInternal(XMLStateStreamWriter xw, IGraphable o, FormatterElementContext context, XmlIts1FormatterGraphResult resultContext)
	{
		
		String typeName = getStructureName(FormatterUtil.getClassForType(o.getClass(), context));
		
		// Find a helper class
		IXmlStructureFormatter helperFormatter = null;
		for(IStructureFormatter helper : this.getGraphAides())
			if(helper.getHandledStructures().contains(typeName))
				helperFormatter = (IXmlStructureFormatter)helper;
		
		// found a helper formatter
		if(helperFormatter != null) 
		{
			helperFormatter.setHost(this);
			IFormatterGraphResult helperResult = helperFormatter.graph(xw, o, context);
			resultContext.addResultDetail(helperResult.getDetails());
			return;
		}
		
		// Graph to the wire
		this.m_reflectFormatter.graph(xw, o, context, resultContext);
		
	}
	
	/**
	 * Get the logical HL7 name for a type
	 */
	private String getStructureName(Class<?> type)
	{
		Structure att = (Structure)type.getAnnotation(Structure.class);
		String typeName = type.getName();
		if(att != null)
			typeName = att.name();
		return typeName;
	}
	
	/**
	 * Gets the list of graph aides that are currently assigned
	 * to this instance of the formatter
	 */
	@Override
	public ArrayList<IStructureFormatter> getGraphAides() throws ObjectDisposedException {
		throwIfDisposed();
		return this.m_graphAides;
	}

	/**
	 * Gets the host of this formatter instance
	 */
	@Override
	public IStructureFormatter getHost() throws ObjectDisposedException {
		throwIfDisposed();
		return this.m_host;
	}

	/**
	 * Sets the host of this formatter instance. This is not expected to be
	 * called by external callers, rather it is set when the host is about
	 * to perform a parse/graph operation
	 */
	@Override
	public void setHost(IStructureFormatter value) throws ObjectDisposedException {
		throwIfDisposed();
		this.m_host = value;
	}

	/**
	 * Gets a list of structures that this 
	 * @return
	 */
	@Override
	public List<String> getHandledStructures() {
		return Arrays.asList("*");
	}

	/**
	 * Graphs object o onto stream s
	 * @param s The stream to which the instance o is to be graphed
	 * @param o The object instance to be graphed
	 * @return An IFormatterGraphResult containing the result of the format operation
	 * @throws XMLStreamException 
	 * @throws ObjectD throws FormatterException 
	 */
	@Override
	public IFormatterGraphResult graph(OutputStream s, IGraphable o) throws ObjectDisposedException, FormatterException {
		throwIfDisposed();
		provisionGraphAides();
		
		if(o == null)
			return new XmlIts1FormatterGraphResult(ResultCodeType.AcceptedNonConformant, null);
		
		// Construct an XML stream writer
		XMLOutputFactory fact = XMLOutputFactory.newInstance();
		XMLStreamWriter xsWriter;
		try {
			xsWriter = new XMLStateStreamWriter(fact.createXMLStreamWriter(s));
						
			// Do we need to emit the xsi and hl7 namespace
			boolean needsRootElement = o.getClass().getAnnotation(Interaction.class) == null;
			if(needsRootElement)
			{
				// Get the structure attribute and see if the element is an entry point
				Structure structureAttribute = (Structure)o.getClass().getAnnotation(Structure.class);
				needsRootElement = structureAttribute == null || !structureAttribute.isEntryPoint();
				if(needsRootElement)
				{
					// Setup namespace prefix
					xsWriter.writeStartDocument();
					// Writer start element
					xsWriter.setPrefix(this.m_prefix, NS_HL7);
					xsWriter.setPrefix("xsi", NS_XSI);
					String str = o.getClass().getName();
					str = str.substring(str.lastIndexOf(".") + 1);
					xsWriter.writeStartElement(this.m_prefix, str, XmlIts1Formatter.NS_HL7);
					xsWriter.writeNamespace(this.m_prefix, XmlIts1Formatter.NS_HL7);
					xsWriter.writeNamespace("xsi", XmlIts1Formatter.NS_XSI);
				}
			}
			
			IFormatterGraphResult result = this.graph(xsWriter, o);
			
			// If we opened a root element then close it
			if(needsRootElement)
				xsWriter.writeEndElement();
			
			// Flush the writer
			xsWriter.flush();

			return result;
			
		} catch (XMLStreamException e) {
			throw new FormatterException(e.getMessage(), e);
		}
	}

	/**
	 * Parses an object from stream s
	 * @param s The stream from which the object instance is to be parsed
	 * @return An IFormatterParseResult that contains the results of the parse operation
	 */
	@Override
	public IFormatterParseResult parse(InputStream s) throws ObjectDisposedException {
		throwIfDisposed();
		provisionGraphAides();
	
		
		// Construct an XML stream writer
		XMLInputFactory fact = XMLInputFactory.newInstance();
		XMLStateStreamReader xsReader;
		try {
			xsReader = new XMLStateStreamReader(fact.createXMLStreamReader(s));
			return this.parse(xsReader);
		} catch (XMLStreamException e) {
			throw new FormatterException(e.getMessage(), e);
		}
	}

	/**
	 * Gets a value which indicates whether the formatter should validate instances
	 */
	@Override
	public boolean getValidateConformance() {
		return this.m_validateConformance;
	}

	/**
	 * sets a value which indicates whether the formatter should validate instances
	 */
	@Override
	public void setValidateConformance(boolean value) {
		this.m_validateConformance = value;
		
	}

	/**
	 * Write an element to the stream
	 * @throws XMLStreamException 
	 */
	void writeElementUtil(XMLStateStreamWriter xw, String elementName, IGraphable value, Type asType, FormatterElementContext context, XmlIts1FormatterGraphResult resultContext) throws XMLStreamException
	{
		
		// throw if the object is disposed
		this.throwIfDisposed();
		
		// Check value
		if(value == null)
			return;
		
		// Normalize if possible
		if(value instanceof INormalizable)
			value = ((INormalizable)value).normalize();
		
		// Write start element
		xw.writeStartElement(this.m_prefix, elementName, XmlIts1Formatter.NS_HL7);
		
		// Output XSI:TYPE
		if(!value.getClass().equals(asType) && ANY.class.isAssignableFrom(value.getClass()))
		{
			String xsiType = FormatterUtil.createXsiTypeName(value),
					pfx = xw.getNamespaceContext().getPrefix(XmlIts1Formatter.NS_HL7);
			
			// HACK: ISetComponent usually means it was a GTS, lets make an adjustment
			if(context.getPropertyAnnotation() != null && context.getPropertyAnnotation().genericSupplier().length != 0 && !xsiType.contains("_"))
			{
				// Make sure the generic supplier is in there
				for(Class<?> gs : context.getPropertyAnnotation().genericSupplier())
				{
					Structure sa = (Structure)gs.getAnnotation(Structure.class);
					if(sa == null) continue;
					xsiType += "_" + sa.name();
				}
			}
				
			if(pfx != null && pfx != "")
				xsiType = pfx + ":" + xsiType;
			
			xw.writeAttribute("xsi", XmlIts1Formatter.NS_XSI, "type", xsiType);
		}
		
		this.graphObjectInternal(xw, value,  context, resultContext);
		xw.writeEndElement();
	}

	/**
	 * Write a null flavor to the wire
	 * @throws XMLStreamException 
	 */
	void writeNullFlavorUtil(XMLStateStreamWriter xw, IGraphable nullFlavor) throws XMLStreamException
	{
		this.throwIfDisposed();
		xw.writeAttribute("xsi", XmlIts1Formatter.NS_XSI, "nil", "true");
		xw.writeAttribute("nullFlavor", FormatterUtil.toWireFormat(nullFlavor));
	}

	/**
	 * Graph onto the specified stream using the provided context
	 */
	@Override
	public IFormatterGraphResult graph(XMLStreamWriter xw, IGraphable o, FormatterElementContext context) {
		
		throwIfDisposed();
		provisionGraphAides();
		XmlIts1FormatterGraphResult resultContext = new XmlIts1FormatterGraphResult(ResultCodeType.Accepted, null);
		if(o == null)
			resultContext.setCode(ResultCodeType.AcceptedNonConformant);
		else
		{
			XMLStateStreamWriter xsw = null;
			if(xw instanceof XMLStateStreamWriter)
				xsw = (XMLStateStreamWriter)xw;
			else
				xsw = new XMLStateStreamWriter(xw);
			
			this.graphObjectInternal(xsw, o, context, resultContext);
			
			resultContext.setCode(this.calculateResultCode(resultContext.getDetails()));
			
			if(!this.getValidateConformance() && !resultContext.getCode().equals(ResultCodeType.Accepted))
				resultContext.setCode(ResultCodeType.AcceptedNonConformant);
		}
		
		return resultContext;
	}

	/**
	 * Calculate the appropriate result code
	 */
	private ResultCodeType calculateResultCode(Iterable<IResultDetail> details)
	{

		ResultCodeType retVal = ResultCodeType.Accepted;
		for(IResultDetail dtl : details)
			if(dtl.getType() == ResultDetailType.ERROR)
			{
				retVal = ResultCodeType.Rejected;
				break;
			}
			else if(dtl.getType() == ResultDetailType.WARNING &&
					retVal == ResultCodeType.Accepted)
				retVal = ResultCodeType.AcceptedNonConformant;
		
		// Now, validate conformance?
		if(!this.getValidateConformance() && retVal != ResultCodeType.Accepted)
			retVal = ResultCodeType.AcceptedNonConformant;
		return retVal;
	}
	
	/**
	 * Parse an object from the specified XMLStreamReader using the specified context
	 */
	@Override
	public IFormatterParseResult parse(XMLStreamReader xr, FormatterElementContext ctx) {
		
		throwIfDisposed();
		provisionGraphAides();

		
		XmlIts1FormatterParseResult resultContext = new XmlIts1FormatterParseResult(ResultCodeType.Accepted, null);
		
		// Go to an element
		try {
			
			while(!xr.isStartElement() && xr.hasNext())
				xr.next();
		
			// Sanity check namespaceURI
			if(!xr.hasNext() || !xr.getNamespaceURI().equals(XmlIts1Formatter.NS_HL7))
				throw new FormatterException(String.format("Can't parse '%s' from namespace '%s'. The data does not appear to be HL7v3 data", xr.getName(), xr.getNamespaceURI()));
						
			resultContext.setStructure(this.parseObjectInternal(xr, ctx, resultContext));
			
			resultContext.setCode(this.calculateResultCode(resultContext.getDetails()));
			
		} catch (XMLStreamException e) {
			throw new FormatterException(e.getMessage(), e);
		}
		return resultContext;
	}

	
	/**
	 * Parse elements internally
	 */
	IGraphable parseObjectInternal(XMLStreamReader xr, FormatterElementContext ctx, XmlIts1FormatterParseResult resultContext) {
		
		this.throwIfDisposed();
		
		// Find a helper
		
		String typeName = this.getStructureName(FormatterUtil.getClassForType(ctx.getOwnerClazz(), ctx));
		
		// Structure formatter helper
		IXmlStructureFormatter ixsf = null;
		
		// Save current path because it will change later
		String currentPath = xr.toString();
		// Is there an XSI type attribute?
		String xsiType = xr.getAttributeValue(XmlIts1Formatter.NS_XSI, "type");
		// Is there a namespace prefix?
		if(xsiType != null && xsiType.contains(":"))
		{
			String pfx = xsiType.substring(0, xsiType.indexOf(":"));
			if(xr.getNamespaceURI(pfx).equals(XmlIts1Formatter.NS_HL7))
				xsiType = xsiType.substring(xsiType.indexOf(":") + 1);
			else
				resultContext.addResultDetail(new NotSupportedChoiceResultDetail(ResultDetailType.WARNING, String.format("Cannot xsi:type is not in the HL7 namespace '%s'", xsiType), xr.toString(), null));
		}
		else if(xsiType != null && !xr.getNamespaceURI().equals(XmlIts1Formatter.NS_HL7)) // not an HL7 namespace
		{
			resultContext.addResultDetail(new NotSupportedChoiceResultDetail(ResultDetailType.ERROR, String.format("Cannot process xsi:type '%s'", xsiType), xr.toString(), null));
			xsiType = null;
		}
		
		if(xsiType != null)
			ixsf = this.getXsiTypeFormatter(xsiType);
		else
			for(IStructureFormatter helper : this.getGraphAides())
				if(helper.getHandledStructures().contains(typeName))
					ixsf = (IXmlStructureFormatter)helper;
		
		// is there a helper?
		if(ixsf != null)
		{
			ixsf.setHost(this);
			IFormatterParseResult aideResult = ixsf.parse(xr, ctx);
			resultContext.addResultDetail(aideResult.getDetails());
			return aideResult.getStructure();
		}
		

		IGraphable result = this.m_reflectFormatter.parse(xr, ctx, resultContext);
		if(this.getValidateConformance())
		{
			Collection<IResultDetail> details = this.m_reflectFormatter.validate(result, ctx, currentPath);
	        if (result == null)
	            resultContext.addResultDetail(new ResultDetail(this.getValidateConformance() ? ResultDetailType.ERROR : ResultDetailType.WARNING, String.format("Couldn't parse class of type '%s'", ctx.getOwnerClazz().getName()), currentPath, null));
	        else if(details != null && details.size() > 0)
	            resultContext.addResultDetail(details);
		}
        return result;
	}

	/**
	 * Get XSI type formatter
	 */
	private IXmlStructureFormatter getXsiTypeFormatter(String xsiType) {
		
		String xsiTypeRoot = xsiType;
		
		// Generic?
		if(xsiTypeRoot == null)
			return null;
		else if(xsiTypeRoot.contains("_"))
			xsiTypeRoot = xsiTypeRoot.substring(0, xsiTypeRoot.indexOf("_"));
		
		// Find the graph aide
		for(IStructureFormatter f : this.m_graphAides)
			if(f.getHandledStructures().contains(xsiTypeRoot))
				return (IXmlStructureFormatter)f;
		return null;
		
	}
}
