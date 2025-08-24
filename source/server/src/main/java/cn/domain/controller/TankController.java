package cn.domain.controller;

import cn.domain.service.TankService;
import cn.domain.entity.Tank;
import cn.domain.pojo.PageVo;
import cn.domain.pojo.TankQo;
import cn.domain.pojo.TankVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 料罐信息管理
 */
@Slf4j
@RestController
@RequestMapping("/api/tank")
public class TankController {

    @Autowired
    private TankService tankService;

    /**
     * 获取料罐列表
     */
    @GetMapping("/list")
    public PageVo<TankVo> getTankList(TankQo tankQo) {
        return tankService.getTankList(tankQo);
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
    public void deleteTank(@RequestBody Tank tank) {
        tankService.deleteTank(tank.getId());
    }

    /**
     * 获取料罐列表
     */
    @GetMapping("/apply")
    public List<TankVo> getTanksForApply() {
        return tankService.getTanksForApply();
    }
}