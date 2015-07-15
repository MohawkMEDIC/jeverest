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

import org.junit.Test;
import org.marc.everest.datatypes.AD;
import org.marc.everest.datatypes.ADXP;
import org.marc.everest.datatypes.AddressPartType;
import org.marc.everest.datatypes.PostalAddressUse;

public class ADTest {

	@Test
	public void ADSemanticEqualsSameDataTest() {
		AD a = AD.fromSimpleAddress(PostalAddressUse.HomeAddress, "123 Main Street West", "Unit 820", "Hamilton", "ON", "CA", "L8K5N2"),
				b = AD.fromSimpleAddress(PostalAddressUse.HomeAddress, "123 Main Street West", "Unit 820", "Hamilton", "ON", "CA", "L8K5N2");
		Assert.assertTrue(a.semanticEquals(b).toBoolean());
	}
	@Test
	public void ADSemanticEqualsDifferentDataTest() {
		AD a = AD.fromSimpleAddress(PostalAddressUse.HomeAddress, "123 Main Street West", "Unit 820", "Hamilton", "ON", "CA", "L8K5N2"),
				b = AD.fromSimpleAddress(PostalAddressUse.HomeAddress, "123 Main Street West", null, "Hamilton", "ON", "CA", "L8K5N2");
		Assert.assertFalse(a.semanticEquals(b).toBoolean());
		Assert.assertFalse(b.semanticEquals(a).toBoolean());
	}
	@Test
	public void ADSemanticEqualsDifferentUseTest() {
		AD a = AD.fromSimpleAddress(PostalAddressUse.HomeAddress, "123 Main Street West", "Unit 820", "Hamilton", "ON", "CA", "L8K5N2"),
				b = AD.fromSimpleAddress(PostalAddressUse.Direct, "123 Main Street West", "Unit 820", "Hamilton", "ON", "CA", "L8K5N2");
		Assert.assertTrue(a.semanticEquals(b).toBoolean());
	}
}
