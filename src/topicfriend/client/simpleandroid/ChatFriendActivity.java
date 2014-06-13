package topicfriend.client.simpleandroid;

import java.util.ArrayList;
import java.util.HashMap;

import topicfriend.netmessage.NetMessageChatFriend;
import topicfriend.netmessage.data.MessageInfo;
import topicfriend.netmessage.data.UserInfo;
import topicfriend.network.Network;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ChatFriendActivity extends Activity
{
	UserInfo m_friendInfo;
	
	private EditText m_editMessageContent;
	private Button m_buttonSend;
	private ListView m_listMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_chat_friend);
		
		int index=this.getIntent().getIntExtra("index", -1);
		ArrayList<UserInfo> friendInfoList=AccountManager.getInstance().getFriendInfoList();
		if(index<0||index>=friendInfoList.size())
		{
			assert(false);
		}
		m_friendInfo=friendInfoList.get(index);
		
		//bind UI
		m_editMessageContent=(EditText)this.findViewById(R.id.edit_message_content);
		m_buttonSend=(Button)this.findViewById(R.id.button_send);
		m_listMessage=(ListView)this.findViewById(R.id.list_message);
		
		TextView textUserName=(TextView)this.findViewById(R.id.text_user_name);
		textUserName.setText(m_friendInfo.getName());
		TextView textSex=(TextView)this.findViewById(R.id.text_sex);
		textSex.setText(""+m_friendInfo.getSex());
		TextView textSignature=(TextView)this.findViewById(R.id.text_signature);
		textSignature.setText(m_friendInfo.getSignature());
		TextView textIcon=(TextView)this.findViewById(R.id.text_icon);
		textIcon.setText(m_friendInfo.getIcon());
		
		//create listener
		m_buttonSend.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(AccountManager.getInstance().hasConnectedServer())
				{
					int fid=m_friendInfo.getID();
					String content=m_editMessageContent.getText().toString();
					doChatFriend(fid, content);
					m_editMessageContent.setText("");
				}
			}
		});
		
		updateMessageListView();
	}
	
	void updateMessageListView()
	{
		ArrayList<HashMap<String, String>> data=new ArrayList<HashMap<String,String>>();
		ArrayList<MessageInfo> messageList=AccountManager.getInstance().getUnreadMessageListOfFriend(m_friendInfo.getID());
		for(int i=0;i<messageList.size();i++)
		{
			MessageInfo msgInfo=messageList.get(i);
			HashMap<String, String> dict=new HashMap<String, String>();
			dict.put("messageContent", msgInfo.getContent());
			
			data.add(dict);
		}
		
		SimpleAdapter adapter=new SimpleAdapter(this, data, R.layout.list_message_item, 
				new String[]{"messageContent"}, 
				new int[]{R.id.text_message_content});
		m_listMessage.setAdapter(adapter);
	}
	
	void doChatFriend(int fid,String content)
	{
		NetMessageChatFriend msgChatFriend=new NetMessageChatFriend(fid, content);
		Network.sendDataOne(msgChatFriend.toByteArrayBuffer(), AccountManager.getInstance().getConnection());
	}
}
