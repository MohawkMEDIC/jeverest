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
 * Date: 12-14-2012
 */
package org.marc.everest.connectors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a utility for connector functions 
 */
public class ConnectorUtil {


	/**
	 * Parses a connection string into a dictionary of string and lists representing the 
	 * connection string parameter and the values associated with the parameter
	 */
	public static HashMap<String, List<String>> parseConnectionString(String connectionString)
	{
		HashMap<String,List<String>> retVal = new HashMap<String,List<String>>();

        for(String kvPair : connectionString.split(";"))
        {
            String[] kv = kvPair.split("=");
            if (kv.length != 2) continue;
            String key = kv[0].toLowerCase().trim(), value = kv[1].trim();
            if (!retVal.containsKey(key))
                retVal.put(key, Arrays.asList(new String[] { value }));
            else
                retVal.get(key).add(kv[1].trim());
        }
        return retVal;
	}
	
}
