package com.example.crispr_x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
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

public class WebActivity extends Activity {  
    private WebView webview;
    private ListView lvMenu;
	private EditText etCheck; // check�༭��
	private Button btnCheck; // check��ť
	
	private long timeInterval = 60 * 1000;
	private int SCREEN_WIDTH, SCREEN_HEIGHT; // ��Ļ�߿�
	private boolean isLogin;
	SimpleAdapter listItemAdapter,listItemAdapter2;
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
		
        setContentView(R.layout.activity_wiki);  
        webview = (WebView) findViewById(R.id.webView_wiki);
        lvMenu = (ListView) findViewById(R.id.listView_menu);


        //����WebView���ԣ��ܹ�ִ��Javascript�ű�  
        webview.getSettings().setJavaScriptEnabled(true);  
        //������Ҫ��ʾ����ҳ  
        webview.loadUrl("http://2014.igem.org/Team:UESTC-Software");  
        //����Web��ͼ  
        webview.setWebViewClient(new HelloWebViewClient ());  
        
        isLogin = new BackGroundService().getIsLogin();
        
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
				Toast.makeText(getApplicationContext(), "Time out", Toast.LENGTH_SHORT)
						.show();
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
					SubmitActivity sa = new SubmitActivity();
					if (sa.getStatus(result)) {
						sa.saveMessage(result);
						Intent intent = new Intent(WebActivity.this,ResultActivity.class); // ����Activity
						startActivity(intent);
					} else {
						sa.debugDialog(result);
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
					intent = new Intent(WebActivity.this,SubmitActivity.class); // ����Activity
					startActivity(intent);
					finish();
					break;
				case 1:
					checkIdDialog();
					break;
				case 2:
					if(isLogin){
						intent = new Intent(WebActivity.this,HistoryActivity.class); // ����Activity
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), "Please login !",
								Toast.LENGTH_SHORT).show();
					}
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				}
			}
		});
		
    }  
      
    @Override 
    //���û���  
    //����Activity���onKeyDown(int keyCoder,KeyEvent event)����  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {  
            webview.goBack(); //goBack()��ʾ����WebView����һҳ��  
            return true;  
        }  
        return false;  
    }
    
  //Web��ͼ  
    private class HelloWebViewClient extends WebViewClient {  
        @Override 
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            view.loadUrl(url);  
            return true;  
        }  
    }
    
    /************************** ��ѯ�Ի��� *************************/

	protected void checkIdDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View DialogView = null;
		DialogView = inflater.inflate(R.layout.checkid_dialog, null);
		AlertDialog.Builder builder = new Builder(WebActivity.this);
		builder.setView(DialogView);

		etCheck = (EditText) DialogView
				.findViewById(R.id.editText_checkid);
		
		btnCheck = (Button) DialogView
				.findViewById(R.id.button_check);

		btnCheck.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (etCheck.getText().toString().isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please fill in the ID",
							Toast.LENGTH_SHORT).show();
					return;
					}
				stopService();
				timeHandler.postDelayed(runnable1, timeInterval);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", etCheck.getText().toString()));
				myHttpThreadPost = new HttpThreadPost(SubmitActivity.URL + "getResult.php",
						params, HttpThreadPost.CHECKID, handler);
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
				//stopService();
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
