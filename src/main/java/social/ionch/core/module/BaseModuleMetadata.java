package social.ionch.core.module;

import social.ionch.api.module.EnableState;
import social.ionch.api.module.Module;
import social.ionch.api.module.ModuleMetadata;

public class BaseModuleMetadata implements ModuleMetadata {
	private String id;
	private String name;
	private String[] contributors;
	private EnableState state;
	private Module module;
	
	private ClassLoader classLoader;

	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String[] getContributors() {
		return contributors;
	}

	@Override
	public EnableState getState() {
		return state;
	}

	@Override
	public Module getModule() {
		return module;
	}
	
	
	/* package-private */ void setEnableState(EnableState state) {
		this.state = state;
	}
	
	/* package-private */ void setModule(Module m) {
		this.module = m;
	}
}
