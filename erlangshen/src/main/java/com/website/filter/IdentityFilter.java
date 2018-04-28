package com.website.filter;

import com.fastjavaframework.common.SecretBase64Enum;
import com.fastjavaframework.util.CommonUtil;
import com.fastjavaframework.util.SecretUtil;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.BodyReaderHttpServletRequestWrapper;
import com.website.common.Constants;
import com.website.common.HttpHelper;
import com.website.model.vo.ClientVO;
import com.website.model.vo.KeyVO;
import com.website.model.vo.TokenVO;
import com.website.service.ClientService;
import com.website.service.KeyService;
import com.website.service.TokenService;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wsl on 1/23 0023.
 */
public class IdentityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if(request.getMethod().equalsIgnoreCase("OPTIONS")) {
                filterChain.doFilter(request, response);
                return;
            }

            // header中base64编码的签名
            String signature = request.getHeader("signature");
            // token
            String token = request.getHeader("token");

            if(VerifyUtils.isEmpty(signature) && VerifyUtils.isEmpty(token)) {
                this.returnError(response);
                return;
            }

            ServletContext sc = request.getSession().getServletContext();
            XmlWebApplicationContext cxt = (XmlWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(sc);

            // ak/sk认证
            if(VerifyUtils.isNotEmpty(signature)) {
                // 解密后的签名
                String signatureData = SecretUtil.base64Decrypt(SecretBase64Enum.URL, signature);

                KeyService keyService = (KeyService) cxt.getBean("keyService");

                String ak = signatureData.split("&")[0];
                String dataAes = signatureData.split("&")[1];   //密文

                if(VerifyUtils.isEmpty(ak) || VerifyUtils.isEmpty(dataAes)) {
                    this.returnError(response);
                    return;
                }

                // 查询sk
                KeyVO keyVO = keyService.baseFind(ak);
                if(null == keyVO) {
                    this.returnError(response);
                    return;
                }

                // 查询request body
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(httpServletRequest);
                String body = HttpHelper.getBodyString(requestWrapper);

                // 签名当前请求
                String bodyAes = this.sign(httpServletRequest, body, keyVO.getSecret());

                // 签名正确
                if(bodyAes.equals(dataAes)) {
                    String clientId = keyVO.getClientId();

                    ClientService clientService = (ClientService) cxt.getBean("clientService");
                    ClientVO clientVO = clientService.baseFind(clientId);

                    // 设置身份认证信息
                    TokenVO tokenVO = new TokenVO();
                    tokenVO.setUserId(clientVO.getCreatedBy());
                    tokenVO.setClientId(clientId);

                    request.setAttribute("identity", tokenVO);

                    filterChain.doFilter(requestWrapper, response);
                } else {
                    this.returnError(response);
                    return;
                }
            } else {    //token认证
                String clientIp = "";
                // admin token只能用于以下操作
                if(token.equals(Constants.ADMIN_TOKEN)) {
                    // 获取token
                    boolean isToken = request.getRequestURI().endsWith(Constants.URL_TOKEN) || request.getRequestURI().endsWith(Constants.URL_TOKEN+"/");
                    // 用户查询或创建
                    boolean isCreateOrSel = (request.getRequestURI().endsWith(Constants.URL_USER) || request.getRequestURI().endsWith(Constants.URL_USER+"/"))
                            && (request.getMethod().equalsIgnoreCase("get") || request.getMethod().equalsIgnoreCase("post"));
                    // 发送邮件
                    boolean isSendMail = (request.getRequestURI().endsWith(Constants.URL_USER+Constants.URL_USER_SENDMAIL) || request.getRequestURI().endsWith(Constants.URL_USER+Constants.URL_USER_SENDMAIL+"/"))
                            && request.getMethod().equalsIgnoreCase("get");
                    // 发送短信
                    boolean isSendPhone = (request.getRequestURI().endsWith(Constants.URL_USER+Constants.URL_USER_SENDPHONE) || request.getRequestURI().endsWith(Constants.URL_USER+Constants.URL_USER_SENDPHONE+"/"))
                            && request.getMethod().equalsIgnoreCase("get");
                    // 校验验证码
                    boolean isCheckCode = (request.getRequestURI().endsWith(Constants.URL_USER+Constants.URL_USER_CHECKCODE) || request.getRequestURI().endsWith(Constants.URL_USER+Constants.URL_USER_CHECKCODE+"/"))
                            && request.getMethod().equalsIgnoreCase("get");
                    // 验证邮箱url
                    boolean isCheckMail = (request.getRequestURI().endsWith(Constants.URL_USER+Constants.URL_USER_CHECKMAIL) || request.getRequestURI().endsWith(Constants.URL_USER+Constants.URL_USER_CHECKMAIL+"/"))
                            && request.getMethod().equalsIgnoreCase("get");
                    // 重置密码
                    boolean isRePwd = (request.getRequestURI().endsWith(Constants.URL_USER+"/rePwd") || request.getRequestURI().endsWith(Constants.URL_USER+"/rePwd/"))
                            && request.getMethod().equalsIgnoreCase("post");

                    if(!isToken && !isCreateOrSel && !isSendMail && !isSendPhone && !isCheckCode && !isCheckMail && !isRePwd) {
                        this.returnError(response);
                        return;
                    }
                } else {
                    clientIp = CommonUtil.getIp(request);
                }

                TokenService tokenService = (TokenService) cxt.getBean("tokenService");

                // 检查token
                TokenVO tokenVO = tokenService.check(token, clientIp);

                // 设置身份认证信息
                request.setAttribute("identity", tokenVO);

                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            this.returnError(response);
            return;
        }
    }

    /**
     * 签名
     * 用 “请求方式:api_url?参数@body” 的格式hmacsha1加密
     * @param request
     * @param body
     * @param sk
     * @return
     * @throws IOException
     */
    private String sign(HttpServletRequest request, String body, String sk) throws IOException {
        String url = request.getMethod() + ":" + request.getRequestURI();
        String params = request.getQueryString();
        if(VerifyUtils.isNotEmpty(params)) {
            url += "?" + params;
        }

        return SecretUtil.hmacsha1(sk, url + "@" + SecretUtil.md5(body));
    }

    /**
     * 设置异常返回
     * @param response
     */
    private void returnError(HttpServletResponse response) {
        CommonUtil.setResponseReturnValue(response, 401, "{\"data\":\"身份认证错误！\"}");
    }
}
