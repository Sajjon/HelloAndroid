package netlight.com.helloandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import netlight.com.helloandroid.User;

public class SignUpActivity extends Activity implements OnClickListener {

	private Button _btnSignUp;
	private Button _btnBack;
	private EditText _editTextMail;
	private EditText _editTextPassword;
	private EditText _editTextPasswordConfirmation;

	private String _email;
	private String _password;
	private String _passwordConfirmation;

	private User _user;
	private DAO _dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		_dao = new DAO(this);
		_dao.open();

		_btnSignUp = (Button) findViewById(R.id.sign_up_button);
		_editTextMail = (EditText) findViewById(R.id.sign_up_email);
		_editTextPassword = (EditText) findViewById(R.id.sign_up_password);
		_editTextPasswordConfirmation = (EditText) findViewById(R.id.sign_up_password_confirm);

		_btnBack = (Button) findViewById(R.id.back_button);

		Intent intent = getIntent();

		if (intent.hasExtra("email")) {
			_editTextMail.setText(intent.getStringExtra("email"));
		}

		if (intent.hasExtra("password")) {
			_editTextPassword.setText(intent.getStringExtra("password"));
		}

		_btnSignUp.setOnClickListener(this);
		_btnBack.setOnClickListener(this);

	}

	@Override
	public void finish() {
		_dao.close();
		super.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	private boolean signUp() {
		if (formsOk()) {
			_user = _dao.createUser(_email, _password);
			if (_user != null) {
				return true;
			}
		}
		return false;
	}

	private boolean formsOk() {
		// Reset errors.
		_editTextMail.setError(null);
		_editTextPassword.setError(null);

		// Store values at the time of the login attempt.
		_email = _editTextMail.getText().toString();
		_password = _editTextPassword.getText().toString();
		_passwordConfirmation = _editTextPasswordConfirmation.getText().toString();

		boolean successfulSignUp = true;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(_password)) {
			_editTextPassword.setError(getString(R.string.error_field_required));
			focusView = _editTextPassword;
			successfulSignUp = false;
		} else if (_password.length() < 4) {
			_editTextPassword.setError(getString(R.string.error_invalid_password));
			focusView = _editTextPassword;
			successfulSignUp = false;
		} else if (!_passwordConfirmation.equals(_password)) {
			_editTextPasswordConfirmation.setError(getString(R.string.error_passwords_mismatch));
			focusView = _editTextPasswordConfirmation;
			successfulSignUp = false;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(_email)) {
			_editTextMail.setError(getString(R.string.error_field_required));
			focusView = _editTextMail;
			successfulSignUp = false;
		} else if (!_email.contains("@")) {
			_editTextMail.setError(getString(R.string.error_invalid_email));
			focusView = _editTextMail;
			successfulSignUp = false;
		} else if (_dao.emailExistsInTable(_email)) {
			_editTextMail.setError(getString(R.string.error_email_taken));
			focusView = _editTextMail;
			successfulSignUp = false;
		}

		if (!successfulSignUp) {
			focusView.requestFocus();
		}

		return successfulSignUp;
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == _btnSignUp.getId()) {
			boolean successful = signUp();
			if (successful) {
				Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
				intent.putExtra("email", _email);
				intent.putExtra("password", _password);
				startActivity(intent);
				finish();
			}
		}

		if (viewId == _btnBack.getId()) {
			Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

}
