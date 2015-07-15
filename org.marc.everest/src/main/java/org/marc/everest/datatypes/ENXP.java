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

import java.util.Collection;
import java.util.List;

import org.marc.everest.annotations.*;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.DatatypeValidationResultDetail;


/**
 * A character string token representing a part of a name
 */
@Structure(name = "ENXP", structureType = StructureType.DATATYPE)
public class ENXP extends ANY {

	// Backing field for value
	private String m_value;
	// backing field for type
	private CS<EntityNamePartType> m_type;
	// Backing field for code
	private String m_code;
	// backing field for code system
	private String m_codeSystem;
	// Backing field for code system version
	private String m_codeSystemVersion;
	// backing field for qualifier
	private SET<CS<EntityNamePartQualifier>> m_qualifier;
	/**
	 * Creates a new instance of the ENXP class
	 */
	public ENXP() { super(); }
	/**
	 * Creates a new instance of the ENXP class with the specified value
	 * @param value The initial value of the ENXP instance
	 */
	public ENXP(String value) { 
		super();
		this.m_value = value;
	}
	/**
	 * Creates a new instance of the ENXP class with the specified value and type
	 * @param value The value of the name part
	 * @param type The type of component the name part represents
	 */
	public ENXP(String value, EntityNamePartType type)
	{
		this(value);
		this.m_type = new CS<EntityNamePartType>(type);
	}
	/**
	 * Gets the value of the name part
	 */
	@Property(name = "value", propertyType= PropertyType.STRUCTURAL, conformance = ConformanceType.REQUIRED)
	public String getValue() {
		return this.m_value;
	}
	/**
	 * Sets the value of the name part
	 */
	public void setValue(String value) {
		this.m_value = value;
	}
	/**
	 * Gets the type of the name part
	 */
	@Property(name = "type", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.REQUIRED)
	public CS<EntityNamePartType> getType() {
		return this.m_type;
	}
	/**
	 * Sets the type of this name part
	 */
	public void setType(CS<EntityNamePartType> value) {
		this.m_type = value;
	}
	/**
	 * Gets the code for this name part
	 */
	@Property(name = "code", propertyType = PropertyType.STRUCTURAL, conformance= ConformanceType.OPTIONAL)
	public String getCode() {
		return this.m_code;
	}
	/**
	 * Sets the code for this name part
	 */
	public void setCode(String value) {
		this.m_code = value;
	}
	/**
	 * Gets the code system for this name part
	 */
	@Property(name = "codeSystem", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.OPTIONAL)
	public String getCodeSystem() {
		return this.m_codeSystem;
	}
	/**
	 * Sets the code system for this name part
	 */
	public void setM_codeSystem(String value) {
		this.m_codeSystem = value;
	}
	/**
	 * Gets the code system version for this name part
	 */
	@Property(name = "codeSystemVersion", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.OPTIONAL)
	public String getCodeSystemVersion() {
		return m_codeSystemVersion;
	}
	/**
	 * Sets the code system version for this name part
	 */
	public void setCodeSystemVersion(String value) {
		this.m_codeSystemVersion = value;
	}
	/**
	 * @return the m_qualifier
	 */
	@Property(name = "qualifier", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.OPTIONAL)
	public SET<CS<EntityNamePartQualifier>> getQualifier() {
		return this.m_qualifier;
	}
	/**
	 * @param m_qualifier the m_qualifier to set
	 */
	public void setQualifier(SET<CS<EntityNamePartQualifier>> value) {
		this.m_qualifier = value;
	}
	/** 
	 * @see org.marc.everest.datatypes.ANY#validateEx()
	 */
	@Override
	public Collection<IResultDetail> validateEx() {
		List<IResultDetail> retVal = (List<IResultDetail>)super.validateEx();

        if (this.isNull() && this.m_value != null)
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "ENXP", EverestValidationMessages.MSG_NULLFLAVOR_WITH_VALUE, null));
        if (this.m_codeSystem != null && this.m_code == null)
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "ENXP", String.format(EverestValidationMessages.MSG_DEPENDENT_VALUE_MISSING, "CodeSystem", "Code"), null));
        if(this.m_qualifier != null && this.m_type != null)
	        for(CS<EntityNamePartQualifier> q : this.m_qualifier)
	            if (!q.getCode().canQualifyPartType(this.m_type.getCode()))
	                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "ENXP", String.format("Qualifier must be one of '%s' when type is populated with '%s'", FormatterUtil.toWireFormat(q.getCode().getAllowedPartTypes()), this.getType().getCode()), null));

        return retVal;        
	}
	/**
	 * @see org.marc.everest.datatypes.HXIT#validate()
	 */
	@Override
	public boolean validate() {
		boolean retVal = this.isNull() ^ (this.m_value != null && ((this.m_codeSystem != null && this.m_code != null) ^ (this.m_code == null)));
        if(this.m_qualifier != null && this.m_type != null)
			for(CS<EntityNamePartQualifier> qlfr : this.m_qualifier)
	            retVal &= qlfr.getCode().canQualifyPartType(this.m_type.getCode());
        return retVal;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.m_value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((m_code == null) ? 0 : m_code.hashCode());
		result = prime * result
				+ ((m_codeSystem == null) ? 0 : m_codeSystem.hashCode());
		result = prime
				* result
				+ ((m_codeSystemVersion == null) ? 0 : m_codeSystemVersion
						.hashCode());
		result = prime * result
				+ ((m_qualifier == null) ? 0 : m_qualifier.hashCode());
		result = prime * result + ((m_type == null) ? 0 : m_type.hashCode());
		result = prime * result + ((m_value == null) ? 0 : m_value.hashCode());
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
		ENXP other = (ENXP) obj;
		if (m_code == null) {
			if (other.m_code != null)
				return false;
		} else if (!m_code.equals(other.m_code))
			return false;
		if (m_codeSystem == null) {
			if (other.m_codeSystem != null)
				return false;
		} else if (!m_codeSystem.equals(other.m_codeSystem))
			return false;
		if (m_codeSystemVersion == null) {
			if (other.m_codeSystemVersion != null)
				return false;
		} else if (!m_codeSystemVersion.equals(other.m_codeSystemVersion))
			return false;
		if (m_qualifier == null) {
			if (other.m_qualifier != null)
				return false;
		} else if (!m_qualifier.equals(other.m_qualifier))
			return false;
		if (m_type == null) {
			if (other.m_type != null)
				return false;
		} else if (!m_type.equals(other.m_type))
			return false;
		if (m_value == null) {
			if (other.m_value != null)
				return false;
		} else if (!m_value.equals(other.m_value))
			return false;
		return true;
	}
	
	
}
