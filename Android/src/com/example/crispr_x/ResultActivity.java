package com.example.crispr_x;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ResultActivity extends Activity {
	
	private TextView tvTargetGenomeT, tvLocusTagT, tvPositionT, tvPositionF, tvRNAF, tvScore;
	private ListView lvFather, lvChild;
	private ImageView ivMap;

	private String strPosition = null;// Position�ִ�
	private String strLocusTag = null;// LocusTag�ִ�
	private String strTargetGenome = null;// TargetGenome�ִ�
	private String jsonString;
	private int SCREEN_WIDTH, SCREEN_HEIGHT; // ��Ļ�߿�
	private String jGrna, jPosition, jTotalScore, jSspe, jSeff, jStrand,fGrna, fPosition, fTotalScore;	//�����ؼ���
	
	SimpleAdapter fatherListAdapter, childListAdapter;
	Bitmap Map ;
	View oldView;
	
	ResultParser resultParser = new ResultParser();
	
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
		
		tvTargetGenomeT = (TextView) this.findViewById(R.id.textView_targetgenomeTR);
		tvLocusTagT = (TextView) this.findViewById(R.id.textView_textView_locustagRT);
		tvPositionT = (TextView) this.findViewById(R.id.textView_positionRT);
		lvFather = (ListView) this.findViewById(R.id.listView_father);
		lvChild = (ListView) this.findViewById(R.id.listView_child);
		tvPositionF = (TextView) this.findViewById(R.id.textView_position);
		tvRNAF = (TextView) this.findViewById(R.id.textView_grna);
		tvScore = (TextView) this.findViewById(R.id.textView_score);
		ivMap = (ImageView) this.findViewById(R.id.imageView_map);
		
		Context ctx = ResultActivity.this;
		SharedPreferences info = ctx.getSharedPreferences("BUFF", MODE_PRIVATE);
		strPosition = info.getString("LOCATION", ""); // ����
		strLocusTag = info.getString("GENE", "");
		strTargetGenome = info.getString("SPECIE", "");
		jsonString = info.getString("JSON", "");
		
		tvTargetGenomeT.setText(strTargetGenome);
		tvLocusTagT.setText(strLocusTag);
		tvPositionT.setText(strPosition);
		
		parseFatherListJson(jsonString);
		
		/*********************** FatherList����¼� *********************/

		lvFather.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg2!=0) {
					parseChildListJson(jsonString,arg2-1);
					getDetail(jsonString,arg2-1);
					tvPositionF.setText(fPosition);
					tvRNAF.setText(fGrna);
					tvScore.setText(fTotalScore);
					CreatMap map = new CreatMap();
					Map =  map.canvasParse(strPosition, jPosition, jStrand, SCREEN_WIDTH);
					ivMap.setImageBitmap(Map);
				}
			}
		});
		
	}
	
	/*********************** ����FatherList���ݲ������б� **********************/

	public void parseFatherListJson(String JSON) {

		List<Map<String, Object>> fatherList = resultParser.fatherList(JSON);

		// �����������ĸ����б�Ͷ�̬�����Ӧ��Ԫ��
		fatherListAdapter = new SimpleAdapter(this, fatherList, R.layout.result_list_father,
				new String[] { "key", "grna", "total_score" },
				new int[] { R.id.textView_key, R.id.textView_grna,
						R.id.textView_total_score});

		// ��Ӳ�����ʾ
		lvFather.setAdapter(fatherListAdapter);
	}
	
	/*********************** ����ChildList���ݲ������б� **********************/

	public void parseChildListJson(String JSON, int itemID) {

		List<Map<String, Object>> childList = resultParser.childList(JSON, itemID);

		// �������������Ӽ��б�Ͷ�̬�����Ӧ��Ԫ��
		childListAdapter = new SimpleAdapter(this, childList, R.layout.result_list_child,
				new String[] { "sequence", "score", "mms", "strand", "position", "region" },
				new int[] { R.id.textView_sequence, R.id.textView_score,
						R.id.textView_mms,
						R.id.textView_strand,
						R.id.textView_position,
						R.id.textView_region});

		// ��Ӳ�����ʾ
		lvChild.setAdapter(childListAdapter);
		
	}
	
	/*********************** ����FatherList������ϸ��Ϣ **********************/
	
	private void getDetail(String json, int itemID){
		String saveMessage = null;
		try {
			//json����Ϣ��ȡ
			JSONArray jsonObjs = new JSONObject(json).getJSONArray("result");

			//json������Ϣ��ȡ
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(itemID));
				jPosition = jsonObj.getString("position"); // λ��
				jStrand = jsonObj.getString("strand"); // ����
				jGrna = jsonObj.getString("grna"); // GRNA
				jTotalScore = jsonObj.getString("total_score"); // ������
				jSspe = jsonObj.getString("Sspe"); // Sspe��
				jSeff = jsonObj.getString("Seff"); // Seff��
				fTotalScore = "TotalScore:" + jTotalScore + "  Specificity:" + jSspe + "  Active:" + jSeff;
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
