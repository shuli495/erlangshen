package com.website.service;

import com.fastjavaframework.Setting;
import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.page.OrderSort;
import com.fastjavaframework.util.CodeUtil;
import com.fastjavaframework.util.VerifyUtils;
import com.website.model.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.ValidateDao;
import com.website.model.vo.ValidateVO;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 邮件、短信等验证码验证
 * @author https://github.com/shuli495/erlangshen
 */
@Service
public class ValidateService extends BaseService<ValidateDao,ValidateVO> {

    @Autowired
    private UserService userService;

    /**
     * 获取防机器人验证码
     * @param type lgoin register
     * @param tokenClientId
     * @param loginIp
     * @return base64图片
     */
    public String verifyCode(String type, String tokenClientId, String loginIp) {
        Map<String, Object> map = CodeUtil.codeImg();

        // code入库
        this.insert(tokenClientId + "_" + loginIp, type, map.get("code").toString());

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write((BufferedImage)map.get("image"), "jpg", baos);
            byte[] bytes = baos.toByteArray();

            BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            return encoder.encodeBuffer(bytes).trim();
        } catch (Exception e) {
            throw new ThrowException("验证码转base64错误：" + e.getMessage(), "122009");
        }
    }

    /**
     * 不存在则新增，存在修改
     * @param userId
     * @param type
     * @param code
     */
    public void insert(String userId, String type, String code) {
        ValidateVO validateVO = new ValidateVO();
        validateVO.setUserId(userId);
        validateVO.setType(type);
        List<ValidateVO> validates = this.dao.baseQueryByAnd(validateVO);

        validateVO.setCode(code);
        validateVO.setCreatedTime(new Date());
        if(validates.size() > 0) {
            this.dao.baseUpdate(validateVO);
        } else {
            this.dao.baseInsert(validateVO);
        }
    }

    /**
     * 删除
     * @param userId
     * @param type
     * @param code
     */
    public void delete(String userId, String type, String code) {
        ValidateVO validateVO = new ValidateVO();
        if(VerifyUtils.isEmpty(userId)) {
            throw new ThrowException("userId必传！", "152006");
        }
        validateVO.setUserId(userId);

        if(VerifyUtils.isNotEmpty(type)) {
            validateVO.setType(type);
        }
        if(VerifyUtils.isNotEmpty(code)) {
            validateVO.setCode(code);
        }

        this.dao.delete(validateVO);
    }

    /**
     * 根据userId校验code是否有效
     * @param userId
     * @param type
     * @param code
     */
    public ValidateVO checkByUserId(String userId, String type, String code) {
        if(VerifyUtils.isEmpty(userId) || VerifyUtils.isEmpty(code)) {
            throw new ThrowException("认证信息不全！", "152001");
        }
        return this.queryAndCheck(userId, type, code).get(0);
    }

    /**
     * 根据mail/phone校验code是否有效
     * @param clientId mail所属clientId
     * @param mail
     * @param phone
     * @param code
     * @param type
     * @return ValidateVO
     */
    public ValidateVO checkByMailOrPhone(String clientId, String mail, String phone, String code, String type) {
        UserVO queryUserVO = new UserVO();
        queryUserVO.setClientId(clientId);
        if(VerifyUtils.isNotEmpty(phone)) {
            queryUserVO.setPhone(phone);
        } else {
            queryUserVO.setMail(mail);
        }
        List<UserVO> users = userService.baseQueryByAnd(queryUserVO);

        String objId = clientId + "_" + mail;
        if(users.size() > 0) {
            objId = users.get(0).getId();
        }

        return this.checkByUserId(objId, type, code);
    }

    /**
     * 查询验证码并验证是否有效
     * @param ObjectId  验证码所属对象
     * @param type      验证类型
     * @param code      验证码
     * @return List<ValidateVO>
     */
    public List<ValidateVO> queryAndCheck(String objectId, String type, String code)  {
        if(VerifyUtils.isEmpty(objectId) && VerifyUtils.isEmpty(type) && VerifyUtils.isEmpty(code)) {
            throw new ThrowException("认证信息不全！", "152002");
        }

        // 根据code查询认证表数据
        ValidateVO validateVO = new ValidateVO();
        if(!VerifyUtils.isEmpty(objectId)) {
            validateVO.setUserId(objectId);
        }
        if(!VerifyUtils.isEmpty(type)) {
            validateVO.setType(type);
        }
        if(!VerifyUtils.isEmpty(code)) {
            validateVO.setCode(code);
        }
        List<ValidateVO> validates = super.baseQueryByAnd(validateVO);

        // 根据code未查到认证数据
        if(null == validates || validates.size() == 0) {
            throw new ThrowPrompt("验证码不正确！", "152003");
        } else {
            // 认证信息是否过期
            for(ValidateVO validate : validates) {
                this.checkOvertime(type, validate.getCreatedTime());
            }
            return validates;
        }
    }

    /**
     * 校验验证码是否在有效期内
     * @param type  验证类型
     * @param validateCreateTime 验证码创建时间
     */
    public void checkOvertime(String type, Date validateCreateTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(validateCreateTime);

        cal.add(Calendar.SECOND, +Integer.parseInt(Setting.getProperty("validate.code.active.time")));

        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        if (cal.before(now)) {
            throw new ThrowPrompt("认证信息超时，请重新认证！", "152004");
        }
    }

    /**
     * 操作间隔
     * @param objectId
     * @param type
     */
    public void canReSend(String objectId, String type) {
        // 根据code查询认证表数据
        ValidateVO validateVO = new ValidateVO();
        if(!VerifyUtils.isEmpty(objectId)) {
            validateVO.setUserId(objectId);
        }
        if(!VerifyUtils.isEmpty(type)) {
            validateVO.setType(type);
        }
        validateVO.setOrderBy("created_time");
        validateVO.setOrderSort(OrderSort.DESC);

        List<ValidateVO> validates = super.baseQueryByAnd(validateVO);

        if(validates.size() == 0) {
            return;
        }

        Date createdTime = validates.get(0).getCreatedTime();

        int re = Integer.parseInt(Setting.getProperty("validate.code.reSend.time"))
                - (int)((System.currentTimeMillis() - createdTime.getTime()) / 1000);

        if(re > 0) {
            throw new ThrowPrompt(re + "秒后再次操作！", "152005");
        }
    }

}