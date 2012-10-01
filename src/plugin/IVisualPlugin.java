package plugin;

import javax.swing.JPanel;


//IVisualPlugin is an interface for plugins that want to implement GUI elements in the host application.
public interface IVisualPlugin {
	
	//Set the JPanel that your plugin uses to display visual elements
	public void setLayout(JPanel panel);
	
}
