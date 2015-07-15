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
import org.marc.everest.datatypes.EntityNameUse;
import org.marc.everest.datatypes.PN;

public class PNTest {

	/**
	 * Determine semantic equality between objects
	 */
	@Test
	public void PNSemanticEqualsSameNameTest() {
		PN a = PN.fromFamilyGiven(EntityNameUse.Legal, "Fyfe", "Justin"),
				b = PN.fromFamilyGiven(EntityNameUse.Legal, "Fyfe", "Justin");
		Assert.assertTrue(a.semanticEquals(b).toBoolean());
	}

	/**
	 * Determine semantic equality between objects
	 */
	@Test
	public void PNSemanticEqualsDifferentNameTest() {
		PN a = PN.fromFamilyGiven(EntityNameUse.Legal, "Fyfe", "Justin"),
				b = PN.fromFamilyGiven(EntityNameUse.Legal, "Fyfe", "Justine");
		Assert.assertFalse(a.semanticEquals(b).toBoolean());
	}

	/**
	 * Determine semantic equality between objects
	 */
	@Test
	public void PNSemanticEqualsDifferentNameAltTest() {
		PN a = PN.fromFamilyGiven(EntityNameUse.Legal, "Smith", "Thomas"),
				b = PN.fromFamilyGiven(EntityNameUse.Legal, "Fyfe", "Justin");
		Assert.assertFalse(a.semanticEquals(b).toBoolean());
	}
	
	/**
	 * Determine semantic equality between objects
	 */
	@Test
	public void PNSemanticEqualsDifferentNameOrderTest() {
		PN a = PN.fromFamilyGiven(EntityNameUse.Legal, "Fyfe", "Justin", "Thomas"),
				b = PN.fromFamilyGiven(EntityNameUse.Legal, "Fyfe", "Thomas", "Justin");
		Assert.assertTrue(a.semanticEquals(b).toBoolean());
	}
	
	
	/**
	 * Determine semantic equality between objects
	 */
	@Test
	public void PNSemanticEqualsDifferentUSeTest() {
		PN a = PN.fromFamilyGiven(EntityNameUse.Legal, "Fyfe", "Justin"),
				b = PN.fromFamilyGiven(EntityNameUse.Artist, "Fyfe", "Justin");
		Assert.assertTrue(a.semanticEquals(b).toBoolean());
	}
	
	
	
}
