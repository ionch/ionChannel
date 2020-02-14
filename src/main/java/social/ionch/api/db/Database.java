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
 * Handles persistence.
 */
public interface Database {

	/**
	 * Deallocate any resources used by this Database instance. Close connections, abort unfinished
	 * transactions, close files, purge caches, etc. Normally called when this Database instance is
	 * the active one and the server is shutting down, but may be called any other time.
	 */
	void destroy();
	
	/**
	 * Retrieve a URI that represents this Database in some way. It will not be used to reconstruct
	 * new Database instances and is purely informational. If this Database is backed by JDBC, then
	 * you may just return the JDBC URL here.
	 * @return a URI that represents this Database
	 */
	String getUri();
	
}
