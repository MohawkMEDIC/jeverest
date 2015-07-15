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
 * Date: 07-01-2011
 */
package org.marc.everest.datatypes.generic;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.ED;
import org.marc.everest.datatypes.EverestValidationMessages;
import org.marc.everest.datatypes.NullFlavor;
import org.marc.everest.datatypes.interfaces.IAny;
import org.marc.everest.datatypes.interfaces.ICodedValue;
import org.marc.everest.datatypes.interfaces.IEncapsulatedData;
import org.marc.everest.datatypes.interfaces.ISet;
import org.marc.everest.interfaces.IEnumeratedVocabulary;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.DatatypeValidationResultDetail;


/**
 * Represents codified data whereby the codified value and code system are unknown
 */
@Structure(name = "CV", structureType = StructureType.DATATYPE, defaultTemplateType = java.lang.String.class)
public class CV<T> extends CS<T> implements ICodedValue<T> {

	// backing field for display name
	private String m_displayName;
	// backing field for original text
	private ED m_originalText;
	// backing field for coding rationale
	private SET<CS<CodingRationale>> m_codingRationale;
	// backing field for code system
	private String m_codeSystem;
	// backing field for code system name
	private String m_codeSystemName;
	// backing field for code system version
	private String m_codeSystemVersion;
	// backing field for value set
	private String m_valueSet;
	// backing field for value set version
	private String m_valueSetVersion;
	
	/**
	 * Creates a new instance of CV
	 */
	public CV() { super();  }
	/**
	 * Creates a new instance of CV with the specified code
	 * @param code The code to set within the CV
	 */
	public CV(T code) { super(code); }
	/**
	 * Creates a new instance of CV with the specified code and code system
	 * @param code The code to set within the CV
	 * @param codeSystem The code system to which the code belongs
	 */
	public CV(T code, String codeSystem) { super(code); this.setCodeSystem(codeSystem); }
	/**
	 * Creates a new instance of CV with the specified parameters
	 * @param code The code to set within the CV
	 * @param codeSystem The code system to which the code belongs
	 * @param codeSystemName The name of the code system to which the code belongs
	 * @param codeSystemVersion The version of the code system from which the code was picked from
	 */
	public CV(T code, String codeSystem, String codeSystemName, String codeSystemVersion) {
		this(code, codeSystem);
		this.setCodeSystemName(codeSystemName);
		this.setCodeSystemVersion(codeSystemVersion);
	}
	/**
	 * Creates a new instance of CV with the specified parameters
	 * @param code The code to set within the CV
	 * @param codeSystem The code system to which the code belongs
	 * @param codeSystemName The name of the code system from which the code was picked
	 * @param codeSystemVersion The version of the code system used to select the code
	 * @param displayName A friendly name for the code that is human readable
	 * @param originalText The original text for the code, gives meaning to the reason why the code was selected
	 * @throws UnsupportedEncodingException 
	 */
	public CV(T code, String codeSystem, String codeSystemName, String codeSystemVersion, String displayName, String originalText)
	{
		this(code, codeSystem, codeSystemName, codeSystemVersion);
		if(displayName != null)
			this.m_displayName = displayName;
		
		if(originalText != null)
			this.m_originalText = new ED(originalText);
	}
	
