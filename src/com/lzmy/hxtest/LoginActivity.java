package com.lzmy.hxtest;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{

	private boolean isRegister = false;
	private EditText etName;
	private EditText etKey;
	private EditText etKeyAgain;
	private Button btGotoLogin;
	private Button btQuickRegister;
	private Button btLogin;
	private Button btRegister;
	
	private boolean loginProgressShow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		
	}
	
	private void initView(){
		etName = (EditText)findViewById(R.id.etName);
		etKey = (EditText)findViewById(R.id.etKey);
		etKeyAgain = (EditText)findViewById(R.id.etKeyAgain);
		btGotoLogin = (Button)findViewById(R.id.bt_goto_login);
		btQuickRegister = (Button)findViewById(R.id.bt_quick_register);
		btLogin = (Button)findViewById(R.id.btLogin);
		btRegister = (Button)findViewById(R.id.btRegister);
		
		btGotoLogin.setOnClickListener(this);
		btQuickRegister.setOnClickListener(this);
		btLogin.setOnClickListener(this);
		btRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.bt_quick_register:
			isRegister = true;
			etKey.getEditableText().clear();
			etKeyAgain.getEditableText().clear();
			etKeyAgain.setVisibility(View.VISIBLE);
			btGotoLogin.setVisibility(View.VISIBLE);
			btLogin.setVisibility(View.GONE);
			btQuickRegister.setVisibility(View.GONE);
			btRegister.setVisibility(View.VISIBLE);
			break;
			
		case R.id.bt_goto_login:
			isRegister = false;
			etKey.getEditableText().clear();
			etKeyAgain.getEditableText().clear();
			etKeyAgain.setVisibility(View.GONE);
			btGotoLogin.setVisibility(View.GONE);
			btLogin.setVisibility(View.VISIBLE);
			btQuickRegister.setVisibility(View.VISIBLE);
			btRegister.setVisibility(View.GONE);
			break;

		case R.id.btRegister:
			if(isRegister){
				register();
			}
			
			break;
			
		case R.id.btLogin:
			Log.d("loginActivity", "login btn clicked!");
			if(!isRegister){
				login();
			}
			
			break;
		}
		
	}
	
	
	private void register(){
		
		final String name = etName.getText().toString().trim();
		final String key = etKey.getText().toString().trim();
		String key_again = etKeyAgain.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
			etName.requestFocus();
			return;
		} else if (TextUtils.isEmpty(key)) {
			Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
			etKey.requestFocus();
			return;
		} else if (TextUtils.isEmpty(key_again)) {
			Toast.makeText(this, "确认密码不能为空！", Toast.LENGTH_SHORT).show();
			etKeyAgain.requestFocus();
			return;
		} else if (!key.equals(key_again)) {
			Toast.makeText(this, "两次输入的密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(key)) {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("正在注册...");
			pd.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						// 调用sdk注册方法
						EMChatManager.getInstance().createAccountOnServer(name, key);
						runOnUiThread(new Runnable() {
							public void run() {
								if(isRegister){
									pd.dismiss();
								}
								Toast.makeText(getApplicationContext(), "注册成功", 0).show();
								btGotoLogin.performClick();
							}
						});
					} catch (final Exception e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if (isRegister)
									pd.dismiss();
								if (e != null && e.getMessage() != null) {
									String errorMsg = e.getMessage();
									if (errorMsg.indexOf("EMNetworkUnconnectedException") != -1) {
										Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", 0).show();
									} else if (errorMsg.indexOf("conflict") != -1) {
										Toast.makeText(getApplicationContext(), "用户已存在！", 0).show();
									}/* else if (errorMsg.indexOf("not support the capital letters") != -1) {
										Toast.makeText(getApplicationContext(), "用户名不支持大写字母！", 0).show();
									} */else {
										Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), 1).show();
									}

								} else {
									Toast.makeText(getApplicationContext(), "注册失败: 未知异常", 1).show();

								}
							}
						});
					}
				}
			}).start();

		}
		
	}
	
	
	
	private void login(){
		final String name = etName.getText().toString().trim();
		final String key = etKey.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
			etName.requestFocus();
			return;
		} else if (TextUtils.isEmpty(key)) {
			Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
			etKey.requestFocus();
			return;
		} 
		
		if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(key)) {
			loginProgressShow = true;
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("正在登陆...");
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					loginProgressShow = false;
				}
			});
			
			pd.show();
			
			
			EMChatManager.getInstance().login(name, key, new EMCallBack() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					if(!loginProgressShow){
						return ;
					}
					
					Toast.makeText(getApplicationContext(), "登陆成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
					
				}
				
				@Override
				public void onProgress(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onError(int arg0, final String arg1) {
					// TODO Auto-generated method stub
					if(!loginProgressShow){
						return ;
					}
					
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), "登录失败: " + arg1, 0).show();

						}
					});
					
				}
			});
			
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(isRegister && keyCode == KeyEvent.KEYCODE_BACK){
			btGotoLogin.performClick();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	

	
	
}
