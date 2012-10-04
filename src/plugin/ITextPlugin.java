package plugin;

import javax.swing.JLabel;


//ITextPlugin is an interface for plugins that want to implement text output in the host application.
public interface ITextPlugin {

	//Return a JLabel that gets displayed by the application in the bottom panel. 
	public JLabel getTextLabel();
}
