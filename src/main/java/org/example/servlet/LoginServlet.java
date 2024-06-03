package org.example.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.Map;

@WebServlet(name = "LoginServlet", value = "/Login")
public class LoginServlet extends HttpServlet {
    private static final String SALT_LENGTH = "16"; // 假设我们使用16字节的盐值
    private static final String HASH_ALGORITHM = "SHA-256";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer=response.getWriter();
        String user_name=request.getParameter("user_name");
        String passwd=request.getParameter("password");
        try {
            Connection connection = MyDataSource.getConnection();
            Statement statement = connection.createStatement();
            String sql = "select user_passwd,salt from user_info where user_name='" + user_name + "';";
            ResultSet set = statement.executeQuery(sql);
            if (!set.first()) {
                writer.write("不存在用户名");
            } else {
                String salt = set.getString("salt");
                String temp = hashPassword(passwd, salt);
                if (temp.equals(set.getString("user_passwd"))) {
                    Cookie cookie = new Cookie("user_name", user_name);
                    cookie.setMaxAge(1000 * 60 * 60 * 2);
                    response.addCookie(cookie);
                    writer.write("登陆成功");
                } else {
                    writer.write("密码错误");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    public static String hashPassword(String password,String salt) throws NoSuchAlgorithmException {

        // 2. 组合密码和盐值
        String saltedPassword = password + salt;

        // 3. 使用哈希函数
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hashedPassword = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));

        // 4. 返回哈希值和盐值的组合（这里只是简单地将它们Base64编码后拼接在一起）
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
}
