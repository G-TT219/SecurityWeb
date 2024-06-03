package org.example.servlet;

import redis.clients.jedis.Jedis;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@WebServlet(name = "RegisterServlet", value = "/Regist")
public class RegisterServlet extends HttpServlet {
    private static final String SALT_LENGTH = "16";
    private static final String HASH_ALGORITHM = "SHA-256";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String user_name=request.getParameter("user_name");
        String password=request.getParameter("password");
        String[] temp= new String[0];
        PrintWriter writer=response.getWriter();
        try {
            temp = hashPassword(password).split(":");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        password=temp[0];
        String salt=temp[1];
        Jedis jedis=new Jedis("localhost");
        String check=jedis.get(user_name);
        if(!check.equals(request.getParameter("check"))){
            writer.write("验证码错误");
        }else {
            try {
                Connection connection = MyDataSource.getConnection();
                Statement statement = connection.createStatement();
                String query = "select user_name from user_info where user_name='" + user_name + "'";
                ResultSet set = null;
                set = statement.executeQuery(query);
                if (set.first()) {
                    writer.write("用户名已存在");
                } else {
                    query = "insert into user_info (user_name,user_passwd,salt) value ('" + user_name + "','" + password + "','" + salt + "')";
                    statement.execute(query);
                    Cookie cookie = new Cookie("user_name", user_name);
                    cookie.setMaxAge(60 * 60 * 24);
                    response.addCookie(cookie);
                    writer.write("success");
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        // 1. 生成随机盐值
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[Integer.parseInt(SALT_LENGTH)];
        random.nextBytes(salt);
        String saltString = Base64.getEncoder().encodeToString(salt);

        // 2. 组合密码和盐值
        String saltedPassword = password + saltString;

        // 3. 使用哈希函数
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hashedPassword = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));

        // 4. 返回哈希值和盐值的组合
        return Base64.getEncoder().encodeToString(hashedPassword) + ":" + saltString;
    }
}
