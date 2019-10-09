package com.erlangshen.service;

import com.erlangshen.common.Constants;
import com.erlangshen.common.HttpHelper;
import com.erlangshen.dao.TokenDao;
import com.erlangshen.executor.LoginPlaceReport;
import com.erlangshen.model.vo.ClientSecurityVO;
import com.erlangshen.model.vo.TokenVO;
import com.erlangshen.model.vo.UserVO;
import com.fastjavaframework.base.BaseService;
import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.page.OrderSort;
import com.fastjavaframework.response.ReturnJson;
import com.fastjavaframework.util.VerifyUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * token
 * @author https://github.com/shuli495/erlangshen
 */
@Service
public class TokenService extends BaseService<TokenDao, TokenVO> {

    @Autowired
    private UserService userService;
    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private ClientSecurityService clientSecurityService;
    @Autowired
    private ValidateService validateService;

    @Value("${aes.secret}")
    private String aesSecret;

    @Value("${token.active.time}")
    private Integer tokenActiveTime;

    @Value("${token.active.new}")
    private Integer tokenActiveNew;

    /**
     * 生成token
     * 查询出所有有效token；当前ip如果有token，判断是否过期，过期重新生成；
     * 可以重复登录不操作其他ip的token，不可以重复登录则把其他ip的token全部注销。
     * @param token
     * @param isCheckStatus 是否校验状态 邮箱登录校验邮箱是否激活 手机登录校验手机是否激活 账号登录不校验
     * @param loginIp
     * @param userName
     * @param pwd
     * @param platform 登录端，区分不同平台的登录，用于校验不同平台是否可以同事登录；入安卓登录、网页能同时登录，其他手机不能登录。空值全局校验
     * @param robotCode
     * @return
     */
    public Object inster(TokenVO token, boolean isCheckStatus, String loginIp, String userName, String pwd,
                         String platform, String robotCode) {
        // 校验防机器人验证码
        String validateId = token.getClientId() + "_" + loginIp;
        validateService.checkRobotCode(validateId, Constants.CODE_TYPE_ROBOT, robotCode);

        // 根据用户名查询用户
        List<UserVO> users = new ArrayList<>();
        try {
            users = userService.checkExist(token.getClientId(), userName);
        } catch (Exception e) {
            return this.returnCode(token.getClientId(), userName, "用户名或密码错误！", "122012");
        }

        if(users.size() == 0) {
            return this.returnCode(token.getClientId(), userName, "用户名或密码错误！", "122013");
        }

        // 验证状态
        if(true == isCheckStatus) {
            for(UserVO user : users) {
                if(userName.equals(user.getMail()) && user.getMailVerify() == 0) {
                    throw new ThrowPrompt("用户邮箱未验证！", "122001");
                }
                if(userName.equals(user.getPhone()) && user.getPhoneVerify() == 0) {
                    throw new ThrowPrompt("用户手机未验证！", "122002");
                }
            }
        }

        UserVO user = users.get(0);

        // 校验密码
        if(!BCrypt.checkpw(pwd, user.getPwd())) {
            return this.returnCode(token.getClientId(), userName, "用户名或密码错误！", "122014");
        }

        // 查询已存在token
        TokenVO tokenVO = new TokenVO();
        tokenVO.setUserId(user.getId());
        tokenVO.setActiveTime(new Date());
        tokenVO.setOrderBy("active_time");
        tokenVO.setOrderSort(OrderSort.DESC);
        List<TokenVO> userTokens = super.baseQueryByAnd(tokenVO);

        ClientSecurityVO clientSecurityVO = clientSecurityService.baseFind(token.getClientId());

        // 异地登录检查，上次登录地址与当前登录地址不同，发告警
        boolean isPlaceOther = null != clientSecurityVO && clientSecurityVO.getIsCheckPlace()
                                && clientSecurityVO.getIsCheckPlace()
                                && VerifyUtils.isNotEmpty(loginIp)
                                && userTokens.size() > 0
                                && !userTokens.get(0).getIp().equals(loginIp)
                                && (VerifyUtils.isNotEmpty(clientSecurityVO.getCheckPlaceMailTypeId())
                                || VerifyUtils.isNotEmpty(clientSecurityVO.getCheckPlacePhoneTypeId()));
        if(isPlaceOther) {
            new LoginPlaceReport(user.getId(), token.getClientId(), userTokens.get(0).getIp(), loginIp);
        }

        if(VerifyUtils.isEmpty(loginIp)) {
            loginIp = "";
        }

        // 没有token，新生成
        if (null == userTokens || userTokens.size() == 0) {
            // 删除登录验证码
            validateService.delete(validateId, Constants.CODE_TYPE_ROBOT, null);
            // 返回token
            return (new ReturnJson()).success(this.newToken(user, loginIp, platform));
        } else {
            // token有效期剩余多少分钟后，生成新token
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MINUTE, tokenActiveNew);

            TokenVO reToken = null;


            //登录通知接口
            String loginApi = clientSecurityVO.getLoginApi();
            // 是否对登陆平台检查
            Integer isCheckPlatform = clientSecurityVO.getIsCheckPlatform();
            // 登录冲突操作
            Integer checkPlatformType = clientSecurityVO.getCheckPlatformType();

            for (TokenVO userToken : userTokens) {
                // 同端同平台
                if(loginIp.equals(userToken.getIp()) && platform.equals(userToken.getPlatform())) {
                    // token是否过期
                    boolean isPast = userToken.getActiveTime().before(cal.getTime());
                    if(isPast) {
                        // 回收过期token
                        this.baseDelete(userToken.getId());

                        // 生成新token
                        reToken = this.newToken(user, loginIp, platform);
                    } else {
                        // 未过期返回当前token
                        reToken = userToken;
                    }

                    // 允许重复登录直接返回当前token，不用设置其他token
                    if(null == isCheckPlatform || isCheckPlatform == 0) {
                        // 删除登录验证码
                        validateService.delete(validateId, "login", null);
                        // 返回token
                        return (new ReturnJson()).success(reToken);
                    }

                // 同端异平台
                } else if(loginIp.equals(userToken.getIp()) && !platform.equals(userToken.getPlatform())) {
                    // 同时只能有一个账号在线
                    if(isCheckPlatform == 2) {
                        this.delToken(checkPlatformType, userToken.getId(), loginApi);
                    }
                // 异端同平台
                } else if(!loginIp.equals(userToken.getIp()) && platform.equals(userToken.getPlatform())) {
                    // 同时只能有一个账号在线
                    if(isCheckPlatform == 2 || isCheckPlatform == 1) {
                        this.delToken(checkPlatformType, userToken.getId(), loginApi);
                    }
                // 异端异平台
                } else if(!loginIp.equals(userToken.getIp()) && !platform.equals(userToken.getPlatform())) {
                    // 同时只能有一个账号在线
                    if(isCheckPlatform == 2) {
                        this.delToken(checkPlatformType, userToken.getId(), loginApi);
                    }
                }
            }

            // 当前ip没token，重新生成
            if(null == reToken) {
                reToken = this.newToken(user, loginIp, platform);
            }

            // 删除登录防机器人验证码
            validateService.delete(validateId, Constants.CODE_TYPE_ROBOT, null);

            // 返回token
            user.setToken(reToken);
            return (new ReturnJson()).success(user);
        }
    }

    /**
     * 返回code
     * @param tokenClientId
     * @param loginIp
     * @param data
     * @param code
     * @return
     */
    private Object returnCode(String tokenClientId, String loginIp, String data, String code) {
        Map<String, String> codeImageMap = new HashMap<>(1);
        codeImageMap.put("codeImage", validateService.robotCode(Constants.CODE_TYPE_ROBOT, tokenClientId, loginIp));
        throw new ThrowPrompt(data, code, codeImageMap);
    }

    /**
     * 登录冲突操作
     * @param checkPlatformType 0登出之前登陆的账号 1新登陆请求失败
     * @param tokenId
     * @param loginApi 登录通知接口
     */
    private void delToken(Integer checkPlatformType, String tokenId, String loginApi) {
        // 登录通知
        try {
            HttpHelper.get(loginApi);
        } catch (Exception e) {
        }

        if(null == checkPlatformType || checkPlatformType == 0) {
            this.baseDelete(tokenId);
        } else {
            throw new ThrowPrompt("已有账号登录！", "122004");
        }
    }

    /**
     * 生成新token
     * @param userVO
     * @param clientIp
     * @param platform
     * @return
     */
    public TokenVO newToken(UserVO userVO, String clientIp, String platform) {
        Date now = new Date(System.currentTimeMillis());
        TokenVO tokenVO = new TokenVO();

        // token有效时间
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, tokenActiveTime);

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] encodedKey = Base64.decodeBase64(aesSecret);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setIssuedAt(now)
                .setExpiration(cal.getTime())
                .claim("id", userVO.getId())
                .claim("cilentId", userVO.getClientId())
                .setIssuer("erlangshen")
                .signWith(signatureAlgorithm, key);
        String jwt = builder.compact();

        tokenVO.setId(jwt);
        tokenVO.setUserId(userVO.getId());
        tokenVO.setCreatedTime(now);
        tokenVO.setActiveTime(cal.getTime());
        tokenVO.setIp(clientIp);
        tokenVO.setPlatform(platform);
        super.baseInsert(tokenVO);

        // 登录日志
        loginLogService.insert(userVO.getId(), clientIp);

        return tokenVO;
    }

    /**
     * 校验token是否有效，有效返回token信息，无效抛异常
     * @param token
     * @param loginIp
     * @return token信息
     */
    public TokenVO check(String token, String loginIp) {
        if(VerifyUtils.isEmpty(token)) {
            throw new ThrowPrompt("token无效！", "122005");
        }

        TokenVO tokenVO = super.baseFind(token);

        if(null == tokenVO) {
            throw new ThrowPrompt("token无效！", "122006");
        }

        if(VerifyUtils.isNotEmpty(tokenVO.getIp()) && VerifyUtils.isNotEmpty(loginIp) && !loginIp.equals(tokenVO.getIp())) {
            throw new ThrowPrompt("token无效！", "122007");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if(tokenVO.getActiveTime().before(cal.getTime())) {
            throw new ThrowPrompt("token失效，请重新获取！", "122008");
        }

        Claims claims = Jwts.parser()
                .setSigningKey(aesSecret)
                .parseClaimsJws(token)
                .getBody();
        if(null == claims) {
            throw new ThrowPrompt("token无效！", "122009");
        }

        tokenVO.setClientId((String)claims.get("cilentId"));
        return tokenVO;
    }
}