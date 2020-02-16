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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.unascribed.asyncsimplelog.AsyncSimpleLog;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSink;
import com.google.common.io.Files;

import com.playsawdust.chipper.toolbox.lipstick.SharedRandom;

import social.ionch.api.ResourcefulReadWriteLock;
import social.ionch.api.ResourcefulReadWriteLock.HeldLock;
import social.ionch.builtin.BuiltInPlugin;

public class PluginManager {
	private enum EdgeType {
		NEED,
		WANT,
		NEED_PROVIDE,
		WANT_PROVIDE,
		;
		public boolean isNeed() {
			return name().startsWith("NEED");
		}
		public boolean isProvide() {
			return name().endsWith("PROVIDE");
		}
	}

	private static class AddableURLClassLoader extends URLClassLoader {

		public AddableURLClassLoader(ClassLoader parent) {
			super(new URL[0], parent);
		}

		@Override
		public void addURL(URL url) {
			super.addURL(url);
		}
		
	}

	private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
	
	private static final ResourcefulReadWriteLock rwl = ResourcefulReadWriteLock.create();
	private static final Map<String, Plugin> plugins = Maps.newHashMap();
	
	private static final AddableURLClassLoader loader = new AddableURLClassLoader(PluginManager.class.getClassLoader());
	
	public static void addPlugin(Plugin plugin) {
		try (HeldLock hl = rwl.obtainWriteLock()) {
			plugins.put(plugin.getId(), plugin);
		}
	}
	
