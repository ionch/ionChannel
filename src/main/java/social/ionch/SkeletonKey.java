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

package social.ionch;

/**
 * Package-private singleton object used to demonstrate privileges to public-by-necessity internal
 * methods.
 */
public final class SkeletonKey {

	private static final SkeletonKey inst = new SkeletonKey();
	
	public static void verify(SkeletonKey key) {
		if (key == null) throw new SecurityException("Null skeleton key");
		if (key != inst) throw new SecurityException("Bad skeleton key");
	}
	
	/*package*/ static SkeletonKey get() {
		return inst;
	}

	private SkeletonKey() {}
	
}
