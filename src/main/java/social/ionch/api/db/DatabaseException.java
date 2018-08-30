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

package social.ionch.api.db;

/**
 * Exception which indicates that something went wrong with database access.
 */
public class DatabaseException extends Exception {
	private static final long serialVersionUID = 6377236655555739690L;
	public DatabaseException() {}
	public DatabaseException(String message) { super(message); }
	public DatabaseException(Throwable cause) { super(cause); }
	public DatabaseException(String message, Throwable cause) { super(message, cause); }
}
