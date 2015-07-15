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
 * Date: 01-16-2013
 */
package org.marc.everest.resultdetails;

import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.interfaces.ResultDetailType;

/**
 * A result detail that signals that a property value has been propagated from a 
 * subordinate property to the root class.
 */
public class PropertyValuePropagatedResultDetail extends ResultDetail
{
    // backing field for value propagated
    public Object m_valuePropagated;
    // backing field for original path
    public String m_originalPath;
    // Gets the destination path that the value has been propagated to
    public String m_destinationPath;

    /**
     * Get the value of the property that was propagated
     * @return
     */
    public Object getValuePropagated() {
		return m_valuePropagated;
	}

    /**
     * Get the original path to the value
     */
	public String getOriginalPath() {
		return m_originalPath;
	}
	
	/**
	 * Get the destination of the value
	 */
	public String getDestinationPath() {
		return m_destinationPath;
	}

	/**
	 * Get the message of the result detail
	 */
    @Override
	public String getMessage() {
        return String.format("Value '%s' set on '%s' has been propagated to '%s'",
                FormatterUtil.toWireFormat(this.m_valuePropagated), this.m_originalPath, this.m_destinationPath);
	}

    /**
     * Creates a new instance of the value propagated result detail
     */
	public PropertyValuePropagatedResultDetail(ResultDetailType type, String originalPath, String destinationPath, Object valuePropagated, String location)
	{
		super(type, null, location, null);
        this.m_destinationPath = destinationPath;
        this.m_originalPath = originalPath;
        this.m_valuePropagated = valuePropagated;
    }


}