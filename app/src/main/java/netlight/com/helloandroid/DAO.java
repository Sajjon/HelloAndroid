/**
 * This class is a DAO (Data Access Object) used for persistance.
 */

package netlight.com.helloandroid;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;


import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class DAO {

	// Database fields
    private DatabaseHelper _databaseHelper;
    private RuntimeExceptionDao<User, Integer> _userDao;
    private RuntimeExceptionDao<LoginEntry, Integer> _loginEntryDao;


	public DAO(Context context) {
        _databaseHelper = new DatabaseHelper(context);
        _userDao = _databaseHelper.getUserDao();
        _loginEntryDao = _databaseHelper.getLoginEntryDao();
	}

	public void close() {
		if (_databaseHelper != null) {
            _databaseHelper.close();
		}
	}

	public void addLogin(long userId) {
        LoginEntry newLoginEntry = new LoginEntry(userId);
        _loginEntryDao.create(newLoginEntry);
	}

	public User createUser(String email, String password) {
		String hashedPassword = PasswordEncrypter.encrypt(password);

        if (hashedPassword == null) {
			System.err.println("Could not hash password");
			return null;
		}

		User newUser = new User(email, hashedPassword);
        _userDao.create(newUser);
		return newUser;
	}

	public String[] getFiveLatestLogins(long userId) {
        HashMap fieldValuesMap = new HashMap();
        fieldValuesMap.put("userId", userId);
        ArrayList<LoginEntry> entryList = new ArrayList<LoginEntry>(_loginEntryDao.queryForFieldValues(fieldValuesMap));

		/* Invert */
        int size = Math.min(5, entryList.size());
		String[] loginsArray = new String[size];
		int j = 0;
		for (int i = entryList.size() - 1; i >= 0; --i) {
            LoginEntry loginEntry = entryList.get(i);
            String date = loginEntry.getDate().toString();
            loginsArray[j] = date;
            ++j;
            if (j >= size) { // We only want the five latest
                break;
            }
		}

		return loginsArray;
	}

	public void deleteUser(User user) {
        _userDao.delete(user);
	}

	public List<User> getAllUsers() {
		List<User> users = _userDao.queryForAll();
		return users;
	}

	public int getSize() {
		int size = 0;
        List<User> users = getAllUsers();
        if (users != null) {
            size = getAllUsers().size();
        }
        return size;
	}

	public void clearDB() {
		_databaseHelper.clearDatabase();
	}

	public boolean emailExistsInTable(String email) {
        HashMap fieldValuesMap = new HashMap();
        fieldValuesMap.put("email", email);
        List userList = _userDao.queryForFieldValues(fieldValuesMap);
        boolean found = userList != null && userList.size() > 0; // should be 1 really, not more than 1.
		return found;
	}

	public User login(String email, String password) {
		User user = null;
		String hashedPassword = PasswordEncrypter.encrypt(password);
		if (hashedPassword == null) {
			System.err.println("Could not hash password");
			return null;
		}

        HashMap fieldValuesMap = new HashMap();
        fieldValuesMap.put("email", email);
        fieldValuesMap.put("password", hashedPassword);
        List userList = _userDao.queryForFieldValues(fieldValuesMap);
        if (userList != null) {
            user = (User) userList.get(0);
        }

		return user;
	}

}