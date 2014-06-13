package topicfriend.client.simpleandroid;

import java.util.ArrayList;
import java.util.HashMap;

import topicfriend.netmessage.data.TopicInfo;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShowTopicActivity extends Activity
{
	private ListView m_listTopic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_show_topic);
		
		//bind UI
		m_listTopic=(ListView)this.findViewById(R.id.list_topic);
		updateTopicListView();
		
		m_listTopic.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				//TODO: send join topic message to server
			}
		});
	}
	
	void updateTopicListView()
	{
		ArrayList<TopicInfo> topicList=AccountManager.getInstance().getTopicList();
		ArrayList<HashMap<String, String>> data=new ArrayList<HashMap<String,String>>();
		for(int i=0;i<topicList.size();i++)
		{
			TopicInfo topicInfo=topicList.get(i);
			HashMap<String, String> dict=new HashMap<String, String>();
			dict.put("title", topicInfo.getTitle());
			dict.put("description", topicInfo.getDescription());
			
			data.add(dict);
		}
		
		SimpleAdapter adapter=new SimpleAdapter(this, data, R.layout.list_topic_item, 
				new String[]{"title","description"}, 
				new int[]{R.id.text_title,R.id.text_description});
		m_listTopic.setAdapter(adapter);
	}
}
