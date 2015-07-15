/* 
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
 * Date: 03-29-2013
 */
package org.marc.everest.connectors.spring;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.addressing.client.ActionCallback;
import org.springframework.ws.soap.addressing.core.EndpointReference;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

/**
 * Represents a WS-A action callback for Spring services, this will (by default)
 * add wsa:from, wsa:to and wsa:action headers
 * @author justin
 *
 */
public class WSAddressingCallback extends ActionCallback {

	
	/**
	 * Creates a new instance of the WSA callback
	 */
	public WSAddressingCallback(String action) throws URISyntaxException {
		 super(action);
    }

	@Override
	public void doWithMessage(WebServiceMessage message) throws IOException,
			TransformerException {
		super.doWithMessage(message);
		
		// Add necessary to/from headers and default action
		/*try {
			this.setFrom(new EndpointReference(new URI("http://www.w3.org/2005/08/addressing/anonymous")));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}*/
	}

	
}
