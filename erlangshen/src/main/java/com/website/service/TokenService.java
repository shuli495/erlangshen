package com.website.service;

import com.fastjavaframework.Setting;
import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.page.OrderSort;
import com.fastjavaframework.util.SecretUtil;
import com.fastjavaframework.util.UUID;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.HttpHelper;
import com.website.common.LoginPlaceReport;
import com.website.model.bo.TokenBO;
import com.website.model.bo.UserBO;
import com.website.model.vo.ClientSecurityVO;
import com.website.model.vo.TokenVO;
import com.website.model.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.TokenDao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TokenService extends BaseService<TokenDao,TokenVO> {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private ClientSecurityService clientSecurityService;

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
     * @return
     */
    public TokenBO inster(TokenVO token, boolean isCheckStatus,
                          String loginIp, String userName, String pwd, String platform) {
        List<UserVO> users = userService.check(token.getClientId(), userName, null);

        if(true == isCheckStatus) {
            for(UserVO user : users) {
                if(userName.equals(user.getMail()) && user.getStatus() !=0 && user.getStatus() != 3) {
                    throw new ThrowPrompt("用户邮箱未验证！", "052001");
                }
                if(userName.equals(user.getPhone()) && user.getStatus() !=0 && user.getStatus() != 2) {
                    throw new ThrowPrompt("用户手机未验证！", "052002");
                }
            }
        }
        UserBO user = users.get(0);

        if(!user.getPwd().equals(SecretUtil.md5(pwd))) {
            throw new ThrowPrompt("账号或密码错误！", "052003");
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
            new LoginPlaceReport(token, user.getId(), token.getClientId(), userTokens.get(0).getIp(), loginIp);
        }

        if(VerifyUtils.isEmpty(loginIp)) {
            loginIp = "";
        }

        // 没有token，新生成
        if (null == userTokens || userTokens.size() == 0) {
            loginLogService.insert(user.getId(), loginIp);
            return this.newToken(user.getId(), loginIp, platform);
        } else {
            // token有效期剩余多少分钟后，生成新token
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MINUTE, Integer.parseInt(Setting.getProperty("token.active.new")));

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
                        return reToken;
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

            loginLogService.insert(user.getId(), loginIp);
            return reToken;
        }
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
            throw new ThrowPrompt("已有账号登录！");
        }
    }

    /**
     * 生成新token
     * @param userId
     * @param clientIp
     * @param platform
     * @return
     */
    private TokenVO newToken(String userId, String clientIp, String platform) {
        TokenVO tokenVO = new TokenVO();

        // token有效时间
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, Integer.parseInt(Setting.getProperty("token.active.time")));

        tokenVO.setId(UUID.uuid());
        tokenVO.setUserId(userId);
        tokenVO.setCreatedTime(new Date());
        tokenVO.setActiveTime(cal.getTime());
        tokenVO.setIp(clientIp);
        tokenVO.setPlatform(platform);
        super.baseInsert(tokenVO);

        return tokenVO;
    }

    /**
     * 校验token是否有效，有效返回token信息，无效抛异常
     * @param token
     * @param clientIp
     * @return token信息
     */
    public TokenVO check(String token, String clientIp) {
        if(VerifyUtils.isEmpty(token)) {
            throw new ThrowPrompt("token无效！");
        }
        TokenVO tokenVO = super.baseFind(token);

        if(null == tokenVO) {
            throw new ThrowPrompt("token无效！");
        }

        if(VerifyUtils.isNotEmpty(clientIp) && !clientIp.equals(tokenVO.getIp())) {
            throw new ThrowPrompt("token无效！");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if(tokenVO.getActiveTime().before(cal.getTime())) {
            throw new ThrowPrompt("token失效，请重新获取！");
        }

        return tokenVO;
    }
}