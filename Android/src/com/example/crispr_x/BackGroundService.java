package com.example.crispr_x;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class BackGroundService extends Service {

	private static final String TAG = "BackGroundService";
	private IBinder binder = new BackGroundService.LocalBinder();
	private static String token;
	private static String user;
	private static boolean isLogin;
	private String taskID;
	private int[] stauts0 = new int[10];
	private int[] stauts1 = new int[10];
	private int maxCount = 20;
	private int plusCount = 1;

	static Handler handler;
	HttpThreadPost myHttpThreadPost;
	NotificationManager mNotificationManager;
	NotificationCompat.Builder mBuilder;

	BackGroundService(String muser, String mytoken, boolean islogin) {
		BackGroundService.user = muser;
		BackGroundService.token = mytoken;
		BackGroundService.isLogin = islogin;
	}

	public String getUserName() {
		return user;
	}

	public boolean getIsLogin() {
		return isLogin;
	}

	public String getToken() {
		return token;
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			if (isWifiConnected(getApplicationContext())) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("token", token));
				// System.out.println(token);
				// Log.d("token", token);
				if(isLogin){
					myHttpThreadPost = new HttpThreadPost(SubmitActivity.URL
							+ "getHistory.php", params, HttpThreadPost.HISTORY,
							handler);
					myHttpThreadPost.start();
				}
//			}
			if (plusCount < maxCount) {
				handler.postDelayed(this, (30 * 1000) * plusCount);
				plusCount++;
			} else {
				handler.postDelayed(this, 10 * 60 * 1000);
			}
		}
	};

	public BackGroundService() {
		super();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();

		plusCount = 0;
		handler = new Handler() {
			public void handleMessage(Message msg) {
				String result = null;
				switch (msg.what) {
				case HttpThreadPost.HISTORY:
					System.out.println("����hanlder");
					result = (String) msg.obj;
					// System.out.println(result);
					new CreatList(result);
					saveAndCheckStauts(result);
					break;
				default:
					break;
				}
			}
		};

	}

	private void creatNotification() {

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);

		Intent intent = new Intent(getApplicationContext(),
				HistoryActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent, 0);

		mBuilder.setContentTitle("Your task is done!")
				// ����֪ͨ������
				.setContentText(
						"Your task is done! \nPlease check and get the result.")
				.setContentIntent(pendingIntent) // ����֪ͨ�������ͼ
				.setTicker("Your task is done!") // ֪ͨ�״γ�����֪ͨ��������������Ч����
				.setWhen(System.currentTimeMillis())// ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ��һ����ϵͳ��ȡ����ʱ��
				.setPriority(Notification.PRIORITY_DEFAULT) // ���ø�֪ͨ���ȼ�
				.setDefaults(Notification.DEFAULT_VIBRATE)// ��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ��������
				.setSmallIcon(R.drawable.ic_launcher);// ����֪ͨСICON

	}

	private void saveAndCheckStauts(String listJson) {
		try {
			JSONArray jsonObjs = new JSONArray(listJson);
			for (int i = 0; i < jsonObjs.length(); i++) {
				// System.out.println(stauts0[i]);
				stauts1[i] = stauts0[i]; // ��¼״̬
				String statusNum = ((JSONObject) jsonObjs.opt(i))
						.getString("status");
				stauts0[i] = Integer.parseInt(statusNum); // ��ȡ״̬
				if ((stauts0[i] - stauts1[i]) != 0 && stauts0[i] == 0) { // ״̬�仯
					taskID = ((JSONObject) jsonObjs.opt(i))
							.getString("request_id");
					if (!taskID.equals(null)) {
						mNotificationManager.notify(i, mBuilder.build());
					}
				}
			}
		} catch (JSONException e) {
			System.out.println("Jsons parse error : list");
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		if (isLogin) {
			handler.postDelayed(runnable, 10);
			creatNotification();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		handler.removeCallbacks(runnable);
		isLogin = false;
		super.onDestroy();
	}

	// ����������̳�Binder
	public class LocalBinder extends Binder {
		// ���ر��ط���
		BackGroundService getService() {
			return BackGroundService.this;
		}
	}

	// �Ƿ�����WIFI
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}

		return false;
	}
}
