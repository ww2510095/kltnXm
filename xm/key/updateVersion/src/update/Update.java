package update;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public  class Update {

public static final String sdkfile="c://";
    public static void updateVerion(){
//        String path = OPen();
        DownLoad mDownLoad =new DownLoad();
        ByteArrayOutputStream mByteArrayOutputStream =mDownLoad.getByteArrayOutputStream("http://"+UPdate_Main.BaseUrl+"/upload/file/"+UPdate_Main.S_FILE_NAME);
        try {
            newFile(mByteArrayOutputStream,sdkfile+UPdate_Main.S_FILE_NAME);
            JOptionPane.showMessageDialog( null, "更新完成，请使用新的版本", "提示", JOptionPane.INFORMATION_MESSAGE);
            UPdate_Main.writeFile(newVersion);
            Runtime.getRuntime().exec("java -Xms256m -Xmx512m -jar "+sdkfile+UPdate_Main.S_FILE_NAME);
        }catch (Exception e){
            JOptionPane.showMessageDialog( null, "更新异常", "提示", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(0);

    }
    private static void newVerion(){
        int ikey =  JOptionPane.showConfirmDialog(null, "有新版本，是否更新？",
                "提示", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if(ikey==JOptionPane.YES_OPTION){
            updateVerion();
        }
    }
    public static String newVersion="";
    public static void InitVerion(){
        try {
              newVersion =  HttpRequest.sendPost("http://"+UPdate_Main.BaseUrl+"/version",null);
            File mfile = new File (sdkfile+UPdate_Main.S_FILE_NAME);
            if(!mfile.isFile()){
                newVerion();
            }else{
                if(!newVersion.equals(UPdate_Main.version_nulber)){
                    newVerion();
                }
            }

            Runtime.getRuntime().exec("java -Xms256m -Xmx512m -jar "+sdkfile+UPdate_Main.S_FILE_NAME);
        }catch (Exception e){
            JOptionPane.showMessageDialog( null, "网络异常，请检查", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }


    }

    /**
     * 根据byte数组，生成文件
     */
    public static void newFile(ByteArrayOutputStream baos , String fileName) throws Exception{
        FileOutputStream mfile = new FileOutputStream(new File(fileName));
        baos.writeTo(mfile);
        baos.flush();
        baos.close();
    }

    private static String OPen(){
        JFileChooser jfc=new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
        jfc.showDialog(new JLabel(), "选择新版本存放路径");
        File file=jfc.getSelectedFile();
        if(file.isDirectory()){
            return  file.toString();
        }else {
            JOptionPane.showMessageDialog( null, "错误，只能选择文件夹", "错误", JOptionPane.ERROR_MESSAGE);
            return  OPen();
        }
    }



}
