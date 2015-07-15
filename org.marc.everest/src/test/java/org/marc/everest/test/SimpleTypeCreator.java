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
 * Date: 06-24-2013
 */
package org.marc.everest.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.marc.everest.annotations.Property;
import org.marc.everest.datatypes.AD;
import org.marc.everest.datatypes.ADXP;
import org.marc.everest.datatypes.ANY;
import org.marc.everest.datatypes.AddressPartType;
import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.CO;
import org.marc.everest.datatypes.ED;
import org.marc.everest.datatypes.EN;
import org.marc.everest.datatypes.ENXP;
import org.marc.everest.datatypes.EncapsulatedDataCompression;
import org.marc.everest.datatypes.EncapsulatedDataIntegrityAlgorithm;
import org.marc.everest.datatypes.EncapsulatedDataRepresentation;
import org.marc.everest.datatypes.EntityNamePartType;
import org.marc.everest.datatypes.EntityNameUse;
import org.marc.everest.datatypes.GTS;
import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.INT;
import org.marc.everest.datatypes.IdentifierScope;
import org.marc.everest.datatypes.MO;
import org.marc.everest.datatypes.ON;
import org.marc.everest.datatypes.PN;
import org.marc.everest.datatypes.PQ;
import org.marc.everest.datatypes.PQR;
import org.marc.everest.datatypes.PostalAddressUse;
import org.marc.everest.datatypes.REAL;
import org.marc.everest.datatypes.SC;
import org.marc.everest.datatypes.ST;
import org.marc.everest.datatypes.TEL;
import org.marc.everest.datatypes.TN;
import org.marc.everest.datatypes.TS;
import org.marc.everest.datatypes.TelecommunicationsAddressUse;
import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.datatypes.generic.CE;
import org.marc.everest.datatypes.generic.CR;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.CV;
import org.marc.everest.datatypes.generic.IVL;
import org.marc.everest.datatypes.generic.LIST;
import org.marc.everest.datatypes.generic.PIVL;
import org.marc.everest.datatypes.generic.RTO;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.datatypes.generic.URG;
import org.marc.everest.datatypes.interfaces.IQuantity;
import org.marc.everest.formatters.FormatterElementContext;

/**
 * A class which creates simple types
 * @author fyfej
 *
 */
public class SimpleTypeCreator {

	/**
	 * Create a string
	 */
	 public static String createString(FormatterElementContext context)
     {
         //Tracer.Trace("Creating String", context);
         return
             context.getPropertyAnnotation() != null ? context.getPropertyAnnotation().name() :
             "String";

     }

	 /**
	  * Create an uncertain range
	  */
	 public static URG<PQ> createURG(FormatterElementContext context)
	 {

		 URG<PQ> retVal = new URG<PQ>();
		 retVal.setLow(new PQ(BigDecimal.valueOf(1.24), "mL"));
		 retVal.setHigh(new PQ(BigDecimal.valueOf(1.45), "mL"));
		 retVal.setProbability(0.95f);
		 return retVal;
	 }
	 /**
	  * Create an ED
	  */
     public static ED createED(FormatterElementContext context) throws NoSuchAlgorithmException, IOException
     {
         //return new ED(new byte[] { 0, 1, 1, 2, 3, 5, 8, 13 }, "other/fibanacci");
		ED retVal = new ED("<test xmlns=\"http://test.com\" xmlns:f=\"http://f.com\">hello<!-- Comment --><f:foo value=\"123\"/><f:me/><test2 xmlns=\"tempuri\"><![CDATA[Test]]></test2></test>");
		retVal.setRepresentation(EncapsulatedDataRepresentation.Xml);
		retVal.setIntegrityCheckAlgorithm(EncapsulatedDataIntegrityAlgorithm.Sha1);
		retVal.setIntegrityCheck(retVal.computeIntegrityCheck());
		ED compressed = null;
		compressed = retVal.compress(EncapsulatedDataCompression.Deflate);
		compressed.setThumbnail(retVal);
		return compressed;
     }

     /**
      * Create TN
      */
     public static TN createTN(FormatterElementContext context)
     {
         return new TN("Bob The Builder");
     }

