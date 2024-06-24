package site.codeyin.javautils.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮箱工具类
 */
@Component
@Data
public class MailUtils {

    @Value("${javaUtils.mail.user}")
    private  String user;

    @Value("${javaUtils.mail.password}")
    private  String password;

    /**
     * 发送邮件
     *
     * @param email   需要发送的邮箱
     * @param content 短信信息
     * @param title   发送邮箱的标题
     */
    public void sendMail(String email, String content, String title) throws MessagingException, IOException {

        // 创建Properties 类用于记录邮箱的一些属性
        Properties props = new Properties();
        // 表示SMTP发送邮件，必须进行身份验证
        props.put("mail.smtp.auth", "true");
        //此处填写SMTP服务器
        props.put("mail.smtp.host", "smtp.qq.com");
        //端口号，QQ邮箱端口587
        props.put("mail.smtp.port", "587");
        // 此处填写，写信人的账号
        props.put("mail.user", user);
        // 此处填写16位STMP口令
        props.put("mail.password", password);
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress form = new InternetAddress(props.getProperty("mail.user"));
        message.setFrom(form);
        // 设置收件人的邮箱
        InternetAddress to = new InternetAddress(email);
        message.setRecipient(RecipientType.TO, to);
        // 设置邮件标题
        message.setSubject(title);
        // 设置邮件的内容体
        message.setContent(content, "text/html;charset=UTF-8");
        // 发送邮件
        Transport.send(message);
    }


    /**
     * 加载email模板
     *
     * @param templateName 模版的相对路径
     * @return 返回读取的模版字符串
     * @throws IOException IO异常
     */
    public String loadEmailTemplate(String templateName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templateName);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        return new String(bytes);
    }

    /**
     * 替换模板中的变量 格式：{1}
     *
     * @param template              读取的模版字符串
     * @param placeholdersAndValues 需要替换的值数组对象
     * @return 返回 替换后的模版字符串
     */
    public String populateTemplate(String template, String... placeholdersAndValues) {
        // Check if the number of placeholders and values match

        // Populate template with provided placeholders and values
        for (int i = 0; i < placeholdersAndValues.length; i++) {
            String value = placeholdersAndValues[i];
            template = template.replace("{" + i + "}", value);
        }

        return template;
    }

    /**
     * 进行邮箱的格式的检测
     *
     * @param email 邮箱地址
     * @return true 正确格式
     */
    public static boolean checkEmail(String email) {
        // 正则表达式模式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}