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

package social.ionch.api;

public final class Version {

	public static final int MAJOR = 0;
	public static final int MINOR = 0;
	public static final int PATCH = 1;
	public static final String NUMBER = MAJOR+"."+MINOR+"."+PATCH;
	
	public static final String CODENAME = "Amino";
	public static final String FULL = NUMBER+" '"+CODENAME+"'";
	public static final String FULLER = "ionChannel v"+FULL;
	
	private Version() {}
}
