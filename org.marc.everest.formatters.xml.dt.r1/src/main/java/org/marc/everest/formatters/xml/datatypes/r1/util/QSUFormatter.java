/* 
 * Copyright 2013 Mohawk College of Applied Arts and Technology
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
 * Date: 05-15-2013 
 */
package org.marc.everest.formatters.xml.datatypes.r1.util;

/**
 * A formatter that can represent a QSET Union on the wire
 * @author fyfej
 *
 */
public class QSUFormatter extends QSETFormatter {

	/* (non-Javadoc)
	 * @see org.marc.everest.formatters.xml.datatypes.r1.util.QSETFormatter#getHandlesType()
	 */
	@Override
	public String getHandlesType() {

		return "QSU";
	}

	
	
}
