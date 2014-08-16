package com.example.crispr_x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultParser {
	
	String jKey, jGrna, jPosition, jTotalScore, jOfftarget;	//�����ؼ���
	String joSequence, joScore, joMms, joStrand, joPosition, joRegion;	//�Ӽ��ؼ���

	//�����б����
	public List<Map<String, Object>> fatherList(String strResult) {
		// ���ɶ�̬���飬��������
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map; // ���map
		map = new HashMap<String, Object>();
		map.put("key", "Count");
		map.put("grna", "gRNA");
		map.put("total_score", "TotalScore");
		//listǶ��list
		map.put("offtarget", "Offtarget");
		
		list.add(map);

		try {
			//json����Ϣ��ȡ
			JSONArray jsonObjs = new JSONObject(strResult)
					.getJSONArray("result");

			//json������Ϣ��ȡ
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
				jKey = jsonObj.getString("key"); // ����
				jGrna = jsonObj.getString("grna"); // GRNA
				jTotalScore = jsonObj.getString("total_score"); // ������

				map = new HashMap<String, Object>();
				map.put("key", jKey);
				map.put("grna", jGrna);
				map.put("total_score", jTotalScore);
				
				list.add(map);
			}

		} catch (JSONException e) {
			System.out.println("Jsons parse error : fatherList");
			e.printStackTrace();
		}
		return list;
	}
	
	//�Ӽ��б����
	public List<Map<String, Object>> childList(String strResult ,int itemID) {
		// ���ɶ�̬���飬��������
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map; // ���map
		map = new HashMap<String, Object>();
		map.put("sequence", "Sequence");
		map.put("score", "Score");
		map.put("mms", "Mms");
		map.put("str", "Strand");
		map.put("position", "Position");
		map.put("region", "Region");
		
		
		list.add(map);

		try {
			//json����Ϣ��ȡ
			JSONArray jsonObjs = new JSONObject(strResult)
					.getJSONArray("result");

			//json������Ϣ��ȡ
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(itemID));
				jKey = jsonObj.getString("key"); // ����
				jGrna = jsonObj.getString("grna"); // GRNA
				jTotalScore = jsonObj.getString("total_score"); // ������
				jPosition = jsonObj.getString("position"); // λ��
				JSONArray jsonObjsc = jsonObj.getJSONArray("offtarget");	// �Ѱ���Ϣ
				
			//json�Ӽ���Ϣ��ȡ
			for (int i = 0; i < jsonObjsc.length(); i++) {
				JSONObject jsonObjc = ((JSONObject) jsonObjsc.opt(i));
				joSequence = jsonObjc.getString("osequence"); // 
				joScore = jsonObjc.getString("oscore"); // 
				joMms = jsonObjc.getString("omms"); // 
				joStrand = jsonObjc.getString("ostrand"); // 
				joPosition = jsonObjc.getString("oposition"); // 
				joRegion = jsonObjc.getString("oregion"); // 

				map = new HashMap<String, Object>();
				map.put("sequence", joSequence);
				map.put("score", joScore);
				map.put("mms", joMms);
				map.put("strand", joStrand);
				map.put("position", joPosition);
				map.put("region", joRegion);
				
				list.add(map);
			}

		} catch (JSONException e) {
			System.out.println("Jsons parse error : childList");
			e.printStackTrace();
		}
		return list;
	}
}
