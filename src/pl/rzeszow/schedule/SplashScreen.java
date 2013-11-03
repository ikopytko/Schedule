package pl.rzeszow.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {
	private boolean mIsBackButtonPressed;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		intent = new Intent(SplashScreen.this, ActivityMain.class);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
				if (!mIsBackButtonPressed) {
					SplashScreen.this.startActivity(intent);
				}
			}
		}, 1500);
	}

	@Override
	public void onBackPressed() {
		// set the flag to true so the next activity won't start up
		mIsBackButtonPressed = true;
		super.onBackPressed();
	}
}