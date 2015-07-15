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
 * Date: 09-25-2012
 */
package org.marc.everest.formatters;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.marc.everest.annotations.Flavor;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.annotations.TypeMap;
import org.marc.everest.annotations.TypeMaps;
import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.generic.PIVL;
import org.marc.everest.datatypes.interfaces.ICodedSimple;
import org.marc.everest.exceptions.FormatterException;
import org.marc.everest.exceptions.VocabularyException;
import org.marc.everest.interfaces.IEnumeratedVocabulary;
import org.marc.everest.util.ClassEnumerator;
import org.marc.everest.xml.XMLStateStreamWriter;


/**
 * A utility class that is used by formatters to perform wire-level tasks
 */
public class FormatterUtil {

	// Wire mapping shortcut
	private static HashMap<String, Method> s_wireMaps = new HashMap<String, Method>();
	// Flavor validators
	private static HashMap<String, Method> s_flavorValidators = new HashMap<String, Method>();
	// Setter methods
	private static HashMap<Class<?>, HashMap<String, Method>> s_setMethods = new HashMap<Class<?>, HashMap<String,Method>>();
	
	/**
	 * Returns true when the class implements an interface
	 */
	public static boolean hasInterface(Class<?> clazz, Class<?> iface)
	{
		for(Object i : clazz.getInterfaces())
			if(i.equals(iface))
				return true;
		if(clazz.getSuperclass() == null || clazz.getSuperclass().equals(Object.class))
			return false;
		return hasInterface(clazz.getSuperclass(), iface);
	}
	
	/**
	 * Represents value in a wire-format friendly way as specified by the datatypes
	 * implementation guide
	 */
	public static String toWireFormat(Object value)
	{
		if(value == null) // No format needed!
			return null;
		
		if(value instanceof IEnumeratedVocabulary)
			return ((IEnumeratedVocabulary)value).getCode();
		else if(value instanceof ICodedSimple)
			return toWireFormat(((ICodedSimple)value).getCode());
		else if(value instanceof Iterable<?>)
		{
			StringBuilder retVal = new StringBuilder();
			for(Object member : (Iterable<?>)value)
			{
				retVal.append(toWireFormat(member));
				retVal.append(" ");
			}
			
			if(retVal.length() > 0)
				retVal.replace(retVal.length() - 1, retVal.length(), "");
			return retVal.toString();
				
		}
		
		return value.toString();
	}
	
