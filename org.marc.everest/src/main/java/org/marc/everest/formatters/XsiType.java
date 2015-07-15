/**
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
 * Date: 12-18-2012
 */
package org.marc.everest.formatters;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a type implementation that is capable of carrying generic information 
 * provided from an XsiType definition
 */
public class XsiType implements Type {

	/**
	 * Parameter types (if any)
	 */
	private List<Type> m_argumentTypes = new ArrayList<Type>();
	
	/**
	 * Gets the name of the type
	 */
	private Class<?> m_underlyingClazz;
	
	/**
	 * Creates a new instance of the XsiType with the specified underlying class
	 */
	public XsiType(Class<?> underlyingClazz)
	{
		this.m_underlyingClazz = underlyingClazz;
	}
	
	/**
	 * Get the type arguments
	 */
	public List<Type> getTypeArguments()
	{
		return this.m_argumentTypes;
	}
	
	/**
	 * Get the underlying class
	 * @return
	 */
	public Class<?> getUnderlyingClazz()
	{
		return this.m_underlyingClazz;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getUnderlyingClazz().getName());
		if(this.m_argumentTypes.size() > 0)
		{
			sb.append("[");
			for(int i = 0; i < this.m_argumentTypes.size(); i++)
			{
				sb.append(this.m_argumentTypes.get(i).toString());
				if(i != this.m_argumentTypes.size() - 1)
					sb.append(",");
			}
			sb.append("]");
		}
		return sb.toString();
	}
	
	
	
}
