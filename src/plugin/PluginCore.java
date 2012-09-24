package plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PluginCore {
	// GUI Widgets that we will need
	private JFrame frame;
	private JPanel contentPane;
	private JLabel bottomLabel;
	private JList sideList;
	private DefaultListModel<String> listModel;
	private JPanel centerEnvelope;
	
	// For holding registered plugin
	private HashMap<String, Plugin> idToPlugin;
	private Plugin currentPluginLeft;
	private Plugin currentPluginRight;
	
	public PluginCore() {
		idToPlugin = new HashMap<String, Plugin>();
		
		// Lets create the elements that we will need
		frame = new JFrame("Pluggable Board Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = (JPanel)frame.getContentPane();
		contentPane.setPreferredSize(new Dimension(700, 500));
		bottomLabel = new JLabel("No plugins registered yet!");
		
		listModel = new DefaultListModel<String>();
		sideList = new JList(listModel);
		sideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sideList.setLayoutOrientation(JList.VERTICAL);
		JScrollPane scrollPane = new JScrollPane(sideList);
		scrollPane.setPreferredSize(new Dimension(100, 50));
		
		// Create center display area
		centerEnvelope = new JPanel (new BorderLayout());
		centerEnvelope.setBorder(BorderFactory.createLineBorder(Color.black, 5));
		
		// Lets lay them out, contentPane by default has BorderLayout as its layout manager
		contentPane.add(centerEnvelope, BorderLayout.CENTER);
		contentPane.add(scrollPane, BorderLayout.EAST);
		contentPane.add(bottomLabel, BorderLayout.SOUTH);
		
		// Add action listeners

		sideList.addMouseListener(listMouseListener());
//		sideList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//				// If the list is still updating, return
//				if(e.getValueIsAdjusting())
//					return;
//				// List has finalized selection, let's process further
//				int index = sideList.getSelectedIndex();
//				String id = listModel.elementAt(index);
//				Plugin plugin = idToPlugin.get(id);
//				
//				if(plugin == null || plugin.equals(currentPlugin))
//					return;
//				
//				// Stop previously running plugin
//				if(currentPlugin != null)
//					currentPlugin.stop();
//				
//				// The newly selected plu	gin is our current plugin
//				currentPlugin = plugin;
//				
//				// Clear previous working area
//				centerEnvelope.removeAll();
//				
//				// Create new working area
//				JPanel centerPanel = new JPanel();
//				centerEnvelope.add(centerPanel, BorderLayout.CENTER); 
//				
//				// Ask plugin to layout the working area
//				currentPlugin.layout(centerPanel);
//				contentPane.revalidate();
//				contentPane.repaint();
//				
//				// Start the plugin
//				currentPlugin.start();
//				
//				bottomLabel.setText("The " + currentPlugin.getId() + " is running!");
//			}
//		});
		
		
	}
	public MouseListener listMouseListener(){
		return new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e))
				{
					int index = ((JList)e.getSource()).locationToIndex(e.getPoint());
					
					String id = listModel.elementAt(index);
					Plugin plugin = idToPlugin.get(id);
					
					if(plugin == null || plugin.equals(currentPluginLeft))
						return;
					
					// Stop previously running plugin
					if(currentPluginLeft != null)
						currentPluginLeft.stop();
					
					// The newly selected plu	gin is our current plugin
					currentPluginLeft = plugin;
					
					// Clear previous working area
					centerEnvelope.removeAll();
					
					// Create new working area
					JPanel centerPanel = new JPanel();
					centerEnvelope.add(centerPanel, BorderLayout.WEST); 
					
					// Ask plugin to layout the working area
					currentPluginLeft.layout(centerPanel);
					contentPane.revalidate();
					contentPane.repaint();
					
					// Start the plugin
					currentPluginLeft.start();
					
					bottomLabel.setText("The " + currentPluginLeft.getId() + " is running!");
				}
				else {
					int index = ((JList)e.getSource()).locationToIndex(e.getPoint());
					
					String id = listModel.elementAt(index);
					Plugin plugin = idToPlugin.get(id);
					
					if(plugin == null || plugin.equals(currentPluginLeft))
						return;
					
					// Stop previously running plugin
					if(currentPluginRight != null)
						currentPluginRight.stop();
					
					// The newly selected plu	gin is our current plugin
					currentPluginRight = plugin;
					
					// Clear previous working area
					centerEnvelope.removeAll();
					
					// Create new working area
					JPanel centerPanel = new JPanel();
					centerEnvelope.add(centerPanel, BorderLayout.EAST); 
					
					// Ask plugin to layout the working area
					currentPluginRight.layout(centerPanel);
					contentPane.revalidate();
					contentPane.repaint();
					
					// Start the plugin
					currentPluginRight.start();
					
					bottomLabel.setText("The " + currentPluginRight.getId() + " is running!");
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}			
		};
	}
	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	public void stop() {
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				frame.setVisible(false);
			}
		});
	}
	
	public void addPlugin(Plugin plugin) {
		this.idToPlugin.put(plugin.getId(), plugin);
		this.listModel.addElement(plugin.getId());
		this.bottomLabel.setText("The " + plugin.getId() + " plugin has been recently added!");
	}
	
	public void removePlugin(String id) {
		Plugin plugin = this.idToPlugin.remove(id);
		this.listModel.removeElement(id);
		
		// Stop the plugin if it is still running
		plugin.stop();

		this.bottomLabel.setText("The " + plugin.getId() + " plugin has been recently removed!");
	}
}
