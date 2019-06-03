package com.bm.task;

import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.bm.base.MyParameter;
import com.myjar.Stringutil;
import com.myjar.desutil.DESUtils;

/**
 * 任务机制，通知心跳系统
 * */
public class TSystem implements Runnable{
	
	private String[] sa;
	private TaskService mTaskService;

	@Override
	public void run() {
		try {
			savetasklog(sa);
//			if(sa[1].equals("-1"))sa[1]="";
			
			DatagramSocket s = new DatagramSocket(null);
	        s.setReuseAddress(true);
	        //这里是指定发送的客户端端口，因为该协议规定只接收由此端口发出的数据
	        s.bind(new InetSocketAddress(9001));
	        String str="";
	        for (String string : sa)
				str=str+string+";";
	        
	        str = str +(new BigDecimal(System.currentTimeMillis()+"").add(new BigDecimal(sa[1]))).toString() +";";
	        str=DESUtils.password(str);
	        
	        byte[] data = str.getBytes();
	        
	        DatagramPacket p = new DatagramPacket(data,0,data.length, new InetSocketAddress(MyParameter.TASK_ADDRESS, 9090));
	        s.send(p);
	        s.close();
	        
	        
		} catch (Exception e) {
			
		}
		
		
	}
	
	private TSystem() {}
	/**
	 * 增加日志
	 */
	public static void start(String[] sa,TaskService mTaskService) {
//		TSystem mTSystem = new TSystem();
//		mTSystem.sa=sa;
//		mTSystem.mTaskService=mTaskService;
//		new Thread(mTSystem).start();
	}
	
	private void savetasklog(String[] sa){
	try {
		Task1 t = new Task1();
			t.setTime(System.currentTimeMillis()+"");; //添加时间
			t.setSid(sa[0]);; //任务id
			t.setDid(sa[2]);; //数据id
		if("-1".equals(sa[1]))
			mTaskService.deleteBySelect(t);//删除任务日志
		else{
			if(Stringutil.isBlank(sa[1]))
				sa[1]="0";
			t.setEtime((new BigDecimal(System.currentTimeMillis()+"").add(new BigDecimal(sa[1]))).toString() ); //任务结束时间
			t.setStime(sa[1]); //延迟时间-1为结束任务
			t.setId(UUID.randomUUID().toString());
			t.setData(sa[3]);
			t.setSystem(MyParameter.system+"");
		//添加任务日志
			mTaskService.add(t);
		}
			
			
	} catch (Exception e) {
		
	}
	
}

}