	/**
	 * Performs dependency and conflict resolution and returns a list of plugins that should be
	 * enabled, in order of when they should be loaded.
	 */
	public static List<Plugin> resolve(List<File> search, Set<String> enable, Set<String> disable) {
		List<Class<? extends Plugin>> foundPluginClasses = Lists.newArrayList();
		Map<Class<? extends Plugin>, File> sourceFiles = Maps.newHashMap();
		Set<String> unknownDisables = Sets.newHashSet(disable);
		Stopwatch sw = Stopwatch.createStarted();
		for (File dir : search) {
			if (!dir.isDirectory()) continue;
			for (File child : dir.listFiles()) {
				if (child.getName().endsWith(".ion")) {
					File target;
					try {
						target = child.getCanonicalFile();
					} catch (IOException e) {
						log.warn("IO error while trying to resolve {}", child);
						continue;
					}
					boolean foundAnything = false;
					if (target.isDirectory()) {
						for (File f : Files.fileTraverser().breadthFirst(target)) {
							if (f.getName().endsWith(".class")) {
								try (InputStream is = new FileInputStream(f)) {
									String pluginClass = getPluginClass(is);
									if (pluginClass != null) {
										loader.addURL(target.toURI().toURL());
										@SuppressWarnings("unchecked")
										Class<? extends Plugin> c = (Class<? extends Plugin>)Class.forName(pluginClass, false, loader);
										foundPluginClasses.add(c);
										sourceFiles.put(c, child);
										foundAnything = true;
									}
								} catch (IOException e) {
									log.warn("IO error while trying to read {}", f, e);
								} catch (ClassNotFoundException e) {
									log.warn("Unexpected error while trying to load {}", f, e);
								}
							}
						}
						if (!foundAnything) {
							log.warn("{} contains no loadable plugins", child);
						}
					} else {
						try (InputStream is = new FileInputStream(target)) {
							byte[] magic = new byte[18];
							ByteStreams.readFully(is, magic);
							if (!new String(magic, Charsets.UTF_8).equals("ionChannel Plugin\n")) {
								log.warn("{} doesn't seem to be a valid ionChannel plugin file", child);
								continue;
							}
						} catch (EOFException e) {
							log.warn("{} doesn't seem to be a valid ionChannel plugin file", child, log.isDebugEnabled() ? e : null);
							continue;
						} catch (IOException e) {
							log.warn("IO error while trying to read {}", child, e);
						}
						try (JarFile jar = new JarFile(target)) {
							for (JarEntry entry : (Iterable<JarEntry>)jar.entries()::asIterator) {
								if (entry.getName().endsWith(".class")) {
									try (InputStream is = jar.getInputStream(entry)) {
										String pluginClass = getPluginClass(is);
										if (pluginClass != null) {
											loader.addURL(target.toURI().toURL());
											@SuppressWarnings("unchecked")
											Class<? extends Plugin> c = (Class<? extends Plugin>)Class.forName(pluginClass, false, loader);
											foundPluginClasses.add(c);
											sourceFiles.put(c, child);
											foundAnything = true;
										}
									} catch (IOException e) {
										log.warn("IO error while trying to read {}!{}", child, entry.getName(), e);
									} catch (ClassNotFoundException e) {
										log.warn("Unexpected error while trying to load {}!{}", child, entry.getName(), e);
									}
								}
							}
						} catch (ZipException e) {
							log.warn("{} doesn't seem to be a valid ionChannel plugin file", child, log.isDebugEnabled() ? e : null);
							continue;
						} catch (IOException e) {
							log.warn("IO error while trying to read {}", child, e);
							continue;
						}
						if (!foundAnything) {
							log.warn("{} contains no loadable plugins", child);
						}
					}
				}
			}
		}
		Map<String, Plugin> allPlugins = Maps.newHashMap();
		for (Class<? extends Plugin> clazz : foundPluginClasses) {
			try {
				Plugin p = clazz.getConstructor().newInstance();
				if (allPlugins.containsKey(p.getId())) {
					Class<? extends Plugin> curClazz = allPlugins.get(p.getId()).getClass();
					log.warn("{} from {} has the same ID ({}) as {} from {} and will clobber it",
							clazz.getName(), sourceFiles.get(clazz),
							p.getId(),
							curClazz.getName(), sourceFiles.containsKey(curClazz) ? sourceFiles.get(curClazz) : "[built-in]");
				}
				allPlugins.put(p.getId(), p);
			} catch (InstantiationException e) {
				log.warn("Plugin {} from {} threw an exception during instantiation", clazz.getName(), sourceFiles.get(clazz), e);
			} catch (InvocationTargetException e) {
				log.warn("Plugin {} from {} threw an exception during instantiation", clazz.getName(), sourceFiles.get(clazz), e.getCause());
			} catch (NoSuchMethodException | IllegalAccessException e) {
				log.warn("Plugin {} from {} cannot be instantiated as it has no public no-args constructor", clazz.getName(), sourceFiles.get(clazz));
			} catch (SecurityException | IllegalArgumentException e) {
				throw new AssertionError(e);
			}
		}
		int builtIn = 0;
		try (HeldLock hl = rwl.obtainReadLock()) {
			for (Plugin p : plugins.values()) {
				if (allPlugins.containsKey(p.getId())) {
					Class<? extends Plugin> clazz = p.getClass();
					Class<? extends Plugin> curClazz = allPlugins.get(p.getId()).getClass();
					log.warn("{} from {} has the same ID ({}) as {} from {} and will clobber it",
							clazz.getName(), sourceFiles.get(clazz),
							p.getId(),
							curClazz.getName(), sourceFiles.containsKey(curClazz) ? sourceFiles.get(curClazz) : "[built-in]");
				}
				if (p instanceof BuiltInPlugin) {
					builtIn++;
				}
				allPlugins.put(p.getId(), p);
			}
		}
		log.info("Discovered {} plugins ({} built-in) in {} files in {}", allPlugins.size(), builtIn, Sets.newHashSet(sourceFiles.values()).size(), sw);
		Multimap<Plugin, Plugin> unresolvedWants = HashMultimap.create();
		Multimap<String, Plugin> conflicts = HashMultimap.create();
		Multimap<String, Plugin> provides = HashMultimap.create();
		for (Plugin p : allPlugins.values()) {
			if (p instanceof BuiltInPlugin && !disable.contains(p.getId())) {
				enable.add(p.getId());
			}
			for (String provide : p.getProvides()) {
				provides.put(provide, p);
			}
		}
		MutableValueGraph<Plugin, EdgeType> graph = ValueGraphBuilder.directed().allowsSelfLoops(false).build();
		for (String en : enable) {
			Plugin p = allPlugins.get(en);
			if (p == null) {
				log.error("Cannot enable {} as it cannot be found", en);
				return null;
			}
			if (!addPluginToGraph(disable, allPlugins, unresolvedWants, conflicts, provides,
					graph, p)) {
				return null;
			}
		}
		for (Map.Entry<Plugin, Collection<Plugin>> p : unresolvedWants.asMap().entrySet()) {
			if (conflicts.containsKey(p.getKey().getId()) ||
					p.getKey().getConflicts().stream()
						.map(allPlugins::get).filter(Predicate.not(Objects::isNull))
						.anyMatch(graph.nodes()::contains)) {
				// probably not going to be enabled for a reason
				continue;
			}
			log.info("{} is wanted by {}\nHowever, it is not going to be enabled",
					p.getKey().toFriendlyString(), summarize(p.getValue(), Plugin::toFriendlyString));
		}
		CharSink dot = Files.asCharSink(new File("plugins.dot"), Charsets.UTF_8);
		try (Writer w = dot.openStream()) {
			w.write("digraph plugins {\n");
			for (Plugin p : graph.nodes()) {
				if (graph.degree(p) == 0 || enable.contains(p.getId())) {
					w.write("\t\"");
					w.write(p.getId());
					w.write("\"");
					if (enable.contains(p.getId())) {
						w.write(" [color=lime]");
					}
					w.write(";\n");
				}
			}
			for (EndpointPair<Plugin> ep : graph.edges()) {
				EdgeType val = graph.edgeValue(ep).orElse(null);
				w.write("\t\"");
				w.write(ep.source().getId());
				w.write("\" -> \"");
				if (val.isProvide()) {
					w.write(Iterables.getFirst(Sets.intersection(val.isNeed() ? ep.source().getNeeds() : ep.source().getWants(), ep.target().getProvides()), ep.target().getId()));
				} else {
					w.write(ep.target().getId());
				}
				w.write("\"");
				if (!val.isNeed()) {
					w.write(" [style=dashed]");
				}
				w.write(";\n");
			}
			for (Plugin p : allPlugins.values()) {
				for (String s : p.getProvides()) {
					w.write("\t\"");
					w.write(s);
					w.write("\"");
					w.write(" [color=blue] [shape=box]");
					w.write(";\n");
					w.write("\t\"");
					w.write(p.getId());
					w.write("\" -> \"");
					w.write(s);
					w.write("\"");
					w.write(" [color=blue]");
					w.write(";\n");
				}
				if (graph.nodes().contains(p)) continue;
				w.write("\t\"");
				w.write(p.getId());
				w.write("\" [style=dotted]");
				w.write(";\n");
				for (String conflict : p.getConflicts()) {
					if (allPlugins.containsKey(conflict)) {
						w.write("\t\"");
						w.write(p.getId());
						w.write("\" -> \"");
						w.write(conflict);
						w.write("\"");
						w.write(" [color=red]");
						w.write(";\n");
					}
				}
			}
			for (Map.Entry<String, Plugin> s : conflicts.entries()) {
				w.write("\t\"");
				w.write(s.getValue().getId());
				w.write("\" -> \"");
				w.write(s.getKey());
				w.write("\"");
				w.write(" [color=red]");
				w.write(";\n");
			}
			for (Map.Entry<Plugin, Plugin> s : unresolvedWants.entries()) {
				w.write("\t\"");
				w.write(s.getValue().getId());
				w.write("\" -> \"");
				w.write(s.getKey().getId());
				w.write("\"");
				w.write(" [style=dashed]");
				w.write(";\n");
			}
			w.write("}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Kahn's algorithm
		List<Plugin> sorted = Lists.newArrayList();
		List<Plugin> noIncoming = Lists.newArrayList();
		for (Plugin p : graph.nodes()) {
			if (graph.inDegree(p) == 0) {
				noIncoming.add(p);
			}
		}
		SharedRandom.shuffle(noIncoming);

		while (!noIncoming.isEmpty()) {
			Plugin n = noIncoming.remove(0);
			sorted.add(n);
			for (Plugin m : Lists.newArrayList(graph.successors(n))) {
				graph.removeEdge(n, m);
				if (graph.inDegree(m) == 0) {
					noIncoming.add(m);
				}
			}
		}

		if (!graph.edges().isEmpty()) {
			System.out.println(graph);
			log.error("There is a dependency cycle");
			return null;
		}
		return Lists.reverse(sorted);
	}

	private static <T> String summarize(Collection<T> c, Function<T, String> toString) {
		if (c.isEmpty()) return "[empty]";
		if (c.size() == 1) {
			return toString.apply(Iterables.getOnlyElement(c));
		} else if (c.size() == 2) {
			return toString.apply(Iterables.getFirst(c, null))+" and "+String.valueOf(Iterables.getLast(c, null));
		}
		return toString.apply(Iterables.getFirst(c, null))+" and "+(c.size()-1)+" others";
	}

	private static boolean addPluginToGraph(Set<String> disable,
			Map<String, Plugin> allPlugins,
			Multimap<Plugin, Plugin> unresolvedWants,
			Multimap<String, Plugin> conflicts, Multimap<String, Plugin> provides,
			MutableValueGraph<Plugin, EdgeType> graph,
			Plugin p) {
		if (conflicts.containsKey(p.getId())) {
			log.error("Cannot enable {}\nIt conflicts with {}",
					p.toFriendlyString(), summarize(conflicts.get(p.getId()), Plugin::toFriendlyString));
			return false;
		}
		Set<Plugin> edgesNeed = Sets.newHashSet();
		Set<Plugin> edgesNeedProvide = Sets.newHashSet();
		Set<Plugin> edgesWant = Sets.newHashSet();
		Set<Plugin> edgesWantProvide = Sets.newHashSet();
		for (String need : p.getNeeds()) {
			if (provides.containsKey(need)) {
				log.debug("{} needs {} which is provided by {}", p.getId(), need, summarize(provides.get(need), Plugin::getId));
				edgesNeedProvide.addAll(provides.get(need));
				continue;
			}
			Plugin needP = allPlugins.get(need);
			if (needP == null) {
				log.warn("Cannot enable {}\nIt needs {} which cannot be found", p.toFriendlyString(), need);
				return false;
			}
			log.debug("Enabling {} as it's needed by {}", needP.getId(), p.getId());
			if (addPluginToGraph(disable, allPlugins, unresolvedWants, conflicts, provides, graph, needP)) {
				edgesNeed.add(needP);
			} else {
				log.warn("Cannot enable {}\nIt needs {} which could not be enabled",
						p.toFriendlyString(), needP.toFriendlyString());
				return false;
			}
		}
		for (String want : p.getWants()) {
			if (provides.containsKey(want)) {
				log.debug("{} wants {} which is provided by {}", p.getId(), want, summarize(provides.get(want), Plugin::getId));
				edgesWantProvide.addAll(provides.get(want));
				continue;
			}
			if (allPlugins.containsKey(want)) {
				Plugin wantP = allPlugins.get(want);
				if (wantP == null) continue;
				if (!graph.nodes().contains(wantP)) {
					unresolvedWants.put(wantP, p);
				} else {
					edgesWant.add(wantP);
				}
			}
		}
		for (String conflict : p.getConflicts()) {
			if (allPlugins.containsKey(conflict) && graph.nodes().contains(allPlugins.get(conflict))) {
				log.warn("Cannot enable {}\nIt conflicts with {}", p.toFriendlyString(), allPlugins.get(conflict).toFriendlyString());
				return false;
			}
			conflicts.put(conflict, p);
		}
		graph.addNode(p);
		for (Plugin edge : edgesNeed) {
			graph.putEdgeValue(p, edge, EdgeType.NEED);
		}
		for (Plugin want : edgesWant) {
			graph.putEdgeValue(p, want, EdgeType.WANT);
		}
		for (Plugin edge : edgesNeedProvide) {
			graph.putEdgeValue(p, edge, EdgeType.NEED_PROVIDE);
		}
		for (Plugin want : edgesWantProvide) {
			graph.putEdgeValue(p, want, EdgeType.WANT_PROVIDE);
		}
		for (Plugin wantsMe : unresolvedWants.get(p)) {
			graph.putEdgeValue(wantsMe, p, EdgeType.WANT);
		}
		unresolvedWants.removeAll(p);
		return true;
	}
	
	private static String getPluginClass(InputStream is) throws IOException {
		ClassReader cr = new ClassReader(is);
		ClassNode cn = new ClassNode(Opcodes.ASM7);
		cr.accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		if ((cn.access & Opcodes.ACC_ABSTRACT) != 0) return null;
		if ("social/ionch/api/plugin/Plugin".equals(cn.superName)) {
			return cn.name.replace('/', '.');
		}
		return null;
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
		resolve(Collections.emptyList(), Collections.emptySet(), Collections.emptySet());
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