     /**
      * Create RTO
      */
     public static RTO<IQuantity, IQuantity> createRtoDefault(FormatterElementContext context)
     {
         RTO<PQ,PQ> res = createRtoOfPqAndPq(context);
         return new RTO<IQuantity, IQuantity>(
             res.getNumerator(), res.getDenominator()
         );
     }

     /**
      * Create RTOPQPQ
      */
     public static RTO<PQ, PQ> createRtoOfPqAndPq(FormatterElementContext context)
     {
         RTO<PQ, PQ> result = new RTO<PQ, PQ>();

         result.setNumerator(new PQ());
         result.setDenominator(new PQ());

         result.getNumerator().setValue(BigDecimal.valueOf(1000000001));
         result.getNumerator().setUnit("CAD");

         result.getDenominator().setUnit("d");
         result.getDenominator().setValue(BigDecimal.ONE);

         return result;
     }

     /**
      * Create GTS
      */
     public static GTS createGTS(FormatterElementContext context)
     {
         //Tracer.Trace("Create GTS", context);
         return new GTS(new IVL<TS>(createTS(context), createTS(context)));
     }

     /**
      * Create RTOMOPQ
      */
     public static RTO<MO, PQ> createRtoOfMoAndPq(FormatterElementContext context)
     {
         //Tracer.Trace("Creating RTO<MO,PQ>", context);

         RTO<MO, PQ> result = new RTO<MO, PQ>(
        		 new MO(BigDecimal.valueOf(100000001), "CAD"),
        		 new PQ(BigDecimal.ONE, "d")
        		 );

         return result;
     }

     /**
      * Create IVLPQ
      */
     public static IVL<PQ> createIVLPQ(FormatterElementContext context)
     {
         //Tracer.Trace("Creating IVL<PQ>", context);

         IVL<PQ> result = new IVL<PQ>(
        		 new PQ(BigDecimal.ONE, "d"),
        		 new PQ(BigDecimal.TEN, "d")
        		 );
         return result;
     }

     /**
      * Create IVLTS
      */
     public static IVL<TS> createIVLTS(FormatterElementContext context)
     {
         //Tracer.Trace("Creating IVL<TS>", context);

         IVL<TS> result = new IVL<TS>(createTS(context),
        		 createTS(context));

         return result;
     }

     /**
      * Create PIVLTS
      */
     public static PIVL<TS> createPIVLTS(FormatterElementContext context)
     {
         //Tracer.Trace("Creating PIVL", context);
         PIVL<TS> result = new PIVL<TS>(
             createIVLTS(context),
             new PQ(BigDecimal.ONE, "d")
         );
         return result;
     }

     /**
      * Create PQ
      */
     public static PQ createPQ(FormatterElementContext context)
     {
         //Tracer.Trace("Creating PQ", context);
         PQ retVal = new PQ(BigDecimal.valueOf(1.20394), "d");
         return retVal;
     }

     /**
      * Create MO
      */
     public static MO createMO(FormatterElementContext context)
     {
         //Tracer.Trace("Creating MO", context);

         MO result = new MO(BigDecimal.TEN,"CAD");

         return result;
     }

     /**
      * Create an ANY
      */
     public static ANY createANY(FormatterElementContext context)
     {
         return new CS<String>("InsteadofAny");
     }

     /**
      * Create AD
      */
     public static AD createAD(FormatterElementContext context)
     {
         
    	 return AD.createAD(
    			 SET.createSET(new CS<PostalAddressUse>(PostalAddressUse.HomeAddress)),
    			 new ADXP("123", AddressPartType.BuildingNumber),
    			 new ADXP("Fake", AddressPartType.StreetNameBase),
    			 new ADXP("Street", AddressPartType.StreetType),
    			 new ADXP("1", AddressPartType.UnitIdentifier),
    			 new ADXP("Door", AddressPartType.UnitDesignator),
        		 new ADXP("Anytown", AddressPartType.City),
        		 new ADXP("AnyCounty", AddressPartType.County),
        		 new ADXP("Ontario", AddressPartType.State),
        		 new ADXP("Canada", AddressPartType.Country));
     }

     /**
      * Create BL
      */
     public static BL createBL(FormatterElementContext context)
     {
         //Tracer.Trace("Creating BL", context);
         return BL.TRUE;
     }

