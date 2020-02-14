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

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A wrapper for a {@link ReadWriteLock} that provides an API compatible with try-with-resources.
 */
public class ResourcefulReadWriteLock {
	private final ReadWriteLock rwl;
	
	private final HeldLock releaseRead;
	private final HeldLock releaseWrite;

	public interface HeldLock extends AutoCloseable {
		/**
		 * Release this lock.
		 */
		@Override
		void close();
	}
	
	private ResourcefulReadWriteLock(ReadWriteLock rwl) {
		this.rwl = rwl;
		releaseRead = rwl.readLock()::unlock;
		releaseWrite = rwl.writeLock()::unlock;
	}
	
	/**
	 * Obtain the read lock on this ReadWriteLock, and return a HeldLock that can be closed to
	 * release the lock.
	 * @return a HeldLock that can be closed to release the lock
	 */
	public HeldLock obtainReadLock() {
		rwl.readLock().lock();
		return releaseRead;
	}
	
	/**
	 * Obtain the write lock on this ReadWriteLock, and return a HeldLock that can be closed to
	 * release the lock.
	 * @return a HeldLock that can be closed to release the lock
	 */
	public HeldLock obtainWriteLock() {
		rwl.writeLock().lock();
		return releaseWrite;
	}
	
	/**
	 * @return a new ResourcefulReadWriteLock backed by an unfair {@link ReentrantReadWriteLock}
	 */
	public static ResourcefulReadWriteLock create() {
		return new ResourcefulReadWriteLock(new ReentrantReadWriteLock());
	}
	
}
