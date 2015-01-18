package netlight.com.helloandroid;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

	private long id;
	private String email;
	private String password;

	// empty constructor
	public User() {
	}

	// Constructor that takes a Parcel and gives you an object populated with it's values
	private User(Parcel in) {
		id = in.readLong();
		email = in.readString();
		password = in.readString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(email);
		dest.writeString(password);
	}

	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};

}
