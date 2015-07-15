/* 
 * Copyright 2008-2011 Mohawk College of Applied Arts and Technology
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
 * Date: 07-25-2011
 */
package org.marc.everest.datatypes.generic;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.ANY;


/**
 * A concept qualifier code with optionally named role
 */
@Structure(name = "CR", structureType = StructureType.DATATYPE)
public class CR<T> extends ANY {

	// Backing field for name
	private CV<?> m_name;
	// Backing field for value
	private CD<?> m_value;
	// Backing field for inverted
	private Boolean m_inverted = false;
	
	/**
	 * Creates a new instance of the CR class
	 */
	public CR() {}
	/**
	 * Creates a new instance of the CR class with the specified name and
	 * value
	 * @param name The name of the code
	 * @param value The value of the code
	 */
	public CR(CV<?> name, CD<? extends T> value)
	{
		this.m_name = name;
		this.m_value = value;
	}
	
	/**
	 * Gets a value specifying the manner in which the concept role value contributes to the concept descriptor
	 */
	@Property(name = "name", conformance = ConformanceType.REQUIRED, propertyType = PropertyType.NONSTRUCTURAL)
	public CV<?> getName() {
		return m_name;
	}
	/**
	 * Sets a value specifying the manner in which the concept role value contributes to the concept descriptor
	 * @param value The new value of the concept qualifier name
	 */
	public void setName(CV<?> value) {
		this.m_name = value;
	}

	/**
	 * Gets a value specifying the concept that modifies the primary code phrase
	 */
	@Property(name = "value", conformance = ConformanceType.REQUIRED, propertyType = PropertyType.NONSTRUCTURAL)
	public CD<?> getValue() {
		return m_value;
	}
	/**
	 * Sets a value specifying the concept that modifies the primary code phrase
	 */
	public void setValue(CD<?> value) {
		this.m_value = value;
	}
	
	/**
	 * Gets a value indicating whether the sense of the name is inverted
	 * 
	 * To be used when a code system supports inversion
	 */
	@Property(name = "inverted", conformance = ConformanceType.REQUIRED, propertyType = PropertyType.STRUCTURAL)
	public Boolean isInverted()
	{
		return this.m_inverted;
	}
	
	/**
	 * Sets a value indicating whether the sense of the name is inverted
	 */
	public void setInverted(boolean value)
	{
		this.m_inverted = value;
	}
	
}
