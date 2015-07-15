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
 * Date: 10-19-2012
 */
package org.marc.everest.annotations;
import java.lang.annotation.*;

/**
 * This annotation doesn't actually do anything, it just contains other annotations
 * because for some reason it is impossible to have more than one of the same type of 
 * annotation on member 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InteractionResponses {
	/**
	 * Gets the interactions that can serve as a response to an interaction  
	 */
	Interaction[] value();
}
