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
 * Date: 10-29-2012
 */
package org.marc.everest.exceptions;

/**
 * Represents an exception that occurs when a date is not valid HL7 format 
 */
public class HL7DateFormatException extends RuntimeException {

	// Backing date value in error
	private String m_dateValueInError;
	
	// Serialization Id
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of the date format exception
	 */
	public HL7DateFormatException() { super(); }
	
	/**
	 * Creates a new instance of the date format exception specifying the date format string whihc was in error
	 * @param dateValueInError The date string that could not be parsed
	 */
	public HL7DateFormatException(String dateValueInError)
	{
		super();
		this.m_dateValueInError = dateValueInError;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return String.format("Date '%s' is not valid", this.m_dateValueInError);
	}


}
