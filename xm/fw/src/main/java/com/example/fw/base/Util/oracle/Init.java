package com.example.fw.base.Util.oracle;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.myjar.Stringutil;

import io.swagger.annotations.ApiModelProperty;

public class Init {
   public Init(String mainclasspath,String classpath,String tabname,String tabnametl,boolean isc){
       this.classpath=classpath;
       this.mainclasspath=mainclasspath;

       String  MethodName = tabname.toLowerCase();
       MethodName = MethodName
               .replaceFirst(MethodName
                       .substring(0, 1), MethodName.substring(0, 1).toUpperCase());
       this.tabname=MethodName;
       this.tabnametl=tabnametl;
       this.isc=isc;


   }
   /**
    * @param classpath 项目路径
    * @param b javabean 的包名
    * @param c Controller的包名
    * @param s Service 的包名
    * @param packagename 外在的包名
    * @param tabnametl 表的注释
    * */
    public Init(String mainclasspath,String classpath,String packagename,String c,String s,String b,String tabname,String tabnametl){
        this.classpath=classpath+"\\";
        this.c=c+"\\";
        this.b=b+"\\";
        this.s=s+"\\";
        this.packagename=packagename+"\\";
        this.mainclasspath=mainclasspath+"\\";
        String  MethodName = tabname.toLowerCase();
        MethodName = MethodName
                .replaceFirst(MethodName
                        .substring(0, 1), MethodName.substring(0, 1).toUpperCase());
        this.tabname=MethodName;
        this.tabnametl=tabnametl;
    }
    public void setBm() {
		c="bm\\";
		b="bm\\";
		s="bm\\";

	}
   String classpath;
   String c="c\\";
   String b="b\\";
   String s="s\\";
   String packagename="main\\";
   String mainclasspath;
   String tabname;
   String tabnametl;
   boolean isc=true;//是否生成Controller文件
//    private static String USERNAMR = "jiabi";
//    private static String PASSWORD = "jiabi";
//    private static String DRVIER = "oracle.jdbc.OracleDriver";
//    private static String URL = "jdbc:oracle:thin:@114.116.88.94:1522:ORCL";
    private static String USERNAMR = "kltnuser";
    private static String PASSWORD = "kltnuser";
    private static String DRVIER = "oracle.jdbc.OracleDriver";
    private static String URL = "jdbc:oracle:thin:@127.0.0.1:1521:ORCL";
    
    
    // 创建一个数据库连接
    Connection connection = null;
    // 创建预编译语句对象，一般都是用这个而不用Statement
    PreparedStatement pstm = null;
    // 创建一个结果集对象
    ResultSet rs = null;
    /**
     * 向数据库中查询数据
     */
    public void SelectData() throws Exception{
        connection = getConnection();
        String sql = "select distinct b.COLUMN_NAME title, b.comments cou, a.column_id,a.DATA_TYPE  \n" +
                "  from user_tab_columns a, user_col_comments b\n" +
                " where a.column_name = b.column_name\n" +
                "   and b.table_name = upper('"+tabname+"')\n" +
                " order by a.column_id\n";
        System.out.println(sql);
        try {
            pstm = connection.prepareStatement(sql);
            rs = pstm.executeQuery();
            List<String> listtitle = new ArrayList<>();
            List<String> listcou = new ArrayList<>();
            List<String> listdatatype = new ArrayList<>();
            while (rs.next()) {
                String title = rs.getString("title");
                String cou = rs.getString("cou");
                String datatype = rs.getString("data_type");
                listtitle.add(title);
                if(Stringutil.isBlank(cou))
                    listcou.add("");
                else
                    listcou.add(cou);
                listdatatype.add(datatype);

            }
            initfile(listtitle,listcou,listdatatype);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
           // ReleaseResource();
        }
    }

    private void initfile(List<String> listtitle, List<String> listcou,List<String> listdatatype) throws  Exception{
            initB(listtitle,listcou,listdatatype);
            if(isc){
            	 initC();
                 initS();
            }
           
    }



    /**
     * 获取Connection对象
     *
     * @return
     */
    public Connection getConnection() {
        try {
            Class.forName(DRVIER);
            connection = DriverManager.getConnection(URL, USERNAMR, PASSWORD);
            System.out.println("成功连接数据库");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not find !", e);
        } catch (SQLException e) {
            throw new RuntimeException("get connection error!", e);
        }

        return connection;
    }

