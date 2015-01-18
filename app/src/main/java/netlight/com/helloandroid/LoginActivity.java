package netlight.com.helloandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class LoginActivity extends Activity implements OnClickListener {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask _authTask = null;

	// Values for email and password at the time of the login attempt.
	private String _email;
	private String _password;

	private User _user;

	private Button _btnSignIn;
	private Button _btnSignUp;
	private Button _btnClearDB;

	// UI references.
	private EditText _editTextMail;
	private EditText _editTextPassword;
	private View _loginFormView;
	private View _loginStatusView;
	private TextView _loginStatusMessageView;
	private TextView _rowsInDBView;

	private DAO _dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		_editTextMail = (EditText) findViewById(R.id.email);

		_editTextPassword = (EditText) findViewById(R.id.password);

		Intent intent = getIntent();
		if (intent.hasExtra("email")) {
			_editTextMail.setText(intent.getStringExtra("email"));
			_editTextPassword.setText(intent.getStringExtra("password"));
		}

		_loginFormView = findViewById(R.id.login_form);
		_loginStatusView = findViewById(R.id.login_status);
		_loginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		_btnSignIn = (Button) findViewById(R.id.sign_in_button);

		_btnSignIn.setOnClickListener(this);

		_btnSignUp = (Button) findViewById(R.id.sign_up_button);

		_btnSignUp.setOnClickListener(this);

		_btnClearDB = (Button) findViewById(R.id.clear_db_button);

		_btnClearDB.setOnClickListener(this);

		_dao = new DAO(this);
		_dao.open();

		int rowsInUserDB = _dao.getSize();

		_rowsInDBView = (TextView) findViewById(R.id.rows_in_db);
		_rowsInDBView.setText("" + rowsInUserDB);

		Log.e("LoginActivity", "Size of database: " + _dao.getSize());
	}

	@Override
	public void finish() {
		_dao.close();
		super.finish();
	}

	/**
	 * Attempts to sign in or register the account specified by the login form. If there are form errors (invalid email, missing fields, etc.), the errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (_authTask != null) {
			return;
		}

		// Reset errors.
		_editTextMail.setError(null);
		_editTextPassword.setError(null);

		// Store values at the time of the login attempt.
		_email = _editTextMail.getText().toString();
		_password = _editTextPassword.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(_password)) {
			_editTextPassword.setError(getString(R.string.error_field_required));
			focusView = _editTextPassword;
			cancel = true;
		} else if (_password.length() < 4) {
			_editTextPassword.setError(getString(R.string.error_invalid_password));
			focusView = _editTextPassword;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(_email)) {
			_editTextMail.setError(getString(R.string.error_field_required));
			focusView = _editTextMail;
			cancel = true;
		} else if (!_email.contains("@")) {
			_editTextMail.setError(getString(R.string.error_invalid_email));
			focusView = _editTextMail;
			cancel = true;
		} else if (!_dao.emailExistsInTable(_email)) {
			_editTextMail.setError(getString(R.string.error_email_not_found));
			focusView = _editTextMail;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			_loginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			_authTask = new UserLoginTask();
			_authTask.execute((Void) null);
		}
	}

	private void signUp() {
		_dao.close();
		_email = _editTextMail.getText().toString();
		_password = _editTextPassword.getText().toString();

		Intent intent;
		intent = new Intent(LoginActivity.this, SignUpActivity.class);

		if (!TextUtils.isEmpty(_email)) {
			intent.putExtra("email", _email);
		}

		if (!TextUtils.isEmpty(_password)) {
			intent.putExtra("password", _password);
		}
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == _btnSignIn.getId()) {
			attemptLogin();
		}

		if (viewId == _btnSignUp.getId()) {
			signUp();
		}

		if (viewId == _btnClearDB.getId()) {
			_dao.clearDB();
			Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			_loginStatusView.setVisibility(View.VISIBLE);
			_loginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					_loginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});

			_loginFormView.setVisibility(View.VISIBLE);
			_loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					_loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			_loginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			_loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			_user = _dao.login(_email, _password);
			_dao.close();

			if (_user != null) {
				return true;
			} else {
				return false;
			}

		}

		@Override
		protected void onPostExecute(final Boolean success) {
			_authTask = null;
			showProgress(false);

			if (success) {
				Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_LONG).show();
				System.err.println("LOGGED IN! :D");

				Intent intent;
				intent = new Intent(LoginActivity.this, LoginHistoryActivity.class);
				intent.putExtra("user", _user);
				startActivity(intent);
				finish();
			} else {
				_editTextPassword.setError(getString(R.string.error_incorrect_password));
				_editTextPassword.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			_authTask = null;
			showProgress(false);
		}
	}

}