	/**
	 * Gets a human readable name for the code mnemonic
	 */
	@Override
	@Property(name = "displayName", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	public String getDisplayName() { return this.m_displayName; }
	/**
	 * Sets the human readable name for the code mnemonic
	 */
	@Override
	public void setDisplayName(String value) { this.m_displayName = value; }
	/**
	 * Gets the text as seen and or selected by the user who entered the data
	 */
	@Override
	@Property(name = "originalText", conformance = ConformanceType.REQUIRED, propertyType = PropertyType.NONSTRUCTURAL)
	public ED getOriginalText() { return this.m_originalText; }
	/**
	 * Sets the text as seen and or selected by the user who entered the data
	 */
	public void setOriginalText(ED value) { this.m_originalText = value; }
	/**
	 * Sets the text as seen and or selected by the user who entered the data
	 */
	@Override
	public void setOriginalText(IEncapsulatedData value) { this.m_originalText = (ED)value; }
	/**
	 * Gets the reason the code was provided
	 */
	@Override
	@Property(name = "codingRationale", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL, genericSupplier = { CodingRationale.class })
	public ISet<CS<CodingRationale>> getCodingRationale() { return this.m_codingRationale; }
	/**
	 * Sets the reason the code was provided
	 */
	public void setCodingRationale(SET<CS<CodingRationale>> value) { this.m_codingRationale = value; }
	/**
	 * Sets the reason the code was provided
	 */
	@Override
	public void setCodingRationale(ISet<CS<CodingRationale>> value) { this.m_codingRationale = (SET<CS<CodingRationale>>)value; }
	
	/**
	 * Sets the code value of this CS.
	 * <p>
	 * When T is an instance of an IEnumeratedVocabulary then this function will
	 * set codeSystem whenever a codeSystem is not yet set. This is done as a
	 * convenience to creating coded simple values.
	 * </p>
	 * @param value The new value of the code field
	 */
	@Override
	public void setCodeEx(T value) {
		// Set a code system if one is not set
		if(this.m_codeSystem == null && value != null && value instanceof IEnumeratedVocabulary)
			this.m_codeSystem = ((IEnumeratedVocabulary)value).getCodeSystem();
		if(this.m_codeSystemName == null && value != null && value instanceof IEnumeratedVocabulary)
			this.m_codeSystemName = value.getClass().getAnnotation(Structure.class).name();
		if(this.getCode() == null || !this.getCode().equals(value))
			super.setCodeEx(value);
	}

	
	/**
	 * Gets the value of the codeSystem.
	*/
	@Override
	@Property(name = "codeSystem", conformance = ConformanceType.REQUIRED, propertyType = PropertyType.STRUCTURAL)
	public String getCodeSystem() { return this.m_codeSystem; }
	/**
	 * Sets the value of the codeSystem.
	 * <p>
	 * When T is an instance of an IEnumeratedVocabulary, this field becomes a little
	 * redundant as the IEnumeratedVocabulary (set in the code) defines the code system
	 * OID. However there are some instances (for example, the value of a codified observation), 
	 * where T is not an IEnumeratedVocabulary instance, rather a String instance. In these
	 * cases, setCodeSystem would be useful
	 * </p>
	 * @param value The new value of the codeSystem field
	 */
	@Override
	public void setCodeSystem(String value) { this.m_codeSystem = value; }
	
	@Override
	@Property(name = "codeSystemName", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	public String getCodeSystemName() { return this.m_codeSystemName; }
	@Override
	public void setCodeSystemName(String value) { this.m_codeSystemName = value; }
	
	@Override
	@Property(name = "codeSystemVersion", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	public String getCodeSystemVersion() { return this.m_codeSystemVersion; }
	@Override
	public void setCodeSystemVersion(String value) { this.m_codeSystemVersion = value; }
	
	@Property(name = "valueSet", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	public String getValueSet() { return this.m_valueSet; }
	public void setValueSet(String value) { this.m_valueSet = value; }

	@Property(name = "valueSetVersion", conformance = ConformanceType.OPTIONAL, propertyType = PropertyType.STRUCTURAL)
	public String getValueSetVersion() { return this.m_valueSetVersion; }
	public void setValueSetVersion(String value) { this.m_valueSetVersion = value; }


	/**
	 * Determines if the coded value is valid according to the following restrictions:
	 * 
	 * <ul>
	 * 	<li>NullFlavor is specified XOR</li>
	 *  <li>The following conditions are met<ul>
	 *  	<li>Code, CodeSystem, DisplayName or CodeSystemName is populated AND</li>
	 *  	<li>Code is Specified, AND</li>
	 *  	<li>If CodeSYstemName is specified, CodeSystem is Specified, AND</li>
	 *  	<li>If CodeSystemVersion is specified, CodeSystem is specified, AND</li>
	 *  	<li>If CodeSystem is specified, Code is Specified, AND</li>
	 *  	<li>If displayName is specified, Code is Specified</li>
	 *  </ul></li>
	 * </ul>
	 */
	@Override
	public boolean validate()
	{
		boolean isValid = true;

        // If null flavor is other or null flavor not specified, validate content
        if (this.getNullFlavor() == null || this.getNullFlavor().getCode() == NullFlavor.Other)
            isValid &= (
                ((this.getCode() != null) ^ (this.getNullFlavor() != null)) && 
                ((this.getCodeSystemName() != null && this.getCodeSystem() != null) || (this.getCodeSystemName() == null)) &&
                ((this.getCodeSystemVersion() != null && this.getCodeSystem() != null) || (this.getCodeSystemVersion() == null)) &&
                ((this.getCodeSystem() != null && (this.getCode() != null || this.getNullFlavor().getCode() == NullFlavor.Other)) || (this.getCodeSystem() == null)) && // Code System cannot be specified without a code, unless a nullFlavor of other is specified
                ((this.getDisplayName() != null && this.getCode() != null) || (this.getDisplayName() == null)) &&
                ((this.getCode() != null && this.getCode() instanceof String && this.getCodeSystem() != null) || (this.getCode() == null || this.getCode() instanceof IEnumeratedVocabulary)) &&
                ((this.getValueSetVersion() != null && this.getValueSet() != null) || (this.getValueSetVersion() == null)) &&
                ((this.getNullFlavor() != null && this.getNullFlavor().getCode() != null && this.getNullFlavor().getCode() == NullFlavor.Other) || (this.getNullFlavor() == null)));
        else // Null flavor is not null
            isValid &= this.getCode() == null && this.getDisplayName() == null && this.getCodeSystem() == null && this.getCodeSystemName() == null && this.getCodeSystemVersion() == null &&
                this.getOriginalText() == null && this.getValueSet() == null && this.getValueSetVersion() == null;

        return isValid;	
    }
	/* (non-Javadoc)
	 * @see org.marc.everest.datatypes.generic.CS#validateEx()
	 */
	@Override
	public Collection<IResultDetail> validateEx() {
		Collection<IResultDetail> retVal = super.validateEx();

        if (this.getNullFlavor() == null || this.getNullFlavor().getCode().isChildConcept(NullFlavor.Other))
        { 
            if(this.getCode() != null && this.getNullFlavor() != null )
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", EverestValidationMessages.MSG_NULLFLAVOR_WITH_VALUE, null));
            if(this.getCodeSystemName() != null && this.getCodeSystem() == null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", String.format(EverestValidationMessages.MSG_DEPENDENT_VALUE_MISSING, "CodeSystemName", "CodeSystem"), null));
            if(this.getCodeSystemVersion() != null && this.getCodeSystem() == null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", String.format(EverestValidationMessages.MSG_DEPENDENT_VALUE_MISSING, "CodeSystemVersion", "CodeSystem"), null));
            if(this.getCodeSystem() != null && !(this.getCode() != null || this.getNullFlavor() != null && this.getNullFlavor().getCode().isChildConcept(NullFlavor.Other)))
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", "CodeSystem can only be used when Code is populated or NullFlavor does implies Other", null));
            if(this.getDisplayName() != null && this.getCode() == null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", String.format(EverestValidationMessages.MSG_DEPENDENT_VALUE_MISSING, "DisplayName", "CodeSystem"), null));
            if(this.getValueSetVersion() != null && this.getValueSet() == null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", String.format(EverestValidationMessages.MSG_DEPENDENT_VALUE_MISSING, "ValueSetVersion", "ValueSet"), null));
        }
        else // NullfLavor is something other than OTHER
        { 
            if(this.getCode() != null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", "Code may only be specified when NullFlavor is not populated, or is populated with a NullFlavor that implies 'Other'", null));
            if(this.getCodeSystem() != null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", "CodeSystem may only be specified when NullFlavor is not populated, or is populated with a NullFlavor that implies 'Other'", null));
            if(this.getCodeSystemVersion() != null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", "CodeSystemVersion may only be specified when NullFlavor is not populated, or is populated with a NullFlavor that implies 'Other'", null));
            if(this.getCodeSystemName() != null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", "CodeSystemName may only be specified when NullFlavor is not populated, or is populated with a NullFlavor that implies 'Other'", null));
            if(this.getOriginalText() != null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", "OriginalText may only be specified when NullFlavor is not populated, or is populated with a NullFlavor that implies 'Other'", null));
            if(this.getValueSet() != null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", "ValueSet may only be specified when NullFlavor is not populated, or is populated with a NullFlavor that implies 'Other'", null));
            if(this.getValueSetVersion() != null)
                retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, "CV", "ValueSetVersion may only be specified when NullFlavor is not populated, or is populated with a NullFlavor that implies 'Other'", null));

        }
        return retVal ;
	}
	
	/**
	 * Determine semantic equality
	 */
	@Override
	public BL semanticEquals(IAny other) {
		BL baseSem = super.semanticEquals(other);
		if(!baseSem.toBoolean())
			return baseSem;
		
		if(other instanceof ICodedValue<?>)
		{
			ICodedValue<?> otherCv = (ICodedValue<?>)other;
			if(otherCv.getCodeSystem() == null && this.getCodeSystem() == null || otherCv.getCodeSystem().equals(this.getCodeSystem()))
				return BL.TRUE;
			else
				return BL.FALSE;
		}
		else
			return BL.FALSE;
	}

	
	
}

