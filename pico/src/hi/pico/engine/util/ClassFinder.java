package hi.pico.engine.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFinder {
	private Map<URL, String> classpathLocations = new HashMap<URL, String>();
	private Map<Class<?>, URL> results = new HashMap<Class<?>, URL>();
	private List<Throwable> errors = new ArrayList<Throwable>();

	public ClassFinder() {
		this(null);
	}

	public ClassFinder(List<File> subClassPaths) {
		synchronized (classpathLocations) {
			String pathSep = System.getProperty("path.separator");
			String classpath = System.getProperty("java.class.path");
			List<File> files = new ArrayList<File>();
			StringTokenizer st = new StringTokenizer(classpath, pathSep);
			while (st.hasMoreTokens()) {
				files.add(new File(st.nextToken()));
			}
			
			if (subClassPaths != null) {
				files.addAll(subClassPaths);
			}
			
			classpathLocations = getClasspathLocations(files);
		}
	}
	
	/**
	 * @param fqcn Name of superclass/interface on which to search
	 */
	public List<Class<?>> findSubclasses() {
		return findSubclasses(null);
	}
	/**
	 * @param fqcn Name of superclass/interface on which to search
	 */
	public List<Class<?>> findSubclasses(String[] packageNames) {
		synchronized (classpathLocations) {
			synchronized (results) {
				errors = new ArrayList<Throwable>();
				results = new TreeMap<Class<?>, URL>(CLASS_COMPARATOR);
				packageNames = (packageNames == null) ? new String[]{""} : packageNames;
				return findSubclasses(classpathLocations, packageNames);
			}
		}
	}

	public final List<Throwable> getErrors() {
		return new ArrayList<Throwable>(errors);
	}

	/**
	 * The result of the last search is cached in this object, along with the
	 * URL that corresponds to each class returned. This method may be called to
	 * query the cache for the location at which the given class was found.
	 * <code>null</code> will be returned if the given class was not found
	 * during the last search, or if the result cache has been cleared.
	 */
	public URL getLocationOf(Class<?> cls) {
		if (results != null)
			return results.get(cls);
		else
			return null;
	}

	/**
	 * Determine every URL location defined by the current classpath, and it's
	 * associated package name.
	 */
	private Map<URL, String> getClasspathLocations(List<File> files) {
		Map<URL, String> map = new TreeMap<URL, String>(URL_COMPARATOR);

		for(File file : files) {
			include(null, file, map);
		}

//		Iterator<URL> it = map.keySet().iterator();
//		while (it.hasNext()) {
//			URL url = it.next();
//			System.out.println (url + "-->" + map.get (url));
//		}

		return map;
	}

	private static FileFilter DIRECTORIES_ONLY = new FileFilter() {
		public boolean accept(File f) {
			if (f.exists() && f.isDirectory())
				return true;
			else
				return false;
		}
	};

	private static Comparator<URL> URL_COMPARATOR = new Comparator<URL>() {
		public int compare(URL u1, URL u2) {
			return String.valueOf(u1).compareTo(String.valueOf(u2));
		}
	};

	private static Comparator<Class<?>> CLASS_COMPARATOR = new Comparator<Class<?>>() {
		public int compare(Class<?> c1, Class<?> c2) {
			return String.valueOf(c1).compareTo(String.valueOf(c2));
		}
	};

	private void include(String name, File file, Map<URL, String> map) {
		if (!file.exists())
			return;
		if (!file.isDirectory()) {
			// could be a JAR file
			includeJar(file, map);
			return;
		}
		
		name = (name == null) ? "" : name + ".";

		// add subpackages
		File[] dirs = file.listFiles(DIRECTORIES_ONLY);
		for (int i = 0; i < dirs.length; i++) {
			try {
				// add the present package
				map.put(new URL("file://" + dirs[i].getCanonicalPath()), name + dirs[i].getName());
			} catch (IOException ioe) {
				return;
			}
			include(name + dirs[i].getName(), dirs[i], map);
		}
	}

	private void includeJar(File file, Map<URL, String> map) {
		if (file.isDirectory())
			return;

		URL jarURL = null;
		JarFile jar = null;
		try {
			jarURL = new URL("file:/" + file.getCanonicalPath());
			jarURL = new URL("jar:" + jarURL.toExternalForm() + "!/");
			JarURLConnection conn = (JarURLConnection) jarURL.openConnection();
			jar = conn.getJarFile();
		} catch (Exception e) {
			// not a JAR or disk I/O error
			// either way, just skip
			return;
		}

		if (jar == null || jarURL == null)
			return;

		// include the jar's "default" package (i.e. jar's root)
		map.put(jarURL, "");
		
		// findSubclasses()에 jar의 전체 요소를 반환하는 jarFile.entries()시
		// 해당 jar의 특정 팩키지(폴더) 밑으로 가져오지 않고 루트부터 모든 클래스를 반환함으로 
		// 반복적으로 동일하게 반복됨으로 jar의 경우, 디렉토리 탐색을 하지 않도록 주석처리
		/*Enumeration<JarEntry> e = jar.entries();
		while (e.hasMoreElements()) {
			JarEntry entry = e.nextElement();

			if (entry.isDirectory()) {
				if (entry.getName().toUpperCase().equals("META-INF/"))
					continue;

				try {
					map.put(new URL(jarURL.toExternalForm() + entry.getName()),
							packageNameFor(entry));
				} catch (MalformedURLException murl) {
					continue;
				}
			}
		}*/
	}

	private static String packageNameFor(JarEntry entry) {
		if (entry == null)
			return "";
		String s = entry.getName();
		if (s == null)
			return "";
		if (s.length() == 0)
			return s;
		if (s.startsWith("/"))
			s = s.substring(1, s.length());
		if (s.endsWith("/"))
			s = s.substring(0, s.length() - 1);
		return s.replace('/', '.');
	}

	private final List<Class<?>> findSubclasses(Map<URL, String> locations, String[] packageNames) {
		List<Class<?>> v = new ArrayList<Class<?>>();
		List<Class<?>> w = null;

		Iterator<URL> it = locations.keySet().iterator();
		while (it.hasNext()) {
			URL url = it.next();
			w = findSubclasses(url, locations.get(url), packageNames);
			if (w != null && (w.size() > 0))
				v.addAll(w);
		}

		return v;
	}

	private final List<Class<?>> findSubclasses(URL location, String packageName, String[] findingPackageNames) {
		synchronized (results) {
			// hash guarantees unique names...
			Map<Class<?>, URL> thisResult = new TreeMap<Class<?>, URL>(CLASS_COMPARATOR);
			List<Class<?>> v = new ArrayList<Class<?>>(); // ...but return a vector
			List<URL> knownLocations = new ArrayList<URL>();
			knownLocations.add(location);

			// iterate matching package locations...
			for (int loc = 0; loc < knownLocations.size(); loc++) {
				URL url = knownLocations.get(loc);

				// Get a File object for the package
				File directory = new File(url.getFile());

				if (directory.exists()) {
					// Get the list of the files contained in the package
					String[] files = directory.list();
					for (int i = 0; i < files.length; i++) {
						// we are only interested in .class files
						if (files[i].endsWith(".class")) {
							// removes the .class extension
							String classname = files[i].substring(0, files[i].length() - 6);

							try {
								classname = packageName + "." + classname;
								for (String findingPackageName : findingPackageNames) {
									if (classname.startsWith(findingPackageName)) {
										thisResult.put(loadClass(classname), url);
									}
								}
							} catch (ClassNotFoundException cnfex) {
								errors.add(cnfex);
							} catch (NoClassDefFoundError ncdfe) {
								errors.add(ncdfe);
							} catch (UnsatisfiedLinkError ule) {
								errors.add(ule);
							} catch (Exception exception) {
								errors.add(exception);
							} catch (Error error) {
								errors.add(error);
							}
						}
					}
				} else {
					try {
						// It does not work with the filesystem: we must
						// be in the case of a package contained in a jar file.
						JarURLConnection conn = (JarURLConnection) url.openConnection();
						JarFile jarFile = conn.getJarFile();

						Enumeration<JarEntry> e = jarFile.entries();
						while (e.hasMoreElements()) {
							JarEntry entry = e.nextElement();
							String entryname = entry.getName();

							if (!entry.isDirectory()
									&& entryname.endsWith(".class")) {
								String classname = entryname.substring(0,
										entryname.length() - 6);
								if (classname.startsWith("/"))
									classname = classname.substring(1);
								classname = classname.replace('/', '.');

								try {
									for (String findingPackageName : findingPackageNames) {
										if (classname.startsWith(findingPackageName)) {
											thisResult.put(loadClass(classname), url);
										}
									}
								} catch (ClassNotFoundException cnfex) {
									errors.add(cnfex);
								} catch (NoClassDefFoundError ncdfe) {
									errors.add(ncdfe);
								} catch (UnsatisfiedLinkError ule) {
									errors.add(ule);
								} catch (Exception exception) {
									errors.add(exception);
								} catch (Error error) {
									errors.add(error);
								}
							}
						}
					} catch (IOException ioex) {
						errors.add(ioex);
					}
				}
			}

			results.putAll(thisResult);

			Iterator<Class<?>> it = thisResult.keySet().iterator();
			while (it.hasNext()) {
				v.add(it.next());
			}
			return v;

		}
	}
	
	public static Class<?> loadClass(String className) throws ClassNotFoundException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		    
		if (loader != null)
			return Class.forName(className, false, loader);
		else
			return Class.forName(className);
	}
}
