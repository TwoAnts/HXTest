package com.lzmy.hxtest;

import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.NetUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	
	private Fragment[] fragments = new Fragment[3];
	private RadioGroup radioGroup;
	private RadioButton[] radioBtn = new RadioButton[3];
	private int currentRabtnSelect;
	private NewMessageBroadcastReceiver msgReceiver;
	
	private MainMsgListener mMainMsgListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		currentRabtnSelect = 1;
		initView();
		setMainMsgListener((MainMsgListener)fragments[1]);
		
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
				.getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		// 注册一个离线消息的BroadcastReceiver
		IntentFilter offlineMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
				.getOfflineMessageBroadcastAction());
		registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);
		
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
		
	}
	
	
	private void initView(){
		radioGroup = (RadioGroup)findViewById(R.id.main_bottom);
		radioBtn[0] = (RadioButton)findViewById(R.id.rabtn_news);
		radioBtn[1] = (RadioButton)findViewById(R.id.rabtn_msg);
		radioBtn[2] = (RadioButton)findViewById(R.id.rabtn_my);
		fragments[0] = new NewsFragment();
		fragments[1] = new MsgFragment();
		fragments[2] = new MyFragment();
		FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
		trx.add(R.id.fragment_container, fragments[currentRabtnSelect]);
		trx.show(fragments[currentRabtnSelect]).commit();
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int selected;
				switch (checkedId) {
				case R.id.rabtn_news:
					selected = 0;
					break;
				case R.id.rabtn_msg:
					selected = 1;
					break;
				case R.id.rabtn_my:
					selected = 2;
					break;
				default:
					selected = -1;
					break;
				}
				if (currentRabtnSelect != selected) {
					FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
					trx.hide(fragments[currentRabtnSelect]);
					if (!fragments[selected].isAdded()) {
						trx.add(R.id.fragment_container, fragments[selected]);
					}
					trx.show(fragments[selected]).commit();
				}
				currentRabtnSelect = selected;
			}
		});
		
	}
	
	
	/**
	 * 新消息广播接收者
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看
			
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			 EMMessage message =
			 EMChatManager.getInstance().getMessage(msgId);
			 mMainMsgListener.OnReceive(message);

//				if (currentTabIndex == 0) {
//					// 当前页面如果为聊天历史页面，刷新此页面
//					if (chatHistoryFragment != null) {
//						chatHistoryFragment.refresh();
//					}
//				}
			// 注销广播，否则在ChatActivity中会收到这个广播
			abortBroadcast();
		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
			abortBroadcast();
		}
	};

	/**
	 * 离线消息BroadcastReceiver
	 * sdk 登录后，服务器会推送离线消息到client，这个receiver，是通知UI 有哪些人发来了离线消息
	 * UI 可以做相应的操作，比如下载用户信息
	 */
	private BroadcastReceiver offlineMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String[] users = intent.getStringArrayExtra("fromuser");
			//String[] groups = intent.getStringArrayExtra("fromgroup");
			mMainMsgListener.OnOfflineReceive(users);
			if (users != null) {
				for (String user : users) {
					System.out.println("收到user离线消息：" + user);
				}
			}
//			if (groups != null) {
//				for (String group : groups) {
//					System.out.println("收到group离线消息：" + group);
//				}
//			}
			abortBroadcast();
		}
	};
	
	
	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements ConnectionListener {

		@Override
		public void onConnected() {
		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				// 显示帐号在其他设备登陆dialog
				Toast.makeText(getApplicationContext(), "设备在其他地方登陆！", Toast.LENGTH_SHORT).show();
			} else {
				if(NetUtils.hasNetwork(MainActivity.this))
					Toast.makeText(getApplicationContext(), "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
					//chatHistoryFragment.errorText.setText("连接不到聊天服务器");
				else
					Toast.makeText(getApplicationContext(), "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
					//chatHistoryFragment.errorText.setText("当前网络不可用，请检查网络设置");
					
			}
		}

		@Override
		public void onReConnected() {
		}

		@Override
		public void onReConnecting() {
		}

		@Override
		public void onConnecting(String progress) {
		}

	}
	
	
	private void setMainMsgListener(MainMsgListener listener){
		if(listener != null && listener instanceof MainMsgListener){
			mMainMsgListener = listener;
		}
		
	}
	
	public interface MainMsgListener{
		public void OnOfflineReceive(String[] users);
		public void OnReceive(EMMessage msg);
	}
}