	/**
	 * Create an object from wire format
	 */
	public static <T> T fromWireFormat(Object value, Class<T> destType)
	{
		try
		{
			return (T)fromWireFormat(value, destType, true);
		}
		catch(FormatterException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Create an object from wire format
	 */
	public static Object fromWireFormat(Object value, Type destType)
	{
		try
		{
			return fromWireFormat(value, destType, true);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Create an object from wire format representation
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static Object fromWireFormat(Object value, Type rawDestType, boolean throwOnError) throws FormatterException
	{
		
		// Destination type
		Class<?> destType = FormatterUtil.getClassForType(rawDestType);
		Structure destTypeStrucutre = destType.getAnnotation(Structure.class);
		
		if(value == null)
			return null; // cannot convert a null value
		
		// Determine if we can just assign it
		if(destType.isAssignableFrom(value.getClass()) || destType.isInstance(value))
			return value;
		else if(IEnumeratedVocabulary.class.isAssignableFrom(destType)) // enumerated vocab, see if we can do this
		{
			// Is an enumeration?
			if(destType.isEnum())
			{
				for(Object ev : destType.getEnumConstants())
					if(((IEnumeratedVocabulary)ev).getCode().equals(value))
						return ev;
				throw new VocabularyException(String.format("Can't find value %s in domain %s", value, destTypeStrucutre == null ? destType.getName() : destTypeStrucutre.name()), value.toString(), destTypeStrucutre == null ? destType.getName() : destTypeStrucutre.name(), null);
			}
			else
			{
				try {
					// Try to find an existing defined enum which can do this
					for(Field fld : destType.getFields())
						if(Modifier.isStatic(fld.getModifiers()) && ((IEnumeratedVocabulary)fld.get(null)).getCode().equals(value))
							return fld.get(null);
				
					// We need to construct a new instance .. somehow
					Constructor<?> ctor = destType.getConstructor(String.class, String.class);
					return ctor.newInstance(value, null);
				}
				catch(Exception e)
				{
					throw new FormatterException(String.format("Can't find valid conversion to from '%s$s' to '%s$s'", value.getClass(), destType), e);
				}
			}
		}
		
		// Attempt to get a method that can convert from/to
		String converterName = String.format("%s$s > %s$s", value.getClass().getName(), rawDestType.toString());
		Method mi = s_wireMaps.get(converterName);
		if(mi == null)
		{
			mi = findConverter(destType, value.getClass(), rawDestType); // Look for a converter on the destType
			if(mi == null) // Look for a converter on the source type
				mi = findConverter(value.getClass(), value.getClass(), rawDestType);
			if(mi == null) // Look on the convert class
				mi = findConverter(Convert.class, value.getClass(), rawDestType);
			if(mi == null) // Look on the convert class
				mi = findConverter(DatatypeConverter.class, value.getClass(), rawDestType);
			
			if(mi != null)
				synchronized (s_wireMaps) {
					if(!s_wireMaps.containsKey(converterName))
					s_wireMaps.put(converterName, mi);
				}
			else // uh-oh, what to do here? I don't know
				;
		}
		
		if(mi != null)
			try {

				if(mi.getParameterTypes().length >= 2)
				{
				
					// Second parameter MUST be generic type
					Class<?> parameterType = FormatterUtil.getClassForType(((ParameterizedType)rawDestType).getActualTypeArguments()[0]);
					if(mi.getParameterTypes().length == 3)
						return mi.invoke(null, value, parameterType, rawDestType);
					else
						return mi.invoke(null, value, parameterType);
				}
				else
					return mi.invoke(null, value);
			} 
			catch (Exception e) {
				throw new FormatterException(String.format("Can't find valid conversion to from '%s' to '%s'", value.getClass(), destType), e);
			}
		
		
		// Can't convert so throw
		throw new FormatterException(String.format("Can't find valid conversion to from '%s' to '%s'", value.getClass(), destType));
	}
	
	/**
	 * Find converter method (static) in scanType
	 * that can convert from sourceType to destType
	 */
	private static Method findConverter(Class<?> scanType, Type sourceType, Type rawDestType)
	{
		
		Method retVal = null;
		synchronized (scanType) {
			for(Method method : scanType.getMethods())
			{
				// Not public or not static
				if(!Modifier.isPublic(method.getModifiers()) || !Modifier.isStatic(method.getModifiers()))
					continue;
				// If the method accepts one parameter and 
				// either the return type matches destType or the destType is assignable from the return type and
				// the parameter to the method matches the source type
				Class<?> destType = FormatterUtil.getClassForType(rawDestType);
				
				if(rawDestType instanceof ParameterizedType &&
						method.getParameterTypes().length == 3 &&
						method.getParameterTypes()[0].equals(sourceType) &&
						method.getParameterTypes()[1].equals(Class.class) &&
						method.getParameterTypes()[2].equals(Type.class) &&
						destType.isAssignableFrom(method.getReturnType()))
					retVal = method;
				else if(rawDestType instanceof ParameterizedType &&
						method.getParameterTypes().length == 2 &&
						method.getParameterTypes()[0].equals(sourceType) &&
						method.getParameterTypes()[1].equals(Class.class) &&
						destType.isAssignableFrom(method.getReturnType()))
					retVal = method;
				else if(method.getParameterTypes().length == 1 &&
					method.getParameterTypes()[0].equals(sourceType) &&
					destType.isAssignableFrom(method.getReturnType()) && 
					retVal == null)
					retVal =  method;
			}
			return retVal;
		}
	}
		
	/**
	 * Generate an XSI type name for the specified class
	 */
	public static String createXsiTypeName(Object instance)
	{
		// Create an xsi type with pointer back to this method
		return createXsiTypeName(instance, new IXsiTypeNameGenerator() {
			
			/** Generate type name (callback to this method)*/
			@Override
			public String createXsiTypeName(Object instance) {
				
				return FormatterUtil.createXsiTypeName(instance);
			}
		});
	}
	
	
	/**
	 * Create an XSI type using the specified callback for creating sub-type names
	 */
	public static String createXsiTypeName(Object instance, IXsiTypeNameGenerator subTypeNameGenerator)
	{
		// Get the type from the instance class
		Class<?> type = instance.getClass();
		StringBuilder xsiTypeName = new StringBuilder();
		
		// Is this a generic?
		if(type.getTypeParameters().length != 0)
		{
			TypeVariable<?>[] genType = type.getTypeParameters();
			
			// Class definition
			Structure struct = (Structure)type.getAnnotation(Structure.class);
			
			if(struct == null || struct.structureType() != StructureType.DATATYPE)
				return "";
			
			if(struct != null)
				xsiTypeName.append(struct.name());
			xsiTypeName.append("_");
			
			for(TypeVariable<?> t : genType)
            {
				// Attempt to infer the actual type because Java is a real winner and erases the actual type
				Object inferInstance = null;  
				for(Method m : type.getMethods())
					try
					{
						if(m.getGenericReturnType().toString().equals(t.getName()) &&
								(m.getName().startsWith("get") || 
								m.getAnnotation(Property.class) != null))
						{
							if(m.getParameterTypes().length == 1) // HACK: For collections
								inferInstance = m.invoke(instance, 0);
							else
								inferInstance = m.invoke(instance);
							if(inferInstance != null) break;
						}
					}
					catch(Exception e)
					{
						
					}
				if(inferInstance != null)
				{
					String subXsiType = subTypeNameGenerator.createXsiTypeName(inferInstance);
					if(subXsiType != null && !subXsiType.equals(""))
					{
						xsiTypeName.append(subXsiType);
						xsiTypeName.append("_");
					}
				}
            }
			
			xsiTypeName.replace(xsiTypeName.length() - 1, xsiTypeName.length(), "");
            // xsiTypeName = xsiTypeName.Remove(xsiTypeName.Length - 1, 1);
		}
		else
		{
			Structure struct = (Structure)type.getAnnotation(Structure.class);
			
			if(struct == null || struct.structureType() != StructureType.DATATYPE)
				return "";
			
			if(struct != null)
				xsiTypeName.append(struct.name());
		}
		return xsiTypeName.toString();
	}
	
	/**
	 * Parse an XSI type name
	 */
	public static Type parseXsiTypeName(String xsiType)
	{

		List<String> typeNames = new ArrayList<String>(Arrays.asList(xsiType.split("_"))); 
		Type retVal = parseXSITypeNameInternal(typeNames);
		if(typeNames.size() > 0)
			throw new IllegalStateException("Generic parameter supplied to a non-generic type");
		return retVal;
	}
	
	/**
	 * Parse an XSI Type name
	 */
	private static Type parseXSITypeNameInternal(List<String> typeNames) {
		
		// Get the type
		String typeName = typeNames.get(0);
		typeNames.remove(0);
		Type retVal = null;
		
		// Look for class
		Class<?> cClass = null;
		
		// Get all classes in this package
		BufferedReader reader = new BufferedReader(new InputStreamReader(FormatterUtil.class.getResourceAsStream("/DatatypeClasses")));
		String className = null,
				packageName = "org.marc.everest.datatypes";
		try
		{
		while ((className = reader.readLine()) != null)
		{
			// Scan the datatypes classes
			try {
				Class<?> cls= Class.forName(String.format("%s.%s", packageName, className));
				Structure struct = cls.getAnnotation(Structure.class);
				if(struct != null && struct.name().equals(typeName))
				{
					
					// are there type maps ?
					// Do they match the supplied generic, as this will ensure we get the 
					// mapped type instead of the supplied type name.
					TypeMaps tmaps = cls.getAnnotation(TypeMaps.class);
					if(tmaps != null)
					{
						for(TypeMap tma : tmaps.value())
							if(tma.name().equals(typeName) && tma.annotationType().equals(TypeMap.NULL) ^ tma.argumentType().equals(typeNames.get(0)))
								cClass = cls;
					}
					else
						cClass = cls;
				}
				
				// Found a class so break
				if(cClass != null)
					break;
			} catch (Exception e) {
				; // ignore exceptions
			}
		}
		}
		catch(Exception e)
		{; // ignore
		}
		
		// Couldn't find class?
		if(cClass == null)
			throw new IllegalStateException(String.format("Couldn't find type %s", typeName));
		
		// Generic class
		if(cClass.getTypeParameters().length > 0)
		{
			retVal = new XsiType(cClass);
			// Construct type parameters (default)
			if(typeNames.size() == 0)
			{
				Structure structureAttribute = cClass.getAnnotation(Structure.class);
				if(structureAttribute != null)
					for(int i = 0; i < cClass.getTypeParameters().length; i++)
						((XsiType)retVal).getTypeArguments().add(structureAttribute.defaultTemplateType());
				else
						throw new IllegalStateException("Should not be here, generic bound to non Structure");
			}
			else // recurse
				for(int i = 0; i < cClass.getTypeParameters().length; i++)
					((XsiType)retVal).getTypeArguments().add(parseXSITypeNameInternal(typeNames));
		}
		else
			retVal = cClass; // Return the class ... 
		
		return retVal;
		
	}

	/**
	 * Gets the setter name of a property given the name of the getter
	 */
	public static Method getSetterMethod(String getterName, Class<?> returnType, Class<?> containingClass)
	{
		// Has this class been scanned before?
		HashMap<String, Method> cachedSetters = s_setMethods.get(containingClass);
		
		// yes?
		if(cachedSetters != null && cachedSetters.containsKey(getterName))
			return cachedSetters.get(getterName);
		else
			synchronized (s_setMethods) {
				cachedSetters = new HashMap<String, Method>();
				if(!s_setMethods.containsKey(containingClass))
					s_setMethods.put(containingClass, cachedSetters);	
			}
			
		// The more expensive operations
		if(!getterName.startsWith("get"))
			return null; // cannot get for a non getter
		String propertyName = getterName.substring(3);
		
		Method rv = null;
		try
		{
			rv = containingClass.getMethod(String.format("set%s", propertyName), returnType);
		}
		catch(NoSuchMethodException e)
		{
			try
			{
				rv = containingClass.getMethod(String.format("override%s", propertyName), returnType);
			}
			catch(NoSuchMethodException e1)
			{
				rv = null;
			}
		}
		
		// register
		synchronized (cachedSetters) {
			cachedSetters.put(getterName, rv);
		}

		return rv;
		/*
		String setMethod = getterName.replaceAll("get(.*)", "set$1"),
				overrideMethod = getterName.replaceAll("get(.*)", "override$1");
		Method retVal;
		try {
			retVal = containingClass.getMethod(setMethod, returnType);
			return retVal;
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
		try {
			retVal = containingClass.getMethod(overrideMethod, returnType);
			return retVal;
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
		return null;*/
	}
	
	/**
	 * Get runtime class for the specified type class checking the context for substitutions
	 */
	public static Class<?> getClassForType(Type type, FormatterElementContext context) {
		
		if(type instanceof TypeVariable<?>)
	    {
	    	return getClassForType(context.getActualTypeArgument((TypeVariable<?>)type), context);
	    }
		else
			return getClassForType(type);
	}
	
	/**
	 * Get runtime class for the specified type class
	 */
	public static Class<?> getClassForType(Type type) {

		if (type instanceof Class) {
		      return (Class<?>) type;
	    }
		else if (type instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType)type;
			
			// Save generic arguments bindings 
			/*if(pType.getRawType() instanceof Class)
			{
				TypeVariable<?>[] typeParms = ((Class<?>)pType.getRawType()).getTypeParameters();
				for(int i = 0; i < typeParms.length; i++)
					context.addActualTypeArgument(typeParms[i], pType.getActualTypeArguments()[i]);
			}*/
			return getClassForType(pType.getRawType());
	    }
	    else if (type instanceof GenericArrayType) {
	      Type componentType = ((GenericArrayType) type).getGenericComponentType();
	      Class<?> componentClass = getClassForType(componentType);
	      if (componentClass != null ) {
	        return Array.newInstance(componentClass, 0).getClass();
	      }
	      else {
	        return null;
	      }
	    }
	    else if(type instanceof XsiType)
	    	return ((XsiType)type).getUnderlyingClazz();
	    /*else if(type instanceof TypeVariable<?>)
	    {
	    	return getClassForType(context.getActualTypeArgument((TypeVariable<?>)type), context);
	    }*/
	    else {
	      return null;
	    }
	}

	/**
	 * Validate results
	 */
	public static boolean validateFlavor(String flavor, Object instance) {
        
        // JF - Makes this much faster
        Method validatorMi = findFlavorValidator(flavor);
        if (validatorMi != null)
			try {
				return (Boolean)validatorMi.invoke(null, instance);
			} catch (Exception e) {
				return false;
			}

        return true; // couldn't find flavor
	}

	/**
	 * Find a flavor validator
	 */
	private static Method findFlavorValidator(String flavor) {

		// Has there been any flavor validators loaded?
		if(s_flavorValidators.size() == 0)
			synchronized (s_flavorValidators) {
				if(s_flavorValidators.size() == 0)
				{
					for(Class<?> clazz : ClassEnumerator.loadClassesInPackage(II.class))
					{
						if(clazz.getAnnotation(Structure.class) != null)
							for(Method meth : clazz.getMethods())
								if(Modifier.isStatic(meth.getModifiers()) &&
										meth.getParameterTypes().length == 1 &&
										meth.getReturnType().equals(Boolean.class) &&
										meth.getAnnotation(Flavor.class) != null
									)
								{
									s_flavorValidators.put(meth.getAnnotation(Flavor.class).name(), meth);
								}
					}
				}
			}
		return s_flavorValidators.get(flavor);
	}

	/**
	 * Read raw XML input
	 * @param s
	 * @return
	 */
	public static String readRawXmlInput(XMLStreamReader xr) {
		StringWriter bos = new StringWriter();
		try {
			int sDepth = 0;
			boolean firstElement = true;
			XMLStreamWriter xw = new XMLStateStreamWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(bos));
			// Read (first element)
			
			while(xr.hasNext() && !(xr.getEventType() == XMLStreamReader.END_ELEMENT && sDepth == 0))
			{
				switch(xr.getEventType())
				{
					case XMLStreamReader.START_ELEMENT:
						sDepth++;
						xw.writeStartElement(xr.getPrefix(), xr.getLocalName(), xr.getNamespaceURI());
						for(int i = 0; i < xr.getAttributeCount(); i++)
							xw.writeAttribute(xr.getAttributePrefix(i), xr.getAttributeNamespace(i), xr.getAttributeLocalName(i), xr.getAttributeValue(i));
						for(int i = 0; i < xr.getNamespaceCount(); i++)
							xw.writeNamespace(xr.getNamespacePrefix(i), xr.getNamespaceURI(i));
						break;
					case XMLStreamReader.END_ELEMENT:
						sDepth--;
						xw.writeEndElement();
						break;
					case XMLStreamReader.CDATA:
						xw.writeCData(xr.getText());
						break;
					case XMLStreamReader.CHARACTERS:
						xw.writeCharacters(xr.getText());
						break;
					case XMLStreamReader.COMMENT:
						xw.writeComment(xr.getText());
						break;
				}
				if(xr.hasNext())
					xr.next();
			}
			
			return bos.toString();
		} catch (Exception e) {
			throw new FormatterException("Can't output raw data", e);
		}
		
	}
	
	/**
	 * Write RAW Xml output
	 */
	public static void writeRawXmlOutput(byte[] data, XMLStreamWriter s) {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		try {
			XMLStreamReader xr = XMLInputFactory.newInstance().createXMLStreamReader(bis);
			
			// Validate
			while(xr.hasNext()) xr.next(); // Forces validation of well-formedness before we write
			xr.close();
			bis.reset();
			xr = XMLInputFactory.newInstance().createXMLStreamReader(bis);
			
			boolean firstElement = true;
			// Read
			while(xr.hasNext())
			{
				switch(xr.next())
				{
					case XMLStreamReader.START_ELEMENT:
						s.writeStartElement(xr.getPrefix(), xr.getLocalName(), xr.getNamespaceURI());
						for(int i = 0; i < xr.getAttributeCount(); i++)
							s.writeAttribute(xr.getAttributePrefix(i), xr.getAttributeNamespace(i), xr.getAttributeLocalName(i), xr.getAttributeValue(i));
						for(int i = 0; i < xr.getNamespaceCount(); i++)
							s.writeNamespace(xr.getNamespacePrefix(i), xr.getNamespaceURI(i));
						break;
					case XMLStreamReader.END_ELEMENT:
						s.writeEndElement();
						break;
					case XMLStreamReader.CDATA:
						s.writeCData(xr.getText());
						break;
					case XMLStreamReader.CHARACTERS:
						s.writeCharacters(xr.getText());
						break;
					case XMLStreamReader.COMMENT:
						s.writeComment(xr.getText());
						break;
				}
			}
		} catch (Exception e) {
			throw new FormatterException("Can't output raw data", e);
		}
		
		
	}
}
