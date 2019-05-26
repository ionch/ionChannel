package social.ionch.builtin.db;

import java.io.File;

import social.ionch.api.Ionch;
import social.ionch.api.db.Database;
import social.ionch.api.db.DatabaseException;
import social.ionch.api.social.User;
import social.ionch.core.IonChannel;

public class FlatfileDatabase implements Database {
	protected File base;
	
	public FlatfileDatabase(String basePath) {
		base = new File(basePath);
		if (!base.exists()) {
			try {
				
				
				
				
			} catch (Throwable t) {
				
			}
		}
	}
	
	@Override
	public User getUserById(int id) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUserByName(String name) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
