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
 * Date: 10-25-2012
 */
package org.marc.everest.exceptions;

import org.marc.everest.interfaces.IGraphable;

/**
 * Represents an exception caused by vocabulary parsing or graphing errors
 */
public class VocabularyException extends MessageValidationException {
	// Serialized version uid
	private static final long serialVersionUID = 1L;
	// Backing field for mnemonic
	private String m_mnemonic;
	// Backing field for codeSet
	private String m_codeSet;
	
	/**
	 * Get the mnemonic causing the problem
	 */
	public String getMnemonic() {
		return m_mnemonic;
	}
	/**
	 * Get the code set from which the mnemonic was drawn
	 */
	public String getCodeSet() {
		return m_codeSet;
	}
	
	/**
	 * Creates a new instance of the VocabularyException class
	 */
	public VocabularyException() { super(); }
	/**
	 * Creates a new instance of the VocabularyException
	 * @param message
	 */
    public VocabularyException(String message) { super(message); }
    /**
     * Creates a new instance of the VocabularyException class with specified message and inner exception
     */
    public VocabularyException(String message, Exception innerException) { super(message, innerException); }
    /**
     * Creates a new instance of the VocabularyException class with the specified message, and offender
     */
    public VocabularyException(String message, IGraphable offender) { super(message, offender); }
    /** 
     * Creates a new instance of the VocabularyException class with the specified message, offender and inner exception
     * @param message
     * @param offender
     * @param innerException
     */
    public VocabularyException(String message, Exception innerException, IGraphable offender) { super(message, innerException, offender); }
    /**
     * Create a new instance of the VocabularyException class with the specified message, offender and inner exception
     */
    public VocabularyException(String message, String mnemonic, String codeSet, IGraphable offender)
    {
    	super(message, offender);
        this.m_mnemonic = mnemonic;
        this.m_codeSet = codeSet;
    }
}
