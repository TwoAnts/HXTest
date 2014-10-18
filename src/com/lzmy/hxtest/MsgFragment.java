package com.lzmy.hxtest;

import com.easemob.chat.EMMessage;
import com.lzmy.hxtest.MainActivity.MainMsgListener;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MsgFragment extends Fragment implements MainMsgListener{

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_msg, null);
		TextView head_title = (TextView)view.findViewById(R.id.head_title);
		head_title.setText("消息");
		return view;
	}

	@Override
	public void OnReceive(EMMessage msg) {
		// TODO Auto-generated method stub
		
		
	}
	
	@Override
	public void OnOfflineReceive(String[] users){
		
	}
	
	

}
