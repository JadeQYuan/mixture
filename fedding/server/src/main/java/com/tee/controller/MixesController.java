package com.tee.controller;

import com.tee.entity.Mixes;
import com.tee.entity.Tank;
import com.tee.pojo.PageQo;
import com.tee.service.MixesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Mixes> getMixesList(@RequestParam(value = "userKey", required = false) String userKey,
                                    @RequestParam(value = "tankNo", required = false) String tankNo,
                                    @RequestParam(value = "shiftType", required = false) String shiftType,
                                    @RequestParam(value = "materialName", required = false) String materialName, PageQo pageQo) {
        return mixesService.getMixesList(userKey, tankNo, shiftType, materialName, pageQo);
    }

    /**
     * 获取我的料罐列表（用于加料申请）
     */
//    @GetMapping("/myTankList")
//    public List<Tank> getMyTankList() {
//        return mixesService.getMyTankList();
//    }
//
//    /**
//     * 获取可用料罐列表（用于加料管理）
//     */
//    @GetMapping("/availableTankList")
//    public List<Tank> getAvailableTankList() {
//        return mixesService.getAvailableTankList();
//    }

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
    public List<Mixes> getMixesRecordList(@RequestParam(value = "userKey", required = false) String userKey,
                                       @RequestParam(value = "tankNo", required = false) String tankNo,
                                       @RequestParam(value = "shiftType", required = false) String shiftType,
                                       @RequestParam(value = "materialName", required = false) String materialName,
                                       @RequestParam(value = "startTime", required = false) String startTime,
                                       @RequestParam(value = "endTime", required = false) String endTime,
                                       @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        return mixesService.getMixesRecordList(userKey, tankNo, shiftType, materialName, startTime, endTime, pageNo, size);
    }

    /**
     * 获取重量数据
     */
    @GetMapping("/weight")
    public Double getWeightData() {
        return mixesService.getWeightData();
    }
} 