     /**
      * Create PN
      **/
     public static PN createPN(FormatterElementContext context)
     {
    	 return PN.fromEN(createEN(context));
     }

     /**
      * Create REAL
      */
     public static REAL createREAL(FormatterElementContext context)
     {
         return new REAL(Math.PI);
     }

     /**
      * Create STring
      */
     public static ST createST(FormatterElementContext context)
     {
         //Tracer.Trace("Creating ST", context);
         return new ST(createString(context));
     }

     /**
      * Create SC
      */
     public static SC createSC(FormatterElementContext context)
     {
         //Tracer.Trace("Creating SC", context);
         return new SC("Hello", "en-US", new CD<String>("120394"));
     }

     /**
      * Create TS
      */
     public static TS createTS(FormatterElementContext context)
     {
         //Tracer.Trace("Creating TS", context);
         return TS.now();
     }

     /**
      * Create ON
      */
     public static ON CreateON(FormatterElementContext context)
     {
    	 return ON.fromEN(EN.createEN(EntityNameUse.Legal, 
    			 new ENXP("St", EntityNamePartType.Prefix),
                 new ENXP("Mary's", EntityNamePartType.Suffix)
    	 ));
     }

     /**
      * Create EN
      */
     public static EN createEN(FormatterElementContext context)
     {
         return EN.createEN(EntityNameUse.Legal, 
                 new ENXP("John", EntityNamePartType.Given),
                 new ENXP("Jacob", EntityNamePartType.Given),
                 new ENXP("Jingleheimer", EntityNamePartType.Family),
                 new ENXP("-", EntityNamePartType.Delimiter),
                 new ENXP("Schmidt", EntityNamePartType.Given)
             
         );
     }

     /**
      * Create INT
      */
     public static INT createINT(FormatterElementContext context)
     {
         //Tracer.Trace("Creating INT", context);

         if (null != context && null != context.getPropertyAnnotation() && context.getPropertyAnnotation().imposeFlavorId().equals(Property.NULL))
         {
             if(context.getPropertyAnnotation().imposeFlavorId().equals("INT.POS"))
                     return new INT(42);
             else if(context.getPropertyAnnotation().imposeFlavorId().equals("INT.NONNEG"))
                     return new INT(0);
             else
                     return new INT(-42);
         }
         else
         {
             return new INT(42);
         }
     }

     /**
      * Create CD
      */
     public static CD<String> createCD(FormatterElementContext context)
     {
         //Tracer.Trace("Creating CD", context);
         CD<String> retVal = new CD<String>("284196006", "2.16.840.1.113883.6.96");
         CD<String> value = new CD<String>("value", "1.2.3.4.5.6");
         CV<String> name = new CV<String>("name", "1.2.3.4.5.6");
         CR<String> qualifier = new CR<String>(name, value);
         LIST<CR<?>> qualifiers = new LIST<CR<?>>();
         qualifiers.add(qualifier);
         retVal.setQualifier(qualifiers);
         return retVal;
     }

     /**
      * Create CV
      */
     public static CV<String> CreateCV(FormatterElementContext context)
     {
         return new CV<String>("284196006","2.16.840.1.113883.6.96");
     }

     /**
      * Create CE
      */
     public static CE<String> createCE(FormatterElementContext context)
     {
         CE<String> retVal = new CE<String>("284196006","2.16.840.1.113883.6.96");
         List<CD<String>> trans = new ArrayList<CD<String>>();
         trans.add(new CD<String>("123", "1.2.3.4.5"));
         retVal.setTranslation(trans);
         return retVal;
     }

     
     /**
      * Create PQR
      */
     public static PQR createPQR(FormatterElementContext context)
     {

         return new PQR(new BigDecimal("1.23"),
             "284196006",
             "2.16.840.1.113883.6.96"
         );
     }
     
     /**
      * Create CO
      */
     public static CO createCO(FormatterElementContext context)
     {

         return new CO(new BigDecimal("1.23"), new CD<String>(
             "284196006",
             "2.16.840.1.113883.6.96"
         ));
     }
     //public static ResponseMode CreateResponseMode(UseContext context)
     //{
     //    //Tracer.Trace("Creating ResponseMode", context);

