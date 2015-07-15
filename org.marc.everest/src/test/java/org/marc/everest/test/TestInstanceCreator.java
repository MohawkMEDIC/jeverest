/* 
 * Copyright 2008-2014 Mohawk College of Applied Arts and Technology
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
 * Date: 06-24-2013
 */
package org.marc.everest.test;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Property;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.FormatterElementContext;

/**
 * The Type creator is responsible for creating interaction instances with
 * full data and then testing them
 */
public class TestInstanceCreator {

	
	// The class that is being created
	private Class<?> m_clazz; 
	
	// Constructor cache
	private static Map<Class<?>, Constructor<?>> m_constructorCache = new HashMap<Class<?>, Constructor<?>>();
	// Simple creators
	private static Map<Type, Method> m_simpleTypeCreators = new HashMap<Type, Method>();
	
	// Class stack of currently creating types
	private Stack<Class<?>> m_clazzStack;
	
	// Object instance
	private Object m_instance;
	
	/**
	 * Creates a new instance of the test instance class creator
	 * @param clazz
	 */
	public TestInstanceCreator(Class<?> clazz)
	{
		this.m_clazz = clazz;
	}
	
	/**
	 * Create an instance
	 * @return
	 */
	public Object createInstance()
	{
		if(this.m_instance != null)
			return this.m_instance;
		
		try {
			createInstanceInternal();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return this.m_instance;
	}
	
	/**
	 * Create an instance
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void createInstanceInternal() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		
		if(!m_constructorCache.containsKey(this.m_clazz))
			m_constructorCache.put(this.m_clazz, this.m_clazz.getConstructor());
		
		// Initialize a type stack
		this.m_clazzStack = new Stack<Class<?>>();
		FormatterElementContext startContext = new FormatterElementContext(this.m_clazz, null);
		this.m_instance = this.createType(m_clazz, startContext);
		this.m_clazzStack.clear();
		
	}

	/**
	 * Create an instance of a type
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	private Object createType(Class<?> clazz, FormatterElementContext context) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		if(this.m_clazzStack.contains(clazz)) // Prevent recursion
			return null;
		
		// Don't create types when:
		// The nesting level is deep  (> 10 properties) unless the properties are mandatory or populated
		// The property is optional
		if(context.getIndent() > 20 &&
				context.getPropertyAnnotation() != null &&
				context.getPropertyAnnotation().conformance()  != ConformanceType.MANDATORY &&
				context.getPropertyAnnotation().conformance() != ConformanceType.POPULATED ||
				context.getPropertyAnnotation() != null &&
				context.getPropertyAnnotation().conformance() == ConformanceType.OPTIONAL)
			return null;

		// Attempt to create simple type
		Object result = null;
		if(context.getGetterMethod() != null)
			result = createSimpleType(context.getGetterMethod().getGenericReturnType(), context);
		if(result == null)
			result = createSimpleType(clazz, context);
		
		if(result != null)
			return result;
		else if(clazz.isEnum())
			return getEnumerationValue(clazz);
		else if(FormatterUtil.hasInterface(clazz, Collection.class) && context.getGetterMethod() != null) // Create collection as a property
			return createCollection(FormatterUtil.getClassForType(context.getGetterMethod().getGenericReturnType()), context);
		else if(isChoice(clazz, context))
		{
			FormatterElementContext rootContext = context;
			while(rootContext.getParentContext() != null)
				rootContext = rootContext.getParentContext();
			
			if(context.getPropertyAnnotation() == null)
				context.setPropertyAnnotation(context.getPropertiesAnnotation().value()[0]);
			// Are we getting the root context from null property attribute?
			if(!context.getPropertyAnnotation().type().equals(Object.class) && !Modifier.isAbstract(context.getPropertyAnnotation().type().getModifiers()) &&
					(context.getPropertyAnnotation().interactionOwner().equals(rootContext.getOwnerClazz()) || context.getPropertyAnnotation().interactionOwner().equals(Object.class)))
				return createType(context.getPropertyAnnotation().type(), context);
			else
				for(Property prop : context.getPropertiesAnnotation().value())
					if(!prop.type().equals(Object.class) && !Modifier.isAbstract(prop.type().getModifiers()) &&
					(prop.interactionOwner().equals(rootContext.getOwnerClazz()) || prop.interactionOwner().equals(Object.class)))
					{
						context.setPropertyAnnotation(prop);
						return createType(prop.type(), context);
					}
		}
		
		// Fallback, create complex type
		this.m_clazzStack.push(clazz);
		result = createComplexObject(FormatterUtil.getClassForType(clazz), context);
		this.m_clazzStack.pop();
		
		return result;
	}

	/**
	 * Create a complex class object
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private Object createComplexObject(Class<?> clazz, FormatterElementContext context) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		// Validate
		if(context.getPropertyAnnotation() != null && context.getPropertyAnnotation().conformance() == ConformanceType.OPTIONAL &&
				context.getPropertiesAnnotation() != null && context.getGetterMethod() != null && context.getGetterMethod().getReturnType().equals(clazz))
			return null;
		
		Constructor<?> ctor = null;
		Object result = null;
		
		// Missing CMET
		// Basically it is impossible to determine what type to create here
		if(
				context.getPropertyAnnotation() != null && 
				context.getPropertyAnnotation().type().equals(Object.class) && 
				context.getPropertiesAnnotation() == null && 
				clazz.equals(Object.class)
			)
			return null;
		
		if(m_constructorCache.containsKey(clazz))
			ctor = m_constructorCache.get(clazz);
		else
		{
			ctor = clazz.getConstructor();
			if(ctor != null)
				m_constructorCache.put(clazz, ctor);
			else
				return null;
		}
		
		result = ctor.newInstance(); // Create the instance
		
		populateObject(result, clazz, context);
		
		return result;
	}

	/**
	 * Populate the object
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private void populateObject(Object result, Class<?> ownerClazz, FormatterElementContext context) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		for(Method m : ownerClazz.getMethods())
		{
			if(!Modifier.isPublic(m.getModifiers()) || Modifier.isStatic(m.getModifiers()))
				continue;
	
			// Setup cascade
			FormatterElementContext cascadeContext = new FormatterElementContext(ownerClazz, m);
			cascadeContext.setParentContext(context);

			// Can't set, can't write
			if(cascadeContext.getSetterMethod() == null || (cascadeContext.getPropertyAnnotation() == null && cascadeContext.getPropertiesAnnotation() == null))
				continue;

			// Already contains a value
			if(m.invoke(result) != null) 
			{
				if(!FormatterUtil.hasInterface(m.getReturnType(), List.class)) // Not a collection?
					continue;
				else
				{
					try {
						cascadeContext.getSetterMethod().invoke(
								result, 
									createCollection(
											m.getGenericReturnType(), 
											cascadeContext
										)
									);
						continue;
					} catch (Exception e) {
						continue; // ignore exceptions
					}
				}
			}
			
			// No null flavors
			if(m.getName().equals("getNullFlavor"))
				continue;
			
			// try to set a value
			try
			{
				cascadeContext.getSetterMethod().invoke(result, 
							convertTypeIfNecessary(
									createType(
											FormatterUtil.getClassForType(
													m.getGenericReturnType(),
													cascadeContext
											), 
											cascadeContext
									)
							)
					);
			}
			catch(Exception e)
			{
				
				e.printStackTrace();
			}
		}
	}

	/**
	 * Convert type if necessary
	 */
	private Object convertTypeIfNecessary(Object o) {
		return o;
	}

	
	/**
	 * True if the context is a choice
	 * @param clazz
	 * @param context
	 * @return
	 */
	private boolean isChoice(Class<?> clazz, FormatterElementContext context) {
		return context != null && 
				context.getGetterMethod() != null && 
				(context.getPropertyAnnotation() == null || 
				clazz.equals(Object.class)|| 
						Modifier.isAbstract(clazz.getModifiers()));
	}

	/**
	 * Create a collection 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private Object createCollection(Type collectionType, FormatterElementContext context) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		if(!(collectionType instanceof ParameterizedType)) // Cannot do non-generic lists
			return null;
		
		// Local ref for the parameterized type
		ParameterizedType pType = ((ParameterizedType)collectionType);
		Class<?> collectionClazz = FormatterUtil.getClassForType(collectionType);
		
		if(pType.getActualTypeArguments().length != 1) // Cannot use more than one binding 
			return null;
		
		// Get the raw class
		Class<?> rawClazz = FormatterUtil.getClassForType(pType.getActualTypeArguments()[0], context);
		if(rawClazz.equals(Object.class))
			return null; // cannot determine the actual type
		
		// Get the add method
		Method addMethod = collectionClazz.getMethod("add", Object.class);
		if(addMethod == null)
			return null;
		
		// Get the collection instance constructor
		Object collectionInstance = collectionClazz.newInstance();
		
		// Next we want to add the data
		Object memberInstance = createType(rawClazz, context);
		if(memberInstance != null)
			addMethod.invoke(collectionInstance, memberInstance);
		
		return collectionInstance;
	}


	/**
	 * Get a random enumeration value
	 */
	private Object getEnumerationValue(Class<?> clazz) {
		return clazz.getEnumConstants()[0];
	}
	
	/**
	 * Create a simple type
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private Object createSimpleType(Type simpleType, FormatterElementContext context) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(m_simpleTypeCreators.containsKey(simpleType))
			return m_simpleTypeCreators.get(simpleType).invoke(null, context);
		
		// Lookup
		for(Method m : SimpleTypeCreator.class.getMethods())
			if(m.getGenericReturnType().equals(simpleType) ||
					m.getReturnType().equals(simpleType))
			{
				m_simpleTypeCreators.put(simpleType, m);
				return m.invoke(null, context);
			}
			
		return null;
	}
	
	
}
