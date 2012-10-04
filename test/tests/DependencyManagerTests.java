package tests;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import plugin.DependencyManager;
import plugin.DependencyManager.Wrapper;
import plugin.Plugin;

public class DependencyManagerTests {

	private DependencyManager dependencyManager;
	
	private Plugin dummyPluginOne;
	private Plugin dummyPluginTwo;
	private Plugin dummyPluginThree;
	
	static String dummyOneId = "One";
	static String dummyTwoId = "Two";
	static String dummyThreeId = "Three";
	
	public void init(){
		dummyPluginOne = new DummyPlugin(dummyOneId);
		dummyPluginTwo = new DummyPlugin(dummyTwoId);
		dummyPluginThree = new DummyPlugin(dummyThreeId);
		
		dependencyManager = new DependencyManager();
	}
	
	@Test
	public void testAddingPluginWithNoDependenciesLoadsSuccessfully() {
		init();
		
		assertEquals(true, dependencyManager.getRunningPlugins().isEmpty());
		dependencyManager.add(null, dummyPluginOne);
		assertEquals(false, dependencyManager.getRunningPlugins().isEmpty());
	}
	
	@Test
	public void testAddingPluginWithUnloadedDependenciesGoesIdle(){
		init();
		
		assertEquals(true, dependencyManager.getIdlePlugins().isEmpty());
		assertEquals(true, dependencyManager.getRunningPlugins().isEmpty());
		dependencyManager.add(new String[]{dummyTwoId}, dummyPluginOne);
		assertEquals(true, dependencyManager.getRunningPlugins().isEmpty());
		assertEquals(false, dependencyManager.getIdlePlugins().isEmpty());
	}
	
	@Test
	public void testAddingPluginOthersDependOnStartsRunningTheIdlePlugins(){
		init();
		
		assertEquals(0, dependencyManager.getIdlePlugins().size());
		assertEquals(0, dependencyManager.getRunningPlugins().size());
		dependencyManager.add(new String[]{dummyTwoId}, dummyPluginOne);
		assertEquals(0, dependencyManager.getRunningPlugins().size());
		assertEquals(1, dependencyManager.getIdlePlugins().size());
		dependencyManager.add(new String[]{}, dummyPluginTwo);
		assertEquals(2, dependencyManager.getRunningPlugins().size());
		assertEquals(0, dependencyManager.getIdlePlugins().size());
	}
	
	@Test
	public void testAddingPluginThatHasDependencyThenAddingPluginThatDependsOnItThenAddingPluginTheFirstPluginDependsOnLoadsAllSuccessfully(){
		init();
		
		assertEquals(0, dependencyManager.getIdlePlugins().size());
		assertEquals(0, dependencyManager.getRunningPlugins().size());
		dependencyManager.add(new String[]{dummyOneId}, dummyPluginTwo);
		assertEquals(0, dependencyManager.getRunningPlugins().size());
		assertEquals(1, dependencyManager.getIdlePlugins().size());
		dependencyManager.add(new String[]{dummyTwoId}, dummyPluginThree);
		assertEquals(0, dependencyManager.getRunningPlugins().size());
		assertEquals(2, dependencyManager.getIdlePlugins().size());
		dependencyManager.add(new String[]{}, dummyPluginOne);
		assertEquals(3, dependencyManager.getRunningPlugins().size());
		assertEquals(0, dependencyManager.getIdlePlugins().size());
	}

	@Test
	public void testRemovePlugin(){
		init();
		
		dependencyManager.add(null, dummyPluginOne);
		assertEquals(0, dependencyManager.getIdlePlugins().size());
		assertEquals(1, dependencyManager.getRunningPlugins().size());
		dependencyManager.remove(dummyPluginOne);
		assertEquals(0, dependencyManager.getIdlePlugins().size());
		assertEquals(0, dependencyManager.getRunningPlugins().size());
	}
	

	@Test
	public void testRemovePluginWithDependency(){
		init();
		
		dependencyManager.add(null, dummyPluginOne);
		dependencyManager.add(new String[]{dummyOneId}, dummyPluginTwo);
		
		assertEquals(0, dependencyManager.getIdlePlugins().size());
		assertEquals(2, dependencyManager.getRunningPlugins().size());
		dependencyManager.remove(dummyPluginOne);
		assertEquals(1, dependencyManager.getIdlePlugins().size());
		assertEquals(0, dependencyManager.getRunningPlugins().size());
	}

	@Test
	public void testRemovePluginWithDependencyTree(){
		init();
		
		dependencyManager.add(null, dummyPluginOne);
		dependencyManager.add(new String[]{dummyOneId}, dummyPluginTwo);
		dependencyManager.add(new String[]{dummyTwoId}, dummyPluginThree);
		assertEquals(0, dependencyManager.getIdlePlugins().size());
		assertEquals(3, dependencyManager.getRunningPlugins().size());
		dependencyManager.remove(dummyPluginOne);
		assertEquals(2, dependencyManager.getIdlePlugins().size());
		assertEquals(0, dependencyManager.getRunningPlugins().size());
	}
	
	@Test
	public void testRemovePluginWithMultipleDependencies(){
		init();
		
		dependencyManager.add(null, dummyPluginOne);
		dependencyManager.add(null, dummyPluginTwo);
		dependencyManager.add(new String[]{dummyOneId,dummyTwoId}, dummyPluginThree);
		assertEquals(0, dependencyManager.getIdlePlugins().size());
		assertEquals(3, dependencyManager.getRunningPlugins().size());
		dependencyManager.remove(dummyPluginOne);
		assertEquals(1, dependencyManager.getIdlePlugins().size());
		assertEquals(1, dependencyManager.getRunningPlugins().size());
	}
	
	
	
	class DummyPlugin extends Plugin {

		public DummyPlugin(String id) {
			super(id);
		}

		@Override
		public void start() {}

		@Override
		public void stop() {}

		@Override
		public void setOtherPlugins(ArrayList<Plugin> plugins) {}
	}
	
}
