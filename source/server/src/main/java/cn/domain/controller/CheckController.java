package cn.domain.controller;

import cn.domain.entity.Check;
import cn.domain.pojo.CheckVo;
import cn.domain.pojo.PageQo;
import cn.domain.pojo.PageVo;
import cn.domain.service.CheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 校验信息管理
 */
@Slf4j
@RestController
@RequestMapping("/api/check")
public class CheckController {

    @Autowired
    private CheckService checkService;

    @GetMapping("/list")
    public PageVo<CheckVo> getCheckList(PageQo pageQo) {
        return checkService.getCheckList(pageQo);
    }

    /**
     * 处理
     */
    @PutMapping("/process")
    public void process(@RequestBody Check check) {
        checkService.process(check);
    }
}