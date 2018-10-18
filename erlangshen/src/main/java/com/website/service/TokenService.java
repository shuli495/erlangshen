package com.website.service;

import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.page.OrderSort;
import com.fastjavaframework.response.ReturnJson;
import com.fastjavaframework.util.SecretUtil;
import com.fastjavaframework.util.UUID;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.HttpHelper;
import com.website.executor.LoginPlaceReport;
import com.website.model.vo.ClientSecurityVO;
import com.website.model.vo.TokenVO;
import com.website.model.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.TokenDao;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * token
 * @author https://github.com/shuli495/erlangshen
 */
@Service
public class TokenService extends BaseService<TokenDao,TokenVO> {

    @Autowired
    private UserService userService;
    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private ClientSecurityService clientSecurityService;
    @Autowired
    private ValidateService validateService;

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
     * @param verifyCode
     * @return
     */
    public Object inster(HttpServletResponse response, TokenVO token, boolean isCheckStatus,
                          String loginIp, String userName, String pwd, String platform, String verifyCode) {
        // 校验防机器人验证码
        String validateId = token.getClientId() + "_" + loginIp;
        validateService.checkVerifyCode(validateId, "login", verifyCode);

        // 根据用户名查询用户
        List<UserVO> users = new ArrayList<>();
        try {
            users = userService.checkExist(token.getClientId(), userName, null);
        } catch (Exception e) {
            return this.returnCode(response, token.getClientId(), loginIp, "用户名或密码错误！", "122012");
        }

        if(users.size() == 0) {
            return this.returnCode(response, token.getClientId(), loginIp, "用户名或密码错误！", "122013");
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
        if(!user.getPwd().equals(SecretUtil.md5(pwd))) {
            return this.returnCode(response, token.getClientId(), loginIp, "用户名或密码错误！", "122014");
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
        if(null != clientSecurityVO && clientSecurityVO.getIsCheckPlace()
                && clientSecurityVO.getIsCheckPlace()
                && VerifyUtils.isNotEmpty(loginIp)
                && userTokens.size() > 0
                && !userTokens.get(0).getIp().equals(loginIp)
                && (VerifyUtils.isNotEmpty(clientSecurityVO.getCheckPlaceMailTypeId())
                || VerifyUtils.isNotEmpty(clientSecurityVO.getCheckPlacePhoneTypeId()))) {
            new LoginPlaceReport(user.getId(), token.getClientId(), userTokens.get(0).getIp(), loginIp);
        }

        if(VerifyUtils.isEmpty(loginIp)) {
            loginIp = "";
        }

        // 没有token，新生成
        if (null == userTokens || userTokens.size() == 0) {
            // 删除登录验证码
            validateService.delete(validateId, "login", null);
            // 返回token
            return (new ReturnJson()).success(this.newToken(user.getId(), loginIp, platform));
        } else {
            // token有效期剩余多少分钟后，生成新token
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MINUTE, tokenActiveNew);

            TokenVO reToken = null;


            String loginApi = clientSecurityVO.getLoginApi();   //登录通知接口
            Integer isCheckPlatform = clientSecurityVO.getIsCheckPlatform();    // 是否对登陆平台检查
            Integer checkPlatformType = clientSecurityVO.getCheckPlatformType();// 登录冲突操作

            for (TokenVO userToken : userTokens) {
                // 同端同平台
                if(loginIp.equals(userToken.getIp()) && platform.equals(userToken.getPlatform())) {
                    // token是否过期
                    boolean isPast = userToken.getActiveTime().before(cal.getTime());
                    if(isPast) {
                        // 回收过期token
                        this.baseDelete(userToken.getId());

                        // 生成新token
                        reToken = this.newToken(user.getId(), loginIp, platform);
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
                reToken = this.newToken(user.getId(), loginIp, platform);
            }

            // 删除登录防机器人验证码
            validateService.delete(validateId, "login", null);

            // 返回token
            user.setToken(reToken);
            return (new ReturnJson()).success(user);
        }
    }

    /**
     * 返回code
     * @param response
     * @param tokenClientId
     * @param loginIp
     * @param data
     * @param code
     * @return
     */
    private Object returnCode(HttpServletResponse response, String tokenClientId, String loginIp, String data, String code) {
        response.setStatus(400);
        Map<String, String> result = new HashMap<>(3);
        result.put("data", data);
        result.put("code", code);
        result.put("image", validateService.verifyCode("login", tokenClientId, loginIp));
        return result;
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
     * @param userId
     * @param clientIp
     * @param platform
     * @return
     */
    public TokenVO newToken(String userId, String clientIp, String platform) {
        TokenVO tokenVO = new TokenVO();

        // token有效时间
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, tokenActiveTime);

        tokenVO.setId(UUID.uuid());
        tokenVO.setUserId(userId);
        tokenVO.setCreatedTime(new Date());
        tokenVO.setActiveTime(cal.getTime());
        tokenVO.setIp(clientIp);
        tokenVO.setPlatform(platform);
        super.baseInsert(tokenVO);

        // 登录日志
        loginLogService.insert(userId, clientIp);

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

        return tokenVO;
    }
}