    private void initC() throws Exception {
        String data = "package "+mainclasspath+"."+packagename.replace("\\","")+"."+c.replace("\\","")+";\n" +
                "\n" +
                "import "+mainclasspath+".base.BaseController;\n" +
                "import "+mainclasspath+".base.RequestType;\n" +
                "import "+mainclasspath+"."+packagename.replace("\\","")+"."+b.replace("\\","")+"."+tabname+";\n" +
                "import "+mainclasspath+"."+packagename.replace("\\","")+"."+s.replace("\\","")+"."+tabname+"Service;\n" +
                "import com.myjar.Stringutil;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RequestMethod;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "import io.swagger.annotations.Api;\n" +
                "import io.swagger.annotations.ApiOperation;"+
                "\n" +
                "@RestController\n" +
                "@RequestMapping(\"/"+tabname+"\")\n" +
                "@Api(tags = \""+tabnametl+"\")\n"+
                "public class "+tabname+"Controller extends BaseController {\n" +
                "    @Autowired\n"+
                "    protected "+tabname+"Service m"+tabname+"Service;\n"+
                "\n" +
                "    @ApiOperation(value = \"添加或修改"+tabnametl+"，id不为空时修改，否则添加\", response = RequestType.class)\n" +
                "    @RequestMapping(value =\"/save\", method = RequestMethod.POST)\n" +
                "    public RequestType save("+tabname+" m"+tabname+") throws Exception {\n" +
                "        if(Stringutil.isBlank(m"+tabname+".getId())){\n" +

                "            m"+tabname+"Service.add(m"+tabname+");\n" +
                "            return sendTrueMsg(\"添加成功\");\n" +
                "        }else{\n" +
                "            m"+tabname+"Service.updateBySelect(m"+tabname+");\n" +
                "            return sendTrueMsg(\"更新成功\");\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "    @ApiOperation(value = \""+tabnametl+"列表，支持所有参数模糊解锁\", response = "+tabname+".class)\n" +
                "    @RequestMapping(value =\"/list\", method = RequestMethod.POST)\n" +
                "    public RequestType list("+tabname+" m"+tabname+",Integer page,Integer rows) throws Exception {\n" +
                "        return sendTrueData(m"+tabname+"Service.getALL(m"+tabname+",page,rows));\n" +
                "    }\n\n" +
                "    @ApiOperation(value = \"查询单个的"+tabnametl+"id不可为空\", response = "+tabname+".class)\n" +
                "    @RequestMapping(value =\"/getByid\", method = RequestMethod.POST)\n" +
                "    public RequestType getByid(String id) throws Exception {\n" +
                "        if(Stringutil.isBlank(id)) return sendFalse(\"编号不可为空\");\n"+
                "        return sendTrueData(m"+tabname+"Service.getById(id));\n" +
                "    }\n" +
                "\n" +
                "}\n";

        File file = new File(classpath +"\\"+ mainclasspath.replace(".","\\")+"\\"+packagename + c+tabname+"Controller.java");
        System.out.println(file);
        if (!file.exists()) {
            file.createNewFile();
        }

        byte[] contentInBytes = data.getBytes();
        FileOutputStream  fop = new FileOutputStream(file);
        fop.write(contentInBytes);
        fop.flush();
        fop.close();
    }
    private String forTitleAndCou(List<String> listtitle,List<String> listcou,List<String> listdatatype){
        List<String> str = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        boolean ida ;
        for(int i=0;i<listtitle.size();i++){
            if(str.contains(listtitle.get(i)))continue;
            str.add(listtitle.get(i));
             ida = listtitle.get(i).toUpperCase().equals("ID");
                sb.append("\t");
        String zs ="\n\t@ApiParam(name=\""+listtitle.get(i).toLowerCase() +"\",value = \""+(ida?"唯一标识":listcou.get(i)) +"\")\n";
                sb.append(zs);
                sb.append("\t");
               if( listdatatype.get(i).toUpperCase().equals("NUMBER")){
            	   sb.append("@ApiModelProperty(value=\""+(ida?"唯一标识":listcou.get(i))+"\",example=\"1\")\n");
                }else{
                	 sb.append("@ApiModelProperty(value=\""+listcou.get(i)+"\",example=\""+listcou.get(i)+"\")\n");
                }
               sb.append("\t");
                sb.append("private ");
                if(listdatatype.get(i).toUpperCase().equals("NUMBER")){
                    if(ida){
                        sb.append("Long id;");
                        sb.append("\t//");
                        sb.append(listcou.get(i));
                        continue;
                    }else{
                        sb.append("Integer ");
                        sb.append(listtitle.get(i).toLowerCase());
                        sb.append(";\t//");
                        sb.append(listcou.get(i));
                        continue;
                    }
                }
            if(listdatatype.get(i).toUpperCase().equals("VARCHAR2")){
                sb.append("String ");
                sb.append(listtitle.get(i).toLowerCase());
                sb.append(";\t//");
                sb.append(listcou.get(i));
                continue;
            }

        }
        sb.append("\n");
//        sb.append("\n");
//        for(int i=0;i<listtitle.size();i++){
//            sb.append("\n");
//            String sa;
//            if(listdatatype.get(i).toUpperCase().equals("NUMBER")){
//                if(listtitle.get(i).toUpperCase().equals("ID")){
//                    sa="Long";
//                }else{
//                    sa="BigDecimal";
//                }
//            }else{
//                sa="String";
//            }
//            String  MethodName = listtitle.get(i).toLowerCase();
//            MethodName = MethodName
//                   .replaceFirst(MethodName
//                           .substring(0, 1), MethodName.substring(0, 1).toUpperCase());
//            sb.append("\t public ");
//            sb.append(sa);
//            sb.append(" get");
//            sb.append(MethodName);
//            sb.append("(){\n");
//            sb.append("\t return this.");
//            sb.append(listtitle.get(i).toLowerCase());
//            sb.append(";");
//            sb.append("\n");
//            sb.append("\t}");
//            sb.append("\n");
//
//            sb.append("\t public void set");
//            sb.append(MethodName);
//            sb.append("(");
//            sb.append(sa);
//            sb.append(" ");
//            sb.append(listtitle.get(i).toLowerCase());
//            sb.append("){\n");
//            sb.append("\t  this.");
//            sb.append(listtitle.get(i).toLowerCase());
//            sb.append("=");
//            sb.append(listtitle.get(i).toLowerCase());
//            sb.append(";");
//            sb.append("\n");
//            sb.append("\t}");
//            sb.append("\n");
//        }

        return  sb.toString();
    }
    private void initB(List<String> listtitle,List<String> listcou,List<String> listdatatype) throws Exception {
        String data =  "package "+mainclasspath+"."+packagename.replace("\\","")+"."+b.replace("\\","")+";\n" +
                "\n" +
                "import "+mainclasspath+".base.BaseEN;\n" +
                "\n" +
                "import io.swagger.annotations.ApiModel;\n" +
                "import io.swagger.annotations.ApiModelProperty;\n" +
                "import io.swagger.annotations.ApiParam;\n" +
                "import lombok.Data;\n" +
                "@ApiModel\n" +
                "@Data\n" +
                "public class "+tabname+" extends BaseEN {\n" +
                forTitleAndCou(listtitle,listcou,listdatatype)+
                "}\n";
        File file = new File(classpath +"\\"+ mainclasspath.replace(".","\\")+"\\"+packagename + b+tabname+".java");

       System.out.println(file);
        if (!file.exists()) {
            file.createNewFile();
        }

        byte[] contentInBytes = data.getBytes();
        FileOutputStream  fop = new FileOutputStream(file);
        fop.write(contentInBytes);
        fop.flush();
        fop.close();
    }

    private void initS() throws Exception {

        String data =  "package "+mainclasspath+"."+packagename.replace("\\","")+"."+s.replace("\\","")+";\n" +
                "\n" +
                "import "+mainclasspath+".base.BaseService;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "\n" +
                "@Service\n" +
                "public class "+tabname+"Service extends BaseService {\n" +
                "    @Override\n" +
                "    protected String getTabName() {\n" +
                "        return \""+tabname+"\";\n" +
                "    }\n" +
                "}\n";

        File file = new File(classpath +"\\"+ mainclasspath.replace(".","\\")+"\\"+packagename + s+tabname+"Service.java");
        System.out.println(file);
        if (!file.exists()) {
            file.createNewFile();
        }
//
//        //true = append file
//        FileWriter fileWritter = new FileWriter(file.getName(), true);
//        fileWritter.write(data);
//        fileWritter.close();
        byte[] contentInBytes = data.getBytes();
        FileOutputStream  fop = new FileOutputStream(file);
        fop.write(contentInBytes);
        fop.flush();
        fop.close();

    }
}
