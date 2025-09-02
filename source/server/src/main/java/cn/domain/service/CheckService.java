package cn.domain.service;

import cn.domain.entity.Check;
import cn.domain.mapper.CheckMapper;
import cn.domain.pojo.CheckQo;
import cn.domain.pojo.CheckVo;
import cn.domain.pojo.PageQo;
import cn.domain.pojo.PageVo;
import cn.domain.util.TokenUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 混合料信息服务实现类
 */
@Service
@Slf4j
public class CheckService {

    @Autowired
    private CheckMapper checkMapper;

    @Autowired
    @Lazy
    private MixtureService mixtureService;

    public PageVo<CheckVo> getCheckList(CheckQo checkQo) {
        PageHelper.startPage(checkQo.getPageNo(), checkQo.getPageSize()).setOrderBy(" f.create_time DESC ");
        Page<CheckVo> checkList = (Page<CheckVo>) checkMapper.selectByCondition(checkQo);
        return new PageVo<>(checkList);
    }

    public void check(Check check) {
        Integer userId = TokenUtil.getToken();
        check.setUserId(userId);
        checkMapper.check(check);
    }

    @Transactional
    public void process(Check check) {
        check.setAdminId(TokenUtil.getToken());
        checkMapper.process(check);
        if (check.getStatus() == 1) {
            Check info = checkMapper.selectById(check.getId());
            mixtureService.updateReturn(info.getReturnId(), check.getCorrectWeight());
        }
    }
}