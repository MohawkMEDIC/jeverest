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
 * Date: 09-25-2012
 */
package org.marc.everest.resultdetails;

import org.marc.everest.interfaces.ResultDetailType;

/**
 * Insufficient repetitions were encountered while parsing or graphing and object
 * <p>
 * This formal constraint violation indicates that an instance has not supplied sufficient
 * repetitions for fulfill the min-occurs constraint on the property.
 * </p>
 */
public class InsufficientRepetitionsResultDetail extends FormalConstraintViolationResultDetail {

	// Serialization version unique identifier
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of the InsufficientRepetitionsResultDetail with the specified type, message, location and exception 
	 * @param type The type of result detail being constructed (error, warning, information)
	 * @param message A textual message which describes the missing element
	 * @param exception The exception that caused the element(s) to not be interpreted (set to null)
	 */
	public InsufficientRepetitionsResultDetail(ResultDetailType type,
			String message, Exception exception) {
		super(type, message, exception);
	}

	/**
	 * Creates a new instance of the InsufficientRepetitionsResultDetail with the specified type, message, location and exception 
	 * @param type The type of result detail being constructed (error, warning, information)
	 * @param message A textual message which describes the missing element
	 * @param exception The exception that caused the element to not be interpreted (set to null)
	 * @param location The message path to the missing elements (or where they should reside)
	 */
	public InsufficientRepetitionsResultDetail(ResultDetailType type,
			String message, String location, Exception exception) {
		super(type, message, location, exception);
	}

	/**
	 * Creates a new instance of the InsufficientRepetitionsResultDetail with the specified type, message, location and exception 
	 * @param type The type of result detail being constructed (error, warning, information)
	 * @param message A textual message which describes the missing element
	 */
	public InsufficientRepetitionsResultDetail(ResultDetailType type,
			String message) {
		super(type, message);
	}

	
}
