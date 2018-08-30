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

import org.slf4j.Logger;
import org.slf4j.Marker;

public class LoggerImpl implements Logger {
	public static final int LEVEL_TRACE = 0;
	public static final int LEVEL_DEBUG = 1;
	public static final int LEVEL_INFO = 2;
	public static final int LEVEL_WARN = 3;
	public static final int LEVEL_ERROR = 4;
	
	private String name;
	private int logLevel = LEVEL_INFO;
	
	/* package-private */ LoggerImpl(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	
	
	
	
	@Override
	public boolean isTraceEnabled() {
		return logLevel<=LEVEL_TRACE;
	}
	
	@Override
	public boolean isTraceEnabled(Marker marker) {
		return isTraceEnabled();
	}

	@Override
	public void trace(String msg) {
		if (isTraceEnabled()) emit("TRACE", msg);
	}

	@Override
	public void trace(String format, Object arg) {
		if (isTraceEnabled()) emit("TRACE", format, arg);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		if (isTraceEnabled()) emit("TRACE", format, arg1, arg2);
	}

	@Override
	public void trace(String format, Object... arguments) {
		if (isTraceEnabled()) emit("TRACE", format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		if (isTraceEnabled()) emit("TRACE", msg, t);
	}

	@Override
	public void trace(Marker marker, String msg) {
		trace(msg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		trace(format, arg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		trace(format, arg1, arg2);
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		trace(format, argArray);
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		trace(msg, t);
	}

	
	
	
	
	@Override
	public boolean isDebugEnabled() {
		return logLevel<=LEVEL_DEBUG;
	}
	
	@Override
	public boolean isDebugEnabled(Marker marker) {
		return isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		if (isDebugEnabled()) emit("DEBUG", msg);
	}

	@Override
	public void debug(String format, Object arg) {
		if (isDebugEnabled()) emit("DEBUG", format, arg);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		if (isDebugEnabled()) emit("DEBUG", format, arg1, arg2);
	}

	@Override
	public void debug(String format, Object... arguments) {
		if (isDebugEnabled()) emit("DEBUG", format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		if (isDebugEnabled()) emit("DEBUG", msg, t);
	}

	@Override
	public void debug(Marker marker, String msg) {
		if (isDebugEnabled()) emit("DEBUG", msg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		if (isDebugEnabled()) emit("DEBUG", format, arg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		if (isDebugEnabled()) emit("DEBUG", format, arg1, arg2);
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		if (isDebugEnabled()) emit("DEBUG", format, arguments);
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		if (isDebugEnabled()) emit("DEBUG", msg, t);
	}

	
	
	
	
	@Override
	public boolean isInfoEnabled() {
		return logLevel<=LEVEL_INFO;
	}
	
	@Override
	public boolean isInfoEnabled(Marker marker) {
		return isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		if (isInfoEnabled()) emit("INFO ", msg);
	}

	@Override
	public void info(String format, Object arg) {
		if (isInfoEnabled()) emit("INFO ", format, arg);
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		if (isInfoEnabled()) emit("INFO ", format, arg1, arg2);
	}

	@Override
	public void info(String format, Object... arguments) {
		if (isInfoEnabled()) emit("INFO ", format, arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		if (isInfoEnabled()) emit("INFO ", msg, t);
	}

	@Override
	public void info(Marker marker, String msg) {
		if (isInfoEnabled()) emit("INFO ", msg);
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		if (isInfoEnabled()) emit("INFO ", format, arg);
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		if (isInfoEnabled()) emit("INFO ", format, arg1, arg2);
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		if (isInfoEnabled()) emit("INFO ", format, arguments);
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		if (isInfoEnabled()) emit("INFO ", msg, t);
	}

	
	
	
	
	@Override
	public boolean isWarnEnabled() {
		return logLevel<=LEVEL_WARN;
	}
	
	@Override
	public boolean isWarnEnabled(Marker marker) {
		return isWarnEnabled();
	}

	@Override
	public void warn(String msg) {
		if (isWarnEnabled()) emit("WARN ", msg);
	}

	@Override
	public void warn(String format, Object arg) {
		if (isWarnEnabled()) emit("WARN ", format, arg);
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		if (isWarnEnabled()) emit("WARN ", format, arg1, arg2);
	}
	
	@Override
	public void warn(String format, Object... arguments) {
		if (isWarnEnabled()) emit("WARN ", format, arguments);
	}

	@Override
	public void warn(String msg, Throwable t) {
		if (isWarnEnabled()) emit("WARN ", msg, t);
	}

	@Override
	public void warn(Marker marker, String msg) {
		if (isWarnEnabled()) emit("WARN ", msg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		if (isWarnEnabled()) emit("WARN ", format, arg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		if (isWarnEnabled()) emit("WARN ", format, arg1, arg2);
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		if (isWarnEnabled()) emit("WARN ", format, arguments);
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		if (isWarnEnabled()) emit("WARN ", msg, t);
	}

	
	
	
	
	@Override
	public boolean isErrorEnabled() {
		return logLevel<=LEVEL_ERROR;
	}
	
	@Override
	public boolean isErrorEnabled(Marker marker) {
		return isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		if (isErrorEnabled()) emit("ERROR", msg);
	}

	@Override
	public void error(String format, Object arg) {
		if (isErrorEnabled()) emit("ERROR", format, arg);
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		if (isErrorEnabled()) emit("ERROR", format, arg1, arg2);
	}

	@Override
	public void error(String format, Object... arguments) {
		if (isErrorEnabled()) emit("ERROR", format, arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		if (isErrorEnabled()) emit("ERROR", msg, t);
	}

	@Override
	public void error(Marker marker, String msg) {
		if (isErrorEnabled()) emit("ERROR", msg);
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		if (isErrorEnabled()) emit("ERROR", format, arg);
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		if (isErrorEnabled()) emit("ERROR", format, arg1, arg2);
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		if (isErrorEnabled()) emit("ERROR", format, arguments);
		
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		if (isErrorEnabled()) emit("ERROR", msg, t);
	}
	
	
	
	
	
	private void emit(String level, String msg, Throwable t) {
		emit(level, msg);
		emit(level, t.getClass().getSimpleName()+": "+t.getMessage());
		
		String lastElem = "";
		int elemCount = 0;
		for(StackTraceElement elem : t.getStackTrace()) {
			String cur = elem.toString();
			if (cur==lastElem) {
				elemCount++;
				continue;
			} else {
				if (elemCount>0) {
					emit(level, "      x "+elemCount);
					elemCount = 0;
				}
				
				emit(level, "    "+cur);
			}
		}
	}
	
	private void emit(String level, String msg) {
		System.out.println("["+level+" : "+name+"] "+msg);
	}
	
	private void emit(String level, String msg, Object... arguments) {
		String composed = "["+level+" : "+name+"] "+String.format(msg, arguments);
		
		System.out.println(composed); //For now...
	}
}
