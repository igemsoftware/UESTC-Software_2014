package com.example.crispr_x;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Handler;
import android.os.Message;

public class HttpThreadPost extends Thread {
	public static final int LOGIN = 21; // �û���½
	public static final int LOGON = 22; // �û�ע��
	public static final int LOGOUT = 23; // �˳���¼
	public static final int HISTORY = 24; // ��ȡ��ʷ
	public static final int CHECKID = 25; // ��ѯID
	public static final int CHECKFILE = 26; // ��ѯ�ļ�
	public static final int CHESPECIES = 27; // ��ѯ����

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
		case CHECKFILE: // ��ѯ�ļ�
			msg.what = CHECKFILE;
			break;
		case CHESPECIES: // ��ѯ�ļ�
			msg.what = CHESPECIES;
			break;
		default:
			break;
		}
		msg.obj = json;
		handler.sendMessage(msg);
	}
}
