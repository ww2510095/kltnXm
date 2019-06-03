package key;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

public class JxBrowser {
    //请使用正版授权
    static {
        try {
            Class claz = null;
            //6.5.1版本破解 兼容xp
            // claz =  Class.forName("com.teamdev.jxbrowser.chromium.aq");
            //6.21版本破解 默认使用最新的6.21版本
            claz =  Class.forName("com.teamdev.jxbrowser.chromium.ba");

            Field e = claz.getDeclaredField("e");
            Field f = claz.getDeclaredField("f");


            e.setAccessible(true);
            f.setAccessible(true);
            Field modifersField = Field.class.getDeclaredField("modifiers");
            modifersField.setAccessible(true);
            modifersField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
            modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            e.set(null, new BigInteger("1"));
            f.set(null, new BigInteger("1"));
            modifersField.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("执行jxbrowser破解程序时出现异常"+LoggerUtil.getErrorMessage(e));
        }
    }
    public static Browser browser=null;
    public static void main(String[] args) {
         browser = new Browser();
        BrowserView view = new BrowserView(browser);
        JFrame frame = new JFrame("Money Issuance Service Management System");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    JSValue window = browser.executeJavaScriptAndReturnValue("window");
                    // 给jswindows对象添加一个扩展的属性
                    javaEx javaObject = new javaEx();
                    window.asObject().setProperty("javaEX", javaObject);
                }
            }
        });

        browser.loadURL("http://101.37.244.165/OAsystem/index.html");
      //  browser.loadURL("C:\\Users\\Administrator\\Desktop\\aa\\1.html");

        initSystem();
    }
    public static void main1(String[] args) {
        initSystem();
        // Specifies remote debugging port for remote Chrome Developer Tools.
        BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222");

        browser = new Browser();
        BrowserView view1 = new BrowserView(browser);
        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    JSValue window = browser.executeJavaScriptAndReturnValue("window");
                    // 给jswindows对象添加一个扩展的属性
                    javaEx javaObject = new javaEx();
                    window.asObject().setProperty("javaEX", javaObject);
                }
            }
        });


        // Gets URL of the remote Developer Tools web page for browser1 instance.
        String remoteDebuggingURL = browser.getRemoteDebuggingURL();

        JFrame frame1 = new JFrame();
        frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame1.add(view1, BorderLayout.CENTER);
        frame1.setSize(700, 500);
        frame1.setLocationRelativeTo(null);
        frame1.setVisible(true);

        browser.loadURL("http://101.37.244.165/OAsystem/index.html");
        // browser.loadURL("https://tieba.baidu.com/p/6139194886");

        // Creates another Browser instance and loads the remote Developer
        // Tools URL to access HTML inspector.
        Browser browser2 = new Browser();
        BrowserView view2 = new BrowserView(browser2);

        JFrame frame2 = new JFrame();
        frame2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame2.add(view2, BorderLayout.CENTER);
        frame2.setSize(700, 500);
        frame2.setLocationRelativeTo(null);
        frame2.setVisible(true);
        browser2.loadURL(remoteDebuggingURL);
    }
    /**
     * 对系统初始化
     * */
    private static void initSystem(){
        // 设置OptionPane组件显按钮示效果
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("宋体", Font.ITALIC, 16)));
        // 设置OptionPane组件显文本显示效果
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.ITALIC, 16)));
        writeFile("1.1");
    }
    public static Browser getBrowser(){
        return browser;
    }


    /**
     * 写入TXT文件
     */
    public static void writeFile(String sa) {
        try {
            File writeName = new File("c://system_java.ini"); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(sa); // \r\n即为换行
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}