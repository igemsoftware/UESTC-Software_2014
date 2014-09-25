package com.example.crispr_x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AboutActivity extends Activity {
	private ListView lvMenu;
	private EditText etCheck; // check�༭��
	private Button btnCheck; // check��ť
	private Button btnNews; // News��ť

	private long timeInterval = 60 * 1000;
	private int SCREEN_WIDTH, SCREEN_HEIGHT; // ��Ļ�߿�
	private boolean isLogin;
	SimpleAdapter listItemAdapter, listItemAdapter2;
	ProgressDialog pDialog;
	AlertDialog alertDialog;
	Handler timeHandler;
	Runnable runnable1;
	HttpThreadPost myHttpThreadPost;
	static Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// ���ر���
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// ����ȫ��

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		SCREEN_WIDTH = dm.widthPixels;
		SCREEN_HEIGHT = dm.heightPixels;

		setContentView(R.layout.activity_about);
		lvMenu = (ListView) findViewById(R.id.listView_menu);
		btnNews = (Button) findViewById(R.id.button_news);

		btnNews.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				newsDialog();
			}
		});

		btnNews.setOnTouchListener(new Button.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.drawable.deep);
				} else {
					v.setBackgroundResource(R.drawable.shallow);
				}
				return false;
			}
		});

		/************************** �������ӽ�����ʾ�� *************************/

		pDialog = new ProgressDialog(this);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		/**************** ��ʱ�� ****************/

		timeHandler = new Handler();

		runnable1 = new Runnable() {
			@Override
			public void run() {
				pDialog.dismiss();
				Toast.makeText(getApplicationContext(), "Time out",
						Toast.LENGTH_SHORT).show();
			}
		};

		/************************** msg���� *************************/

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String result = null;
				switch (msg.what) {
				case HttpThreadPost.CHECKID:
					pDialog.dismiss();
					timeHandler.removeCallbacks(runnable1);
					System.out.println("����CHECKID");
					result = (String) msg.obj;
					startService();
					if (getStatus(result)) {
						saveMessage(result);
						Intent intent = new Intent(AboutActivity.this,
								ResultActivity.class); // ����Activity
						startActivity(intent);
					} else {
						debugDialog(result);
					}
					break;

				default:
					break;
				}
			}
		};

		/************** ��Ӳ˵� ***************/

		CreatList menuList = new CreatList();
		List<Map<String, Object>> list = menuList.creatMenu();
		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		listItemAdapter = new SimpleAdapter(this, list, R.layout.menu_list,
				new String[] { "icon", "menu" }, new int[] {
						R.id.imageView_icon, R.id.textView_menu });

		// ��Ӳ�����ʾ
		lvMenu.setAdapter(listItemAdapter);

		lvMenu.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent;
				switch (arg2) {
				case 0:
					intent = new Intent(AboutActivity.this,
							SubmitActivity.class); // ����Activity
					startActivity(intent);
					finish();
					break;
				case 1:
					checkIdDialog();
					break;
				case 2:
					if (isLogin) {
						intent = new Intent(AboutActivity.this,
								HistoryActivity.class); // ����Activity
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(),
								"Please login !", Toast.LENGTH_SHORT).show();
					}
					break;
				case 3:
					intent = new Intent(AboutActivity.this, WebActivity.class); // ����Activity
					startActivity(intent);
					finish();
					break;
				case 4:
					break;
				case 5:
					break;
				}
			}
		});

	}

	/************************** News�Ի��� *************************/

	protected void newsDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View DialogView = null;
		DialogView = inflater.inflate(R.layout.news_dialog, null);
		AlertDialog.Builder builder = new Builder(AboutActivity.this);
		builder.setView(DialogView);

		final WebView webview = (WebView) DialogView.findViewById(R.id.webView_news);
		Button btnBavk = (Button) DialogView.findViewById(R.id.button_back);

		// ����WebView���ԣ��ܹ�ִ��Javascript�ű�
		webview.getSettings().setJavaScriptEnabled(true);
		// ������Ҫ��ʾ����ҳ
		webview.loadUrl("http://www.baidu.com/s?wd=crispr&ie=utf-8&tn=baiduhome_pg&f=8&oq=crispe&rsv_bp=1&rsv_spt=1&rsv_enter=1&rsv_sug3=15&rsv_sug4=1549&rsv_sug1=12&rsv_sug2=0&rsp=0&inputT=308");
		// ����Web��ͼ
		webview.setWebViewClient(new HelloWebViewClient());

		btnBavk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				webview.goBack();
			}
		});
		
		alertDialog = builder.create();
		alertDialog.show();
		WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
		lp.width = (int) (SCREEN_WIDTH);// ������
		lp.height = (int) (SCREEN_HEIGHT);// ����߶�
		alertDialog.getWindow().setAttributes(lp);
	}

	/************************** ��ѯ�Ի��� *************************/

	protected void checkIdDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View DialogView = null;
		DialogView = inflater.inflate(R.layout.checkid_dialog, null);
		AlertDialog.Builder builder = new Builder(AboutActivity.this);
		builder.setView(DialogView);

		etCheck = (EditText) DialogView.findViewById(R.id.editText_checkid);

		btnCheck = (Button) DialogView.findViewById(R.id.button_check);

		btnCheck.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (etCheck.getText().toString().isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please fill in the ID", Toast.LENGTH_SHORT).show();
					return;
				}
				stopService();
				timeHandler.postDelayed(runnable1, timeInterval);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", etCheck.getText()
						.toString()));
				myHttpThreadPost = new HttpThreadPost(SubmitActivity.URL
						+ "getResult.php", params, HttpThreadPost.CHECKID,
						handler);
				myHttpThreadPost.start();
				pDialog.setMessage("Check Result...");
				pDialog.show();
			}
		});

		btnCheck.setOnTouchListener(new Button.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.drawable.deep);
				} else {
					v.setBackgroundResource(R.drawable.shallow);
				}
				return false;
			}
		});

		alertDialog = builder.create();
		alertDialog.show();
		WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
		lp.width = (int) (SCREEN_WIDTH * 0.8);// ������
		lp.height = (int) (SCREEN_HEIGHT * 0.8);// ����߶�
		alertDialog.getWindow().setAttributes(lp);
	}

	/************************** ��ȡ���״̬ *************************/

	public boolean getStatus(String json) {
		String strStatus = "";
		try {
			strStatus = new JSONObject(json).getString("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (strStatus.equals("0")) {
			return true;
		} else
			return false;
	}

	/************************** ��ȡ������Ϣ *************************/

	public String getErrorMessage(String json) {
		String errorMessage = null;
		try {
			errorMessage = new JSONObject(json).getString("message");
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return errorMessage;
	}

	/************************** ���������Ϣ *************************/

	public void saveMessage(String json) {
		String saveSPECIE = null;
		String saveGENE = null;
		String saveLOCATION = null;
		String saveREGION = null;
		try {
			JSONObject jsonObj = new JSONObject(json);
			saveSPECIE = jsonObj.getString("specie");
			saveGENE = jsonObj.getString("gene");
			saveLOCATION = jsonObj.getString("location");
			saveREGION = jsonObj.getString("region");
		} catch (JSONException e) {

			e.printStackTrace();
		}
		// ����json��һЩ��Ϣ
		Context ctx = AboutActivity.this;
		SharedPreferences info = ctx.getSharedPreferences("BUFF", MODE_PRIVATE);
		Editor editor = info.edit();
		editor.putString("SPECIE", saveSPECIE);
		editor.putString("GENE", saveGENE);
		editor.putString("LOCATION", saveLOCATION);
		editor.putString("REGION", saveREGION);
		editor.putString("JSON", json);
		editor.commit();
	}

	/************************** debug��ʾ�� *************************/

	protected void debugDialog(String message) {
		AlertDialog.Builder builder = new Builder(AboutActivity.this);
		builder.setMessage(message);
		builder.setTitle("ErrorInfo");
		builder.setCancelable(true);
		builder.create().show();

	}

	// Web��ͼ
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	protected void onResume() {
		/**
		 * ����Ϊ����
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onResume();
	}

	// �˳�ʱ������dialog
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != pDialog) {
			pDialog.dismiss();
		}
		// stopService();
		// //�����߳�ͨ��
		// myHttpThread.interrupt();
	}

	// ��������
	private void startService() {
		Intent intent = new Intent(this, BackGroundService.class);
		startService(intent);
	}

	// �رշ���
	private void stopService() {
		Intent intent = new Intent(this, BackGroundService.class);
		stopService(intent);
	}
}
