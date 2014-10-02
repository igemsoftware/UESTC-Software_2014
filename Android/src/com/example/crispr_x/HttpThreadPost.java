package com.example.crispr_x;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Handler;
import android.os.Message;

public class HttpThreadPost extends Thread {
	public static final int LOGIN = 1; // �û���½
	public static final int LOGON = 2; // �û�ע��
	public static final int LOGOUT = 3; // �˳���¼
	public static final int HISTORY = 4; // ��ȡ��ʷ
	public static final int CHECKID = 5; // ��ѯID

	List<NameValuePair> params;
	String httpUrl;
	int type;
	Handler handler;

	HttpThreadPost(String url, List<NameValuePair> params, int type, Handler handler) {
		
		this.params = new ArrayList<NameValuePair>();
		this.params.addAll(params);
		this.type = type;
		this.httpUrl = url;
		this.handler = handler;
	}

	public void run() {
		HttpRunner jsonParser = new HttpRunner();
		String json = jsonParser.makeHttpPOST(httpUrl, params);
		Message msg = new Message();
		switch (type) {
		case LOGIN: // �û���½
			msg.what = LOGIN;
			break;
		case LOGON: // �û�ע��
			msg.what = LOGON;
			break;
		case LOGOUT: // �˳���¼
			msg.what = LOGOUT;
			break;
		case HISTORY: // ��ȡ��ʷ
			msg.what = HISTORY;
			break;
		case CHECKID: // ��ѯID
			msg.what = CHECKID;
			break;
		default:
			break;
		}
		msg.obj = json;
		handler.sendMessage(msg);
	}
}
