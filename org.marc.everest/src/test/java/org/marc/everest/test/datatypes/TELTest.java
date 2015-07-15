/* 
 * Copyright 2008-2014 Mohawk College of Applied Arts and Technology
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
 * Date: 03-06-2014
 */
package org.marc.everest.test.datatypes;

import org.junit.*;
import junit.framework.Assert;

import org.junit.Test;
import org.marc.everest.datatypes.TEL;
import org.marc.everest.datatypes.TelecommunicationsAddressUse;

public class TELTest {

	@Test
	public void TELSemanticEqualsDifferentNumberTest() {
		TEL a = new TEL("tel:+1-905-575-1212;postd=1023", TelecommunicationsAddressUse.Home),
				b = new TEL("tel:+1-905-575-1212;postd=3045", TelecommunicationsAddressUse.Home);
		Assert.assertFalse(a.semanticEquals(b).toBoolean());
	}
	@Test
	public void TELSemanticEqualsSameNumberTest() {
		TEL a = new TEL("tel:+1-905-575-1212;postd=1023", TelecommunicationsAddressUse.Home),
				b = new TEL("tel:+1-905-575-1212;postd=1023", TelecommunicationsAddressUse.Home);
		Assert.assertTrue(a.semanticEquals(b).toBoolean());
	}
	@Test
	public void TELSemanticEqualsSameNumberDifferentUseTest() {
		TEL a = new TEL("tel:+1-905-575-1212;postd=1023", TelecommunicationsAddressUse.WorkPlace),
				b = new TEL("tel:+1-905-575-1212;postd=1023", TelecommunicationsAddressUse.Home);
		Assert.assertTrue(a.semanticEquals(b).toBoolean());
	}

}
