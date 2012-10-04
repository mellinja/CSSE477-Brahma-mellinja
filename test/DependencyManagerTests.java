import static org.junit.Assert.fail;

import org.junit.Test;

import plugin.DependencyManager;
import plugin.DependencyManager.Wrapper;
import plugin.Plugin;

public class DependencyManagerTests {

	private DependencyManager dependencyManager;
	
	private Plugin dummyPluginOne;
	private Plugin dummyPluginTwo;
	private Wrapper wrapperOne;
	private Wrapper wrapperTwo;
	
	static String dummyOneId = "One";
	static String dummyTwoId = "Two";
	public void init(){
		dummyPluginOne = new DummyPluginOne(dummyOneId);
		dummyPluginTwo = new DummyPluginTwo(dummyTwoId);
		
		dependencyManager = new DependencyManager();
	}
	
	@Test
	public void testAddingPluginWithNoDependenciesLoadsSuccessfully() {
		wrapperOne = new Wrapper(dummyPluginOne)
	}

	@Test
	public void testCheckDependenciesAreMet() {
		fail("Not yet implemented");
	}
	
	
	class DummyPluginOne extends Plugin {

		public DummyPluginOne(String id) {
			super(id);
		}

		@Override
		public void start() {}

		@Override
		public void stop() {}
	}
	
	class DummyPluginTwo extends Plugin {

		public DummyPluginTwo(String id) {
			super(id);
		}

		@Override
		public void start() {
			
		}

		@Override
		public void stop() {
			
		}
	
	}
	
}
