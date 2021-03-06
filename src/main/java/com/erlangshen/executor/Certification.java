package com.erlangshen.executor;

import com.baidu.aip.ocr.AipOcr;
import com.erlangshen.common.Constants;
import com.erlangshen.model.vo.UserVO;
import com.erlangshen.service.UserInfoService;
import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.util.VerifyUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * 实名认证
 * @author https://github.com/shuli495/erlangshen
 */
public class Certification extends AbstractQpsControl {

    private String userId;

    /**
     * 姓名 不传取user表中的name
     */
    private String name;

    /**
     * 身份证号 不传取user表中的idcard
     */
    private String idcard;

    @Value("${path.idcard}")
    private String pathIdcard;

    @Value("${bdyun.ak}")
    private String bdyunAk;

    @Value("${bdyun.sk}")
    private String bdyunSk;

    @Value("${bdyun.appid}")
    private String bdyunAppid;

    @Value("${bdyun.idcard.qps.value}")
    private Integer bdyunIdcardQps;

    public Certification(String userId, String name, String idcard) {
        super("bdyun");

        this.userId = userId;
        this.name = name;
        this.idcard = idcard;

        // 根据qps规则执行
        super.exe();
    }

    @Override
    public void run() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:config/applicationContext.xml");
        UserInfoService userInfoService = (UserInfoService) ctx.getBean("userInfoService");

        try {
            if(VerifyUtils.isEmpty(userId)) {
                throw new Exception("userId不能为空！");
            }

            UserVO user = userInfoService.baseFind(userId);

            if(null == user) {
                throw new Exception(userId+" 该用户不存在！");
            }

            // 认证中、认证成功
            if(user.getCertification() == Constants.USER_CERTIFICATION_ING || user.getCertification() == Constants.USER_CERTIFICATION_SUCCESS) {
                return;
            }

            // 不传name、idcard取user表中数据
            if(VerifyUtils.isEmpty(name)) {
                name = user.getName();
            }
            if(VerifyUtils.isEmpty(idcard)) {
                idcard = user.getIdcard();
            }

            if(VerifyUtils.isEmpty(name)) {
                throw new Exception("请设置姓名！");
            }
            if(VerifyUtils.isEmpty(idcard)) {
                throw new Exception("请设置身份证号码！");
            }

            // 身份证反正面图片文件
            StringBuilder path = new StringBuilder(
                    this.getClass().getClassLoader().getResource("").getPath()
                        .replaceAll("/", Matcher.quoteReplacement(File.separator))
                        .replace("WEB-INF"+File.separator+"classes"+File.separator, "")
                    )
                    .append(pathIdcard.replaceAll("\\\\", Matcher.quoteReplacement(File.separator)))
                    .append(userId);
            File front = new File(path.toString() + "_0.jpg");
            File back = new File(path.toString() + "_1.jpg");

            if(!front.exists() || !back.exists()) {
                throw new Exception("未上传身份证！");
            }

            // 身份证文件大小限制4M
            if(front.length() > Constants.IDCARD_UP_MAX || back.length() > Constants.IDCARD_UP_MAX ) {
                throw new Exception("身份证图片必须小于4M！");
            }

            // 百度云client
            AipOcr client = new AipOcr(bdyunAppid, bdyunAk, bdyunSk);

            // 根据qps间隔调用
            if(VerifyUtils.isNotEmpty(bdyunIdcardQps)) {
                bdyunIdcardQps = 0;
            }
            while (bdyunIdcardQps > 0 && super.getQpsSwitch().compareAndSet(true, true)) {
                Thread.sleep(1000/bdyunIdcardQps/2);
            }

            // 识别身份证反正面数据
            JSONObject forntRes = null;
            try{
                forntRes = this.idcardApi(client, userId, front.getPath(), "front");
            } catch (Exception e) {
                super.getIndex().decrementAndGet();
                throw new ThrowException(e.getMessage());
            }
            JSONObject backRes = null;
            try {
                backRes = this.idcardApi(client, userId, back.getPath(), "back");
            } catch (Exception e) {
                super.getIndex().decrementAndGet();
                throw new ThrowException(e.getMessage());
            }

            // 调用完成，关闭开关
            super.getQpsSwitch().set(false);

            // 判断有效期
            String befores = backRes.getJSONObject("签发日期").getString("words");
            String afters = backRes.getJSONObject("失效日期").getString("words");

            Calendar now = Calendar.getInstance();
            Calendar before = Calendar.getInstance();
            before.set(Integer.valueOf(befores.substring(0, 4)), Integer.valueOf(befores.substring(4, 6)), Integer.valueOf(befores.substring(6)));
            Calendar after = Calendar.getInstance();
            after.set(Integer.valueOf(afters.substring(0, 4)), Integer.valueOf(afters.substring(4, 6)), Integer.valueOf(afters.substring(6)));

            // 身份证失效
            if(now.after(after) || now.before(before)) {
                throw new Exception("身份证已过期！");
            }

            // 姓名、号码与图片相同，认证成功
            if(user.getName().equals(forntRes.getJSONObject(Constants.BAIDUYUN_OCR_KEY_NAME).getString(Constants.BAIDUYUN_OCR_KEY_WORDS))
                    && user.getIdcard().equals(forntRes.getJSONObject(Constants.BAIDUYUN_OCR_KEY_IDCARD).getString(Constants.BAIDUYUN_OCR_KEY_WORDS))) {
                UserVO updateUser = new UserVO();
                updateUser.setId(user.getId());
                updateUser.setName(name);
                updateUser.setIdcard(idcard);
                updateUser.setCertification(3);
                updateUser.setCertificationFailMsg("");
                userInfoService.baseUpdate(updateUser);
            } else {
                // 认证失败
                throw new Exception("身份信息错误！");
            }
        } catch (Exception e) {
            // 认证失败回写错误信息
            UserVO updateUser = new UserVO();
            updateUser.setId(userId);
            updateUser.setCertification(2);
            updateUser.setCertificationFailMsg(e.getMessage());
            userInfoService.baseUpdate(updateUser);
            throw new ThrowException(e.getMessage(), "811001");
        }
    }

    /**
     * 百度云身份证识别接口
     * @param client
     * @param userId
     * @param imagePath
     * @param type
     * @return
     */
    private JSONObject idcardApi(AipOcr client, String userId, String imagePath, String type) throws Exception {

        HashMap<String, String> options = new HashMap<String, String>(2);
        options.put("detect_direction", "false");
        options.put("detect_risk", "true");

        JSONObject res = client.idcard(imagePath, type, options);

        if(res.has(Constants.BAIDUYUN_OCR_KEY_EDIT_TOOL)) {
            throw new Exception("身份证被编辑软件编辑过，请上传原版身份证！");
        }

        switch (res.getString("image_status")) {
            case "reversed_side": throw new Exception("未摆正身份证！");
            case "non_idcard": throw new Exception("上传的图片中不包含身份证！");
            case "blurred": throw new Exception("身份证模糊！");
            case "over_exposure": throw new Exception("身份证关键字段反光或过曝！");
            case "unknown": throw new Exception("实名认证失败，请重新上传身份证！");
            default:
        }

        return res.getJSONObject("words_result");
    }
}
