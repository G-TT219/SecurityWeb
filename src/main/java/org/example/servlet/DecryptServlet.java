package org.example.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
@MultipartConfig
@WebServlet(name = "DecryptServlet", value = "/decrypt")
public class DecryptServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        //获取普通表单项（获取参数）
        String key = req.getParameter("key"); //表单中表单元素的name属性值
        String fileName = req.getParameter("filename");

        //获取Part对象 （Servlet 将 mutipart/form-data 的 POST 请求封装成 Part对象）
        Part part = req.getPart("decryptfile");
        //通过Part对象得到上传的文件名
        //得到文件存放的路径
        String filePath = req.getServletContext().getRealPath("/")+ "temp/";
        //上传文件到指定目录
        part.write(filePath + fileName);
        String fileFullName=filePath + fileName;
        int flag=FileSM4.decrypt(fileFullName,FileSM4.keyPadding(key).getBytes());
        if(flag==0){
            PrintWriter writer=resp.getWriter();
            writer.write("密钥错误");
        }else{
            String newFileName=getNewFileName(fileName);
            resp.setContentType("application/octet-stream");
            //设置响应头信息
            resp.setHeader("Content-Disposition","attachment;filename=" +  java.net.URLEncoder.encode(newFileName, "UTF-8"));
            //得到file文件的输入流
            System.out.println("下载文件："+filePath+newFileName);
            InputStream in = new FileInputStream(filePath+newFileName);
            //得到字节输出流
            ServletOutputStream out = resp.getOutputStream();
            //定义byte数组
            byte[] bytes = new byte[1024];
            //定义长度
            int len =0;
            //循环输出
            while((len = in.read(bytes))!= -1){
                //输出
                out.write(bytes,0,len);
            }
            in.close();
            out.close();
        }
    }
    public String getNewFileName(String filename){
        String[] strs=filename.split("\\.");
        StringBuilder sb=new StringBuilder(strs[0]);
        sb.append("(decrypt).");
        sb.append(strs[1]);
        return sb.toString();
    }
}
