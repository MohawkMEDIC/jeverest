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
 * Date: 12-14-2012
 */
package org.marc.everest.exceptions;

/**
 * Exceptions triggered by the action of sending or receiving 
 * HL7 instances via a connector
 */
public class ConnectorException extends RuntimeException {

	/**
	 * Default serialization identifier
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor
	 */
    public ConnectorException() { super(); }
    /**
     * Creates a new instance of the ConnectorException class with the specified message
     * @param message
     */
    public ConnectorException(String message) { super(message); }
    /**
     * Constructs a new instance of the ConnectorException class with the specified message and cause
     */
    public ConnectorException(String message, Throwable innerException) { super(message, innerException); }
}
