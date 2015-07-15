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
 * Date: 06-24-2012
 */
package org.marc.everest.formatters.test;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;
import org.marc.everest.datatypes.*;
import org.marc.everest.datatypes.generic.*;
import org.marc.everest.datatypes.interfaces.IQuantity;
import org.marc.everest.formatters.FormatterUtil;
import org.marc.everest.formatters.XsiType;

public class XSITypeProcessing {

	/**
	 * XSIType Simple Type Test
	 */
	@Test
	public void testCreateXsiTypeSimple() {
		String expected = "II";
		String actual = FormatterUtil.createXsiTypeName(new II());
		Assert.assertEquals(expected, actual);
	}

	/**
	 * XSIType Generic test
	 */
	@Test 
	public void testCreateXsiTypeGeneric() {
		String expected = "IVL_TS";
		String actual = FormatterUtil.createXsiTypeName(new IVL<TS>(TS.now()));
		Assert.assertEquals(expected, actual);
	}
	
	/**
	 * XSIType Generic test
	 */
	@Test 
	public void testCreateXsiTypeCS() {
		String expected = "CS";
		String actual = FormatterUtil.createXsiTypeName(new CS<NullFlavor>(NullFlavor.NoInformation));
		Assert.assertEquals(expected, actual);
	}
	
	/**
	 * XSIType Generic test
	 */
	@Test 
	public void testCreateXsiTypeNested() {
		String expected = "IVL_RTO_RTO_MO_PQ_PQ";
		IVL<RTO<RTO<MO,PQ>,PQ>> instance = new IVL<RTO<RTO<MO,PQ>,PQ>>(
				new RTO<RTO<MO,PQ>,PQ>(
						new RTO<MO,PQ>(
									new MO(BigDecimal.valueOf(1.23), "CAD"),
									new PQ(BigDecimal.valueOf(1), "D")
								),
								new PQ(BigDecimal.valueOf(1), "D")
						)
				
				);
		String actual = FormatterUtil.createXsiTypeName(instance);
		Assert.assertEquals(expected, actual);
	}
	
	/**
	 * XSIType Gneric test
	 */
	@Test 
	public void testCreateXsiTypeDefault() {
		String expected = "IVL_RTO";
		IVL<RTO<IQuantity, IQuantity>> instance = new IVL<RTO<IQuantity, IQuantity>>(
						new RTO<IQuantity, IQuantity>(
								null, null
								)
				);
		String actual = FormatterUtil.createXsiTypeName(instance);
		Assert.assertEquals(expected, actual);
	}
	
	/**
	 * XSI Type Parse (simple) test
	 */
	@Test
	public void testParseXsiTypeSimple()
	{
		String type = "II";
		Class<?> expected = II.class;
		Type actual = FormatterUtil.parseXsiTypeName(type);
		Assert.assertEquals(expected, actual);
	}
	
	/**
	 * Parse XSI Type generic
	 */
	@Test
	public void testParseXsiTypeGenerics()
	{
		String type = "IVL_INT";
		Class<?> expectedRoot = IVL.class, 
				expectedGeneric = INT.class;
		XsiType actual = (XsiType)FormatterUtil.parseXsiTypeName(type);
		Assert.assertEquals(expectedRoot, actual.getUnderlyingClazz());
		Assert.assertEquals(expectedGeneric, actual.getTypeArguments().get(0));
	}
	
	/**
	 * Parse XSI Type default types
	 */
	@Test
	public void testParseXsiTypeDefaults()
	{
		String type = "RTO";
		Class<?> expectedRoot = RTO.class,
				expectedGeneric = IQuantity.class;
		XsiType actual = (XsiType)FormatterUtil.parseXsiTypeName(type);
		Assert.assertEquals(expectedRoot, actual.getUnderlyingClazz());
		Assert.assertEquals(2, actual.getTypeArguments().size());
		Assert.assertEquals(expectedGeneric, actual.getTypeArguments().get(0));
	}
	
	/**
	 * Parse XSI Type with nested generics
	 */
	@Test
	public void testParseXsiTypeNestedGenerics()
	{
		String type = "RTO_IVL_PQ_IVL_MO";
		Class<?> expectedRoot = RTO.class,
				expectedGenericLevel1 = IVL.class,
				expectedGenericLevel2a = PQ.class,
				expectedGenericLevel2b = MO.class;
		XsiType actual = (XsiType)FormatterUtil.parseXsiTypeName(type);
		Assert.assertEquals(expectedRoot, actual.getUnderlyingClazz());
		Assert.assertEquals(2, actual.getTypeArguments().size());
		Assert.assertEquals(expectedGenericLevel1, ((XsiType)actual.getTypeArguments().get(0)).getUnderlyingClazz());
		Assert.assertEquals(expectedGenericLevel1, ((XsiType)actual.getTypeArguments().get(1)).getUnderlyingClazz());
		Assert.assertEquals(expectedGenericLevel2a, ((XsiType)actual.getTypeArguments().get(0)).getTypeArguments().get(0));
		Assert.assertEquals(expectedGenericLevel2b, ((XsiType)actual.getTypeArguments().get(1)).getTypeArguments().get(0));

	}
}
