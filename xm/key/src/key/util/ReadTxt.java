//package key.util;
//
//
//import java.io.*;
//
//
//public class ReadTxt {
// static String keypath="c:/ipConfig.ini";
//
//    /**
//     * 读入TXT文件
//     */
//    public static String readFile(String pathname) {
//        cj(pathname);
//    	StringBuffer sa = new StringBuffer();
//        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
//        //不关闭文件会导致资源的泄露，读写文件都同理
//        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
//        try (FileReader reader = new FileReader(pathname);
//             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
//        ) {
//
//            String line;
//            //网友推荐更加简洁的写法
//            while ((line = br.readLine()) != null) {
//                // 一次读入一行数据
//            	sa.append(line);
//               // System.out.println(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//		return sa.toString();
//    }
//    private static void cj(String s){
//        File
//        file = new File(s);
//        try {
//            if(!file.exists()){
//                file.createNewFile();
//                writeFile("101.37.244.165:8001");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//    }
//    /**
//     * 读入TXT文件
//     */
//    public static String readFile() {
//    	return readFile(keypath);
//    }
//
//    /**
//     * 写入TXT文件
//     */
//    public static void writeFile(String sa) {
//        try {
//            File writeName = new File(keypath); // 相对路径，如果没有则要建立一个新的output.txt文件
//            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
//            try (FileWriter writer = new FileWriter(writeName);
//                 BufferedWriter out = new BufferedWriter(writer)
//            ) {
//                out.write(sa); // \r\n即为换行
//                out.flush(); // 把缓存区内容压入文件
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//
//
