package topicfriend.client.simpleandroid;

import java.util.ArrayList;

import topicfriend.netmessage.data.MessageInfo;
import topicfriend.netmessage.data.TopicInfo;
import topicfriend.netmessage.data.UserInfo;
import topicfriend.network.Network;

public class AccountManager
{
	private static AccountManager s_instance=null;
	
	private int m_connection;
	private UserInfo m_myInfo;
	private ArrayList<UserInfo> m_friendInfoList;
	private ArrayList<MessageInfo> m_unreadMessageList;
	private ArrayList<TopicInfo> m_topicList;
	
	////////////////////////////////
	//public
	public static AccountManager getInstance()
	{
		if(s_instance==null)
		{
			s_instance=new AccountManager();
		}
		return s_instance;
	}
	
	public void purgeInstance()
	{
		s_instance=null;
	}
	
	public void initAccount(UserInfo myInfo,ArrayList<UserInfo> friendInfoList,ArrayList<MessageInfo> unreadMessageList,ArrayList<TopicInfo> topicList)
	{
		m_myInfo=myInfo;
		m_friendInfoList=friendInfoList;
		m_unreadMessageList=unreadMessageList;
		m_topicList=topicList;
	}
	
	public UserInfo getMyInfo()
	{
		return m_myInfo;
	}
	
	public ArrayList<UserInfo> getFriendInfoList()
	{
		return m_friendInfoList;
	}
	
	public ArrayList<MessageInfo> getUnreadMessageList()
	{
		return m_unreadMessageList;
	}
	
	public ArrayList<MessageInfo> getUnreadMessageListOfFriend(int fid)
	{
		ArrayList<MessageInfo> res=new ArrayList<MessageInfo>();
		
		for(int i=0;i<m_unreadMessageList.size();i++)
		{
			MessageInfo msgInfo=m_unreadMessageList.get(i);
			if(msgInfo.getSenderID()==fid)
			{
				res.add(msgInfo);
			}
		}
		return res;
	}
	
	public ArrayList<TopicInfo> getTopicList()
	{
		return m_topicList;
	}
	
	public int getConnection()
	{
		return m_connection;
	}
	
	public void setConnection(int connection)
	{
		m_connection=connection;
	}
	
	public boolean hasConnectedServer()
	{
		return m_connection!=Network.NULL_CONNECTION;
	}
	
	/////////////////////////////////
	//private
	private AccountManager()
	{
		m_connection=Network.NULL_CONNECTION;
	}
}
