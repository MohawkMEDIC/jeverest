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
 * Date: 09-02-2011
 */
package org.marc.everest.datatypes;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Flavor;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.CodingRationale;
import org.marc.everest.datatypes.generic.PDV;
import org.marc.everest.datatypes.generic.QTY;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.datatypes.interfaces.ISet;
import org.marc.everest.datatypes.interfaces.IUnitConverter;


/**
 * Represents a provisioned quantity which is a decimal value with an associated unit
 */
@Structure(name = "PQ", structureType = StructureType.DATATYPE)
public class PQ extends QTY<BigDecimal> {

	
	// backing field for unit
	private String m_unit;
	// coding rationale
	private SET<CS<CodingRationale>> m_rationale;
	// backing field for translation
	private SET<PQR> m_translation;
	// Tick maps
	private final static Map<String, Double> s_tickMap = new HashMap<String, Double>();
	// Backing field for get converters
	private final static List<IUnitConverter> s_converters = new ArrayList<IUnitConverter>();

	
	/**
	 * Static constructor
	 */
	static
	{
		s_tickMap.put("us",             0.001d);
		s_tickMap.put("ms",             1d);
		s_tickMap.put("s",           1000d);
		s_tickMap.put("ks",       1000000d);
		s_tickMap.put("Ms",    1000000000d);
		s_tickMap.put("Gs", 1000000000000d);
		s_tickMap.put("min",        60000d);
		s_tickMap.put("h",        3600000d);
		s_tickMap.put("d",       86400000d);
		s_tickMap.put("wk",     604800000d);
		s_tickMap.put("mo",    2628000000d);
		s_tickMap.put("a",    31557600000d ); // To match .NET impl
	}
	
	/**
	 * Creates a new instance of PQ
	 */
	public PQ() { super(); }
	/**
	 * Creates a new instance of PQ with the specified value and unit
	 * @param value The value of the PQ
	 * @param unit The unit of the PQ
	 */
	public PQ(BigDecimal value, String unit) {  super(value); this.m_unit = unit; }
	
	/**
	 * Gets the precision of the BigDecimal that is the primary value of this PQ
	 */
	public int getPrecision() { 
		return this.getValue().precision();
	}
	/**
	 * Sets the precision
	 */
	public void setPrecision(int precision) {
		this.setValue(this.getValue().round(new MathContext(precision)));
	}
	/**
	 * Gets the unit associated with the quantity
	 */
	@Property(name = "unit", conformance = ConformanceType.REQUIRED, propertyType = PropertyType.STRUCTURAL)
	public String getUnit() { return this.m_unit; }
	/**
	 * Sets the unit associated with the quantity
	 */
	public void setUnit(String value) { this.m_unit = value; }
	/**
	 * Gets the rationale as to why this PQ or PQR is provided
	 */
	@Property(name = "codingRationale", conformance = ConformanceType.REQUIRED, propertyType = PropertyType.NONSTRUCTURAL)
	public ISet<CS<CodingRationale>> getCodingRationale() { return this.m_rationale; }
	/**
	 * Sets the rationale as to why this PQ or PQR is provided
	 */
	public void setCodingRationale(ISet<CS<CodingRationale>> value) { this.m_rationale = (SET<CS<CodingRationale>>)value; }
	/**
	 * Gets a set of alternate representations of the provisioned quantity
	 */
	@Property(name = "translation", conformance = ConformanceType.REQUIRED, propertyType = PropertyType.NONSTRUCTURAL)
	public SET<PQR> getTranslation() { return this.m_translation; }
	/**
	 * Sets a set of alternate representations of the provisioned quantity
	 * @param value
	 */
	public void setTranslation(SET<PQR> value) { this.m_translation = value; }
	
	/**
	 * Adds the value of this quantity with another quantity
	 */
	public PQ add(PQ other) {
		
		PQ retVal = new PQ();
		
		if(other == null)
			return null; // This differs from standard Java Integer + null as no boxing is performed
		else if(this.isNull() || other.isNull())
			retVal.setNullFlavor(NullFlavor.NoInformation);
		else if(!this.isUnitComparable(other.getUnit()))
			throw new IllegalArgumentException("Units must be comparable to add PQ instances");
		else if(other.getValue() != null && this.getValue() != null)
			retVal.setValue(BigDecimal.valueOf(this.getValue().doubleValue() + other.convert(this.getUnit()).getValue().doubleValue()));
		else 
			retVal.setNullFlavor(NullFlavor.Other);
		retVal.setUnit(this.getUnit());

		return retVal;
	}
	
	/**
	 * Adds the value of this quantity with a real
	 */
	public PQ add(REAL other)	{
		
		PQ retVal = new PQ();
		
		if(other == null)
			return null; // This differs from standard Java Integer + null as no boxing is performed
		else if(this.isNull() || other.isNull())
			retVal.setNullFlavor(NullFlavor.NoInformation);
		else if(other.getValue() != null && this.getValue() != null)
			retVal.setValue(this.getValue().add(new BigDecimal(other.getValue())));
		else 
			retVal.setNullFlavor(NullFlavor.Other);
		return retVal;
		
	}
	
