package netlight.com.helloandroid;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;

public class User implements Parcelable {

    // id is generated by the database and set on the object automagically
    @DatabaseField(generatedId = true)
	long id;

	// Constructor that takes a Parcel and gives you an object populated with it's values
	private User(Parcel in) {
		id = in.readLong();
		email = in.readString();
		password = in.readString();
	}
    @DatabaseField
	String email;

    @DatabaseField
    String password;

	// empty constructor needed by OrmLite
	public User() {
	}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
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


    // Here comes some stuff about "Parcelable", what is this?
    // Well this is some stuff we have to do if we want to send
    // User objects between classes using intents. Which we want
    // to do! Between LoginActivity and LoginHistoryActivity e.g.


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

    @Override
    public int describeContents() {
        return 0;
    }

}
