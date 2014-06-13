package topicfriend.client.simpleandroid;

import java.util.ArrayList;
import java.util.HashMap;

import topicfriend.netmessage.data.MessageInfo;
import topicfriend.netmessage.data.UserInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class HomeActivity extends Activity
{
	private TextView m_textUserName;
	private TextView m_textSex;
	private TextView m_textSignature;
	private TextView m_textIcon;
	private ListView m_listFriend;
	private Button m_buttonShowTopic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_home);
		
		//bind UI
		m_textUserName=(TextView)this.findViewById(R.id.text_user_name);
		m_textSex=(TextView)this.findViewById(R.id.text_sex);
		m_textSignature=(TextView)this.findViewById(R.id.text_signature);
		m_textIcon=(TextView)this.findViewById(R.id.text_icon);
		m_listFriend=(ListView)this.findViewById(R.id.list_friend);
		m_buttonShowTopic=(Button)this.findViewById(R.id.button_show_topic);
		
		//init UI
		UserInfo myInfo=AccountManager.getInstance().getMyInfo();
		m_textUserName.setText(myInfo.getName());
		m_textSex.setText(""+myInfo.getSex());
		m_textSignature.setText(myInfo.getSignature());
		m_textIcon.setText(myInfo.getIcon());
		
		updateFriendListView();
		
		//create listener
		m_listFriend.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
			{
				Intent intent=new Intent(HomeActivity.this,ChatFriendActivity.class);
				intent.putExtra("index", arg2);
				HomeActivity.this.startActivity(intent);
			}
		});
		m_buttonShowTopic.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent=new Intent(HomeActivity.this,ShowTopicActivity.class);
				HomeActivity.this.startActivity(intent);
			}
		});
	}
	
	private void updateFriendListView()
	{
		final String keyUserName="userName";
		final String keySex="sex";
		final String keySignature="signature";
		final String keyIcon="icon";
		final String keyLastestMessage="lastestMessage";
		
		ArrayList<UserInfo> friendInfoList=AccountManager.getInstance().getFriendInfoList();
		ArrayList<HashMap<String,String>> data=new ArrayList<HashMap<String,String>>();
		for(int i=0;i<friendInfoList.size();i++)
		{
			HashMap<String, String> dict=new HashMap<String, String>();
			UserInfo friendInfo=friendInfoList.get(i);
			
			dict.put(keyUserName,friendInfo.getName());
			dict.put(keySex, ""+friendInfo.getSex());
			dict.put(keySignature, friendInfo.getSignature());
			dict.put(keyIcon, friendInfo.getIcon());
			
			ArrayList<MessageInfo> msgList=AccountManager.getInstance().getUnreadMessageListOfFriend(friendInfo.getID());
			String lastestMessage="NONE";
			if(msgList.size()>0)
			{
				lastestMessage=msgList.get(0).getContent();
			}
			dict.put(keyLastestMessage, lastestMessage);
			
			data.add(dict);
		}
		
		SimpleAdapter adapter=new SimpleAdapter(this,data,R.layout.list_friend_item,
				new String[]{keyUserName,keySex,keySignature,keyIcon,keyLastestMessage},
				new int[]{R.id.text_user_name,R.id.text_sex,R.id.text_signature,R.id.text_icon,R.id.text_lastest_message});
		m_listFriend.setAdapter(adapter);
	}
}
