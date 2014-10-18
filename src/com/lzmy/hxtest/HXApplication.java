package com.lzmy.hxtest;

import com.easemob.chat.EMChat;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class HXApplication extends Application{

	public static Context appContext;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		appContext = this;
		
		// 初始化环信聊天SDK
		Log.d("EMChat Demo", "initialize EMChat SDK");
		EMChat.getInstance().setDebugMode(true);
		EMChat.getInstance().init(appContext);

	}

	
	
}
