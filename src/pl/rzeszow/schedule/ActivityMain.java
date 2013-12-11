package pl.rzeszow.schedule;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ActivityMain extends SherlockActivity {
	private final String JSONFILE = "plan.json";
	private final Type collectionType = new TypeToken<ArrayList<Day>>(){}.getType();
	private DayArray ints;
	private DayAdapter dayAdapter;
	private ListView mainList;
	Gson gson;
	
	private LinearLayout ll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mainList = (ListView) findViewById(R.id.listView1);
		ll = (LinearLayout) findViewById(R.id.ll);
		
		gson = new Gson();
		ints = gson.fromJson(getJsonString(), DayArray.class);
		
		dayAdapter = new DayAdapter(this, ints);
		mainList.setAdapter(dayAdapter);
	}
	
	private String getJsonString() {
		try {
			FileInputStream inputStream = openFileInput(JSONFILE);
			StringBuffer sb = new StringBuffer("");
			
			byte[] buffer = new byte[1024];
			int  n;
			while ((n = inputStream.read(buffer)) != -1) {
				sb.append(new String(buffer, 0, n));
			}
			inputStream.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i("Error", "FileNotFoundException");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "[]";
	}
	
	private void update() {
		new Download(this, ll, mainList).execute();
	}
	
	private void show() {
		ints = gson.fromJson(getJsonString(), collectionType);
		dayAdapter = new DayAdapter(this, ints);
		mainList.setAdapter(dayAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinf = getSupportMenuInflater();
		menuinf.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuUpd:
			update();
			break;
			
		case R.id.menuExit:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	class Download extends AsyncTask<Void, Integer, Void> {

		Context context;
		private LinearLayout ll;
		private ListView lv;
		
		public Download (Context cont, LinearLayout ll, ListView lv){
			context = cont;
			this.ll = ll;
			this.lv = lv;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			lv.setVisibility(View.GONE);
			ll.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://srv.rzeszow.net/plan.json");
			
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					
					String line;
					
					while ((line = reader.readLine()) != null)
						builder.append(line);
				} else {
					Log.e("TAG", "Failed to download file");
				}
				
				FileOutputStream outputStream = openFileOutput(JSONFILE, Context.MODE_PRIVATE);
				outputStream.write(builder.toString().getBytes());
				outputStream.close();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			lv.setVisibility(View.VISIBLE);
			ll.setVisibility(View.GONE);
			((ActivityMain) context).show();
		}
	}
}
