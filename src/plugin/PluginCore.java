package plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.sun.org.apache.bcel.internal.generic.RETURN;

public class PluginCore {
	// GUI Widgets that we will need
	private final static int DEL_CODE = 127;

	private JFrame frame;
	private JPanel contentPane;
	private JLabel bottomLabel;
	private JLabel defaultLabel;
	private JList availablePluginsList;
	private DefaultListModel<String> listModel;
	private DefaultListModel<String> runningPluginsListModel;
	private JPanel centerEnvelope;
	private JPanel rightPane;
	private JPanel leftPane;
	// For holding registered plugin
	private HashMap<String, Plugin> idToPlugin;
	private Plugin currentPluginRight;
	private Plugin currentPluginLeft;
	private Plugin currentTextPlugin;
	private ArrayList<Plugin> backgroundPlugins;
	private JList runningPluginsList;
	private JPanel sideListEnvelope;

	public PluginCore() {
		backgroundPlugins = new ArrayList<Plugin>();
		idToPlugin = new HashMap<String, Plugin>();

		// Lets create the elements that we will need
		frame = new JFrame("Pluggable Board Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = (JPanel) frame.getContentPane();
		contentPane.setPreferredSize(new Dimension(700, 500));
		defaultLabel = new JLabel("No plugins registered yet!");
		bottomLabel = defaultLabel;
		listModel = new DefaultListModel<String>();

		availablePluginsList = new JList(listModel);
		availablePluginsList
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availablePluginsList.setLayoutOrientation(JList.VERTICAL);
		JScrollPane availablePluginPane = new JScrollPane(availablePluginsList);
		availablePluginPane.setPreferredSize(new Dimension(100, 50));

		runningPluginsListModel = new DefaultListModel<String>();
		runningPluginsList = new JList(runningPluginsListModel);
		runningPluginsList
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		runningPluginsList.setLayoutOrientation(JList.VERTICAL);
		JScrollPane runningPluginPane = new JScrollPane(runningPluginsList);
		runningPluginPane.setPreferredSize(new Dimension(100, 50));

		sideListEnvelope = new JPanel(new GridLayout(2, 1));
		sideListEnvelope.add(availablePluginPane);
		sideListEnvelope.add(runningPluginPane);
		// Create center display area
		centerEnvelope = new JPanel(new GridLayout(1, 2));
		centerEnvelope
				.setBorder(BorderFactory.createLineBorder(Color.black, 5));

		rightPane = new JPanel(new BorderLayout());
		leftPane = new JPanel(new BorderLayout());

		centerEnvelope.add(rightPane);
		centerEnvelope.add(leftPane);

		// Lets lay them out, contentPane by default has BorderLayout as its
		// layout manager
		contentPane.add(centerEnvelope, BorderLayout.CENTER);
		contentPane.add(sideListEnvelope, BorderLayout.EAST);
		contentPane.add(bottomLabel, BorderLayout.SOUTH);

		// Add action listeners
		availablePluginsList.addMouseListener(availablePluginMouseListener());
		runningPluginsList.addMouseListener(runningPluginMouseListener());
		runningPluginsList.addKeyListener(keyListener());
		runningPluginsList.setFocusable(true);
	}

	private KeyListener keyListener() {
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == DEL_CODE && !backgroundPlugins.isEmpty()) {
					int index = runningPluginsList.getSelectedIndex();
					if (index == -1)
						return;
					Plugin plugin = backgroundPlugins.get(index);
					plugin.stop();
					backgroundPlugins.remove(index);
					runningPluginsListModel.removeElement(plugin.getId());
					if (currentPluginLeft == plugin) {
						currentPluginLeft = null;
						leftPane.removeAll();
					}
					if (currentPluginRight == plugin) {
						currentPluginRight = null;
						rightPane.removeAll();
					}
					if (currentTextPlugin == plugin) {
						currentTextPlugin = null;
						bottomLabel = defaultLabel;
					}
					
					contentPane.revalidate();
					contentPane.repaint();
				}
			}
		};
	}

	public MouseListener availablePluginMouseListener() {
		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int index = ((JList) e.getSource()).locationToIndex(e
						.getPoint());

				String id = listModel.elementAt(index);
				Plugin plugin = idToPlugin.get(id);

				runningPluginsListModel.addElement(plugin.getId());

				backgroundPlugins.add(plugin);
				if (plugin instanceof IVisualPlugin) {
					((IVisualPlugin) plugin).setLayout(new JPanel());
				}
				plugin.start();

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		};

	}

	public MouseListener runningPluginMouseListener() {
		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (backgroundPlugins.isEmpty())
					return;
				int index = ((JList) e.getSource()).locationToIndex(e
						.getPoint());

				Plugin plugin = backgroundPlugins.get(index);

				if (plugin instanceof ITextPlugin) {
					contentPane.remove(bottomLabel);
					bottomLabel = ((ITextPlugin) plugin).getTextLabel();
					contentPane.add(bottomLabel, BorderLayout.SOUTH);
				}
				if (plugin instanceof IVisualPlugin) {
					if (SwingUtilities.isRightMouseButton(e)) {
						if (plugin == null || plugin.equals(currentPluginRight))
							return;

						// Stop previously running plugin
						if (currentPluginRight != null)
							((IVisualPlugin) currentPluginRight)
									.setLayout(new JPanel());
						// The newly selected plu gin is our current plugin
						currentPluginRight = plugin;

						// Clear previous working area
						rightPane.removeAll();

						// Ask plugin to layout the working area
						((IVisualPlugin) currentPluginRight)
								.setLayout(rightPane);

					} else {
						if (plugin == null || plugin.equals(currentPluginLeft))
							return;

						// Stop previously running plugin
						if (currentPluginLeft != null)
							((IVisualPlugin) currentPluginLeft)
									.setLayout(new JPanel());
						// The newly selected plu gin is our current plugin
						currentPluginLeft = plugin;

						// Clear previous working area
						leftPane.removeAll();

						// Ask plugin to layout the working area
						((IVisualPlugin) currentPluginLeft).setLayout(leftPane);
					}
				}
				contentPane.revalidate();
				contentPane.repaint();

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
			public void run() {
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void stop() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(false);
			}
		});
	}

	public void addPlugin(Plugin plugin) {
		this.idToPlugin.put(plugin.getId(), plugin);
		this.listModel.addElement(plugin.getId());
		this.defaultLabel.setText("The " + plugin.getId()
				+ " plugin has been recently added!");
	}

	public void removePlugin(String id) {
		Plugin plugin = this.idToPlugin.remove(id);
		this.listModel.removeElement(id);

		// Stop the plugin if it is still running
		plugin.stop();

		this.defaultLabel.setText("The " + plugin.getId()
				+ " plugin has been recently removed!");
	}
}
