package com.bm;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bm.base.MyParameter;
import com.bm.systemMessage.SystemMessage;
import com.bm.systemMessage.SystemMessageService;

/**
 * 已多线程的方式添加消息
 * */
public class Message implements Runnable{
	
	
	private final Logger mLogger= LoggerFactory.getLogger(getClass());

	private String title; //标题
	private String content; //内容
	private String summary; //摘要
	private Long mymemberid; //所属人，0代表所有人
	private SystemMessageService mSystemMessageService;
	
	@Override
	public void run() {
		SystemMessage mSystemMessage= new SystemMessage();
		mSystemMessage.setTitle(this.title);
		mSystemMessage.setContent(content);
		mSystemMessage.setSummary(summary);
		String s =MyParameter.TomcatFileImage+"xiaoxi.png";
		mSystemMessage.setImage(s.substring(2,s.length()));
		mSystemMessage.setCode(UUID.randomUUID().toString());
		mSystemMessage.setType(0);
		mSystemMessage.setIstrue(0);
		mSystemMessage.setMemberid(1L);
		mSystemMessage.setMymemberid(mymemberid);
		try {
			mSystemMessageService.add(mSystemMessage);
		} catch (Exception e) {
			mLogger.error(e.getMessage());
		}
		
	}
	private Message() {}
	/**
	 * 给指定用户发送一条消息
	 * */
	public static void start(SystemMessageService mSystemMessageService,String title,String content,String summary,Long memberid){
		Message m = new Message();
		m.mSystemMessageService=mSystemMessageService;
		m.title=title;
		m.content=content;
		m.summary=summary;
		m.mymemberid=memberid;
		new Thread(m).start();
	}

}
