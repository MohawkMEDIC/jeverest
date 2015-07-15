/**
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
 * Date: 06-10-2013
 */
package org.marc.everest.samples.connectors.spring.client;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.marc.everest.connectors.interfaces.IReceiveResult;
import org.marc.everest.connectors.interfaces.ISendResult;
import org.marc.everest.connectors.spring.SpringClientConnector;
import org.marc.everest.datatypes.*;
import org.marc.everest.datatypes.generic.*;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.R1FormatterCompatibilityMode;
import org.marc.everest.formatters.xml.its1.XmlIts1Formatter;
import org.marc.everest.interfaces.IResultDetail;
import org.marc.everest.interfaces.ResultCodeType;
import org.marc.everest.rmim.ca.r020402.coct_mt090102ca.AssignedEntity;
import org.marc.everest.rmim.ca.r020402.coct_mt090102ca.Person;
import org.marc.everest.rmim.ca.r020402.interaction.PRPA_IN101103CA;
import org.marc.everest.rmim.ca.r020402.interaction.PRPA_IN101104CA;
import org.marc.everest.rmim.ca.r020402.mcci_mt002200ca.AcknowledgementDetail;
import org.marc.everest.rmim.ca.r020402.mcci_mt002200ca.Device1;
import org.marc.everest.rmim.ca.r020402.mcci_mt002200ca.Device2;
import org.marc.everest.rmim.ca.r020402.mcci_mt002200ca.Receiver;
import org.marc.everest.rmim.ca.r020402.mcci_mt002200ca.Sender;
import org.marc.everest.rmim.ca.r020402.mfmi_mt700746ca.RegistrationEvent;
import org.marc.everest.rmim.ca.r020402.mfmi_mt700746ca.Subject2;
import org.marc.everest.rmim.ca.r020402.mfmi_mt700751ca.Author;
import org.marc.everest.rmim.ca.r020402.mfmi_mt700751ca.ControlActEvent;
import org.marc.everest.rmim.ca.r020402.prpa_mt101103ca.ParameterList;
import org.marc.everest.rmim.ca.r020402.prpa_mt101103ca.PersonName;
import org.marc.everest.rmim.ca.r020402.prpa_mt101104ca.IdentifiedEntity;
import org.marc.everest.rmim.ca.r020402.quqi_mt120008ca.QueryByParameter;
import org.marc.everest.rmim.ca.r020402.vocabulary.AcknowledgementCondition;
import org.marc.everest.rmim.ca.r020402.vocabulary.AcknowledgementType;
import org.marc.everest.rmim.ca.r020402.vocabulary.ProcessingID;
import org.marc.everest.rmim.ca.r020402.vocabulary.ResponseMode;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

