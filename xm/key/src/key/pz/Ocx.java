package key.pz;

import key.JxBrowser;
import key.javaEx;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.ole.win32.*;
import org.eclipse.swt.widgets.*;

import javax.swing.*;

class Controller{

	private OleAutomation oleAutomation;
	public Controller(OleAutomation oleAutomation)
	{
		this.oleAutomation = oleAutomation;
	}
	public OleAutomation GetOleAutomation(){
		return oleAutomation;
	}

	public Variant execute(String methodName) {
		return execute(methodName, null);
	}

	public Variant execute(String methodName, Variant[] args) {
		int mid = getID(methodName);
		if (mid < 0) {
			return null;
		}
		Variant rtnv = null;
		if (args == null) {
			rtnv = oleAutomation.invoke(mid);
		} else {
			rtnv = oleAutomation.invoke(mid, args);
		}
		return rtnv;
	}

	private int getID(String name) {
		try {
			int[] ids = oleAutomation.getIDsOfNames(new String[] { name });
			if(ids == null)
			{
				return -1;
			}
			if (ids.length >= 0)
				return ids[0];
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return -1;
	}

}


public class Ocx {

	private static boolean istrue=false;

	OleFrame m_oleVideoFrame;
	OleControlSite m_oleControlSite;
	Controller m_controller;
	Shell m_shell;
	Display m_display;
	Button m_btnCaptureImage;
	Button m_btnInitDevice, m_btnStartDevice, m_btnCloseDevice, m_btnRelease;
	String m_strDeviceIdx;
	Label m_labelInfo;
	boolean m_bInit;

	public void start() throws InterruptedException {
		m_bInit = false;
		m_strDeviceIdx = "0";
		m_display = Display.getDefault();
		m_shell = new Shell(SWT.ON_TOP);
		//m_shell = new Shell();
		m_shell.setSize(528, 620);
		m_shell.setText("Java-OCX拍照");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.makeColumnsEqualWidth = false;
		//m_shell.setLayout(gridLayout);
		m_shell.setLayout(null);
		m_shell.addListener(SWT.Close, new Listener(){
			public void handleEvent(Event e){
				m_controller.execute("ReleaseDevice");
				if(istrue){
					javaEx mjavaEx =new javaEx();
					String a =mjavaEx.sendImage();
					if (a.indexOf("/upload") != -1) {
						a=a.substring(63,104);
						JxBrowser.getBrowser().executeJavaScript("rowBackMessage('"+a+"')");
					}else {
						JOptionPane.showMessageDialog(
								null, "文件上传失败", "错误", JOptionPane.ERROR_MESSAGE);
					}

					istrue=false;
				}

			}
		});


		// 创建OLEFrame
	//	m_oleVideoFrame = new OleFrame(m_shell, SWT.ON_TOP);// 绑定OCX
		m_oleVideoFrame = new OleFrame(m_shell, SWT.Activate);
		m_oleControlSite = new OleControlSite(m_oleVideoFrame, SWT.Activate,
				"ICAPTUREVIDEOTY.ICaptureVideoCtrlTY.1");
		m_oleControlSite.doVerb(OLE.OLEIVERB_SHOW);
		m_controller = new Controller(new OleAutomation(m_oleControlSite));
		m_oleVideoFrame.setBounds(5, 5, 500, 500);

		m_labelInfo = new Label(m_shell, SWT.NONE);
		m_labelInfo.setBounds(10, 550, 500, 30);
		m_labelInfo.setText("Java-OCX拍照程序");

		m_btnInitDevice = new Button(m_shell, SWT.NONE);
		m_btnInitDevice.setBounds(10, 510, 80, 30);
		m_btnInitDevice.setText("初始化");
		m_btnInitDevice.setVisible(false);
		m_btnInitDevice.addListener(SWT.MouseDown, new Listener(){
			public void handleEvent(Event e){
				initsb();
			}
		});

		m_btnStartDevice = new Button(m_shell, SWT.NONE);
		m_btnStartDevice.setBounds(100, 510, 80, 30);
		m_btnStartDevice.setText("开启设备");
		m_btnStartDevice.setVisible(false);
		m_btnStartDevice.addListener(SWT.MouseDown, new Listener(){
			public void handleEvent(Event e){

				Startsb();
			}
		});

		m_btnCaptureImage = new Button(m_shell, SWT.NONE);
		m_btnCaptureImage.setBounds(190, 510, 80, 30);
		m_btnCaptureImage.setText("拍照");
		m_btnCaptureImage.addListener(SWT.MouseDown, new Listener(){
			public void handleEvent(Event e){
				paizhao();
			}
		});


		m_btnCloseDevice = new Button(m_shell, SWT.NONE);
		m_btnCloseDevice.setBounds(280, 510, 80, 30);
		//m_btnCloseDevice.setText("关闭摄像头");
		m_btnCloseDevice.setText("确定并关闭");
		m_btnCloseDevice.addListener(SWT.MouseDown, new Listener(){
			public void handleEvent(Event e){
//				int iRet = m_controller.execute("StopDevice", new Variant[] { new Variant(m_strDeviceIdx)}).getInt();
//				if(iRet == 0)
//					m_labelInfo.setText("已关闭摄像头" + m_strDeviceIdx);
//				else
//					m_labelInfo.setText("关闭摄像头" + m_strDeviceIdx + "失败");
				m_shell.close();
			}

		});

		m_btnRelease = new Button(m_shell, SWT.NONE);
		m_btnRelease.setBounds(370, 510, 80, 30);
		m_btnRelease.setText("关闭窗口");
		m_btnRelease.setVisible(false);
		m_btnRelease.addListener(SWT.MouseDown, new Listener(){
			public void handleEvent(Event e){
//				m_controller.execute("ReleaseDevice");
//				m_labelInfo.setText("当前设备已释放");
				istrue=true;
				m_shell.close();
			}
		});

		m_shell.open();
		Startsb();
		while (!m_shell.isDisposed()) {
			if (!m_display.readAndDispatch())
				m_display.sleep();
		}

	}
	public boolean initsb(){
	int iRet = m_controller.execute("InitDevice").getInt();
	m_bInit = (iRet == 0);
		if(m_bInit)
		m_labelInfo.setText("初始化设备成功");
	else
		m_labelInfo.setText("初始化设备失败");
	return m_bInit;

}
	public int  Startsb(){
	if(m_bInit == false)
	{
		//m_labelInfo.setText("设备未初始化");
		initsb();
		Startsb();
		return -1;
	}
	int iRet = m_controller.execute("StartDevice", new Variant[] { new Variant(m_strDeviceIdx)}).getInt();

	if(iRet == 0)
		m_labelInfo.setText("启动设备" + m_strDeviceIdx + "成功");
	else
		m_labelInfo.setText("启动设备" + m_strDeviceIdx + "失败");

	iRet = m_controller.execute("SetCutPageType", new Variant[] { new Variant(m_strDeviceIdx), new Variant("1")}).getInt();

	return iRet;
}
	public void  paizhao(){
	if(m_bInit == false)
	{
		m_labelInfo.setText("设备未初始化");
		return;
	}
	String strImageFile = "c:/0.jpg";
	int iRet = m_controller.execute("CaptureImage",
			new Variant[] { new Variant(m_strDeviceIdx), new Variant(strImageFile)}).getInt();
	if(iRet == 0) {
		istrue=true;
		//m_labelInfo.setText("拍照成功，保存图像至：" + strImageFile);
		m_labelInfo.setText("拍照成功");
	}
	else
		m_labelInfo.setText("拍照失败");
}

	public void closed() throws InterruptedException {
		m_shell.close();
	}


}
