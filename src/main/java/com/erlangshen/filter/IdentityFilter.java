package com.erlangshen.filter;

import com.erlangshen.common.BodyReaderHttpServletRequestWrapper;
import com.erlangshen.common.Constants;
import com.erlangshen.common.HttpHelper;
import com.erlangshen.model.vo.ClientVO;
import com.erlangshen.model.vo.KeyVO;
import com.erlangshen.model.vo.TokenVO;
import com.erlangshen.service.ClientService;
import com.erlangshen.service.KeyService;
import com.erlangshen.service.TokenService;
import com.fastjavaframework.listener.SystemSetting;
import com.fastjavaframework.util.CommonUtil;
import com.fastjavaframework.util.SecretUtil;
import com.fastjavaframework.util.VerifyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录权限过滤器 token/aksk
 * @author https://github.com/shuli495/erlangshen
 */
public class IdentityFilter implements Filter {

    @Value("${token.admin}")
    private String tokenAdmin;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        try {
            if(HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            // header中base64编码的签名
            String signature = request.getHeader("signature");
            // token
            String token = request.getHeader("token");

            ServletContext sc = request.getSession().getServletContext();
            WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(sc);

            if(VerifyUtils.isNotEmpty(signature)) {
                // ak/sk认证
                this.checkKey(cxt, signature ,response, request, filterChain);
            } else {
                //token认证
                this.checkToken(cxt, token, response, request, filterChain);
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
        String url = request.getMethod() + ":" + request.getServletPath();
        if(HttpMethod.GET.toString().equalsIgnoreCase(request.getMethod())) {
            body = request.getQueryString();
        }

        return SecretUtil.hmacsha1(sk, url + "@" + SecretUtil.md5(body));
    }

    /**
     * ak/sk校验
     * @param cxt
     * @param signature
     * @param response
     * @param request
     * @param filterChain
     * @throws IOException
     */
    private void checkKey(WebApplicationContext cxt, String signature, HttpServletResponse response,
                          HttpServletRequest request, FilterChain filterChain) throws IOException, ServletException {
        // 解密后的签名
        String signatureData = SecretUtil.base64Decrypt(signature);

        KeyService keyService = (KeyService) cxt.getBean("keyService");

        String ak = signatureData.split("&")[0];
        //密文
        String dataAes = signatureData.split("&")[1];

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
            tokenVO.setAuthenticationMethod("KEY");

            request.setAttribute("identity", tokenVO);

            filterChain.doFilter(requestWrapper, response);
        } else {
            this.returnError(response);
            return;
        }
    }

    /**
     * 校验token
     * @param cxt
     * @param token
     * @param response
     * @param request
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    private void checkToken(WebApplicationContext cxt, String token, HttpServletResponse response,
                            HttpServletRequest request, FilterChain filterChain) throws IOException, ServletException {
        String clientIp = "";
        TokenVO tokenVO = null;

        if(VerifyUtils.isEmpty(token)) {
            // admin token只能用于以下操作
            String requestUri = request.getMethod() + ":" + request.getRequestURI();

            if(!SystemSetting.authorityByRole().get(Constants.ADMIN_TOKEN).contains(requestUri) &&
                    !SystemSetting.authorityByRole().get("").contains(requestUri + "/")) {
                this.returnError(response);
                return;
            }

            tokenVO = new TokenVO();
            tokenVO.setId("erlangshen");
            tokenVO.setUserId("erlangshen");
            tokenVO.setClientId("erlangshen");
        } else {
            clientIp = CommonUtil.getIp(request);

            TokenService tokenService = (TokenService) cxt.getBean("tokenService");

            // 检查token
            tokenVO = tokenService.check(token, clientIp);
        }

        // 设置身份认证信息
        tokenVO.setAuthenticationMethod("TOKEN");
        request.setAttribute("identity", tokenVO);

        filterChain.doFilter(request, response);
    }

    /**
     * 设置异常返回
     * @param response
     */
    private void returnError(HttpServletResponse response) {
        CommonUtil.setResponseReturnValue(response, 401, "身份认证错误！");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
