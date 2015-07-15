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
 * Date: 06-28-2011
 */
package org.marc.everest.datatypes.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.EverestValidationMessages;
import org.marc.everest.datatypes.interfaces.IPrimitiveDataValue;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.DatatypeValidationResultDetail;


/**
 * The primitive data value (PDV) data type is not explicitly defined by HL7, it is merely
 * a shortcut to allow many types which encapsulate primitives to share functionality
 * @param <T> The encapsulated primitive value
 */
@Structure(name = "PDV", structureType = StructureType.DATATYPE)
public abstract class PDV <T> extends ANY implements Comparable<PDV<?>>, IPrimitiveDataValue<T> {


	// Backing field for the value property
	private T m_value;

	// Identifies the floating point precision whereby equality can be determined
	protected double p_floatingPointEqualityTolerance = 1e-15;
	
	/**
	 * Creates a new instance of the PDV class
	 */
	public PDV() { super(); }
	/**
	 * Creates an instance of the PDV class with the specified value
	 * @param value The initial value of the PDV
	 */
	public PDV(T value) { 
		
		this.m_value = value;

	}
	
	/**
	 * Gets the value encapsulated by this object
	 * @return The value encapsulated by this object
	 */
	@Property(name = "value", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	@Override
	public T getValue() { return this.m_value; }
	/**
	 * Sets the value to be encapsulated by this object and updates the 
	 * IsValueSet property
	 * @param value The new value to be encapsulated
	 */
	@Override
	public void setValue(T value)  {
		
		this.m_value = value; 
	}
	
	/**
	 * Validates that PDV meets the basic validation criteria
	 * @return
	 */
	@Override
	public boolean validate()
	{
		return (this.getNullFlavor() != null) ^ (this.getValue() != null) && super.validate();
	}
	
	/**
	 * Compare this instance of PDV to the other instance of PDV
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(PDV<?> o) throws IllegalArgumentException
	{
		// Determine if T is comparable
		if(o == null || !(o.getValue() instanceof Comparable<?>) ||
			!(this.getValue() instanceof Comparable<?>))
			throw new IllegalArgumentException();
		
		Comparable<T> thisValue = (Comparable<T>)this.getValue(),
			otherValue = (Comparable<T>)o.getValue();
		return thisValue.compareTo((T)otherValue);
		
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((m_value == null) ? 0 : m_value.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof PDV)) {
			return false;
		}
		PDV<?> other = (PDV<?>) obj;
		if (m_value == null && other.m_value != null)
			return false;
		else if (this.m_value instanceof Float || this.m_value instanceof Double)
		{
			// Sometimes when deserializing a real number some precision is lost. In this case we
			// need to compensate by introducing a tolerance to the recognized precision
			boolean result = true;
			Double thisValue = (Double)this.m_value,
				otherValue = (Double)other.getValue();
			if(Double.isNaN(otherValue)) result &= Double.isNaN(thisValue);
			else if(Double.isInfinite(otherValue)) result &= Double.isInfinite(thisValue);
			else if(otherValue == 0) result &= thisValue == 0;
			else // Tolerance is the same (ie: precision is the same)
				result &= Math.abs(otherValue - thisValue) <= Math.abs(otherValue * this.p_floatingPointEqualityTolerance);
			return result;

		}
		else if (m_value == null && other.m_value != null || 
				m_value != null && !m_value.equals(other.m_value)) 
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see ca.marc.everest.datatypes.ANY#validateEx()
	 */
	@Override
	public Collection<IResultDetail> validateEx() {
		List<IResultDetail> retVal = new ArrayList<IResultDetail>(super.validateEx());
        if (this.isNull() && this.m_value != null)
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "PDV", EverestValidationMessages.MSG_NULLFLAVOR_WITH_VALUE, null));
        else if (!this.isNull() && this.m_value == null)
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "PDV", EverestValidationMessages.MSG_NULLFLAVOR_MISSING, null));
        return retVal;
	}
	/**
	 * Convert to string
	 */
	@Override
	public String toString() {
		return this.getValue() == null ? super.toString() : this.getValue().toString();
	}
	
	

}
