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
 * Date: 10-22-2012
 */
package org.marc.everest.formatters.xml.its1;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Interaction;
import org.marc.everest.annotations.Properties;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.annotations.TypeMap;
import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.NullFlavor;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.datatypes.interfaces.ICodedSimple;
import org.marc.everest.datatypes.interfaces.ICodedValue;
import org.marc.everest.datatypes.interfaces.ICollection;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.formatters.FormatterElementContext;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.interfaces.IImplementsNullFlavor;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.FixedValueMisMatchedResultDetail;
import org.marc.everest.resultdetails.InsufficientRepetitionsResultDetail;
import org.marc.everest.resultdetails.MandatoryElementMissingResultDetail;
import org.marc.everest.resultdetails.NotImplementedElementResultDetail;
import org.marc.everest.resultdetails.NotImplementedResultDetail;
import org.marc.everest.resultdetails.NotSupportedChoiceResultDetail;
import org.marc.everest.resultdetails.RequiredElementMissingResultDetail;
import org.marc.everest.resultdetails.ResultDetail;
import org.marc.everest.xml.XMLStateStreamWriter;


/**
 * A formatter that uses reflection to format instances
 */
public class ReflectionFormatter {

	// Backing field for host
	private XmlIts1Formatter m_host;
	// Cached build properties for each type encountered
	private HashMap<Class<?>, List<Method>> m_cachedBuildProperties = new HashMap<Class<?>, List<Method>>();
	
	/**
	 * Gets the host of the formatter
	 */
	public XmlIts1Formatter getHost() { return this.m_host; }
	/**
	 * Sets the host of this formatter
	 */
	public void setHost(XmlIts1Formatter value) { this.m_host = value; }
	
	/**
	 * Validate the instance x returning validation errors
	 */
	public Collection<IResultDetail> validate(IGraphable o, FormatterElementContext context, String locationPath)
	{
		List<IResultDetail> dtls = new ArrayList<IResultDetail>(10);
        boolean isValid = true;

        // Null return bool
        if (o == null)
            return null;

        Method nullFlavorAttrib = null;
		try {
			nullFlavorAttrib = o.getClass().getMethod("getNullFlavor");
			if (nullFlavorAttrib != null && nullFlavorAttrib.invoke(o) != null)
	            return null;
		} catch (Exception e) {
		}
        

        // Scan property info for violations
        List<Method> methods = this.getBuildProperties(o.getClass());
        for(Method prop : methods)
        {

            Object propertyValue = null;
			try {
				propertyValue = prop.invoke(o);
			} catch (Exception e) {
				dtls.add(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), locationPath, e));
			} 
        	FormatterElementContext currentContext = new FormatterElementContext(propertyValue, prop, context);
            
        	// Get get the first one
        	if(currentContext.getPropertiesAnnotation() != null)
        		currentContext.setPropertyAnnotation(currentContext.getPropertiesAnnotation().value()[0]);
        	
        	// Shortcut vars
        	Property pa = currentContext.getPropertyAnnotation();
        	Class<?> actualReturnType = FormatterUtil.getClassForType(prop.getReturnType(), currentContext);
        	
