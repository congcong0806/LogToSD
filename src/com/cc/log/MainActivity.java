package com.cc.log;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.umeng.analytics.MobclickAgent;

public class MainActivity extends Activity {

	private Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LogCrashHandler handler = LogCrashHandler.getInstance();
		handler.init(MainActivity.this);
		// MobclickAgent.onError(this);
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(btnOnClickListener);
	}

	private OnClickListener btnOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			throw new IndexOutOfBoundsException();
		}
	};

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
