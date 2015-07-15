/* 
 * Copyright 2011-2012 Mohawk College of Applied Arts and Technology
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
 * Date: 11-09-2012
 */
package org.marc.everest.formatters.xml.datatypes.r1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.marc.everest.datatypes.interfaces.IPredicate;
import org.marc.everest.formatters.interfaces.IFormatterGraphResult;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;

/**
 * DT R1 formatter graph result
 */
public class DatatypeFormatterGraphResult implements IFormatterGraphResult {
	
	
	// Backing field for details
	private List<IResultDetail> m_details = new ArrayList<IResultDetail>(10);

	// Backing field for compatibility mode
	private R1FormatterCompatibilityMode m_hostCompatibilityMode = R1FormatterCompatibilityMode.Universal;
	
	// Backing field for validate conformance
	private boolean m_validateConformance = true;
	
	// backing field for result code
	private ResultCodeType m_resultCode = ResultCodeType.Accepted;
	
	/**
	 * Gets a value indicating whether the host formatter is validating conformance
	 */
	public boolean getValidateConformance() { return this.m_validateConformance; }

	/**
	 * Gets a value indicating the compatibility mode for the formatter
	 * @return
	 */
	public R1FormatterCompatibilityMode getCompatibilityMode() { return this.m_hostCompatibilityMode; }

	/**
	 * Package scoped constructor
	 */
	DatatypeFormatterGraphResult(R1FormatterCompatibilityMode compatibilityMode, boolean validatingConformance) {
		this.m_validateConformance = validatingConformance;
		this.m_hostCompatibilityMode = compatibilityMode;
	}
	
	/**
	 * Package scoped constructor
	 */
	DatatypeFormatterGraphResult(R1FormatterCompatibilityMode compatibilityMode, boolean validatingConformance, ResultCodeType result) {
		this(compatibilityMode, validatingConformance);
		this.m_resultCode = result;
	}
	
	/**
	 * Package scoped constructor
	 */
	DatatypeFormatterGraphResult(R1FormatterCompatibilityMode compatibilityMode, boolean validatingConformance, ResultCodeType result, Collection<IResultDetail> details)
	{
		this(compatibilityMode, validatingConformance, result);
		this.m_details = new ArrayList<IResultDetail>(details);
	}
	
	/**
	 * Add a result detail item to the result collection
	 */
	public void addResultDetail(IResultDetail detail)
	{
		if(detail != null)
			this.m_details.add(detail);
	}
	/**
	 * Add a result detail item to the result collection
	 */
	public void addResultDetail(Iterable<IResultDetail> detail)
	{
		for(IResultDetail dtl : detail)
			this.m_details.add(dtl);
	}	
	/**
	 * Removes all result details matching the predicate
	 * @param removeFilter
	 */
	public void removeResultDetail(IPredicate<IResultDetail> removeFilter)
	{
		if(removeFilter == null)
			throw new IllegalArgumentException("removeFilter");
		for(int i = this.m_details.size() - 1; i >= 0; i--)
			if(removeFilter.match(this.m_details.get(i)))
				this.m_details.remove(i);
	}
	/**
	 * Get the results of the graphing operation
	 */
	@Override
	public Iterable<IResultDetail> getDetails() {
		return this.m_details;
	}

	/**
	 * Sets the overall result code
	 */
	public void setCode(ResultCodeType value)
	{
		this.m_resultCode = value;
	}

	/**
	 * Gets the overall result code
	 */
	@Override
	public ResultCodeType getCode() {
		return this.m_resultCode;
	}

	
}
