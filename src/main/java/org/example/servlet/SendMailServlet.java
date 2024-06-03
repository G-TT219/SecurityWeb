package org.example.servlet;

import redis.clients.jedis.Jedis;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Random;

@WebServlet(name = "SendMailServlet", value = "/SendMail")
public class SendMailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setContentType("text/html;charset=UTF-8");
        String email=request.getParameter("email");
        JavaMail javaMail=new JavaMail();
        String code=getCode();
        try {
            javaMail.sendTo(email,code);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        Jedis jedis=new Jedis("localhost");
        String user_name=request.getParameter("user_name");
        jedis.set(user_name,code);
        PrintWriter writer=response.getWriter();
        writer.write("0");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    private static String getCode(){
        String t="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789123456789123456789";
        Random random=new Random();
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<6;i++) {
            stringBuilder.append(t.charAt(random.nextInt(72)));
        }
        return stringBuilder.toString();
    }
}
