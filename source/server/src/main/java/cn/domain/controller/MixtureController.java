package cn.domain.controller;

import cn.domain.entity.Mixture;
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

    /**
     * 获取加料管理列表
     */
    @GetMapping("/list")
    public PageVo<MixtureVo> getMixesList(MixtureQo mixtureQo) {
        return mixtureService.getMixesList(mixtureQo);
    }

    /**
     * 加料申请
     */
    @PostMapping("/apply")
    public void applyMixes(@RequestBody Mixture mixture) {
        mixtureService.applyMixes(mixture);
    }


    /**
     * 执行加料操作
     */
    @PostMapping("/feed")
    public void executeMixes(@RequestBody Mixture mixture) {
        mixtureService.executeMixes(mixture);
    }

    /**
     * 执行退料操作
     */
    @PostMapping("/return")
    public void executeReturn(@RequestBody Mixture mixture) {
        mixtureService.executeReturn(mixture);
    }

    /**
     * 获取加料记录列表
     */
    @GetMapping("/recordList")
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
     * 获取重量数据
     */
    @GetMapping("/weight")
    public Double getWeightData() {
        return mixtureService.getWeightData();
    }

    @GetMapping("/return")
    public List<Mixture> getTankForReturn() {
        return mixtureService.getTankForReturn();
    }

    @PostMapping("/remark")
    public void remark(@RequestBody Mixture mixture) {
        mixtureService.remark(mixture);
    }
} 