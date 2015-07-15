package org.marc.everest.rmim.uv.cdar2.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;
import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.ST;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.R1FormatterCompatibilityMode;
import org.marc.everest.formatters.xml.its1.XmlIts1Formatter;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalDocument;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Component2;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Component3;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Consumable;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Entry;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.EntryRelationship;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Specimen;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.StructuredBody;
import org.marc.everest.xml.XMLStateStreamWriter;

public class CustomTypeImplementationTest {

	/**
	 * Create a document
	 */
	private ClinicalDocument createDocument()
	{
		ClinicalDocument cd = new ClinicalDocument();
		cd.setComponent(new Component2());
		cd.getComponent().setBodyChoice(new StructuredBody());
		Component3 section = new Component3();
		section.setSection(new Section());
		Entry ent = new Entry();
		CustomAdministration observation = new CustomAdministration();
		observation.setExtraProperty(BL.FALSE);
		observation.setExtraData(new ST("This is a test"));
		observation.getSpecimen().add(new Specimen());
		observation.setConsumable(new Consumable());
		observation.getEntryRelationship().add(new EntryRelationship());
		
		
		ent.setClinicalStatement(observation);
		section.getSection().getEntry().add(ent);
		
		cd.getComponent().getBodyChoiceIfStructuredBody().getComponent().add(section);
		return cd;
	}
	
	/**
	 * A simple test which verifies that Everest formatters are outputting an 
	 * appropriate XSI:TYPE
	 */
	@Test
	public void formatCustomAdministrationWithXsiType() {
		
		XmlIts1Formatter fmtr = new XmlIts1Formatter();
		fmtr.getGraphAides().add(new DatatypeFormatter(R1FormatterCompatibilityMode.ClinicalDocumentArchitecture));
		
		// Output to a string
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			fmtr.graph(baos, this.createDocument());
			String data = new String(baos.toByteArray());
			System.out.println(data);
			Assert.assertTrue(data.contains("xsi:type=\"POCD_MT000040.SubstanceAdministration"));
		}
		finally
		{
			try {
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * A simple test which verifies that Everest formatters aren't outputting an 
	 * appropriate XSI:TYPE
	 */
	@Test
	public void formatCustomRecordTargetWithXsiType() {
		
		XmlIts1Formatter fmtr = new XmlIts1Formatter();
		fmtr.getGraphAides().add(new DatatypeFormatter(R1FormatterCompatibilityMode.ClinicalDocumentArchitecture));
		
		// Output to a string
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			fmtr.registerXSITypeName("POCD_MT000040UV.RecordTarget", CustomRecordTarget.class);
			ClinicalDocument doc = this.createDocument();
			CustomRecordTarget rct = new CustomRecordTarget();
			rct.setId(new II(UUID.randomUUID()));
			doc.getRecordTarget().add(rct);
			fmtr.graph(baos, doc);
			String data = new String(baos.toByteArray());
			System.out.println(data);
			Assert.assertTrue(data.contains("xsi:type=\"POCD_MT000040.SubstanceAdministration"));
			Assert.assertFalse(data.contains("xsi:type=\"POCD_MT000040UV.RecordTarget"));
		}
		finally
		{
			try {
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * A simple test which verifies that Everest formatters are parsing
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void parseCustomRecordTargetWithXsiType() {
		
		XmlIts1Formatter fmtr = new XmlIts1Formatter();
		fmtr.getGraphAides().add(new DatatypeFormatter(R1FormatterCompatibilityMode.ClinicalDocumentArchitecture));
		
		// Output to a string
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = null;
		try
		{
			fmtr.registerXSITypeName("POCD_MT000040UV.RecordTarget", CustomRecordTarget.class);
			fmtr.addCachedClass(ClinicalDocument.class);
			ClinicalDocument doc = this.createDocument();
			CustomRecordTarget rct = new CustomRecordTarget();
			rct.setId(new II(UUID.randomUUID()));
			doc.getRecordTarget().add(rct);
			fmtr.graph(baos, doc);
			String data = new String(baos.toByteArray());
			System.out.println(data);
			
			bais = new ByteArrayInputStream(baos.toByteArray());
			doc = (ClinicalDocument)fmtr.parse(bais).getStructure();
			Assert.assertEquals(1, doc.getRecordTarget().size());
			Assert.assertTrue(doc.getRecordTarget().get(0) instanceof CustomRecordTarget);
			Assert.assertTrue(((CustomRecordTarget)doc.getRecordTarget().get(0)).getId() != null);
			
		}
		finally
		{
			try {
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
