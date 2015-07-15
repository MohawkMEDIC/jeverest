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
 * Date: 10-19-2012
 */
package org.marc.everest.datatypes;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.DatatypeValidationResultDetail;


/**
 * Represents a name for an entity (person, organization, place or thing)
 */
@Structure(name = "EN", structureType = StructureType.DATATYPE)
public class EN extends ANY {

	// backing field for parts
	private List<ENXP> m_parts = new ArrayList<ENXP>();
	// backing field for use
	private SET<CS<EntityNameUse>> m_use;
	
	/**
	 * Creates a new instance of EN
	 */
	public EN() { super(); }
	/**
	 * Creates a new instance of EN with the given use and parts
	 * @param use The intended use of the entity name
	 * @param parts The parts which compose the EN
	 */
	public EN(EntityNameUse use, Collection<ENXP> parts) { 
		this(parts);
		if(use != null)
			this.m_use = new SET<CS<EntityNameUse>>(new CS<EntityNameUse>(use));
	}
	/**
	 * Creates a new instance of EN with the specified parts
	 */
	public EN(Collection<ENXP> parts)
	{
		this.m_parts.addAll(parts);
	}
	/**
	 * Creates a new instance of the EN type with the specified uses and parts
	 */
	public EN(SET<CS<EntityNameUse>> use, List<ENXP> parts) {
		this(parts);
		this.m_use = use;
	}
	/**
	 * Creates an EN instance with the specified use and parts
	 */
	public static EN createEN(EntityNameUse use, ENXP... parts)
	{
		return new EN(use, Arrays.asList(parts));
	}
	/**
	 * Gets a list of parts that make up this entity name
	 */
	@Property(name = "part", conformance = ConformanceType.REQUIRED, propertyType = PropertyType.NONSTRUCTURAL, minOccurs = 0, maxOccurs = -1)
	public List<ENXP> getParts() {
		return this.m_parts;
	}
	/**
	 * Gets the name part at the specified index
	 */
	public ENXP getPart(int index)
	{
		return this.m_parts.get(index);
	}
	/**
	 * Sets the list of parts that make up this entity name
	 */
	public void setParts(List<ENXP> value) {
		this.m_parts = value;
	}
	/**
	 * Gets the valid uses of this entity name
	 */
	@Property(name = "use", conformance = ConformanceType.REQUIRED, propertyType = PropertyType.STRUCTURAL)
	public SET<CS<EntityNameUse>> getUse() {
		return this.m_use;
	}
	/**
	 * Sets the valid uses of this entity name
	 */
	public void setUse(SET<CS<EntityNameUse>> value) {
		this.m_use = value;
	}
	/**
	 * @see org.marc.everest.datatypes.ANY#semanticEquals(org.marc.everest.datatypes.interfaces.IAny)
	 */
	@Override
	public BL semanticEquals(IAny other) {
		BL baseSem = super.semanticEquals(other);
        if (!baseSem.toBoolean())
            return baseSem;

        boolean result = true;
        EN otherEn = (EN)other;
        if ((this.m_parts == null) ^ (otherEn.m_parts == null))
            return BL.FALSE;
        else if (this.m_parts == otherEn.m_parts)
            return BL.TRUE;
        else
        {
            for(ENXP part : this.m_parts)
                result &= otherEn.m_parts.contains(part);
            return BL.fromBoolean(result && this.m_parts.size() == otherEn.m_parts.size());
        }
	}
	/**
	 * Construct an entity name from the specified use family and given names
	 * @param use The desired use of the name
	 * @param family The family name portion
	 * @param given The given names
	 * @return
	 */
	public static EN fromFamilyGiven(EntityNameUse use, String family, String... given)
	{
		EN retVal = EN.createEN(use, new ENXP(family, EntityNamePartType.Family));
		for(String gn : given)
			retVal.getParts().add(new ENXP(gn, EntityNamePartType.Given));
		return retVal;
		
	}
	/**
	 * @see org.marc.everest.datatypes.ANY#validateEx()
	 */
	@Override
	public Collection<IResultDetail> validateEx() {
        List<IResultDetail> retVal = (List<IResultDetail>)super.validateEx();
        if (this.isNull() && this.m_parts.size() > 0)
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "EN", EverestValidationMessages.MSG_NULLFLAVOR_WITH_VALUE, null));
        else if (this.m_parts.size() == 0 && !this.isNull())
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "EN", EverestValidationMessages.MSG_NULLFLAVOR_MISSING, null));

        // Validate parts
        for(ENXP pt : this.m_parts)
            retVal.addAll(pt.validateEx());

        return retVal;
	}
	/**
	 * @see org.marc.everest.datatypes.HXIT#validate()
	 */
	@Override
	public boolean validate() {
		return (this.m_parts.size() > 0) ^ this.isNull();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();

        // Iterate through the sequence
        for (ENXP p : this.m_parts)
        {
            if (p.getType() == null)
                sw.write(p.getValue());
            else
                switch (p.getType().getCode())
                {
                    case Delimiter:
                        sw.write(p.getValue() == null || p.getValue() == "" ? "\n" : p.getValue());
                        break;
                    default:
                        sw.write(p.getValue());
                        sw.write(" ");
                        break;
                }
        }

        return sw.toString();	
	}
	/** (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((m_parts == null) ? 0 : m_parts.hashCode());
		result = prime * result + ((m_use == null) ? 0 : m_use.hashCode());
		return result;
	}
	/** (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EN other = (EN) obj;
		if (m_parts == null) {
			if (other.m_parts != null)
				return false;
		} else if (!m_parts.equals(other.m_parts))
			return false;
		if (m_use == null) {
			if (other.m_use != null)
				return false;
		} else if (!m_use.equals(other.m_use))
			return false;
		return true;
	}
	
	
}
