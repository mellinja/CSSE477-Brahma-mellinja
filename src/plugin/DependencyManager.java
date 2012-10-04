package plugin;

import java.awt.List;
import java.util.ArrayList;
import java.util.Stack;

public class DependencyManager {
	ArrayList<Wrapper> idlePlugins;
	PluginCore core;
	ArrayList<Wrapper> runningPlugins;

	public ArrayList<Wrapper> getIdlePlugins() {
		return idlePlugins;
	}

	public ArrayList<Wrapper> getRunningPlugins() {
		return runningPlugins;
	}

	public DependencyManager() {
		runningPlugins = new ArrayList<Wrapper>();
		idlePlugins = new ArrayList<Wrapper>();
	}

	public DependencyManager(PluginCore core) {
		runningPlugins = new ArrayList<Wrapper>();
		idlePlugins = new ArrayList<Wrapper>();
		this.core = core;
		core.start();
	}

	public void add(String[] dependencies, Plugin p) {
		Wrapper newWrapper = new Wrapper(p, dependencies);
		runningPlugins.add(newWrapper);

		if (checkDependenciesAreMet(newWrapper)) {
			if (core != null){
				p.setOtherPlugins(getRunningPluginsArray());
				core.addPlugin(p);}
			ArrayList<Wrapper> toAdd;
			do {
				toAdd = new ArrayList<>();
				for (Wrapper w : idlePlugins) {
					if (checkDependenciesAreMet(w))
						toAdd.add(w);
				}
				idlePlugins.removeAll(toAdd);
				runningPlugins.addAll(toAdd);
				for (Wrapper w : toAdd) {
					if (core != null) {
						w.getNode().setOtherPlugins(getRunningPluginsArray());
						core.addPlugin(w.getNode());
					}

				}
			} while (!toAdd.isEmpty());
		} else {
			idlePlugins.add(newWrapper);
		}
	}

	public boolean checkDependenciesAreMet(Wrapper w) {
		String[] dependencies = w.getDependencies();
		if (dependencies != null) {
			for (String s : dependencies) {
				boolean isMet = false;
				for (Wrapper wrapper : runningPlugins) {
					if (wrapper.getNode().getId().equals(s)) {
						wrapper.addChild(w);
						isMet = true;
					}
				}
				if (!isMet) {
					return false;
				}
			}
		}
		return true;
	}

	public void remove(Plugin p) {
		Wrapper wrapper = null;
		for (Wrapper w : runningPlugins) {
			if (w.getNode().getId().equals(p.getId()))
				wrapper = w;
		}

		if (wrapper != null) {
			runningPlugins.remove(wrapper);

			Stack<Wrapper> children = new Stack<>();
			children.addAll(wrapper.getChildren());
			while (!children.isEmpty()) {
				Wrapper w = children.pop();
				children.addAll(w.getChildren());
				if (core != null)
					core.removePlugin(w.getNode().getId());
				idlePlugins.add(w);
			}
		}
		if (core != null)
			core.removePlugin(p.getId());
	}

	public ArrayList<Plugin> getRunningPluginsArray() {
		ArrayList<Plugin> retVal = new ArrayList<>();
		for (Wrapper w : runningPlugins) {
			retVal.add(w.getNode());
		}
		return retVal;
	}

	private class Wrapper {
		String[] dependencies;
		Plugin node;
		ArrayList<Wrapper> children;

		public Wrapper(Plugin p, String[] d) {
			children = new ArrayList<Wrapper>();
			node = p;
			dependencies = d;
		}

		public void addChild(Wrapper child) {
			children.add(child);
		}

		public ArrayList<Wrapper> getChildren() {
			return children;
		}

		public Plugin getNode() {
			return node;
		}

		public String[] getDependencies() {
			return dependencies;
		}
	}

}
