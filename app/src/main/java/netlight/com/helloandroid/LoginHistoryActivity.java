package netlight.com.helloandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class LoginHistoryActivity extends Activity implements OnClickListener {

	private TextView _textViewHello;
	private User _user;
	private Button _btnSignOut;
	private ListView _loginList;
	private DAO _dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_history);

		_user = (User) getIntent().getParcelableExtra("user");
		_textViewHello = (TextView) findViewById(R.id.history_textview);

		_textViewHello.setText(String.format("Welcome %s", _user.getEmail()));

		_loginList = (ListView) findViewById(R.id.login_list);

		_dao = new DAO(this);
		_dao.open();
		_dao.addLogin(_user.getId());
		
		String[] values = _dao.getFiveLatestLogins(_user.getId());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		_loginList.setAdapter(adapter);

		_btnSignOut = (Button) findViewById(R.id.sign_out_button);
		_btnSignOut.setOnClickListener(this);
	}

	@Override
	public void finish() {
		_dao.close();
		super.finish();
	}

	private void signOut() {
		Intent intent;
		intent = new Intent(LoginHistoryActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == _btnSignOut.getId()) {
			signOut();
		}
	}

}
