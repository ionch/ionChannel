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

package social.ionch.core;

public class PluginException extends Exception {
	private static final long serialVersionUID = -8588626457315510047L;
	
	public PluginException(String detail) { super(detail); }
	public PluginException(String detail, Throwable source) { super(detail, source); }
	
	public static class Load extends PluginException {
		private static final long serialVersionUID = 2156604419084987292L;
		
		public Load(String detail) { super(detail); }
		public Load(String detail, Throwable source) { super(detail, source); }
	}
}
