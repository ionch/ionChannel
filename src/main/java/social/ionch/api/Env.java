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

import java.util.Locale;

/**
 * Utilities for reading environment variables.
 */
public class Env {

	/**
	 * @param var the env var to read
	 * @return {@code true} if the var is set to 1, true, on, or any variation thereof
	 */
	public static boolean isTruthy(String var) {
		String val = System.getenv(var);
		if (val == null) return false;
		switch (val.toLowerCase(Locale.ROOT)) {
			case "1": case "on": case "true":
				return true;
		}
		return false;
	}
	
	private Env() {}
	
}
