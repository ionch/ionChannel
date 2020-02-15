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

import org.h2.jdbcx.JdbcConnectionPool;

import com.google.common.net.UrlEscapers;

import com.playsawdust.chipper.toolbox.MoreStrings;

import social.ionch.api.JsonObjectBuilder;
import social.ionch.api.StandardVirtuals;
import social.ionch.api.config.ConfigSectionHandler;
import social.ionch.api.db.Database;
import social.ionch.api.db.DatabaseFactory;
import social.ionch.api.db.DatabaseFactoryRegistry;

public class H2DatabasePlugin extends BuiltInPlugin {

	public H2DatabasePlugin() {
		id("social.ionch.h2");
		name("H2SQL Database Support");
		author("ionChannel");
		provides(StandardVirtuals.DATABASE);
	}
	
	private final DatabaseFactory factory = DatabaseFactory.from("h2", (config) -> {
		String fileStr = MoreStrings.removeSuffix(config.get(String.class, "file"), ".mv.db");
		String url = "jdbc:h2:"+UrlEscapers.urlPathSegmentEscaper().escape(fileStr).replace("%2F", "/");
		JdbcConnectionPool jcp = JdbcConnectionPool.create(url, "", "");
		return new Database() {
			
			@Override
			public String getUri() {
				return url;
			}
			
			@Override
			public void destroy() {
				jcp.dispose();
			}
		};
	});
	
	@Override
	public void enable() {
		DatabaseFactoryRegistry.register(factory);
		ConfigSectionHandler.contributeSection("database.h2", new JsonObjectBuilder()
				.put("file", "ionch.mv.db", "Must end in .mv.db")
				.put("writeFrequency", 0,
						"The maximum frequency at which ionChannel will persist to the file on disk, in seconds.\n" +
						"Higher values cause more data loss in the event of a crash but increase performance and\n" +
						"can reduce wear on eMMC storage. If set to 0, this will be decided automatically based on\n" +
						"the amount of buffered data. 0 is the recommended option.")
				.build(),
			"Options for using the H2SQL database backend. H2SQL is a fast pure Java SQL database comparable\n" +
			"to SQLite. It is ionChannel's default backend, and the recommended one for most users. H2SQL\n" +
			"provides perfectly acceptable performance for small to medium instances, and makes administration\n" +
			"very easy - H2 works out-of-the-box and doesn't require setting up and managing a separate\n" +
			"database server.");
	}
	
	@Override
	public void hotDisable() throws UnsupportedOperationException {
		DatabaseFactoryRegistry.unregister(factory);
		ConfigSectionHandler.removeSection("database.h2");
	}

	@Override
	public void init() {
		
	}
	
	
}
