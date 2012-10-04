package plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PluginManager implements Runnable {
	private DependencyManager manager;
	private WatchDir watchDir;
	private HashMap<Path, Plugin> pathToPlugin;

	public PluginManager() throws IOException {
		this.manager = new DependencyManager(new PluginCore());
		this.pathToPlugin = new HashMap<Path, Plugin>();
		watchDir = new WatchDir(this, FileSystems.getDefault().getPath(
				"plugins"), false);
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		// First load existing plugins if any
		try {
			Path pluginDir = FileSystems.getDefault().getPath("plugins");
			File pluginFolder = pluginDir.toFile();
			File[] files = pluginFolder.listFiles();
			if (files != null) {
				for (File f : files) {
					this.loadBundle(f.toPath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Listen for newly added plugins
		watchDir.processEvents();
	}

	void loadBundle(Path bundlePath) throws Exception {
		// Get hold of the jar file
		File jarBundle = bundlePath.toFile();
		JarFile jarFile = new JarFile(jarBundle);

		// Get the manifest file in the jar file
		Manifest mf = jarFile.getManifest();
		Attributes mainAttribs = mf.getMainAttributes();

		// Get hold of the Plugin-Class attribute and load the class
		String className = mainAttribs.getValue("Plugin-Class");
		URL[] urls = new URL[] { bundlePath.toUri().toURL() };
		for (URL u : urls) {
			System.out.println(u.toString());
		}
		ClassLoader classLoader = new URLClassLoader(urls);
		Class<?> pluginClass = classLoader.loadClass(className);

		String dependency = mainAttribs.getValue("Dependency");
		String[] dependencies = dependency==null?null : dependency.split("[,]");
		// Create a new instance of the plugin class and add to the core
		Plugin plugin = (Plugin) pluginClass.newInstance();
		this.manager.add(dependencies, plugin);
		this.pathToPlugin.put(bundlePath, plugin);

		// Release the jar resources
		jarFile.close();
	}

	void unloadBundle(Path bundlePath) {
		Plugin plugin = this.pathToPlugin.remove(bundlePath);
		if (plugin != null) {
			this.manager.remove(plugin);
		}
	}
}