            if (pa.conformance().equals(ConformanceType.MANDATORY) &&
                FormatterUtil.hasInterface(actualReturnType, IImplementsNullFlavor.class) &&
                (propertyValue == null || ((IImplementsNullFlavor)propertyValue).getNullFlavor() != null))
            {
                isValid = false;
                dtls.add(new MandatoryElementMissingResultDetail(ResultDetailType.ERROR, String.format("Property %s in %s is marked mandatory and is either not assigned, or is assigned a null flavor. This is not permitted.", pa.name(), o.getClass().getName()), locationPath, null));
            }
            else if (pa.conformance() == ConformanceType.POPULATED && propertyValue == null)
            {
                isValid &= this.getHost().getCreateRequiredElements();
                dtls.add(new RequiredElementMissingResultDetail(isValid ? ResultDetailType.WARNING : ResultDetailType.ERROR, String.format("Property %s in %s is marked 'populated' and isn't assigned (you must at minimum, assign a nullFlavor for this attribute)!", pa.name(), o.getClass().getName()), locationPath, null));
            }
            else if (pa.minOccurs() != 0 && FormatterUtil.hasInterface(actualReturnType, Collection.class))
            {
                Integer minOccurs = pa.minOccurs(), 
                    maxOccurs = pa.maxOccurs() < 0 ? Integer.MAX_VALUE : pa.maxOccurs();
                
                Collection<?> piCollection = (Collection<?>)propertyValue;
                if(piCollection.size() > maxOccurs || piCollection.size() < minOccurs)
                { 
                    isValid = false; 
                    dtls.add(new InsufficientRepetitionsResultDetail(ResultDetailType.ERROR, String.format("Property %s in %s does not have enough elements in the list, have %d elements, need between %d and %s elements!", pa.name(), o.getClass().getName(), piCollection.size(), minOccurs, maxOccurs == Integer.MAX_VALUE ? "infinite" : maxOccurs.toString()), locationPath, null));
                }
            }
        }

        return dtls;
	}
	
	/**
	 * Graph o onto xw returning the results in resultContext (with the overall code being returned)
	 * 
	 * Graphs according to XML ITS 1.0 rules using Java Reflection.
	 */
	@SuppressWarnings("unchecked")
	public void graph(XMLStateStreamWriter xw, Object o, FormatterElementContext context, XmlIts1FormatterGraphResult resultContext)
	{
	
		// Root instance
		FormatterElementContext rootContext = context.getRootContext();
		
		// Instance
		Class<?> instanceType = o.getClass();
		
		// Verify that the passed instance is not null
		if(o == null)
			throw new IllegalArgumentException();

		// Determine null flavor
		boolean isInstanceNull = false, 
				isEntryPoint = false;
		
		// Execution
		try {


			// Attempt to get the null flavor
			if(o instanceof IImplementsNullFlavor)
				isInstanceNull = ((IImplementsNullFlavor)o).getNullFlavor() != null;

			// Get structureAttribute
			Structure struct = instanceType.getAnnotation(Structure.class);
			if(struct != null && xw.getCurrentElement() == null)
			{
				isEntryPoint = true;
				// Write interaction name
				String renderName = struct.name();
				String namespaceUri = struct.namespaceUri(),
						pfx = xw.getPrefix(namespaceUri);

				if(!struct.isEntryPoint() && struct.structureType() != StructureType.INTERACTION)
				{
					// TODO: Output warning to result
					String str = o.getClass().getName();
					str = str.substring(str.lastIndexOf(".") + 1);
					xw.writeStartElement(pfx, str, namespaceUri);
				}
				else
					xw.writeStartElement(pfx, struct.name(), namespaceUri);

				xw.setDefaultNamespace(namespaceUri);
				xw.writeDefaultNamespace(namespaceUri);
				xw.setPrefix("xsi", XmlIts1Formatter.NS_XSI);
				xw.writeNamespace("xsi", XmlIts1Formatter.NS_XSI);

				// Write the ITS version if this is an interaction
				if(struct.structureType() == StructureType.INTERACTION)
					xw.writeAttribute("ITSVersion", "XML_1.0");
				
			}

			// Validate
			if(this.getHost().getValidateConformance())
			{
				Collection<IResultDetail> dtls = this.validate((IGraphable)o, context, xw.toString());
				resultContext.addResultDetail(dtls);
			}
			
			// Reflect the properties and ensure they are in the appropriate order
            List<Method> buildProperties = getBuildProperties(instanceType);
            
            // This is used because sometimes methods are already rendered
            // whenever they're overridden..
            List<String> alreadyRenderedMethods = new ArrayList<String>(); 
            
            // Now iterate through each of the properties and emit the property
            for(Method prop : buildProperties)
            {
            	
            	Object propertyValue =  prop.invoke(o); // get the value of the property
            	FormatterElementContext currentContext = new FormatterElementContext(propertyValue, prop, context);
            	
            	// Choice
            	if(propertyValue != null && currentContext.getPropertiesAnnotation() != null && o != null && !isInstanceNull)
            	{
            		for(Property candidateAtt : currentContext.getPropertiesAnnotation().value())
            			if(propertyValue.getClass().equals(candidateAtt.type()) && // The property class equals the property attribute class  
            					(rootContext.getOwnerClazz().equals(candidateAtt.interactionOwner()) || (candidateAtt.interactionOwner().equals(Object.class) && currentContext.getPropertyAnnotation() == null))) // either the interaction owner equals the interaction owner of the pa or there is no interaction owner and we haven't selected a value
            			{
            					currentContext.setPropertyAnnotation(candidateAtt);
            					if(context == null || rootContext.getOwnerClazz().equals(currentContext.getPropertyAnnotation().interactionOwner()))
            						break;
            			}
            			else if((candidateAtt.type().isAssignableFrom(propertyValue.getClass()) || candidateAtt.type().equals(Object.class)) && currentContext.getPropertyAnnotation() == null)
            				currentContext.setPropertyAnnotation(candidateAtt);
            			
            		
            	}

            	// Property attribute
            	if(currentContext.getPropertyAnnotation() == null)
            	{
            		resultContext.addResultDetail(new NotSupportedChoiceResultDetail(ResultDetailType.WARNING, String.format("Property %s does not have a serialization annotation and will not be included", prop.getName()))); 
            		continue;
            	}
            	else if(alreadyRenderedMethods.contains(currentContext.getPropertyAnnotation().name()))
            		continue; // HACK: Don't render the same attribute/element from two different method calls
            	
            	
            	
            	// Determine if we should add a null flavor
            	if (propertyValue == null &&
            			this.getHost().getCreateRequiredElements() &&
            			(currentContext.getConformance() == ConformanceType.REQUIRED || currentContext.getConformance() == ConformanceType.POPULATED) &&
            			currentContext.getPropertyType() != PropertyType.STRUCTURAL &&
            			FormatterUtil.hasInterface(prop.getReturnType(), IImplementsNullFlavor.class) &&
                        !Modifier.isAbstract(currentContext.getPropertyAnnotation().type().getModifiers()) &&
                        currentContext.getSetterMethod() != null
                        )
                    {
            			// If the instance is null and we want to create required elements then create it and set null flavor
            			Class<?> type = FormatterUtil.getClassForType(currentContext.getPropertyAnnotation().type(), currentContext);
            			if(type.equals(Object.class))
            				type = prop.getReturnType();
                        propertyValue = type.newInstance();
                        if(propertyValue instanceof IImplementsNullFlavor)
                        	((IImplementsNullFlavor)propertyValue).setNullFlavor(new CS<NullFlavor>(NullFlavor.NoInformation));
                    }

            	
            	// Emit the property to the wire
            	switch(currentContext.getPropertyType())
            	{
            		case STRUCTURAL:
            			if(propertyValue != null && !isInstanceNull)
            				xw.writeAttribute(currentContext.getPropertyAnnotation().name(), FormatterUtil.toWireFormat(propertyValue));
            			else if(isInstanceNull && currentContext.getPropertyAnnotation().name().equals("nullFlavor"))
            				this.getHost().writeNullFlavorUtil(xw, (IGraphable)propertyValue);
            			
            			// HACK: Java's getMethod() will return the same methods at different levels of the class
            			// heirarchy, so this will make sure we don't render the same attribute twice
            			if(propertyValue != null) 
            				alreadyRenderedMethods.add(currentContext.getPropertyAnnotation().name());
            			break;
            		default: // others (elements)
            			if(propertyValue == null || isInstanceNull)
            				continue; // No need to write elements because either the value is null or the hosting intance is null
            			
            			// HACK: Java's getMethod() will return the same methods at different levels of the class
            			// heirarchy, so this will make sure we don't render the same element from different methods
            			alreadyRenderedMethods.add(currentContext.getPropertyAnnotation().name());
            			
            			 // Impose flavors or code?
                        if (!currentContext.getPropertyAnnotation().imposeFlavorId().equals(Property.NULL) &&
                            propertyValue instanceof ANY)
                            ((ANY)propertyValue).setFlavorId(currentContext.getPropertyAnnotation().imposeFlavorId());
                        if (!currentContext.getPropertyAnnotation().supplierDomain().equals(Property.NULL) &&
                            propertyValue instanceof ICodedValue &&
                            ((ICodedSimple)propertyValue).getCode() != null &&
                            ((ICodedValue)propertyValue).getCodeSystem() == null &&
                            !((IAny)propertyValue).isNull())
                            ((ICodedValue)propertyValue).setCodeSystem(currentContext.getPropertyAnnotation().supplierDomain());
            			
            			
            			// Graph
            			if(propertyValue instanceof IGraphable)
            			{
            				if((propertyValue instanceof ICollection<?>) && ((ICollection<?>)propertyValue).isEmpty())
            					continue; // nothing to write
            				Class<?> intendedType = FormatterUtil.getClassForType(currentContext.getPropertyAnnotation().type(), currentContext);
            				if(intendedType.equals(Object.class))
            					intendedType = FormatterUtil.getClassForType(prop.getGenericReturnType(), context);
            				this.getHost().writeElementUtil(xw, currentContext.getPropertyAnnotation().name(), (IGraphable)propertyValue, intendedType, currentContext, resultContext);
            			}
            			else if(propertyValue instanceof Iterable<?>) // Not IGraphable but iterable.. maybe format each of the child elements
            			{
            				// Get the intended generic type
            				Type genType = prop.getGenericReturnType();
            				if(!(genType instanceof ParameterizedType))
            					resultContext.addResultDetail(new NotImplementedResultDetail(ResultDetailType.ERROR, "Cannot format Iterable type with no generic definition", xw.toString(), null));
            				else
            				{
            					ParameterizedType pType = (ParameterizedType)genType;
            					genType = FormatterUtil.getClassForType(pType.getActualTypeArguments()[0], context); // get the first generic arg
            				}
            						
            				// Can we infer the generic type from the property attribute?
            				if(genType.equals(Object.class) && currentContext.getPropertyAnnotation().genericSupplier().length > 0)
            					genType = currentContext.getPropertyAnnotation().genericSupplier()[0];
            				
            				// Because we don't want the "current context" to be "ArrayList"
            				// we'll set the owner class instance to the real type
            				if(!genType.equals(Object.class))
            					currentContext.setOwnerClazz(FormatterUtil.getClassForType(genType));
            				
            				for(Object collValue : (Iterable<?>)propertyValue)
            				{
            					if(genType == null) // HACK: Java erases generic and the property attribute didn't have any info
            					{
            						genType = collValue.getClass();
            						currentContext.setOwnerClazz((Class<?>)genType);
            					}
            					this.getHost().writeElementUtil(xw, currentContext.getPropertyAnnotation().name(), (IGraphable)collValue, genType, currentContext, resultContext);
            				}
            			}
            			else
            			{
            				xw.writeStartElement("hl7", currentContext.getPropertyAnnotation().name(), XmlIts1Formatter.NS_HL7);
            				xw.writeCharacters(propertyValue.toString());
            				xw.writeEndElement();
            			}
            			break;
            	} // switch
            }
            
            // Output the end element
            if(isEntryPoint)
            	xw.writeEndElement(); // the opening elements
		} catch (Exception e) {
			e.printStackTrace();
			resultContext.addResultDetail(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), xw.toString(), e));
		}

	}

	/**
	 * Determine if the specified method is already listed in the list of methods
	 */
	private boolean isPropertyAlreadyNoted(Method m, List<Method> methods)
	{
		boolean retVal = false;
		for(Method mthd : methods)
			retVal |= mthd.getName().equals(m.getName());
		return retVal;
	}
	/**
	 * Determine if the method is of a particular type of property
	 */
	private boolean isOfPropertyType(Method m, PropertyType type)
	{
		Property propertyAnnotation = m.getAnnotation(Property.class);
		Properties propertiesAnnotation = m.getAnnotation(Properties.class);
		
		if(propertyAnnotation != null)
			return propertyAnnotation.propertyType().equals(type);
		else if(propertiesAnnotation != null)
			return propertiesAnnotation.value()[0].propertyType().equals(type);
		return false;
	}
	
	/**
	 * Build a list of properties that are sorted such that they can be rendered to an 
	 * XML stream without violating XML rules or ITS rules. These are:
	 * 
	 * <ol>
	 *  <li>Structural (attribute) properties are rendered first (after the start element is opened</li>
	 *  <li>Non Structural (element) properties are rendered as elements in sequence ordered by their sort key from the super most class down.</li>
	 *  <li>Traversable associations are rendered as elements</li>
	 * </ol>
	 * 
	 * <p>For example, consider a class hierarchy of:</p>
	 *  	<ul>
	 *  	<li>class a<ul>
	 *  		<li>A() // Structural - SortKey = 1</li>
	 *  		<li>B() // Structural - SortKey = 2</li>
	 *  		<li>C() // NonStructural - SortKey = 3</li>
	 *  		<li>D() // Traversable - SortKey = 4</li>
	 *  	</ul></li>
	 *  	<li>class b extends a<ul>
	 *  		<li>E() // NonStructural - SortKey = 1</li>
	 *  		<li>F() // Traversable - SortKey = 2</li>
	 *  	</ul></li>
	 *  </ul>
	 *  
	 *  <p>This will result in an XML output of:</p>
	 *  <pre>
	 *  	&lt;b A="" B="">
	 *  		&lt;C/>
	 *  		&lt;E/>
	 *  		&lt;D/>
	 *  		&lt;F/>
	 *  	&lt;/b>
	 *  </pre>
	 */
	private synchronized List<Method> getBuildProperties(Class<?> instanceType) {
		
		// See if there are any cached items as this is an expensive operation
		if(this.m_cachedBuildProperties.containsKey(instanceType))
			return this.m_cachedBuildProperties.get(instanceType);
		
		// Arrays of items representing the property types
		// Structural, NonStructural and Traversable Associations
		// These will be used to ensure that the return array is ordered correctly
		// such that XML instances will 
		List<Method> structural = new ArrayList<Method>(10),
				nonStructural = new ArrayList<Method>(10),
				traversable = new ArrayList<Method>(10),
				retVal = new ArrayList<Method>();
		
		Class<?> currentType = instanceType;
		if(currentType.getAnnotation(Interaction.class) != null) // HACK: Don't to this as the properties will not be sorted properly
			currentType = instanceType.getSuperclass();
		
		while(!currentType.equals(Object.class))
		{

		
			// Find all methods with property annotation
			Method[] thisMethods = currentType.getDeclaredMethods();
			
			// Sort the array of methods by the sort key
			Arrays.sort(thisMethods, new Comparator<Method>() {
	
				@Override
				public int compare(Method a, Method b) {
					Property propertyAnnotation = a.getAnnotation(Property.class);
					Properties propertiesAnnotation = a.getAnnotation(Properties.class);
					
					// Sort key for a
					Integer aSortKey = 0; 
					if(propertyAnnotation != null)
						aSortKey = propertyAnnotation.sortKey();
					else if(propertiesAnnotation != null)
						aSortKey = propertiesAnnotation.value()[0].sortKey();
					
					propertyAnnotation = b.getAnnotation(Property.class);
					propertiesAnnotation = b.getAnnotation(Properties.class);
					
					// Sort key for b
					Integer bSortKey = 0; 
					if(propertyAnnotation != null)
						bSortKey = propertyAnnotation.sortKey();
					else if(propertiesAnnotation != null)
						bSortKey = propertiesAnnotation.value()[0].sortKey();
	
					// Compare the two
					// You might see the reverse sort order and ask yourself "why?"
					// No, it's not because of the time (12:30 AM), or the beer (haven't had any yet)
					// it is because as we iterate through the type hierarchy we have to insert
					// the items as the first index of each of the arrays for structural, nonStructural
					// and traversable objects so that we reproduce the order that is expected by the XSD 
					// classes.
					return bSortKey.compareTo(aSortKey); 
				}
				
			});
			
			// Iterate through methods and prepare the output arrays
			for(Method meth : thisMethods)
			{
				Property propertyAnnotation = meth.getAnnotation(Property.class);
				Properties propertiesAnnotation = meth.getAnnotation(Properties.class);
				if(propertyAnnotation == null && propertiesAnnotation == null)
					continue; // No bother adding useless methods
				else if(this.isOfPropertyType(meth, PropertyType.TRAVERSABLEASSOCIATION) && !this.isPropertyAlreadyNoted(meth, traversable))
					traversable.add(0,meth);
				else if(this.isOfPropertyType(meth, PropertyType.NONSTRUCTURAL) && !this.isPropertyAlreadyNoted(meth, nonStructural))
					nonStructural.add(0,meth);
				else if(this.isOfPropertyType(meth, PropertyType.STRUCTURAL) && !this.isPropertyAlreadyNoted(meth, structural))
					structural.add(0,meth);
			}
			currentType = currentType.getSuperclass();
		}
		retVal.addAll(structural);
		retVal.addAll(nonStructural);
		retVal.addAll(traversable);
		
		synchronized (this.m_cachedBuildProperties) {
			this.m_cachedBuildProperties.put(instanceType, retVal);
		}
		
		return retVal;
	}
	
	/**
	 * Parse an object from the specified XML reader
	 */
	public IGraphable parse(XMLStreamReader xr, FormatterElementContext ctx, XmlIts1FormatterParseResult resultContext) {
		
	
		// Namespace URI is not HL7, so don't process
		if(!xr.getNamespaceURI().equals(XmlIts1Formatter.NS_HL7))
		{
			resultContext.addResultDetail(new NotImplementedElementResultDetail(ResultDetailType.WARNING, xr.getLocalName(), xr.getNamespaceURI(), xr.toString(), null));
			return null;
		}
		
		// Construct the object
		Class<?> actualType = FormatterUtil.getClassForType(ctx.getOwnerClazz(), ctx);

		// HACK: Overcome the List interface by getting the actual type
		if(actualType.isInterface() && actualType.equals(List.class))
			actualType = ArrayList.class;
		
		IGraphable retVal = null;
		try {
			retVal = (IGraphable)actualType.newInstance();
		} catch (Exception e) {
			throw new FormatterException(String.format("Could not instantiate %s", actualType.getName()), e);
		}

		// Get all properties for the type
		List<Method> properties = this.getBuildProperties(actualType);
		
		// Iterate through the attributes on the current element before advancing the stream
		for(int attNo = 0; attNo < xr.getAttributeCount(); attNo++)
		{
			// Because the Java environment won't report attributes in error
			String currentLocation = xr.toString() + "/@" + xr.getAttributeLocalName(attNo);
			
			if(xr.getAttributeLocalName(attNo).equals("ITSVersion") && !xr.getAttributeValue(attNo).equals("XML_1.0"))
				throw new FormatterException("This formatter can only process XML ITS 1.0 instances");
			else if(xr.getAttributePrefix(attNo) != null && xr.getAttributePrefix(attNo).equals("xmlns") || 
					xr.getAttributeLocalName(attNo).equals("xmlns") || 
					xr.getAttributeLocalName(attNo).equals("ITSVersion"))
				continue; // ignore XMLNS namespace declarations
			
			
			FormatterElementContext childContext = ctx.findChildContextFromName(xr.getAttributeLocalName(attNo), PropertyType.STRUCTURAL);
			// No property?
			if(childContext == null)
			{
				resultContext.addResultDetail(new NotImplementedElementResultDetail(ResultDetailType.WARNING, "@" + xr.getAttributeLocalName(attNo), xr.getAttributeNamespace(attNo), currentLocation, null));
				continue;
			}
			
			// Keep a local copy of the setter method (improves performance)
			Method setterMethod = childContext.getSetterMethod();
			
			if(!childContext.getPropertyAnnotation().fixedValue().equals(Property.NULL) && !childContext.getPropertyAnnotation().fixedValue().equals(xr.getAttributeValue(attNo)))
				resultContext.addResultDetail(new FixedValueMisMatchedResultDetail(xr.getAttributeValue(attNo), childContext.getPropertyAnnotation().fixedValue(), true, currentLocation));
			if(setterMethod != null)
				try {
					
					Type argType = childContext.getGetterMethod().getGenericReturnType();
					
					if(argType instanceof TypeVariable<?>)
						argType = childContext.getActualTypeArgument((TypeVariable<?>)argType);
					
					Object value = FormatterUtil.fromWireFormat(xr.getAttributeValue(attNo), argType);
					
					// Setter method matches?
					setterMethod.invoke(retVal, value);
				} catch (Exception e) {
					throw new FormatterException(String.format("Could not set value on attribute '%s' at %s", xr.getAttributeLocalName(attNo), currentLocation), e);
				}
		}
			
		// Is the instance null?
		String nil = xr.getAttributeValue(XmlIts1Formatter.NS_XSI, "nil");
		if(nil != null && DatatypeConverter.parseBoolean(nil))
			return retVal;
		
		// Capture the current element name so we know when the end has hit
		String currentElementName = xr.getLocalName(),
				lastElementRead = currentElementName;
		int currentDepth = 0;
		try {
			while(true)
			{

				// If we're on characters then do some logic
				if(xr.getEventType() == XMLStreamReader.START_ELEMENT || xr.getEventType() == XMLStreamReader.END_ELEMENT)
				{
					// Should we consume the next element?
					if(lastElementRead.equals(xr.getLocalName()) && !xr.hasNext())
						break;
					else if(lastElementRead.equals(xr.getLocalName()))
						xr.next();
				}
				else
					xr.next(); // read the next element
				
				int type = xr.getEventType();
				if(type == XMLStreamReader.START_ELEMENT || type == XMLStreamReader.END_ELEMENT)
					lastElementRead = xr.getLocalName();

				
				// Element is an end element and the name matches the starting element and it is at the same leve
				if(type == XMLStreamReader.CHARACTERS || type == XMLStreamReader.COMMENT || type == XMLStreamReader.CDATA)
				{
					//resultContext.addResultDetail(new NotImplementedResultDetail(ResultDetailType.WARNING, "Cannot process entity COMMENT, CHARACTERS, or CDATA in this context"));
				}
				else if(type == XMLStreamReader.END_ELEMENT && xr.getLocalName().equals(currentElementName) && currentDepth == 0)
						break;
					else if(type == XMLStreamReader.START_ELEMENT)
					{
						// Find a context for this item
						FormatterElementContext childContext = ctx.findChildContextFromName(xr.getLocalName(), PropertyType.NONSTRUCTURAL);
						if(childContext == null)
							childContext = ctx.findChildContextFromName(xr.getLocalName(), PropertyType.TRAVERSABLEASSOCIATION);

						// Can't serialize this
						if(childContext == null || 
								childContext.getPropertyAnnotation() != null &&
								!xr.getNamespaceURI().equals(childContext.getPropertyAnnotation().namespaceUri()))
						{
							childContext = ctx.findChildContextFromName(xr.getLocalName(), PropertyType.TRAVERSABLEASSOCIATION);
							resultContext.addResultDetail(new NotImplementedElementResultDetail(ResultDetailType.WARNING, xr.getLocalName(), xr.getNamespaceURI(), xr.toString(), null));
							continue;
						}
						// Keep a local copy of the setter method (improves performance)
						Method setterMethod = childContext.getSetterMethod();

						// Can't set this
						if(setterMethod == null)
						{
							resultContext.addResultDetail(new ResultDetail(ResultDetailType.ERROR, String.format("Can't set element '%s'", xr.getLocalName()), xr.toString(), null));
							continue;
						}

						
						// Parse if is IGraphable
						if(FormatterUtil.hasInterface(childContext.getOwnerClazz(), IGraphable.class))
						{
							
							Object tValue = this.getHost().parseObjectInternal(xr, childContext, resultContext);
							// Verify
							if (!childContext.getPropertyAnnotation().fixedValue().equals(Property.NULL) && !childContext.getPropertyAnnotation().fixedValue().equals(FormatterUtil.toWireFormat(tValue)) && childContext.getPropertyAnnotation().propertyType() != PropertyType.TRAVERSABLEASSOCIATION)
	                            resultContext.addResultDetail(new FixedValueMisMatchedResultDetail(FormatterUtil.toWireFormat(tValue), childContext.getPropertyAnnotation().fixedValue(), xr.toString()));
	                        
							// Invoke the setter
							try {
								if(!setterMethod.getParameterTypes()[0].isAssignableFrom(tValue.getClass()))
									tValue = FormatterUtil.fromWireFormat(tValue, setterMethod.getParameterTypes()[0]);
								setterMethod.invoke(retVal, tValue);
							} catch (Exception e) {
								resultContext.addResultDetail(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), xr.toString(), e));
							} 
						}
						else if(FormatterUtil.hasInterface(childContext.getOwnerClazz(), Collection.class))
						{
							// Create a real context which pretends to be the generic type
							// Get the argument type
							Type argType = childContext.getGetterMethod().getGenericReturnType();
							if(argType instanceof ParameterizedType)
								argType = ((ParameterizedType)argType).getActualTypeArguments()[0];
							
							// Create a fake child context
							FormatterElementContext fakeChildContext = new FormatterElementContext(FormatterUtil.getClassForType(argType, childContext), childContext.getGetterMethod()); 
							fakeChildContext.setParentContext(childContext);
							// Now attempt to add
							Object tValue = this.getHost().parseObjectInternal(xr, fakeChildContext, resultContext);
							
							// Get the value
							((Collection)childContext.getGetterMethod().invoke(retVal)).add(tValue);
						}
						
						
					}
			}
		} catch (XMLStreamException e) {
			throw new FormatterException(e.getMessage(), e);
		}
		catch(Exception e)
		{
			resultContext.addResultDetail(new ResultDetail(ResultDetailType.ERROR, e.getMessage(), xr.toString(), e));
		}
		
		return retVal;
	}
}
