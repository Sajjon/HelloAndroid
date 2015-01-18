package netlight.com.helloandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_USERS = "users";
	
	public static final String TABLE_USERS_COLUMN_ID = "_id";
	public static final String TABLE_USERS_COLUMN_EMAIL = "email";
	public static final String TABLE_USERS_COLUMN_PASSWORD = "password";
	
	public static final String TABLE_LOGINS = "logins";
	public static final String TABLE_LOGINS_COLUMN_ID = "_id";
	public static final String TABLE_LOGINS_COLUMN_USER_ID = "user_id";
	public static final String TABLE_LOGINS_COLUMN_TIMESTAMP = "time";

	private static final String DATABASE_NAME = "users.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation SQL statement
	private static final String CREATE_TABLE_USERS = String.format("create table %s(%s integer primary key autoincrement, %s text not null, %s text not null);", TABLE_USERS,
			TABLE_USERS_COLUMN_ID, TABLE_USERS_COLUMN_EMAIL, TABLE_USERS_COLUMN_PASSWORD);
	
	private static final String CREATE_TABLE_LOGINS = String.format("create table %s(%s integer primary key autoincrement, %s integer not null, %s integer not null);", TABLE_LOGINS,
			TABLE_LOGINS_COLUMN_ID, TABLE_LOGINS_COLUMN_USER_ID, TABLE_LOGINS_COLUMN_TIMESTAMP);

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_USERS);
		database.execSQL(CREATE_TABLE_LOGINS);
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		onCreate(db);
	}

}