     //    return ResponseMode.Immediate;
     //}

     //public static QueryRequestLimit CreateQueryRequestLimit(UseContext context)
     //{
     //    //Tracer.Trace("Creating QueryRequestLimit", context);

     //    return QueryRequestLimit.Record;
     //}

     /**
      * Create URG_PQ
      * @param context
      * @return
      */
     public static URG<PQ> createURG_PQ(FormatterElementContext context)
     {
         URG<PQ> result = new URG<PQ>();

         result.setValue(createPQ(context));
         result.setProbability(0.9999999999999f);
         return result;
     }

     /// <summary>
     /// Create a CS
     /// </summary>
     /// <param name="context"></param>
     /// <returns></returns>
     public static CS<String> createCS(FormatterElementContext context)
     {
         //Tracer.Trace("Creating CS", context);

         if(context.getPropertyAnnotation() != null && context.getPropertyAnnotation().name().equals("moodCode"))
             return new CS<String>("EVN");
         return new CS<String>("PPP");
     }

     /**
      * Create TEL
      * @param context
      * @return
      */
     public static TEL createTEL(FormatterElementContext context)
     {
         //Tracer.Trace("Creating TEL", context);

         if (null != context && null != context.getPropertyAnnotation() && !context.getPropertyAnnotation().imposeFlavorId().equals(Property.NULL))
         {
             TEL result = new TEL();
             result.setUse(new SET<CS<TelecommunicationsAddressUse>>());

             if(context.getPropertyAnnotation().imposeFlavorId().equals("TEL.URI"))
             {
                     result.getUse().add(new CS<TelecommunicationsAddressUse>(TelecommunicationsAddressUse.Direct));
                     result.setValue("http://www.marc-hi.ca");
                     return result;
             }
             else if(context.getPropertyAnnotation().imposeFlavorId().equals("TEL.PHONE"))
             {
                 result.getUse().add(new CS<TelecommunicationsAddressUse>(TelecommunicationsAddressUse.WorkPlace));
                 result.setValue("tel:+1-905-575-1212;ext=3112");
                 return result;
             }
             else if(context.getPropertyAnnotation().imposeFlavorId().equals("TEL.PHONEMAIL"))
             {
                 result.getUse().add(new CS<TelecommunicationsAddressUse>(TelecommunicationsAddressUse.WorkPlace));
                 result.setValue("mailto:marc-hi@mohawkcollege.ca");
                 return result; 
             }
             else
             {
                 result.getUse().add(new CS<TelecommunicationsAddressUse>(TelecommunicationsAddressUse.Direct));
                 result.setValue("http://www.marc-hi.ca");
                 return result; 
             }
         }
         else
         {
             TEL result = new TEL();
             result.getUse().add(new CS<TelecommunicationsAddressUse>(TelecommunicationsAddressUse.Direct));
             result.setValue("http://www.marc-hi.ca");
             return result; 
         }
     }

     /**
      * Create II
      */
     public static II createII(FormatterElementContext context)
     {
         //Tracer.Trace("Creating II", context);

         if (null != context && null != context.getPropertyAnnotation() && !context.getPropertyAnnotation().imposeFlavorId().equals(Property.NULL))
         {
             if(context.getPropertyAnnotation().imposeFlavorId().equals("II.BUS"))
             {
                 II retVal = new II("1.2.3.4", UUID.randomUUID().toString());
                 retVal.setScope(IdentifierScope.BusinessIdentifier);
                 return retVal;
             }
             else if(context.getPropertyAnnotation().imposeFlavorId().equals("II.TOKEN"))
            	 return new II(UUID.randomUUID());
             else if(context.getPropertyAnnotation().imposeFlavorId().equals("II.PUBLIC"))
             {
                 II retVal = new II("1.2.3.4", UUID.randomUUID().toString());
                 retVal.setScope(IdentifierScope.BusinessIdentifier);
                 retVal.setDisplayable(true);
                 return retVal;
             }
             else
                 return new II("1.2.3.4", UUID.randomUUID().toString());
         }
         else //Return the default
         {
             return new II("1.2.3.4",
                 UUID.randomUUID().toString());
         }
     }


	

}
