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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.unascribed.asyncsimplelog.AsyncSimpleLog;

import com.google.common.base.Stopwatch;

import com.playsawdust.chipper.toolbox.io.LoggerPrintStream;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.SyntaxError;
import social.ionch.api.JsonArrayBuilder;
import social.ionch.api.JsonObjectBuilder;
import social.ionch.api.Version;
import social.ionch.api.config.ConfigSectionHandler;
import social.ionch.api.db.DatabaseFactoryRegistry;
import social.ionch.api.plugin.Plugin;
import social.ionch.api.plugin.PluginManager;
import social.ionch.builtin.H2DatabasePlugin;

public class Bootstrap {
	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
	
	public static void main(String[] args) {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
//		AsyncSimpleLog.setMinLogLevel(LogLevel.TRACE);
		AsyncSimpleLog.setAnsi(true);
		AsyncSimpleLog.ban(Pattern.compile("org.jline", Pattern.LITERAL));
		AsyncSimpleLog.startLogging();
		
		LoggerPrintStream.initializeDefault();
		log.info("{} starting up", Version.FULLER);
		
		ConfigSectionHandler.contributeSection("plugins", new JsonObjectBuilder()
				.put("search", new JsonArrayBuilder()
						.add("plugins")
						.build(), "The directories to find third-party plugins in.")
				.put("enabled", new JsonArray(), "Third-party plugin IDs to enable.")
				.put("disabled", new JsonArray(), "Built-in or transitively-enabled plugin IDs to disable.")
				.build(), "Settings for enabling, disabling, and finding plugins.");
		
		ConfigSectionHandler.contributeSection("database", new JsonObjectBuilder()
				.put("backend", "h2")
				.build(), "Settings for connecting to a database.");
		
		File cfgFile = new File("ionch.jkson");
		JsonObject cfg;
		if (cfgFile.exists()) {
			try {
				cfg = ConfigSectionHandler.read(cfgFile);
			} catch (IOException e) {
				log.error("IO error while trying to read ionch.jkson", e);
				System.exit(1);
				return;
			} catch (SyntaxError e) {
				log.error("Syntax error in ionch.jkson: {}", e.getCompleteMessage());
				System.exit(2);
				return;
			}
		} else {
			cfg = ConfigSectionHandler.getConsolidatedDefaults();
		}
		
		PluginManager.addPlugin(new H2DatabasePlugin());
		
		List<File> search = cfg.recursiveGet(JsonArray.class, "plugins.search").stream()
				.map(Bootstrap::jsonElementToString)
				.map(File::new)
				.collect(Collectors.toList());
		Set<String> enable = cfg.recursiveGet(JsonArray.class, "plugins.enabled").stream()
				.map(Bootstrap::jsonElementToString)
				.collect(Collectors.toSet());
		Set<String> disable = cfg.recursiveGet(JsonArray.class, "plugins.disabled").stream()
				.map(Bootstrap::jsonElementToString)
				.collect(Collectors.toSet());
		
		List<Plugin> resolved = PluginManager.resolve(search, enable, disable);
		if (resolved == null) {
			log.error("Plugin resolution failed. Cannot continue, exiting\nRead the errors above and adjust your ionch.jkson");
			System.exit(4);
			return;
		}
		Stopwatch sw = Stopwatch.createStarted();
		for (Plugin p : resolved) {
			if (p.canHotDisable()) {
				try {
					for (int i = 0; i < 4; i++) {
						DatabaseFactoryRegistry.$$_saveState(SkeletonKey.get());
						ConfigSectionHandler.$$_saveState(SkeletonKey.get());
						p.enable();
						p.hotDisable();
						if (!DatabaseFactoryRegistry.$$_restoreState(SkeletonKey.get())) {
							log.debug("Plugin {} left a dangling database factory during cleanliness check. Marking as unclean", p.getId());
							p.$$_markUnclean(SkeletonKey.get());
							break;
						}
						if (!ConfigSectionHandler.$$_restoreState(SkeletonKey.get())) {
							log.debug("Plugin {} left a dangling config section during cleanliness check. Marking as unclean", p.getId());
							p.$$_markUnclean(SkeletonKey.get());
							break;
						}
					}
				} catch (Throwable t) {
					log.debug("Plugin {} threw an exception during cleanliness check. Marking as unclean", p.getId(), t);
					p.$$_markUnclean(SkeletonKey.get());
				}
				if (p.isUnclean()) {
					log.warn("{} has failed the cleanliness check. Marking as not hot-disableable", p.toFriendlyString());
				} else {
					log.debug("Plugin {} passed cleanliness check", p.getId());
				}
			}
			log.debug("Enabling {}", p.toFriendlyString());
			try {
				p.enable();
			} catch (Throwable t) {
				log.error("Plugin {} threw an exception during enable", p.getId(), t);
				System.exit(5);
				return;
			}
		}
		log.info("Enabled {} plugins in {}", resolved.size(), sw);
		sw.reset().start();
		for (Plugin p : resolved) {
			log.debug("Initializing {}", p.toFriendlyString());
			try {
				p.init();
			} catch (Throwable t) {
				log.error("Plugin {} threw an exception during init", p.getId(), t);
				System.exit(6);
				return;
			}
		}
		log.info("Initialized {} plugins in {}", resolved.size(), sw);
		try {
			ConfigSectionHandler.write(cfgFile, cfg);
		} catch (IOException e) {
			log.error("IO error while trying to write ionch.jkson", e);
			System.exit(3);
			return;
		}
	}
	
	private static String jsonElementToString(JsonElement e) {
		if (!(e instanceof JsonPrimitive) || !(((JsonPrimitive)e).getValue() instanceof String)) {
			throw new IllegalArgumentException("Expected a string, got "+e);
		} else {
			return ((JsonPrimitive)e).asString();
		}
	}

}
