package com.tee.controller;

import com.tee.entity.Tank;
import com.tee.pojo.PageQo;
import com.tee.pojo.PageVo;
import com.tee.service.TankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 料罐信息管理
 */
@Slf4j
@RestController
@RequestMapping("/service/tank")
public class TankController {

    @Autowired
    private TankService tankService;

    /**
     * 获取料罐列表
     */
    @GetMapping("/list")
    public PageVo<Tank> getTankList(String tankNo, PageQo pageQo) {
        return tankService.getTankList(tankNo, pageQo);
    }

    /**
     * 新增料罐
     */
    @PostMapping("/add")
    public void addTank(@RequestBody Tank tank) {
        tankService.addTank(tank);
    }

    /**
     * 更新料罐信息
     */
    @PutMapping("/update")
    public void updateTank(@RequestBody Tank tank) {
        tankService.updateTank(tank);
    }

    /**
     * 删除料罐
     */
    @DeleteMapping("/delete")
    public void deleteTank(@RequestParam Integer id) {
        tankService.deleteTank(id);
    }

    /**
     * 获取料罐列表
     */
    @GetMapping("/apply")
    public List<Tank> getTanksForApply() {
        return tankService.getTanksForApply();
    }
}