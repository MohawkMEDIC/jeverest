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
 * Represents a mesage validation exception
 */
public class MessageValidationException extends RuntimeException {

	// Serialization uid
	private static final long serialVersionUID = 1L;

	// Offender
	private IGraphable m_offender;
	
	/**
	 * Gets the structure that caused this exception
	 */
	public IGraphable getOffender() { return this.m_offender; }

	/**
	 * Constructs new instance of the MessageValidationException class
	 */
	public MessageValidationException() {super(); }
	/**
	 * Constructs a new instance of the MessageValidationException class with the specified message
	 */
	public MessageValidationException(String message) { super(message); }
	/**
	 * Constructs a new instance of the MessageValidationException class with the specfied message and causedBy exception
	 */
	public MessageValidationException(String message, Exception innerException) { super(message, innerException); }
	/**
	 * Constructs a new instance of the MessageValidationException class with the specified message, causedBy and offender 
	 */
	public MessageValidationException(String message, Exception innerException, IGraphable offender)
	{
		this(message, innerException);
		this.m_offender = offender;
	}
	/**
	 * Constructs a new instance of the MessageValidationException with the specified message and offender
	 * @param message
	 * @param offender
	 */
	public MessageValidationException(String message, IGraphable offender)
	{
		this(message);
		this.m_offender = offender;
	}
}
