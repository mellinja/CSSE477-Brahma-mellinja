package plugin;

import java.util.ArrayList;


//Plugin is the base class for all plugins. By default, Plugins just run in the background without any display.
public abstract class Plugin {
	private String id;

	public Plugin(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// Callback method
	public abstract void start();
	public abstract void stop();
	
	//Used for setting dependencies with other plugins
	public abstract void setOtherPlugins(ArrayList<Plugin> plugins);
}
