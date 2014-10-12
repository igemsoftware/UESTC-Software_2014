package com.example.crispr_x;

import java.util.List;
import org.apache.http.NameValuePair;
import android.os.Handler;
import android.os.Message;

public class HttpThreadGet extends Thread {
	public static final int MAIN_SUBMIT = 10; // �������ύ
	public static final int MORE_INFO = 11; //���η���
	public static final int RNAFOLG_IMG = 12; //���η���ͼƬ

	List<NameValuePair> params;
	String httpUrl;
	int type;
	Handler handler;

	HttpThreadGet(String url, int type, Handler handler) {
		this.type = type;
		this.httpUrl = url;
		this.handler = handler;
	}

	public void run() {
		HttpRunner jsonParser = new HttpRunner();
		String json = null ;
		byte[] imgBuff = null;
		Message msg = new Message();
		switch (type) {
		case MAIN_SUBMIT: // �������ύ
			json = jsonParser.makeMainHttpGET(httpUrl);
			msg.what = MAIN_SUBMIT;
			msg.obj = json;
			break;
		case MORE_INFO: // ���η���
			json = jsonParser.makeHttpGET(httpUrl);
			msg.what = MORE_INFO;
			msg.obj = json;
			break;
		case RNAFOLG_IMG: // ���η���ͼƬ
			imgBuff = jsonParser.makeImgHttpGET(httpUrl);
			msg.what = RNAFOLG_IMG;
			msg.obj = imgBuff;
			break;
		default:
			break;
		}
		handler.sendMessage(msg);
	}
}
