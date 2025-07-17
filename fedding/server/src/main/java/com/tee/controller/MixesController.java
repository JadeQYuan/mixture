package com.tee.controller;

import com.tee.entity.Mixes;
import com.tee.pojo.MixesQo;
import com.tee.pojo.MixesVo;
import com.tee.pojo.PageVo;
import com.tee.service.MixesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 加料信息管理
 */
@Slf4j
@RestController
@RequestMapping("/service/mixes")
public class MixesController {

    @Autowired
    private MixesService mixesService;

    /**
     * 获取加料管理列表
     */
    @GetMapping("/list")
    public PageVo<MixesVo> getMixesList(MixesQo mixesQo) {
        return mixesService.getMixesList(mixesQo);
    }

    /**
     * 加料申请
     */
    @PostMapping("/apply")
    public void applyMixes(@RequestBody Mixes mixes) {
        mixesService.applyMixes(mixes);
    }


    /**
     * 执行加料操作
     */
    @PostMapping("/feed")
    public void executeMixes(@RequestBody Mixes mixes) {
        mixesService.executeMixes(mixes);
    }

    /**
     * 执行退料操作
     */
    @PostMapping("/return")
    public void executeReturn(@RequestBody Mixes mixes) {
        mixesService.executeReturn(mixes);
    }

    /**
     * 获取加料记录列表
     */
    @GetMapping("/recordList")
    public PageVo<MixesVo> getMixesRecordList(MixesQo mixesQo) {
        return mixesService.getMixesRecordList(mixesQo);
    }

    /**
     * 获取加料统计列表
     */
    @GetMapping("/stats")
    public PageVo<MixesVo> getMixesStatsList(MixesQo mixesQo) {
        return mixesService.getMixesStatsList(mixesQo);
    }

    /**
     * 获取重量数据
     */
    @GetMapping("/weight")
    public Double getWeightData() {
        return mixesService.getWeightData();
    }

    @GetMapping("/return")
    public List<Mixes> getTankForReturn() {
        return mixesService.getTankForReturn();
    }

    @PostMapping("/remark")
    public void remark(@RequestBody Mixes mixes) {
        mixesService.remark(mixes);
    }
} 