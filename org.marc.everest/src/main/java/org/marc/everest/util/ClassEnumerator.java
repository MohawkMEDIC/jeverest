/* 
 * Copyright 2011-2012 Mohawk College of Applied Arts and Technology
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
 * Date: 11-09-2012
 */
package org.marc.everest.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.marc.everest.exceptions.FormatterException;

/**
 * A utility class that will enumerate a package or jar file and load
 * all classes in that package
 */
public class ClassEnumerator {

	/**
	 * Loads all classes in the specified package
	 */
	public static List<Class<?>> loadClassesInPackage(Class<?> clazz)
	{
		// HACK: Well, where do I begin ... At first this function accepted a java.lang.reflection.Package instance
		// and would use the system class loader to fetch the Package information the package URL was then passed to the 
		// processXContents method where it would load all the classes in that package.. Worked perfectly in Eclipse.
		// So, I was just about to make an installer for jEverest and guess what? That doesn't work in the Jar file because
		// for some reason getsystemclassloader.getresource doesn't do anything for packages (unless this is bundled in the same
		// JAR file, which I def. do not want to do). So, now this hackish solution works, here is what it does:
		// 	1. Get a URL to the resource of the passed in clazz parameters (that MUST exist in the clazz.getClassLoader 
		//     because the classloader attached to the class loaded it)
		//  2. Chop off the last part of the URL, this is because we don't want to scan the class, we want to scan the directory.
		//  3. Go on as normal
		
		URL resource = clazz.getClassLoader().getResource(clazz.getName().replace(".", "/") + ".class");
		try {
			resource = new URL(resource.toString().substring(0, resource.toString().lastIndexOf('/')));
		} catch (Exception e) {
			throw new FormatterException("Could not enumerate classpath", e);
		}
		
		if(resource.toString().startsWith("jar:"))
		{
			return processJarFileContents(resource, clazz.getPackage().getName());
		}
		else
		{
			return processDirectoryContents(new File(resource.getPath()), clazz.getPackage().getName());
		}
		
		
	}

	/**
	 * Find resource from class path
	 */
	// HACK: This is a total hack
	private static URL findPackageJarFile(String pkgName) {
		URL retVal = null;
		try {
			
			for(String itm : System.getProperty("java.class.path").split(";"))
			{
				if(!itm.contains(pkgName + ".jar")) continue;
				
				File itmFile = new File(itm);
				return new URL("jar:" + itmFile.toURI().toURL() + "!/");
			}
			
		} catch (IOException e) {
			throw new FormatterException("Could not load package JAR file", e);
		} 
		return retVal;
	}

	/**
	 * Get all GPMR packages on the current classpath
	 */
	public static List<Class<?>> loadGpmrPackages()
	{
		List<Class<?>> retVal = new ArrayList<Class<?>>();
		try {
			
			for(String itm : System.getProperty("java.class.path").split(":"))
			{
				File itmFile = new File(itm);
				if(itmFile.isDirectory()) // skip dirs, only JARs
					continue;
				
				URLClassLoader clazzLoader = new URLClassLoader(new URL[] { new URL("jar:" + itmFile.toURI().toURL() + "!/")  });
				try {
					// Namespace
					String className = itmFile.getName().substring(0, itmFile.getName().length() - 3) + "JarInfo";
					Class<?> clazz = clazzLoader.loadClass(className);
					retVal.addAll(processJarFileContents(clazzLoader.getURLs()[0], clazz.getPackage().getName()));
				} catch (ClassNotFoundException e) {
				}
			}
				
				
		} catch (IOException e) {
			throw new FormatterException("Could not load GPMR generated package", e);
		} 
				
	  return retVal;	
	}
	
	/**
	 * Process all classes in the specified directory
	 * @param file
	 * @param name
	 * @return
	 */
	private static List<Class<?>> processDirectoryContents(File directory,
			String packageName) {
		
		List<Class<?>> retVal = new ArrayList<Class<?>>();
		
		for(String file : directory.list())
		{
			// File is a class?
			if(!file.endsWith(".class"))
			{
				File subdir = new File(directory, file);
				if(subdir.isDirectory())
					retVal.addAll(processDirectoryContents(subdir, packageName + '.' + file));
				continue;
			}
			
			String className = packageName + '.' + file.substring(0, file.length() - 6); // TODO: Find a better way to do this
			Class<?> clazz = loadClass(className);
			if (clazz != null) retVal.add(clazz);
		}
		
		return retVal;
	}

	/**
	 * Load a class
	 */
	private static Class<?> loadClass(String className) {
		try
		{
			return Class.forName(className);
		}
		catch(ClassNotFoundException e)
		{
			return null; // suppress not found
		}
	}

	/**
	 * Load all classes from the jar file
	 */
	private static List<Class<?>> processJarFileContents(URL resource, String packageName) {
		
		List<Class<?>> retVal = new ArrayList<Class<?>>();
		try
		{
		
			String relPath = packageName.replace('.', '/'), // correct to pat
					resPath = URLDecoder.decode(resource.getPath(), "UTF-8"),
					jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
						
			// Load Jar File
			JarFile jarFile = null;
				jarFile = new JarFile(jarPath);
			// Enumerate entries
			Enumeration<JarEntry> entries = jarFile.entries();
			while(entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				String className = null;
				if(entry.getName().endsWith(".class") && 
						entry.getName().startsWith(relPath) && 
						entry.getName().length() > (relPath.length() + "/".length())) 
				{				
					className = entry.getName().replace('/', '.').replace('\\', '.').replace(".class", "");			
				}			
				
				// Class name is null
				if (className != null) 
				{
					Class<?> clazz = loadClass(className);
					if(clazz != null)
						retVal.add(clazz);			
				}
			}
		
			return retVal;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
		

	}

}

