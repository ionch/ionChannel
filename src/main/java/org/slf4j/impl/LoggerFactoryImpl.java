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

package org.slf4j.impl;

import java.util.concurrent.ConcurrentSkipListMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class LoggerFactoryImpl implements ILoggerFactory {
	private ConcurrentSkipListMap<String, Logger> loggers = new ConcurrentSkipListMap<>();
	
	
	@Override
	public Logger getLogger(String name) {
		Logger l = loggers.get(name);
		if (l==null) {
			l = new LoggerImpl(name);
			loggers.put(name, l);
		}
		
		return l;
	}

}
