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
 * Date: 08-16-2011
 */
package org.marc.everest.datatypes.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.marc.everest.annotations.*;
import org.marc.everest.datatypes.*;
import org.marc.everest.datatypes.interfaces.IEncapsulatedData;
import org.marc.everest.datatypes.interfaces.IQuantity;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultDetailType;
import org.marc.everest.resultdetails.DatatypeValidationResultDetail;


/**
 * The quantity data type is an abstract generalization for all data types whose value set has an
 * order relation and where difference is defined in all of the data type's totally ordered value.
 */
@Structure(name = "QTY", structureType = StructureType.DATATYPE)
public abstract class QTY<T> extends PDV<T> implements IQuantity {

	// backing field for expression
	private ED m_expression;
	// Backing field for expression language
	private String m_expressionLanguage;
	// backing field for original text
	private ED m_originalText;
	// backing field for uncertainty
	private IQuantity m_uncertainty;
	// backing field for uncertainty type
	private QuantityUncertaintyType m_uncertaintyType;
	// backing field for uncertainty range
	private IVL<IQuantity> m_uncertaintyRange;
	
	/**
	 * Creates a new instance of QTY
	 */
	public QTY() {	}

	/**
	 * Creates a new instance of QTY with the specified value
	 */
	public QTY(T value) {
		super(value);
	}

	/**
	 * Gets an expression that represents the value of the quantity
	 */
	@Property(name = "expression", propertyType = PropertyType.NONSTRUCTURAL, conformance = ConformanceType.OPTIONAL)
	@Override
	public ED getExpression() { return this.m_expression; }
	/**
	 * Sets an expression that represents the value of the quantity
	 */
	public void setExpression(ED value) { this.m_expression = value; }
	/**
	 * Sets an expression that represents the value of the quantity
	 */
	@Override
	public void setExpression(IEncapsulatedData value) { this.m_expression = (ED)value; }
	/**
	 * Gets the language used for the expression
	 */
	@Property(name="expressionLanguage", propertyType = PropertyType.NONSTRUCTURAL, conformance = ConformanceType.REQUIRED)
	public String getExpressionLanguage() { return this.m_expressionLanguage; }
	/**
	 * Sets the language used for the expression
	 */
	public void setExpressionLanguage(String value) { this.m_expressionLanguage = value; }
	/**
	 * Gets a value that represents the original text that was used to derive the quantity 
	 */
	@Property(name = "originalText", propertyType = PropertyType.NONSTRUCTURAL, conformance = ConformanceType.REQUIRED)
	public ED getOriginalText() { return this.m_originalText; }
	/**
	 * Sets the original text that was used to derive the quantity
	 */
	public void setOriginalText(ED value) { this.m_originalText = value; }
	/**
	 * Gets a value that represents the uncertainty of the quantity using a distribution function
	 * and its parameters
	 */
	@Property(name = "uncertainty", propertyType = PropertyType.NONSTRUCTURAL, conformance = ConformanceType.REQUIRED)
	@Override
	public IQuantity getUncertainty() { return this.m_uncertainty; }
	/**
	 * Sets a value that represents the uncertainty of the quantity using a distribution function
	 * and its parameters
	 */
	public void setUncertainty(IQuantity value) { this.m_uncertainty = value; }
	/**
	 * Gets a code specifying the type of probability distribution in uncertainty.
	 */
	@Property(name="uncertaintyType", propertyType = PropertyType.STRUCTURAL, conformance = ConformanceType.REQUIRED)
	@Override
	public QuantityUncertaintyType getUncertaintyType() { return this.m_uncertaintyType; }
	/**
	 * Sets a code specifying the type of probability distribution in uncertainty.
	 */
	public void setUncertaintyType(QuantityUncertaintyType value) { this.m_uncertaintyType = value; }
	/**
	 * Gets the value that indicates the value comes from a range of possible values
	 * <p>The uncertain range is used where the actual value is not known,
	 * but a range of possible values are</p>
	 */
	@Property(name = "uncertainRange", propertyType = PropertyType.NONSTRUCTURAL, conformance = ConformanceType.REQUIRED)
	@Override
	public IVL<IQuantity> getUncertainRange() { return this.m_uncertaintyRange; }
	/**
	 * Sets the value that indicates the value comes from a range of possible values
	 * <p>The uncertain range is used where the actual value is not known,
	 * but a range of possible values are</p>
	 */
	public void setUncertainRange(IVL<IQuantity> value) { this.m_uncertaintyRange = value; }
	
	
	/**
	 * Validate the structure
	 */
	@Override
	public boolean validate()
	{
        boolean valid = (this.getValue() == null && this.getNullFlavor() != null) || (this.getNullFlavor() == null && this.getValue() != null);

        // When 
        // Uncertainty or uncertainty range must be used exclusively
        valid &= ((this.getUncertainRange() != null) ^ (this.getUncertainty() != null)) || this.getUncertainRange() == null && this.getUncertainty() == null;

        // When Uncertainty is populated, the Uncertainty type must be populated
        valid &= ((this.getUncertainty() != null && this.getUncertaintyType() != null) || this.getUncertainty() == null);

        // When the uncertain range is populated it cannot have a width or value
        valid &= ((this.getUncertainRange() != null && this.getUncertainRange().getWidth() == null && this.getUncertainRange().getValue() == null) || this.getUncertainRange() == null);

        return valid;
	}

