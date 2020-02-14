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

import java.io.IOException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.unascribed.asyncsimplelog.AsyncSimpleLog;

import com.playsawdust.chipper.toolbox.io.LoggerPrintStream;

import social.ionch.api.JsonObjectBuilder;
import social.ionch.api.Version;
import social.ionch.api.config.ConfigHandler;
import social.ionch.api.plugin.PluginManager;
import social.ionch.builtin.H2DatabasePlugin;

public class Bootstrap {
	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
	
	public static void main(String[] args) throws IOException {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		AsyncSimpleLog.setAnsi(true);
		AsyncSimpleLog.ban(Pattern.compile("org.jline", Pattern.LITERAL));
		AsyncSimpleLog.startLogging();
		
		LoggerPrintStream.initializeDefault();
		log.info("{} starting up", Version.FULLER);
		
		ConfigHandler.contributeSection("db", new JsonObjectBuilder()
				.put("backend", "h2")
				.build());
		
		PluginManager.addPlugin(new H2DatabasePlugin());
		
		
	}

}
