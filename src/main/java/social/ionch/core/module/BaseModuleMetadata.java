/*
 * This file is part of ionChannel.
 *
 * ionChannel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, version 3.
 *
 * ionChannel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ionChannel.  If not, see <https://www.gnu.org/licenses/>.
 */

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
