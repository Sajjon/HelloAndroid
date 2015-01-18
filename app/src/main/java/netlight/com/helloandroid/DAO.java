/**
 * This class is a DAO (Data Access Object) used for persistance.
 */

package netlight.com.helloandroid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DAO {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public DAO(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() {
		if (dbHelper != null) {
			try {
				database = dbHelper.getWritableDatabase();
			} catch (SQLException e) {
				System.err.println("DAO: error while opening database");
			}
		}
	}

	public void close() {
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

	public void addLogin(long userId) {

		ContentValues values = new ContentValues();

		/* Insert values in database */
		values.put(MySQLiteHelper.TABLE_LOGINS_COLUMN_USER_ID, userId);

		long unixTimestamp = System.currentTimeMillis() / 1000L;
		values.put(MySQLiteHelper.TABLE_LOGINS_COLUMN_TIMESTAMP, unixTimestamp);

		database.insert(MySQLiteHelper.TABLE_LOGINS, null, values);
	}

	public User createUser(String email, String password) {
		String hashedPassword = PasswordEncrypter.encrypt(password);
		if (hashedPassword == null) {
			System.err.println("Could not hash password");
			return null;
		}

		ContentValues values = new ContentValues();

		/* Insert values in database */
		values.put(MySQLiteHelper.TABLE_USERS_COLUMN_EMAIL, email);
		values.put(MySQLiteHelper.TABLE_USERS_COLUMN_PASSWORD, hashedPassword);

		long insertId = database.insert(MySQLiteHelper.TABLE_USERS, null, values);
		String idColumn = String.format("%s=%d", MySQLiteHelper.TABLE_USERS_COLUMN_ID, insertId);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, null, idColumn, null, null, null, null);
		cursor.moveToFirst();
		User newUser = cursorToUser(cursor);
		cursor.close();
		return newUser;
	}
	
	public String[] getLoginsCopy(long id) {
		ArrayList<String> logins = new ArrayList<String>();
		String loginQuery = String.format("SELECT * FROM %s WHERE %s=?", MySQLiteHelper.TABLE_LOGINS, MySQLiteHelper.TABLE_LOGINS_COLUMN_ID);
		String idString = "" + id;
		Cursor cursor = database.rawQuery(loginQuery, new String[] { idString });
		cursor.moveToLast();
		if (cursor != null) {
			while (!cursor.isBeforeFirst()) {
				long unixTimeStamp = cursor.getLong(2);
				Date time = new java.util.Date((long) unixTimeStamp * 1000);
				String date = time.toString();
				logins.add(date);
				cursor.moveToPrevious();
			}
		}

		cursor.close();

		/* Invert */
		String[] loginsArray = new String[logins.size()];
		int j = 0;
		for (int i = logins.size(); i > 0; --i) {
			loginsArray[j] = logins.get(i - 1);
			j++;
		}

		return loginsArray;
	}

	public String[] getFiveLatestLogins(long id) {
		ArrayList<String> logins = new ArrayList<String>();

		String loginQuery = String.format("SELECT * FROM %s WHERE %s=?", MySQLiteHelper.TABLE_LOGINS, MySQLiteHelper.TABLE_LOGINS_COLUMN_USER_ID);
		String idString = "" + id;
		Cursor cursor = database.rawQuery(loginQuery, new String[] { idString });
		cursor.moveToLast();
		int rows = 0;
		if (cursor != null) {
			while (!cursor.isBeforeFirst()) {
				long unixTimeStamp = cursor.getLong(2);
				Date time = new java.util.Date((long) unixTimeStamp * 1000);
				String date = time.toString();
				logins.add(date);
				rows++;
				if (rows == 5) {
					break;
				}
				cursor.moveToPrevious();
			}
		}

		cursor.close();

		/* Invert */
		String[] loginsArray = new String[logins.size()];
		int j = 0;
		for (int i = logins.size(); i > 0; --i) {
			loginsArray[j] = logins.get(i - 1);
			j++;
		}

		return loginsArray;
	}

	public void deleteUser(User user) {
		long id = user.getId();
		System.out.printf("Deleted user with id: %d\n", id);
		String idColumn = String.format("%s=%d", MySQLiteHelper.TABLE_USERS_COLUMN_ID, id);
		database.delete(MySQLiteHelper.TABLE_USERS, idColumn, null);
	}

	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			User user = cursorToUser(cursor);
			users.add(user);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return users;
	}

	public int getSize() {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, null, null, null, null, null, null);
		return cursor.getCount();
	}

	public void clearDB() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(MySQLiteHelper.TABLE_LOGINS, null, null);
		db.delete(MySQLiteHelper.TABLE_USERS, null, null);
	}

	public boolean emailExistsInTable(String email) {
		String loginQuery = String.format("SELECT * FROM %s WHERE %s=?", MySQLiteHelper.TABLE_USERS, MySQLiteHelper.TABLE_USERS_COLUMN_EMAIL);
		Cursor cursor = database.rawQuery(loginQuery, new String[] { email });
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
		}
		cursor.close();
		return false;
	}

	public User login(String email, String password) {
		User user = null;
		String hashedPassword = PasswordEncrypter.encrypt(password);
		if (hashedPassword == null) {
			System.err.println("Could not hash password");
			return null;
		}
		String loginQuery = String.format("SELECT * FROM %s WHERE %s=? AND %s=?", MySQLiteHelper.TABLE_USERS, MySQLiteHelper.TABLE_USERS_COLUMN_EMAIL,
				MySQLiteHelper.TABLE_USERS_COLUMN_PASSWORD);
		Cursor cursor = database.rawQuery(loginQuery, new String[] { email, hashedPassword });
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				user = cursorToUser(cursor);
			}
		}
		cursor.close();

		return user;
	}

	private User cursorToUser(Cursor cursor) {
		User user = new User();
		cursor.moveToFirst();
		long id = cursor.getLong(0);
		String email = cursor.getString(1);
		String password = cursor.getString(2);

		user.setId(id);
		user.setEmail(email);
		user.setPassword(password);
		return user;
	}
}