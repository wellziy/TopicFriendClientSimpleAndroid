package topicfriend.client.simpleandroid;

import java.io.IOException;
import java.util.ArrayList;

import topicfriend.client.netwrapper.NetMessageHandler;
import topicfriend.client.netwrapper.NetMessageReceiver;
import topicfriend.netmessage.NetMessage;
import topicfriend.netmessage.NetMessageError;
import topicfriend.netmessage.NetMessageID;
import topicfriend.netmessage.NetMessageLogin;
import topicfriend.netmessage.NetMessageLoginSucceed;
import topicfriend.netmessage.NetMessageRegister;
import topicfriend.netmessage.data.MessageInfo;
import topicfriend.netmessage.data.TopicInfo;
import topicfriend.netmessage.data.UserInfo;
import topicfriend.network.Network;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private Spinner m_spHostIP;
	private Button m_buttonConnect;
	private EditText m_editUserName;
	private EditText m_editPassword;
	private Button m_buttonLogin;
	private RadioButton m_radioMale;
	private RadioButton m_radioFemale;
	private Button m_buttonRegister;
	private ProgressDialog m_connectDialog;
	
	private Handler m_handler=new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//init the network
		Network.initNetwork(1, 1, 5);
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.ERROR, new NetMessageHandler()
		{
			@Override
			public void handleMessage(int connection, NetMessage msg)
			{
				NetMessageError msgError=(NetMessageError)msg;
				final String errorStr=msgError.getErrorStr();
				m_handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						showToast(errorStr);
					}
				});
			}
		});
		
		//bind UI
		m_spHostIP=(Spinner) this.findViewById(R.id.sp_host_ip);
		m_buttonConnect=(Button)this.findViewById(R.id.button_connect);
		m_editUserName=(EditText) this.findViewById(R.id.edit_username);
		m_editPassword=(EditText)this.findViewById(R.id.edit_password);
		m_buttonLogin=(Button)this.findViewById(R.id.button_login);
		m_radioMale=(RadioButton)this.findViewById(R.id.radio_male);
		m_radioFemale=(RadioButton)this.findViewById(R.id.radio_female);
		m_buttonRegister=(Button)this.findViewById(R.id.button_register);
		
		//create dialog for connecting server
		m_connectDialog=new ProgressDialog(this);
		m_connectDialog.setTitle("Hint");
		m_connectDialog.setMessage("Connecting to server...");
		m_connectDialog.setCancelable(false);
		
		m_buttonConnect.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				 Intent startMain = new Intent(Intent.ACTION_MAIN);
//				 startMain.addCategory(Intent.CATEGORY_HOME);
//				 startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY);
//				 startActivity(startMain);
				 
				String hostIP=m_spHostIP.getSelectedItem().toString();
				int port=55555;
				connectHost(hostIP,port);
			}
		});
		
		m_buttonLogin.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(AccountManager.getInstance().hasConnectedServer())
				{
					String userName=m_editUserName.getText().toString();
					String password=m_editPassword.getText().toString();
					doLogin(userName, password);
				}
			}
		});
		
		m_buttonRegister.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(AccountManager.getInstance().hasConnectedServer())
				{
					String userName=m_editUserName.getText().toString();
					String password=m_editPassword.getText().toString();
					int sex=(m_radioMale.isChecked()?UserInfo.SEX_MALE:UserInfo.SEX_FEMALE);
					doRegister(userName, password, sex);
				}
			}
		});
	}

	@Override
	protected void onDestroy() 
	{
		AccountManager.getInstance().purgeInstance();
		NetMessageReceiver.getInstance().purgeInstance();
		//destroy the network
		Network.destroyNetwork();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void showToast(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	private void connectHost(final String hostIP,final int port)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					int connection=Network.connectHostPort(hostIP, port, 1000);
					AccountManager.getInstance().setConnection(connection);
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				m_handler.post(new Runnable()
				{
					@Override
					public void run() 
					{
						if(AccountManager.getInstance().hasConnectedServer())
						{
							showToast("connect server succeed");
						}
						else
						{
							showToast("failed to connect server");
						}
						m_connectDialog.dismiss();
					}
				});
			}
		}).start();
		m_connectDialog.show();
	}
	
	private void doRegister(String userName,String password,int sex)
	{
		NetMessageRegister msgRegister=new NetMessageRegister(userName, password, sex);
		Network.sendDataOne(msgRegister.toByteArrayBuffer(), AccountManager.getInstance().getConnection());
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.LOGIN_SUCCEED, new NetMessageHandler()
		{
			@Override
			public void handleMessage(int connection, NetMessage msg) 
			{
				final NetMessageLoginSucceed msgLoginSucceed=(NetMessageLoginSucceed)msg;
				m_handler.post(new Runnable()
				{
					@Override
					public void run() 
					{
						onReceiveMessageLoginSucceed(msgLoginSucceed);
						showToast("register succeed");
					}
				});
			}
		});
		
	}
	
	private void doLogin(String userName,String password)
	{
		NetMessageLogin msgLogin=new NetMessageLogin(userName, password);
		Network.sendDataOne(msgLogin.toByteArrayBuffer(), AccountManager.getInstance().getConnection());
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.LOGIN_SUCCEED, new NetMessageHandler()
		{
			@Override
			public void handleMessage(int connection, NetMessage msg)
			{
				final NetMessageLoginSucceed msgLoginSucceed=(NetMessageLoginSucceed)msg;
				m_handler.post(new Runnable()
				{
					@Override
					public void run() 
					{
						onReceiveMessageLoginSucceed(msgLoginSucceed);
						showToast("login suceed");
					}
				});
			}
		});
	}
	
	private void onReceiveMessageLoginSucceed(final NetMessageLoginSucceed msgLoginSucceed)
	{
		UserInfo myInfo=msgLoginSucceed.getMyInfo();
		ArrayList<UserInfo> friendInfoList=msgLoginSucceed.getFriendInfoList();
		ArrayList<MessageInfo> unreadMessageList=msgLoginSucceed.getUnreadMessageList();
		ArrayList<TopicInfo> topicList=msgLoginSucceed.getTopicList();
		AccountManager.getInstance().initAccount(myInfo,friendInfoList,unreadMessageList,topicList);
		
		Intent intent=new Intent(this,HomeActivity.class);
		this.startActivity(intent);
	}
}
