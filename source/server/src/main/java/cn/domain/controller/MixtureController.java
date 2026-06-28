package cn.domain.controller;

import cn.domain.config.AppConfig;
import cn.domain.entity.Mixture;
import cn.domain.pojo.MixtureBottomVo;
import cn.domain.service.MixtureService;
import cn.domain.pojo.MixtureQo;
import cn.domain.pojo.MixtureVo;
import cn.domain.pojo.PageVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 混合料信息管理
 */
@Slf4j
@RestController
@RequestMapping("/api/mixture")
public class MixtureController {

    @Autowired
    private MixtureService mixtureService;

    @Autowired
    private AppConfig appConfig;

    @GetMapping("/todo")
    public List<MixtureVo> getTodoList(MixtureQo mixtureQo) {
        return mixtureService.getTodoList(mixtureQo);
    }

    /**
     * 获取加料管理列表
     */
    @GetMapping("/list")
    public PageVo<MixtureVo> getMixesList(MixtureQo mixtureQo) {
        return mixtureService.getMixesList(mixtureQo);
    }

    /**
     * 获取加料记录列表
     */
    @GetMapping("/record")
    public PageVo<MixtureVo> getMixesRecordList(MixtureQo mixtureQo) {
        return mixtureService.getMixesRecordList(mixtureQo);
    }

    /**
     * 获取加料统计列表
     */
    @GetMapping("/stats")
    public PageVo<MixtureVo> getMixesStatsList(MixtureQo mixtureQo) {
        return mixtureService.getMixesStatsList(mixtureQo);
    }

    /**
     * 加料申请
     */
    @PostMapping("/apply")
    public void apply(@RequestBody Mixture mixture) {
        mixtureService.apply(mixture);
    }

    /**
     * 执行备料操作
     */
    @PostMapping("/prepare")
    public void prepare(@RequestBody Mixture mixture) {
        mixtureService.prepare(mixture);
    }

    /**
     * 底罐重量
     * @return
     */
    @GetMapping("/bottom")
    public Mixture getLastReturnWeight(Integer tankId) {
        return mixtureService.getLastReturnWeight(tankId);
    }

    /**
     * 底罐重量
     */
    @PostMapping("/bottom")
    public void bottom(@RequestBody MixtureBottomVo bottomVo) {
        mixtureService.bottom(bottomVo);
    }

    /**
     * 执行加料操作
     */
    @PostMapping("/feed")
    public void executeFeed(@RequestBody Mixture mixture) {
        mixtureService.executeFeed(mixture);
    }

    /**
     * 获取可领料列表
     */
    @GetMapping("/picking")
    public List<MixtureVo> getTankForPicking() {
        return mixtureService.getTankForPicking();
    }

    /**
     * 执行领料操作
     */
    @PostMapping("/picking")
    public void picking(@RequestBody Mixture mixture) {
        mixtureService.picking(mixture);
    }

    @GetMapping("/return")
    public List<Mixture> getTankForReturn() {
        return mixtureService.getTankForReturn();
    }

    /**
     * 执行退料操作
     */
    @PostMapping("/return")
    public void executeReturn(@RequestBody Mixture mixture) {
        mixtureService.executeReturn(mixture);
    }

    /**
     * 执行取消操作
     */
    @PostMapping("/cancel")
    public void executeCancel(@RequestBody Mixture mixture) {
        mixtureService.executeCancel(mixture);
    }

    @PostMapping("/remark")
    public void remark(@RequestBody Mixture mixture) {
        mixtureService.remark(mixture);
    }

    /**
     * 获取重量数据
     */
    @GetMapping("/weight")
    public Double getWeightData() {
        return mixtureService.getWeightData();
    }

    /**
     * 加料阈值
     * @return
     */
    @GetMapping("/feedThreshold")
    public Double feedThreshold() {
        return appConfig.getFeedThreshold();
    }

    /**
     * 罐底阈值
     */
    @GetMapping("/bottomThreshold")
    public Double bottomThreshold() {
        return appConfig.getBottomThreshold();
    }

    /**
     * 领料配置（合并接口）
     */
    @GetMapping("/pickingConfig")
    public java.util.Map<String, Object> pickingConfig() {
        java.util.Map<String, Object> config = new java.util.HashMap<>();
        config.put("bottomThreshold", appConfig.getPickingBottomThreshold());
        config.put("flameRetardantRatio", appConfig.getFlameRetardantRatio());
        config.put("flameRetardantRatioMin", appConfig.getFlameRetardantRatioMin());
        config.put("flameRetardantRatioMax", appConfig.getFlameRetardantRatioMax());
        return config;
    }
}