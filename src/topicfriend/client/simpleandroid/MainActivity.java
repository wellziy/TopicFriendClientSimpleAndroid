package topicfriend.client.simpleandroid;

import java.io.IOException;

import topicfriend.client.netwrapper.NetMessageHandler;
import topicfriend.client.netwrapper.NetMessageReceiver;
import topicfriend.netmessage.NetMessage;
import topicfriend.netmessage.NetMessageError;
import topicfriend.netmessage.NetMessageID;
import topicfriend.netmessage.NetMessageLogin;
import topicfriend.netmessage.NetMessageLoginSucceed;
import topicfriend.network.Network;
import android.app.Activity;
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
	private EditText m_editUserName;
	private EditText m_editPassword;
	private Button m_buttonLogin;
	private RadioButton m_radioMale;
	private RadioButton m_radioFemale;
	private Button m_buttonRegister;
	private int m_connection;
	
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
						showErrorStr(errorStr);
					}
				});
			}
		});
		
		m_spHostIP=(Spinner) this.findViewById(R.id.sp_host_ip);
		m_editUserName=(EditText) this.findViewById(R.id.edit_username);
		m_editPassword=(EditText)this.findViewById(R.id.edit_password);
		m_buttonLogin=(Button)this.findViewById(R.id.button_login);
		m_radioMale=(RadioButton)this.findViewById(R.id.radio_male);
		m_radioFemale=(RadioButton)this.findViewById(R.id.radio_female);
		m_connection=Network.NULL_CONNECTION;
		
		m_buttonLogin.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(lazyConnect())
				{
					String userName=m_editUserName.getText().toString();
					String password=m_editPassword.getText().toString();
					doLogin(userName, password);
				}
			}
		});
		
	}

	@Override
	protected void onDestroy() 
	{
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
	
	private boolean lazyConnect()
	{
		if(m_connection!=Network.NULL_CONNECTION)
		{
			return true;
		}
		String hostIP=m_spHostIP.toString();
		int port=55555;
		
		try 
		{
			m_connection=Network.connectHostPort(hostIP, port, 1000);
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		showErrorStr("failed to connection host");
		m_connection=Network.NULL_CONNECTION;
		return false;
	}
	
	private void showErrorStr(String errorStr)
	{
		Toast.makeText(this, errorStr, Toast.LENGTH_LONG).show();
	}
	
	private void doLogin(String userName,String password)
	{
		NetMessageLogin msgLogin=new NetMessageLogin(userName, password);
		Network.sendDataOne(msgLogin.toByteArrayBuffer(), m_connection);
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.LOGIN_SUCCEED, new NetMessageHandler()
		{
			@Override
			public void handleMessage(int connection, NetMessage msg)
			{
				NetMessageLoginSucceed msgLoginSucceed=(NetMessageLoginSucceed)msg;
				m_handler.post(new Runnable()
				{
					@Override
					public void run() 
					{
						showErrorStr("login suceed");
					}
				});
			}
		});
	}
}
