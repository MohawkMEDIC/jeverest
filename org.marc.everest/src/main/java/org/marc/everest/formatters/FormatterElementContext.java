package org.marc.everest.formatters;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Properties;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;

/**
 * Represents a context for creation of type
 */
public class FormatterElementContext {

	// Backing field for property annotation
	private Property m_propertyAnnotation;
	// Backing field for properties annotation (choices)
	private Properties m_propertiesAnnotation;
	// Backing field for getter method
	private Method m_getterMethod;
	// Backing field for setter method
	private Method m_setterMethod;
	// Backing field for owner class
	private Type m_ownerType;
	// Backing field for parent context
	private FormatterElementContext m_parentContext;
	// Value context
	private Object m_instance;
	// Cached method scan
	private static HashMap<Class<?>, List<Method>> s_cachedMethodScan = new HashMap<Class<?>, List<Method>>();
	
	/**
	 * @return the instance
	 */
	public Object getInstance() {
		return m_instance;
	}
	/**
	 * @param instance the instance to set
	 */
	public void setInstance(Object instance) {
		this.m_instance = instance;
	}
	/**
	 * @return the propertyAnnotation
	 */
	public Property getPropertyAnnotation() {
		return m_propertyAnnotation;
	}
	/**
	 * @param propertyAnnotation the propertyAnnotation to set
	 */
	public void setPropertyAnnotation(Property propertyAnnotation) {
		this.m_propertyAnnotation = propertyAnnotation;
	}
	/**
	 * @return the propertiesAnnotation
	 */
	public Properties getPropertiesAnnotation() {
		return m_propertiesAnnotation;
	}
	/**
	 * @param propertiesAnnotation the propertiesAnnotation to set
	 */
	public void setPropertiesAnnotation(Properties propertiesAnnotation) {
		this.m_propertiesAnnotation = propertiesAnnotation;
	}
	/**
	 * @return the getterMethod
	 */
	public Method getGetterMethod() {
		return m_getterMethod;
	}
	/**
	 * @param getterMethod the getterMethod to set
	 */
	public void setGetterMethod(Method getterMethod) {
		this.m_getterMethod = getterMethod;
	}
	/**
	 * @return the setterMethod
	 */
	public Method getSetterMethod() {
		return m_setterMethod;
	}
	
	/**
	 * @param setterMethod the setterMethod to set
	 */
	public void setSetterMethod(Method setterMethod) {
		this.m_setterMethod = setterMethod;
	}
	/**
	 * Converts the owner type to a raw class
	 */
	public Class<?> getOwnerClazz() {
		return FormatterUtil.getClassForType(this.m_ownerType, this);
	}
	/**
	 * Sets the owner type with the given class
	 */
	public void setOwnerClazz(Class<?> ownerClazz) {
		this.m_ownerType = ownerClazz;
	}
	
	/**
	 * Get the owner type
	 */
	public Type getOwnerType() {
		return this.m_ownerType;
	}
	/**
	 * Sets the owner type
	 */
	public void setOwnerType(Type ownerType)
	{
		this.m_ownerType = ownerType;
	}
	/**
	 * @return the parentContext
	 */
	public FormatterElementContext getParentContext() {
		return m_parentContext;
	}
	/**
	 * @param parentContext the parentContext to set
	 */
	public void setParentContext(FormatterElementContext parentContext) {
		this.m_parentContext = parentContext;
	}
	
	/**
	 * Get the conformance type of the current context
	 */
	public ConformanceType getConformance()
	{
		if(this.m_propertyAnnotation != null)
			return this.m_propertyAnnotation.conformance();
		return null;
	}

	/**
	 * Get the property type
	 * @return
	 */
	public PropertyType getPropertyType()
	{
		if(this.m_propertyAnnotation != null)
			return this.m_propertyAnnotation.propertyType();
		return null;
	}
	
	/**
	 * Get the level of indentation
	 */
	public int getIndent()
	{
		FormatterElementContext parent = this.m_parentContext;
		int i = 0;
		
		while(parent != null)
		{
			i++;
			parent = parent.getParentContext();
		}
		return i;
	}
	
	/**
	 * Create a new use context
	 */
	public FormatterElementContext(Object instance, Method getterMethod, FormatterElementContext parentContext)
	{
		this.m_instance = instance;
		if(instance != null)
			this.m_ownerType = this.m_instance.getClass();
		else if(getterMethod != null)
			this.m_ownerType = getterMethod.getGenericReturnType();
		this.m_getterMethod = getterMethod;
        this.m_parentContext = parentContext;
        
        // Is there property method
        if(getterMethod != null)
        {
       	 	this.m_setterMethod = FormatterUtil.getSetterMethod(getterMethod.getName(), getterMethod.getReturnType(), this.getGetterMethod().getDeclaringClass());
            this.m_propertiesAnnotation = getterMethod.getAnnotation(Properties.class);
            this.m_propertyAnnotation = getterMethod.getAnnotation(Property.class);
        }
	}
	
