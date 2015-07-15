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
import org.marc.everest.datatypes.PN;
import org.marc.everest.datatypes.PostalAddressUse;
import org.marc.everest.datatypes.TEL;
import org.marc.everest.datatypes.TelecommunicationsAddressUse;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.exceptions.DuplicateItemException;

public class SETTest {

	/**
	 * SET of AD test
	 */
	@Test
	public void SETSameADAddedTest() {
		AD ad1 = AD.fromSimpleAddress(PostalAddressUse.HomeAddress, "123 Main Street West", "Unit 820", "Hamilton", "ON", "CA", "L8K5N2"),
				ad2 = AD.fromSimpleAddress(PostalAddressUse.HomeAddress, "123 Main Street West", "Unit 820", "Hamilton", "ON", "CA", "L8K5N2");
					
		try
		{
			SET<AD> a = SET.createSET(ad1, ad2);
			Assert.fail("Should've thrown an exception");
		}
		catch(DuplicateItemException e)
		{
		}
		catch(Exception e)
		{
			Assert.fail();
		}
	}
	
	/**
	 * SET of AD test
	 */
	@Test
	public void SETDifferentADAddedTest() {
		AD ad1 = AD.fromSimpleAddress(PostalAddressUse.HomeAddress, "123 Main Street West", "Unit 820", "Hamilton", "ON", "CA", "L8K5N2"),
				ad2 = AD.fromSimpleAddress(PostalAddressUse.HomeAddress, "123 Main Street West", null, "Hamilton", "ON", "CA", "L8K5N2");
					
		try
		{
			SET<AD> a = SET.createSET(ad1, ad2);
		}
		catch(Exception e)
		{
			Assert.fail("Shouldn't throw an exception");
		}
	}

	/**
	 * SET of AD test
	 */
	@Test
	public void SETSameTELAddedTest() {
		TEL tel1 = new TEL("tel:+1", TelecommunicationsAddressUse.Direct), 
				tel2 = new TEL("tel:+1", TelecommunicationsAddressUse.Home);
					
		try
		{
			SET<TEL> a = SET.createSET(tel1, tel2);
			Assert.fail("Should've thrown an exception");
		}
		catch(DuplicateItemException e)
		{
		}
		catch(Exception e)
		{
			Assert.fail();
		}
	}

	/**
	 * SET of AD test
	 */
	@Test
	public void SETDifferentTELAddedTest() {
		TEL tel1 = new TEL("tel:+1", TelecommunicationsAddressUse.Direct), 
				tel2 = new TEL("tel:+7", TelecommunicationsAddressUse.Home);
					
		try
		{
			SET<TEL> a = SET.createSET(tel1, tel2);
		}
		catch(Exception e)
		{
			Assert.fail("Should not have thrown an exception");
		}
	}

}
