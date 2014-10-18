package com.lzmy.hxtest.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MsgAdapter extends BaseAdapter{
	
	private Context mContext = null; 
	private ArrayList<HashMap<String, Object>> data = null;
	
	public MsgAdapter(Context context, ArrayList<HashMap<String, Object>> data){
		this.mContext = context;
		this.data = data; 
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private static class ViewHolder{
		
	}

}
