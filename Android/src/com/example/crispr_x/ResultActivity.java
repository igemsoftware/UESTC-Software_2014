package com.example.crispr_x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ResultActivity extends Activity {

	private TextView tvTargetGenomeT, tvLocusTagT, tvPositionT, tvPositionF, tvGCT,
			tvRNAF, tvScore;
	private Button btnMore;
	private ListView lvFather, lvChild;
	private ImageView ivMap;
	private ImageView imImg;

	private String strPosition = null;// Position�ִ�
	private String strLocusTag = null;// LocusTag�ִ�
	private String strTargetGenome = null;// TargetGenome�ִ�
	private String strRegion = null;// Region�ִ�
	private String strGC = null;// GC�ִ�
	private String jsonString;
	private int SCREEN_WIDTH, SCREEN_HEIGHT; // ��Ļ�߿�
	private String jGrna, jPosition, jTotalScore, jSspe, jSeff, jStrand, fGrna,
			fPosition, fTotalScore; // �����ؼ���
	private long timeInterval = 1 * 60 * 1000; // ��ʱʱ��

	ArrayList<Integer> ListEndpoint = new ArrayList<Integer>();
	ArrayList<String> ListDescription = new ArrayList<String>();

	SimpleAdapter fatherListAdapter, childListAdapter;
	Bitmap Map;
	View oldView;
	ProgressDialog pDialog;
	AlertDialog alertDialog;
	Handler timeHandler;
	Runnable runnable1;
	HttpThreadGet myHttpThreadGet;
	HttpThreadPost myHttpThreadPost;
	static Handler handler;

	ResultParser resultParser = new ResultParser();
	private ListAdapter listItemAdapter2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// ���ر���
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// ����ȫ��

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		SCREEN_WIDTH = dm.widthPixels;
		SCREEN_HEIGHT = dm.heightPixels;
		setContentView(R.layout.activity_result);

		tvTargetGenomeT = (TextView) this
				.findViewById(R.id.textView_targetgenomeTR);
		tvLocusTagT = (TextView) this
				.findViewById(R.id.textView_textView_locustagRT);
		tvPositionT = (TextView) this.findViewById(R.id.textView_positionRT);
		tvGCT = (TextView) this.findViewById(R.id.textView_GCRT);
		lvFather = (ListView) this.findViewById(R.id.listView_father);
		lvChild = (ListView) this.findViewById(R.id.listView_child);
		tvPositionF = (TextView) this.findViewById(R.id.textView_position);
		tvRNAF = (TextView) this.findViewById(R.id.textView_grna);
		tvScore = (TextView) this.findViewById(R.id.textView_score);
		ivMap = (ImageView) this.findViewById(R.id.imageView_map);
		btnMore = (Button) this.findViewById(R.id.button_more);

		Context ctx = ResultActivity.this;
		SharedPreferences info = ctx.getSharedPreferences("BUFF", MODE_PRIVATE);
		strPosition = info.getString("LOCATION", ""); // ����
		strLocusTag = info.getString("GENE", "");
		strTargetGenome = info.getString("SPECIE", "");
		strRegion = info.getString("REGION", "");
		strGC = info.getString("GC", "");
		jsonString = info.getString("JSON", "");

		tvTargetGenomeT.setText(strTargetGenome);
		tvLocusTagT.setText(strLocusTag);
		tvPositionT.setText(strPosition);
		tvGCT.setText(strGC);

		parseFatherListJson(jsonString);
		parseMapJson(jsonString);

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
				byte[] imgBuff = null;
				switch (msg.what) {
				case HttpThreadGet.MORE_INFO:
					pDialog.dismiss();
					timeHandler.removeCallbacks(runnable1);
					System.out.println("����MOREINFO");
					result = (String) msg.obj;
					moreInfoDialog(result);
					break;
				case HttpThreadGet.RNAFOLG_IMG:
					pDialog.dismiss();
					timeHandler.removeCallbacks(runnable1);
					System.out.println("����RNAFOLG_IMG");
					imgBuff = (byte[]) msg.obj;
					try {
						Bitmap bm = BitmapFactory.decodeByteArray(imgBuff, 0,
								imgBuff.length);
						imImg.setImageBitmap(bm);
					} catch (Exception e) {
						Log.d("img", e.toString());
						Toast.makeText(ResultActivity.this, "Get img fail...",
								1).show();
					}
					break;
				default:
					break;
				}
			}
		};

		/*********************** FatherList����¼� *********************/

		lvFather.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 != 0) {
					parseChildListJson(jsonString, arg2 - 1);
					getDetail(jsonString, arg2 - 1);
					tvPositionF.setText(fPosition);
					tvRNAF.setText(fGrna);
					tvScore.setText(fTotalScore);
					CreatMap map = new CreatMap();
					Map = map.canvasParse(strPosition, jPosition, jStrand,
							SCREEN_WIDTH);
					ivMap.setImageBitmap(Map);
				}
			}
		});

		btnMore.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timeHandler.postDelayed(runnable1, timeInterval);
				MoreInfoGet(SubmitActivity.URL + "getMoreInfo.php", "");
			}
		});
		btnMore.setOnTouchListener(new Button.OnTouchListener() {
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
	}

	/*********************** ���η����ύ����GET **********************/

	private void MoreInfoGet(String URL, String strSequence) {
		System.out.println("����MoreInfoGet");
		// ��Ӳ���
		String strRequest = "?sequence=" + jGrna;
		URL = URL + strRequest;
		myHttpThreadGet = new HttpThreadGet(URL, HttpThreadGet.MORE_INFO,
				handler);
		myHttpThreadGet.start();
		pDialog.setMessage("Program is running...");
		pDialog.show();
	}

	/************************** ע��Ի��� *************************/

	protected void moreInfoDialog(String json) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View DialogView = null;
		DialogView = inflater.inflate(R.layout.more_info_dialog, null);
		AlertDialog.Builder builder = new Builder(ResultActivity.this);
		builder.setView(DialogView);

		TextView tvSequence = (TextView) DialogView
				.findViewById(R.id.textView_sequence);
		TextView tvRatio = (TextView) DialogView
				.findViewById(R.id.textView_GC_ratio);
		TextView tvRes = (TextView) DialogView
				.findViewById(R.id.textView_RNAfold_res);
		imImg = (ImageView) DialogView.findViewById(R.id.imageView_RNAfold_img);
		ListView lvEnzyme = (ListView) DialogView
				.findViewById(R.id.listView_enzyme);

		String strSeq, strRes = null, urlImg = null, strGCratio = null;
		strSeq = "sequence : " + jGrna;
		Log.d("map", "parseMoreInfoJson");
		try {
			JSONObject jsonObj = new JSONObject(json);
			urlImg = jsonObj.getString("RNAfold-img");
			strRes = jsonObj.getString("RNAfold-res");
			// strGCratio = jsonObj.getString("GC-ratio");
			strGCratio = String.format("%.2f", jsonObj.getDouble("GC-ratio"));
			strGCratio = "GC-ratio : " + strGCratio;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			System.out.println("Jsons parse error : MoreInfo");
			e1.printStackTrace();
		}
		// ���ò���
		tvSequence.setText(strSeq);
		tvRatio.setText(strGCratio);
		tvRes.setText(strRes);
		// Bitmap bitmap = null;
		// try
		// {
		// URL url = new URL(SubmitActivity.URL + "RNAFold/"+urlImg);
		// Log.e("img", SubmitActivity.URL + "RNAFold/"+urlImg);
		// bitmap = BitmapFactory.decodeStream(url.openStream());
		// imImg.setImageBitmap(bitmap);
		// } catch (Exception e)
		// {
		// // TODO Auto-generated catch block
		// System.out.println("BitmapFactory error : BitmapFactory");
		// e.printStackTrace();
		// }
		Log.e("img", SubmitActivity.URL + "RNAFold/" + urlImg);
		myHttpThreadGet = new HttpThreadGet(SubmitActivity.URL + "RNAFold/"
				+ urlImg, HttpThreadGet.RNAFOLG_IMG, handler);
		myHttpThreadGet.start();
		pDialog.setMessage("Img is Creating...");
		pDialog.show();

		/************** ����б� ***************/

		CreatList historyList = new CreatList();
		List<Map<String, Object>> list2 = historyList.creatEnzyme(json);
		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		listItemAdapter2 = new SimpleAdapter(this, list2, R.layout.enzyme_list,
				new String[] { "enzyme_name", "strand", "matched_seq" },
				new int[] { R.id.textView_enzyme_name, R.id.textView_strand,
						R.id.textView_matched_seq });

		// ��Ӳ�����ʾ
		lvEnzyme.setAdapter(listItemAdapter2);

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
					}
				});
		alertDialog = builder.create();
		alertDialog.show();
		WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
		lp.width = (int) (SCREEN_WIDTH * 0.98);// ������
		lp.height = (int) (SCREEN_HEIGHT * 0.9);// ����߶�
		alertDialog.getWindow().setAttributes(lp);
	}

	/************************** debug��ʾ�� *************************/

	protected void debugDialog(String message) {
		AlertDialog.Builder builder = new Builder(ResultActivity.this);
		builder.setMessage(message);
		builder.setTitle("ErrorInfo");
		builder.setCancelable(true);
		builder.create().show();

	}

	/*********************** ����map���ݲ������б� **********************/

	public void parseMapJson(String JSON) {
		Log.d("map", "parseMapJson");
		try {
			// json����Ϣ��ȡ
			JSONArray jsonObjs = new JSONObject(JSON).getJSONArray("region");

			// json������Ϣ��ȡ
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));

				ListEndpoint
						.add(Integer.parseInt(jsonObj.getString("endpoint")));
				ListDescription.add(jsonObj.getString("description"));
			}
		} catch (JSONException e) {
			System.out.println("Jsons parse error : map");
			e.printStackTrace();
		}
		new CreatMap(ListEndpoint, ListDescription);
	}

	/*********************** ����FatherList���ݲ������б� **********************/

	public void parseFatherListJson(String JSON) {

		List<Map<String, Object>> fatherList = resultParser.fatherList(JSON);

		// �����������ĸ����б�Ͷ�̬�����Ӧ��Ԫ��
		fatherListAdapter = new SimpleAdapter(this, fatherList,
				R.layout.result_list_father, new String[] { "key", "grna",
						"total_score" }, new int[] { R.id.textView_key,
						R.id.textView_grna, R.id.textView_total_score });

		// ��Ӳ�����ʾ
		lvFather.setAdapter(fatherListAdapter);
	}

	/*********************** ����ChildList���ݲ������б� **********************/

	public void parseChildListJson(String JSON, int itemID) {

		List<Map<String, Object>> childList = resultParser.childList(JSON,
				itemID);

		// �������������Ӽ��б�Ͷ�̬�����Ӧ��Ԫ��
		childListAdapter = new SimpleAdapter(this, childList,
				R.layout.result_list_child, new String[] { "sequence", "score",
						"mms", "strand", "position", "region" }, new int[] {
						R.id.textView_sequence, R.id.textView_score,
						R.id.textView_mms, R.id.textView_strand,
						R.id.textView_position, R.id.textView_region });

		// ��Ӳ�����ʾ
		lvChild.setAdapter(childListAdapter);

	}

	/*********************** ����FatherList������ϸ��Ϣ **********************/

	private void getDetail(String json, int itemID) {
		try {
			// json����Ϣ��ȡ
			JSONArray jsonObjs = new JSONObject(json).getJSONArray("result");

			// json������Ϣ��ȡ
			JSONObject jsonObj = ((JSONObject) jsonObjs.opt(itemID));
			jPosition = jsonObj.getString("position"); // λ��
			jStrand = jsonObj.getString("strand"); // ����
			jGrna = jsonObj.getString("grna"); // GRNA
			jTotalScore = jsonObj.getString("total_score"); // ������
			jSspe = jsonObj.getString("Sspe"); // Sspe��
			jSeff = jsonObj.getString("Seff"); // Seff��
			fTotalScore = "TotalScore:" + jTotalScore + "  Specificity:"
					+ jSspe + "  Active:" + jSeff;
			fPosition = "Position:" + jStrand + jPosition;
			fGrna = "gRNA:" + jGrna;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	}

}