/**
 * A simple program that illustrates the construction of a client registry message
 * and interprets the result.
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Create the request
		PRPA_IN101103CA request = createPRPA_IN101103CA();
		
		// Create the connector
		ApplicationContext appContext = new ClassPathXmlApplicationContext("sample.context.xml");
		SpringClientConnector connector = new SpringClientConnector(appContext, "templateBeanName=clientRegistry");
		
		// Create the XmlIts1Formatter
		XmlIts1Formatter fmtr = new XmlIts1Formatter();
		fmtr.setValidateConformance(false);
		fmtr.getGraphAides().add(new DatatypeFormatter(R1FormatterCompatibilityMode.Canadian));
		connector.setFormatter(fmtr);
		fmtr.addCachedClass(PRPA_IN101103CA.class);
		fmtr.addCachedClass(PRPA_IN101104CA.class);
		
		// Send & receive
		try
		{
			connector.open();
			fmtr.graph(System.out, request);
			ISendResult sendResult = connector.send(request);
			
			// Output errors
			if(sendResult.getCode() != ResultCodeType.Accepted &&
					sendResult.getCode() != ResultCodeType.AcceptedNonConformant)
			{
				System.out.println("Could not send message!");
				for(IResultDetail dtl : sendResult.getDetails())
					System.out.printf("\t%s : %s\r\n", dtl.getType(), dtl.getMessage());
				return;
			}
			
			IReceiveResult receiveResult = connector.receive(sendResult);
			
			// Output errors
			if(receiveResult.getCode() != ResultCodeType.Accepted &&
					receiveResult.getCode() != ResultCodeType.AcceptedNonConformant)
			{
				System.out.println("Could not interpret response message!");
				for(IResultDetail dtl : receiveResult.getDetails())
				{
					System.out.printf("\t%s : %s\r\n", dtl.getType(), dtl.getMessage());
					if(dtl.getException() != null)
						dtl.getException().printStackTrace();
				}
				return;
			}

			// Process results
			PRPA_IN101104CA responseMessage = (PRPA_IN101104CA)receiveResult.getStructure();
			if(!responseMessage.getAcknowledgement().getTypeCode().getCode().equals(AcknowledgementType.ApplicationAcknowledgementAccept))
			{
				System.out.println("Client registry responded with an error:");
				for(AcknowledgementDetail dtl : responseMessage.getAcknowledgement().getAcknowledgementDetail())
					System.out.printf("\t%s : %s\r\n", dtl.getTypeCode(), dtl.getText());
				return;
			}
			
			// Loop through results
			System.out.printf("Found %d results:\r\n", responseMessage.getControlActEvent().getSubject().size());
			for(Subject2<IdentifiedEntity> res : responseMessage.getControlActEvent().getSubject())
			{
				IdentifiedEntity entity = res.getRegistrationEvent().getSubject().getRegisteredRole();
				System.out.printf("\t%s - %s\r\n", entity.getIdentifiedPerson().getName().first().toString(), entity.getIdentifiedPerson().getAdministrativeGenderCode().getCode());
			}
		}
		finally
		{
			connector.close();
		}
		// Process
		
	}

	/**
	 * Create a basic PRPA_IN101103CA (not this is not conformant just enough to get a response)
	 */
	private static PRPA_IN101103CA createPRPA_IN101103CA() {
		// TODO Auto-generated method stub
		PRPA_IN101103CA retVal = new PRPA_IN101103CA();
		retVal.setAcceptAckCode(AcknowledgementCondition.Always);
		retVal.setCreationTime(TS.now());
		retVal.setId(UUID.randomUUID());
		retVal.setInteractionId(PRPA_IN101103CA.defaultInteractionId());
		retVal.setProcessingCode(ProcessingID.Production);
		retVal.setProfileId(PRPA_IN101103CA.defaultProfileId());
		retVal.setResponseModeCode(ResponseMode.Immediate);
		retVal.setReceiver(
				new Receiver(new TEL("http://cr.marc-hi.ca:8080/cr"), 
				new Device2(
						new II("1.3.6.1.4.1.33349.3.1.1.2", "CR")
					)
			)
		);
		retVal.setSender(
				new Sender(
						new TEL("http://anonymous"),
						new Device1(new II("1.3.6.1.4.1.33349.3.1.1.22", "MARC-W3-1"), 
						new ST("Everest Sample"), 
						new ST("Everest"), 
						null, 
						null, 
						null
				)
			)
		);
		
		// Control act
		retVal.setControlActEvent(new ControlActEvent<ParameterList>());
		retVal.getControlActEvent().setId(UUID.randomUUID());
		retVal.getControlActEvent().setCode(PRPA_IN101103CA.defaultTriggerEvent());
		retVal.getControlActEvent().setEffectiveTime(TS.now());
		
		// Author
		retVal.getControlActEvent().setAuthor(new Author(TS.now()));
		AssignedEntity author = new AssignedEntity();
		author.setId(SET.createSET(new II("1.3.6.1.4.1.33349.3.1.2.2.0.1", "1")));
		author.setAssignedPerson(new Person(PN.fromFamilyGiven(EntityNameUse.Legal, "Family", "Frederick"), null));
		retVal.getControlActEvent().getAuthor().setAuthorPerson(author);

		// Query parameter
		retVal.getControlActEvent().setQueryByParameter(new QueryByParameter<ParameterList>(new II(UUID.randomUUID())));
		retVal.getControlActEvent().getQueryByParameter().setParameterList(new ParameterList());
		retVal.getControlActEvent().getQueryByParameter().getParameterList().getPersonName().add(
				new PersonName(
						PN.fromFamilyGiven(EntityNameUse.Search, "Davis", "Arianna")
				)
		);
		
		return retVal;
	}

}
