package com.website.controller;

import com.fastjavaframework.page.Page;
import com.website.common.BaseElsController;
import com.website.common.Constants;
import com.website.model.vo.LoginLogVO;
import com.website.service.LoginLogService;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志
 * @author https://github.com/shuli495/erlangshen
 */
@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_LOGIN_LOG)
public class LoginLogController extends BaseElsController<LoginLogService> {


    /**
     * 列表查询 and条件
     */
    @RequestMapping(method= RequestMethod.GET)
    public Object query(@RequestParam(required = false) Integer pageSize, @RequestParam(required = false) Integer pageNum,
                        @RequestParam(required = false) String orderBy, @RequestParam(required = false) String orderSort,
                        @RequestParam(required = false) String username, @RequestParam(required = false) String nickname,
                        @RequestParam(required = false) String mail, @RequestParam(required = false) String phone,
                        @RequestParam(required = false) String clientId) {
        LoginLogVO vo = new LoginLogVO();
        vo.setCreatedBy(super.identity().getUserId());

        if(null != username) {
            vo.setUsername(username);
        }
        if(null != nickname) {
            vo.setNickname(nickname);
        }
        if(null != mail) {
            vo.setMail(mail);
        }
        if(null != phone) {
            vo.setPhone(phone);
        }
        if(null != clientId) {
            vo.setClientId(clientId);
        }

        // 排序
        if(null != orderBy) {
            vo.setOrderBy(orderBy);
        }
        if(null != orderSort) {
            vo.setOrderSort(orderSort);
        }

        if(null != pageSize && null != pageNum && pageSize != 0 && pageNum != 0) {	//分页查询
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setPageNum(pageNum);
            vo.setPage(page);

            return success(this.service.baseQueryPageByAnd(vo));
        } else {	//列表查询
            return success(this.service.baseQueryByAnd(vo));
        }
    }
}
