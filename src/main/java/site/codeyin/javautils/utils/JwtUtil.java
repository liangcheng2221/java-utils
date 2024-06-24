package site.codeyin.javautils.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
@Data
public class JwtUtil {

    private final JWT jwtUtil = JWT.create();

    /**
     * jwt的有效期
     */
    @Value("${javaUtils.jwt.ttl}")
    private Long jwtTTL;
    /**
     * jwt秘钥
     */
    @Value("${javaUtils.jwt.secret}")
    private String jwtSecret;


    /**
     * 根据 Map 对象生成 JWT
     *
     * @param payload map对象
     * @return 返回 jwt 字符串
     */
    public String createJWT(Map<String, Object> payload) {
        JWT jwt = jwtUtil.setExpiresAt(new Date(System.currentTimeMillis() + jwtTTL));
        jwt.setKey(generalKey().getEncoded());
        jwt.addPayloads(payload);
        return jwt.sign();
    }


    /**
     * 生成加密后的秘钥 secretKey
     *
     * @return 返回秘钥
     */
    public SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(jwtSecret);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }


    /**
     * @param jwt    token字符串
     * @param tClass 字节码对象类型
     * @param <T>    字节码对象类型
     * @return 返回对应字节码类型对象
     */
    public <T> T parseJWT(String jwt, Class<T> tClass) {
        JSONObject payloads = jwtUtil.parse(jwt).getPayloads();
        return JSONUtil.toBean(payloads, tClass);
    }

    /**
     * 判断jwt是否有效
     *
     * @param jwt jwt字符串
     * @return true - 有效
     */
    public Boolean validateJwt(String jwt) {
        try {
            JWTValidator.of(jwt).validateDate(DateUtil.date(), 0L);
        } catch (ValidateException e) {
            return false;
        }
        return jwtUtil.parse(jwt).verify(JWTSignerUtil.hs256(generalKey().getEncoded()));
    }

    public static void main(String[] args) throws Exception {
        JwtUtil jwtUtil1 = new JwtUtil();
        Map<String, Object> map = new HashMap<>();
        map.put("id", "46456");
        map.put("role", 1);
        String jwt = jwtUtil1.createJWT(map);
        Thread.sleep(2000);
        System.out.println(jwtUtil1.validateJwt(jwt));
        Object o = jwtUtil1.parseJWT(jwt, Object.class);
    }

}