	/**
	 * Create a new use context
	 */
	public FormatterElementContext(Class<?> ownerClazz, Property property, Method getterMethod, Method setterMethod)
	{
		this.m_ownerType = ownerClazz;
		this.m_propertyAnnotation = property;
		this.m_getterMethod = getterMethod;
		this.m_setterMethod = setterMethod;
	}

	/**
	 * Create a class for the getter where ownerclazz is the class that contains the getterMethod
	 */
	public FormatterElementContext(Type ownerClazz, Method getterMethod)
	{
		 this.m_ownerType = ownerClazz;
         this.m_getterMethod = getterMethod;
         
         // Is there property method
         if(getterMethod != null)
         {
        	 this.m_setterMethod = FormatterUtil.getSetterMethod(getterMethod.getName(), getterMethod.getReturnType(), getterMethod.getDeclaringClass());
             this.m_propertiesAnnotation = getterMethod.getAnnotation(Properties.class);
             this.m_propertyAnnotation = getterMethod.getAnnotation(Property.class);
         }
	}
	
	/**
	 * Get actual type argument.
	 * <p>
	 * This method exists because the Class object in Java erases the generic types
	 * and substitutes java.lang.Object. This substitution means that creating instances
	 * at runtime is a pain as we don't know the what the real type is.
	 * </p>
	 * <p>
	 * For example, consider declaration:
	 * <pre>
	 * class ControlActEvent<TSubject, TParameterList>
	 * {
	 * 		TSubject getSubject();
	 * 		TParameterList getParameterList();
	 * }
	 * </pre>
	 * being bound to: ControlActEvent&lt;PatientRegistration,ParametersList>. At runtime this
	 * is replaced as ControlActEvent&lt;Object, Object> so getSubject and getParameterList 
	 * are effectively wiped out and replaced with Object getSubject() and Object getParameterList(). 
	 * If we were to try an populate an instance of ControlActEvent (and set subject), the creator
	 * would have no idea that subject was supposed to be PatientRegistration, rather it would see
	 * Object, and populating would be impossible. 
 	 * </p>
 	 * @param bindingVar The type variable (for example TSubject) that the "real" type is being found
	 */
	public Type getActualTypeArgument(TypeVariable<?> bindingVar)
	{

		// Scanning type
		Type scanType = null;
		
		// First, is this instance the "root" instance?
		// If so then check the generic type declaration
		if(this.getParentContext() == null) 
			scanType = this.getOwnerClazz().getGenericSuperclass();
		else if(this.getGetterMethod() != null)
			scanType = this.getGetterMethod().getGenericReturnType();
		else
			return Object.class; // cannot infer

		// Does OwnerType override the getter method? If so then use that!
		if(!this.getOwnerType().equals(scanType))
			scanType = this.getOwnerType();
		
		// Getter method type is a parameterized type, so maybe it has bound the checked var?
		if (scanType instanceof ParameterizedType) {
			Type retVal = deepScanTypeArguments((ParameterizedType)scanType, bindingVar);
			if (retVal instanceof Class<?> || retVal instanceof ParameterizedType)
				return retVal;
		} // otherwise is T specified in an inheritence structure?
		else if(scanType instanceof Class<?> && ((Class<?>)scanType).getGenericSuperclass() instanceof ParameterizedType)
		{
			Type retVal = deepScanTypeArguments((ParameterizedType)((Class<?>)scanType).getGenericSuperclass(), bindingVar);
			if (retVal instanceof Class<?> || retVal instanceof ParameterizedType)
				return retVal;
			
		}
		else if(scanType instanceof XsiType)
		{
			XsiType xsiType = (XsiType)scanType;
			// Get index
			for(int i = 0; i < xsiType.getTypeArguments().size(); i++)
			{
				TypeVariable<?> tp = xsiType.getUnderlyingClazz().getTypeParameters()[i];
				if(tp.getName().equals(bindingVar.getName()))
					return xsiType.getTypeArguments().get(i);
			}
		}
		
		// Check parents
		FormatterElementContext tContext = this.getParentContext();
		while(tContext != null)
		{
			Type retVal = tContext.getActualTypeArgument(bindingVar);
			if (retVal instanceof Class<?> || retVal instanceof ParameterizedType)
				return retVal;
			tContext = tContext.getParentContext();
		}
		return Object.class;
	}
	