	/* (non-Javadoc)
	 * @see ca.marc.everest.datatypes.generic.PDV#validateEx()
	 */
	@Override
	public Collection<IResultDetail> validateEx() {
		List<IResultDetail> retVal = new ArrayList<IResultDetail>();
        
        // Either null Flavor or Value or Uncertain range
        if ((this.getValue() != null) && this.getNullFlavor() != null)
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, this.getDataTypeName(), EverestValidationMessages.MSG_NULLFLAVOR_WITH_VALUE, null));
        if(this.getValue() == null && this.getNullFlavor() == null)
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, this.getDataTypeName(), EverestValidationMessages.MSG_NULLFLAVOR_MISSING, null));
        if(!((this.getUncertainRange() != null) ^ (this.getUncertainty() != null)|| this.getUncertainRange() == null && this.getUncertainty() == null))
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, this.getDataTypeName(), String.format(EverestValidationMessages.MSG_INDEPENDENT_VALUE, "Uncertainty", "UncertainRange"), null));
        if (!((this.getUncertainty() != null && this.getUncertaintyType() != null) || this.getUncertainty() == null))
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, this.getDataTypeName(), String.format(EverestValidationMessages.MSG_DEPENDENT_VALUE_MISSING, "Uncertainty", "UncertaintyType"), null));
        if(!((this.getUncertainRange() != null && this.getUncertainRange().getWidth() == null && this.getUncertainRange().getValue() == null) || this.getUncertainRange() == null))
            retVal.add(new DatatypeValidationResultDetail(ResultDetailType.ERROR, this.getDataTypeName(), String.format(EverestValidationMessages.MSG_PROPERTY_NOT_PERMITTED, "Width or Value", "UncertainRange"), null));
        return retVal;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((m_expression == null) ? 0 : m_expression.hashCode());
		result = prime
				* result
				+ ((m_expressionLanguage == null) ? 0 : m_expressionLanguage
						.hashCode());
		result = prime * result
				+ ((m_originalText == null) ? 0 : m_originalText.hashCode());
		result = prime * result
				+ ((m_uncertainty == null) ? 0 : m_uncertainty.hashCode());
		result = prime
				* result
				+ ((m_uncertaintyRange == null) ? 0 : m_uncertaintyRange
						.hashCode());
		result = prime
				* result
				+ ((m_uncertaintyType == null) ? 0 : m_uncertaintyType
						.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		QTY<?> other = (QTY<?>) obj;
		if (m_expression == null) {
			if (other.m_expression != null)
				return false;
		} else if (!m_expression.equals(other.m_expression))
			return false;
		if (m_expressionLanguage == null) {
			if (other.m_expressionLanguage != null)
				return false;
		} else if (!m_expressionLanguage.equals(other.m_expressionLanguage))
			return false;
		if (m_originalText == null) {
			if (other.m_originalText != null)
				return false;
		} else if (!m_originalText.equals(other.m_originalText))
			return false;
		if (m_uncertainty == null) {
			if (other.m_uncertainty != null)
				return false;
		} else if (!m_uncertainty.equals(other.m_uncertainty))
			return false;
		if (m_uncertaintyRange == null) {
			if (other.m_uncertaintyRange != null)
				return false;
		} else if (!m_uncertaintyRange.equals(other.m_uncertaintyRange))
			return false;
		if (m_uncertaintyType != other.m_uncertaintyType)
			return false;
		return true;
	}
	
	
}