	/**
	 * Subract the value of another quantity from this quantity
	 */
	public PQ subtract(PQ other)	{
		
		PQ retVal = new PQ();
		
		if(other == null)
			return null; // This differs from standard Java Integer + null as no boxing is performed
		else if(this.isNull() || other.isNull())
			retVal.setNullFlavor(NullFlavor.NoInformation);
		else if(!this.isUnitComparable(other.getUnit()))
			throw new IllegalArgumentException("Units must be comparable to divide PQ instances");
		else if(other.getValue() != null && this.getValue() != null)
			retVal.setValue(BigDecimal.valueOf(this.getValue().doubleValue() - other.convert(this.getUnit()).getValue().doubleValue()));
		else 
			retVal.setNullFlavor(NullFlavor.Other);
		retVal.setUnit(this.getUnit());

		return retVal;
	}
	
	/**
	 * Subtract the value of another REAL from this quantity
	 */
	public PQ subtract(REAL other) {
		
		PQ retVal = new PQ();
		
		if(other == null)
			return null; // This differs from standard Java Integer + null as no boxing is performed
		else if(this.isNull() || other.isNull())
			retVal.setNullFlavor(NullFlavor.NoInformation);
		else if(other.getValue() != null && this.getValue() != null)
			retVal.setValue(this.getValue().subtract(new BigDecimal(other.getValue())));
		else 
			retVal.setNullFlavor(NullFlavor.Other);
		return retVal;
	}
	
	/**
	 * Divide this quantity by another quantity 
	 */
	public PQ divide(PQ other) {

		PQ retVal = new PQ();
		
		if(other == null)
			return null; // This differs from standard Java Integer + null as no boxing is performed
		else if(this.isNull() || other.isNull())
			retVal.setNullFlavor(NullFlavor.NoInformation);
		else if(!this.isUnitComparable(other.getUnit()))
			throw new IllegalArgumentException("Units must be comparable to divide PQ instances");
		else if(other.getValue() != null && this.getValue() != null)
			retVal.setValue(BigDecimal.valueOf(this.getValue().doubleValue() / other.convert(this.getUnit()).getValue().doubleValue()));
		else 
			retVal.setNullFlavor(NullFlavor.Other);
		retVal.setUnit(this.getUnit());

		return retVal;
		
	}
	
	/**
	 * Perform floating point division between this quantity and the specified REAL value
	 */
	public PQ divide(REAL other) {
		
		PQ retVal = new PQ();
		
		if(other == null)
			return null; // This differs from standard Java Integer + null as no boxing is performed
		else if(this.isNull() || other.isNull())
			retVal.setNullFlavor(NullFlavor.NoInformation);
		else if(other.getValue() != null && this.getValue() != null)
			retVal.setValue(this.getValue().divide(new BigDecimal(other.getValue())));
		else 
			retVal.setNullFlavor(NullFlavor.Other);
		return retVal;
	}
	
	/**
	 * Multiply this quantity by the specified quantity
	 */
	public PQ multiply(PQ other) {
		
		PQ retVal = new PQ();
		
		if(other == null)
			return null; // This differs from standard Java Integer + null as no boxing is performed
		else if(this.isNull() || other.isNull())
			retVal.setNullFlavor(NullFlavor.NoInformation);
		else if(!this.isUnitComparable(other.getUnit()))
			throw new IllegalArgumentException("Units must be comparable to multiply PQ instances");
		else if(other.getValue() != null && this.getValue() != null)
			retVal.setValue(BigDecimal.valueOf(this.getValue().doubleValue() * other.convert(this.getUnit()).getValue().doubleValue()));
		else 
			retVal.setNullFlavor(NullFlavor.Other);
		retVal.setUnit(this.getUnit());
		return retVal;
		
	}
	
	/**
	 * Multiply this quantity by the specified REAL
	 */
	public PQ multiply(REAL other) {
		
		PQ retVal = new PQ();
		
		if(other == null)
			return null; // This differs from standard Java Integer + null as no boxing is performed
		else if(this.isNull() || other.isNull())
			retVal.setNullFlavor(NullFlavor.NoInformation);
		else if(other.getValue() != null && this.getValue() != null)
			retVal.setValue(this.getValue().multiply(new BigDecimal(other.getValue())));
		else 
			retVal.setNullFlavor(NullFlavor.Other);
		return retVal;
		
	}
	
	/**
	 * Negate the quantity
	 */
	public PQ negate()
	{
		PQ retVal = new PQ();
        if (this.isNull())
            retVal.setNullFlavor(this.getNullFlavor());
        else if (this.getValue() == null)
            retVal.setNullFlavor(NullFlavor.NoInformation);
        else
        {
            this.setValue(this.getValue().negate());
            this.setUnit(this.getUnit());
        }
        return retVal;
	}
	
	/**
	 * Represent this PQ as an integer
	 */
	@Override
	public Integer toInteger() {
		return this.getValue().intValue();
	}
	/**
	 * Represent this PQ as a double
	 */
	@Override
	public Double toDouble() {
		return this.getValue().doubleValue();
	}
	