	/**
	 * Deep scan arguments
	 * <p>This function exists incase one of the bound arguments is nested. For example, consider this 
	 * class declaration:
	 * <pre>
	 * class Message&lt;TControlActEvent&lt;TParameterList>>
	 * {
	 * 	    TControlActEvent&lt;TParameterList> getControlActEvent();
	 * }
	 * class ControlActEvent&lt;TParameterList>
	 * {
	 * 		TParameterList getParameterList();
	 * }
	 * </pre>
	 * If this was bound such that : 
	 * <pre>XXYY_ZZ000000AA : Message&lt;ControlActEvent&lt;MyParameterList>></pre>
	 * There would be a problem getting the actual type of getParameterList because
	 * the only binding for this generic parameter is on the base class's generic
	 * argument's generic argument (ie: scan Message.getActualTypeArguments()[0].getActualTypeArguments()[0])
	 * </p>
	 * <p>This method will do the recursive search of all type variables on a parameterized type</p>
	 */
	private Type deepScanTypeArguments(ParameterizedType scanType, TypeVariable<?> bindingVar) {
		
		// Save generic arguments bindings 
		if(scanType.getRawType() instanceof Class)
		{
			TypeVariable<?>[] typeParms = ((Class<?>)scanType.getRawType()).getTypeParameters();
			for(int i = 0; i < typeParms.length; i++)
			{
				if(bindingVar.getName().equals(typeParms[i].getName()))
					return scanType.getActualTypeArguments()[i];
				else if(scanType.getActualTypeArguments()[i] instanceof ParameterizedType)
					return deepScanTypeArguments((ParameterizedType)scanType.getActualTypeArguments()[i], bindingVar);
			}
		}		
		return null;
	}
	
	/**
	 * Find a child context with the specified name
	 */
	public FormatterElementContext findChildContextFromName(String propertyName, PropertyType type) {
		
		return this.findChildContextFromName(propertyName, type, this.getOwnerClazz());
	}
	
	/**
	 * Find a child context from the given name given the overridden datatype
	 */
	public FormatterElementContext findChildContextFromName(String propertyName, PropertyType type, Class<?> scanType)
	{
		// Find the property
				FormatterElementContext rootContext = this.getRootContext();
				
				for(Method m : deepScanMethods(scanType))
				{
					// Scan regular ol properties
					Property pDef = m.getAnnotation(Property.class);
					Properties props = m.getAnnotation(Properties.class);
					
					if(props == null && pDef != null && pDef.propertyType() == type && pDef.name().equals(propertyName))
					{
						FormatterElementContext candidate = new FormatterElementContext(null, m, this);
						if(candidate.getOwnerClazz().equals(List.class)) // HACK: Can't create List but we need to... Let's make it an array List
							candidate.setOwnerClazz(ArrayList.class);
						else if(Modifier.isAbstract(candidate.getOwnerClazz().getModifiers()))
							candidate.setOwnerClazz(pDef.type());
						return candidate;
					}
					
					// Scan choices
					if(props != null)
						for(Property subDef : props.value())
							if(subDef != null && subDef.propertyType() == type && subDef.name().equals(propertyName) &&
									(subDef.interactionOwner().equals(Object.class) || rootContext.getOwnerClazz().equals(subDef.interactionOwner())))
							{
								FormatterElementContext candidate = new FormatterElementContext(subDef.type(), subDef, m, FormatterUtil.getSetterMethod(m.getName(), m.getReturnType(), m.getDeclaringClass()));
								candidate.setParentContext(this);
								return candidate;
								
							}
					
				}
			
					
				return null;
	}
	
	/**
	 * Deep scan a class for methods
	 */
	private synchronized List<Method> deepScanMethods(Class<?> clazz)
	{
		
		// Cached check
		if(s_cachedMethodScan.containsKey(clazz))
			return s_cachedMethodScan.get(clazz);
		
		List<Method> retVal = new ArrayList<Method>();
		for(Method m : clazz.getMethods())
		{
			Property prop = m.getAnnotation(Property.class);
			Properties props = m.getAnnotation(Properties.class);
			if(prop != null || props != null)
				retVal.add(m);
		}
		if(clazz.getSuperclass() != null)
			retVal.addAll(deepScanMethods(FormatterUtil.getClassForType(clazz.getGenericSuperclass(), this)));
		
		// Put the cache method
		s_cachedMethodScan.put(clazz, retVal);
		
		return retVal;
	}
	
	/**
	 * Get the root context
	 */
	public FormatterElementContext getRootContext() {
		FormatterElementContext rootContext = this;
		while(rootContext.getParentContext() != null)
			rootContext = rootContext.getParentContext();
		return rootContext;
	}
}
