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

package social.ionch.builtin.db;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import social.ionch.api.module.Module;
import social.ionch.api.module.ModuleMetadata;

public class DatabaseModule implements Module {

	@Override
	public ListenableFuture<Void> enable(ModuleMetadata metadata) {
		//Ionch.registerDatabase(db);
		metadata.getLogger().info("DB Module enabled.");
		
		return Futures.immediateFuture(null);
	}

	@Override
	public ListenableFuture<Void> disable() {
		return Futures.immediateFuture(null);
	}

	@Override
	public void forceStop() {
		
	}

}
