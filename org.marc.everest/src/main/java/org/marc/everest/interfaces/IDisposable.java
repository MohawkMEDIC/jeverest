/* 
 * Copyright 2008-2011 Mohawk College of Applied Arts and Technology
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
 * Date: 08-31-2011
 */
package org.marc.everest.interfaces;

/**
 * The IDisposable interface marks an object as disposable.
 *
 * This is a wrapper interface for AutoCloseable. The reason that this interface
 * now simply points to AutoCloseable is a history with jEverest (started on 1.6).
 * <pre>
 * try(SpringClientConnector c = new SpringClientConnector())
 * {
 * 	
 * }
 * catch(Exception e)
 * {
 * }
 * </pre>
 * Will ensure that c is closed. This is equivalent to:
 * <pre>
 * SpringClientConnector c = new SpringClientConnector();
 * try
 * { 
 * }
 * catch(Exception e)
 * {
 * }
 * finally
 * {
 * 	c.close();
 * }
 * </pre>
 */
public interface IDisposable {

	/**
	 * Close method. This should be available in Java 1.7 however because
	 * some applications use JRE 1.6 we need to implement this manually on this
	 * interface.
	 */
	void close();
}