    /** 
     * Determine if this PQ validates to the PQ.TIME flavor
     */
    @Flavor(name = "PQ.TIME")
    public static Boolean isValidTimeFlavor(PQ a)
    {
        return s_tickMap.containsKey(a.getUnit());
    }
    

    /**
     * Converts the current PQ to the specified unit and returns the 
     * result in a new instance of PQ
     */
    public PQ convert(String toUnit) throws UnsupportedOperationException, IllegalArgumentException
    {
    	if (toUnit == null)
            throw new IllegalArgumentException("unit");
        else if (toUnit.equals(this.getUnit()))
            return (PQ)this.shallowCopy();
        else if (!this.isUnitComparable(toUnit))
            throw new UnsupportedOperationException(String.format("Cannot convert '%s' to '%s' as no map exists", this.getUnit(), toUnit));
        else if (this.getValue() == null)
            throw new UnsupportedOperationException("Cannot translate a null value");

        BigDecimal newValue = this.getValue();
        
        // find a unit converter
        IUnitConverter uc = null;
        for(IUnitConverter conv : PQ.getUnitConverters())
        	if(conv.canConvert(this, toUnit))
        	{
        		uc = conv;
        		break;
        	}
        
        // Translation map
        Double thisBase = PQ.s_tickMap.get(this.getUnit()),
            scaleBase = PQ.s_tickMap.get(toUnit);
        if(uc != null)
            return uc.convert(this, toUnit);
        else if (thisBase != null &&
            scaleBase != null)
        {
            thisBase *= this.getValue().doubleValue();
            newValue = BigDecimal.valueOf(thisBase / scaleBase);
        }
        else
        {
            toUnit = this.getUnit();
        }

        return new PQ(newValue, toUnit);
    }
    
    /**
     * Determines if the current instance of PQ is comparable with the specified unit
     */
    public boolean isUnitComparable(String unit) throws IllegalArgumentException
    {
    	 if (unit == null)
             throw new IllegalArgumentException("unit");

         if(unit == this.getUnit() || unit.equals(this.getUnit())) return true;
         
         boolean isTimeMeasure = PQ.s_tickMap.containsKey(unit) && PQ.s_tickMap.containsKey(this.getUnit());
         boolean hasConverter = false;
         for(IUnitConverter o : PQ.getUnitConverters())
        	 hasConverter |= o.canConvert(this, unit);
         
         return isTimeMeasure || hasConverter;    	
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((m_rationale == null) ? 0 : m_rationale.hashCode());
		result = prime * result
				+ ((m_translation == null) ? 0 : m_translation.hashCode());
		result = prime * result + ((m_unit == null) ? 0 : m_unit.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PQ other = (PQ) obj;
		if (m_rationale == null) {
			if (other.m_rationale != null)
				return false;
		} else if (!m_rationale.equals(other.m_rationale))
			return false;
		if (m_translation == null) {
			if (other.m_translation != null)
				return false;
		} else if (!m_translation.equals(other.m_translation))
			return false;
		if (m_unit == null) {
			if (other.m_unit != null)
				return false;
		} else if (!m_unit.equals(other.m_unit))
			return false;
		return true;
	}

	/**
	 * Get the unit converters
	 */
	public static List<IUnitConverter> getUnitConverters()
	{
		return s_converters;
	}
	
	/**
	 * Return a formatted string
	 */
	@Override
	public String toString() {
		return String.format("%s %s", this.getValue().toString(), this.getUnit());
	}
	/* (non-Javadoc)
	 * @see org.marc.everest.datatypes.generic.PDV#compareTo(org.marc.everest.datatypes.generic.PDV)
	 */
	@Override
	public int compareTo(PDV<?> other) throws IllegalArgumentException {
		if(other instanceof PQ)
		{
			PQ otherPq = (PQ)other;
			if (other == null || other.isNull())
                return 1;
            else if (this.isNull() && !other.isNull())
                return -1;
            else if (this.getValue() != null && other.getValue() == null)
                return 1;
            else if (other.getValue() != null && this.getValue()== null)
                return -1;
            else if (other.getValue() == null && this.getValue() == null)
                return 0;
            else if (!this.isUnitComparable(otherPq.getUnit()))
                throw new IllegalArgumentException("Units must match to compare PQ");
            else
                return this.getValue().compareTo(otherPq.convert(this.getUnit()).getValue());
		}
		else
			return super.compareTo(other);
	}
	
	/**
	 * Divide this PQ by an INT
	 */
	public PQ divide(INT other) {
		PQ retVal = new PQ();
		
		if(other == null)
			return null; // This differs from standard Java Integer + null as no boxing is performed
		else if(this.isNull() || other.isNull())
			retVal.setNullFlavor(NullFlavor.NoInformation);
		else if(other.getValue() != null && this.getValue() != null)
			retVal.setValue(this.getValue().divide(new BigDecimal(other.getValue())));
		else 
			retVal.setNullFlavor(NullFlavor.Other);
		return retVal;
	}
	
	
	
}
