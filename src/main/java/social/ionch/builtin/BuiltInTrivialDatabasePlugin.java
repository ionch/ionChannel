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

package social.ionch.builtin;

import social.ionch.api.db.DatabaseFactory;
import social.ionch.api.db.DatabaseFactoryRegistry;

public abstract class BuiltInTrivialDatabasePlugin extends BuiltInPlugin implements DatabaseFactory {

	@Override
	public final void enable() {
		DatabaseFactoryRegistry.register(this);
	}

	@Override
	public final void hotDisable() throws UnsupportedOperationException {
		DatabaseFactoryRegistry.unregister(this);
	}
	
	@Override
	public final boolean canHotDisable() {
		return true;
	}

	@Override
	public final void init() {
		// nothing to do
	}
	
	@Override
	public final void shutdown() {
		
	}
	
}
