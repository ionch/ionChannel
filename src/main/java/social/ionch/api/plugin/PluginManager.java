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

package social.ionch.api.plugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.unascribed.asyncsimplelog.AsyncSimpleLog;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import social.ionch.api.ResourcefulReadWriteLock;
import social.ionch.api.ResourcefulReadWriteLock.HeldLock;

public class PluginManager {
	private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
	
	private static final ResourcefulReadWriteLock rwl = ResourcefulReadWriteLock.create();
	private static final Map<String, Plugin> plugins = Maps.newHashMap();
	private static List<Plugin> sorted = null;
	
	public static void addPlugin(Plugin plugin) {
		try (HeldLock hl = rwl.obtainWriteLock()) {
			plugins.put(plugin.getId(), plugin);
			sorted = null;
		}
	}
	
	private static void sort() {
		Set<Plugin> candidates = Sets.newHashSet(plugins.values());
		Multimap<String, Plugin> available = HashMultimap.create();
		Multimap<String, Plugin> unavailable = HashMultimap.create();
		for (Plugin p : candidates) {
			available.put(p.getId(), p);
			for (String provide : p.getProvides()) {
				available.put(provide, p);
			}
		}
		boolean changed = true;
		outer: while (changed) {
			changed = false;
			for (Plugin p : candidates) {
				for (String s : p.getNeeds()) {
					if (!available.containsKey(s)) {
						if (s.startsWith("?") && unavailable.containsKey(s)) {
							log.error("Plugin {} cannot be enabled as it needs {} which is not available (would have been provided by {})", p.getId(), s, Joiner.on(", ").join(Iterables.transform(unavailable.get(s), Plugin::getId)));
						} else {
							log.error("Plugin {} cannot be enabled as it needs {} which is not available", p.getId(), s);
						}
						unavailable.put(p.getId(), p);
						for (String provide : p.getProvides()) {
							unavailable.put(provide, p);
						}
						available.values().removeIf(pl -> pl == p);
						candidates.remove(p);
						changed = true;
						continue outer;
					}
				}
				for (String s : p.getConflicts()) {
					if (available.containsKey(s)) {
						if (s.startsWith("?")) {
							log.error("Plugin {} cannot be enabled as it conflicts with {} provided by {}", p.getId(), s, Joiner.on(", ").join(Iterables.transform(available.get(s), Plugin::getId)));
						} else {
							log.error("Plugin {} cannot be enabled as it conflicts with {}", p.getId(), s);
						}
						unavailable.put(p.getId(), p);
						for (String provide : p.getProvides()) {
							unavailable.put(provide, p);
						}
						available.values().removeIf(pl -> pl == p);
						candidates.remove(p);
						changed = true;
						continue outer;
					}
				}
			}
		}
		for (Plugin p : candidates) {
			log.info(p.getId());
		}
	}
	
	public static void main(String[] args) {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		AsyncSimpleLog.setAnsi(true);
		AsyncSimpleLog.startLogging();
		addPlugin(new TestPlugin() {{
			id("test1");
			needs("nothere");
		}});
		addPlugin(new TestPlugin() {{
			id("test2");
			provides("?testVirtual");
			conflicts("test3");
		}});
		addPlugin(new TestPlugin() {{
			id("test3");
			needs("?testVirtual");
		}});
		addPlugin(new TestPlugin() {{
			id("test4");
			provides("?testVirtual");
		}});
		sort();
	}
	
	private static class TestPlugin extends Plugin {

		@Override
		public void enable() {
			// TODO Auto-generated method stub

		}

		@Override
		public void hotDisable() throws UnsupportedOperationException {
			// TODO Auto-generated method stub

		}

		@Override
		public void init() {
			// TODO Auto-generated method stub

		}

	}

}
