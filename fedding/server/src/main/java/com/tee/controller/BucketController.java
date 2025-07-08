package com.tee.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tee.pojo.qo.BucketQo;
import com.tee.pojo.vo.Bucket;
import com.tee.pojo.vo.User;
import com.tee.service.BucketService;
import com.tee.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 料罐信息管理
 *
 */
@Slf4j
@RestController
@RequestMapping("/service/bucket")
public class BucketController {

    @Autowired
    private BucketService bucketService;

    @Value("${user.default.password}")
    private String defaultPassword;


    /**
     * 加料管理列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/bucketList")
    public Result bucketList(@RequestParam(value = "bucketNo", required = false) int bucketNo, @RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "size", defaultValue = "10") int size) {
        BucketQo bucketQo = new BucketQo();
        bucketQo.setBucketNo(bucketNo);
        bucketQo.setType("add");
        PageHelper.startPage(pageNo, size);
        List<Bucket> bucketInfo = bucketService.getBucketApplyLog(bucketQo);
        if (CollectionUtils.isEmpty(bucketInfo)) {
            return Result.success();
        }

        PageInfo<User> pageInfo = new PageInfo(bucketInfo);
        return Result.success(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }



    /**
     * 退料管理列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/delBucketList")
    public Result delBucketList(@RequestParam(value = "bucketNo", required = false) int bucketNo, @RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "size", defaultValue = "10") int size) {
        BucketQo bucketQo = new BucketQo();
        bucketQo.setBucketNo(bucketNo);
        bucketQo.setType("del");
        PageHelper.startPage(pageNo, size);
        List<Bucket> bucketInfo = bucketService.getBucketApplyLog(bucketQo);
        if (CollectionUtils.isEmpty(bucketInfo)) {
            return Result.success();
        }

        PageInfo<User> pageInfo = new PageInfo(bucketInfo);
        return Result.success(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    /**
     * 申请加料
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/bucketApplyAdd")
    public Result bucketApplyAdd(@RequestBody BucketQo bucketQo) {
        String userId = bucketQo.getUserId();
        int bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(userId) || bucketNo <= 0) {
            Result.error("输入参数有误");
        }
        bucketQo.setType("add");
        // 增加校验当天申请一次，待优化
        bucketService.insertBucketApply(bucketQo);
        return Result.success();
    }

    /**
     * 退料申请
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/bucketApplyDel")
    public Result bucketApplyDel(@RequestBody BucketQo bucketQo) {
        String userId = bucketQo.getUserId();
        if (StringUtils.isEmpty(userId)) {
            Result.error("输入参数有误");
        }
        bucketQo.setType("del");
        // 增加校验当天申请一次，待优化
        bucketService.insertBucketApply(bucketQo);
        return Result.success();
    }

    /**
     * 加料
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/bucketAdd")
    public Result bucketAdd(@RequestBody BucketQo bucketQo) {
        String userId = bucketQo.getUserId();
        BigDecimal capacity = bucketQo.getCapacity();
        BigDecimal capacityAdd = bucketQo.getCapacityAdd();
        BigDecimal abs = bucketQo.getAbs();
        int bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(userId)|| bucketNo <= 0 || capacity == null || capacityAdd == null || abs == null) {
            Result.error("输入参数有误");
        }
        capacity = capacity.add(capacityAdd);
        bucketQo.setCapacity(capacity);
        bucketService.updateBucketNum(bucketQo); // 加料
        bucketQo.setStatus("1");
        bucketService.updateBucketApply(bucketQo); // 修改加料记录状态
        bucketService.insertBucketOperateLog(bucketQo); // 加料日志
        return Result.success();
    }

    /**
     * 退料
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/bucketDel")
    public Result bucketDel(@RequestBody BucketQo bucketQo) {
        String userId = bucketQo.getUserId();
        BigDecimal capacity = bucketQo.getCapacity();
        int bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(userId) || capacity == null || bucketNo <= 0) {
            Result.error("输入参数有误");
        }

        bucketQo.setCapacity(BigDecimal.ZERO);
        bucketService.updateBucketNum(bucketQo);
        bucketQo.setStatus("1");
        bucketService.updateBucketApply(bucketQo);
        return Result.success();
    }

    /**
     * 加料记录
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/bucketLogList")
    public Result bucketLogList(@RequestParam(value = "bucketNo", required = false) int bucketNo,
                                @RequestParam(value = "userName", required = false) String userName,
                                @RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "size", defaultValue = "10") int size) {

        BucketQo bucketQo = new BucketQo();
        bucketQo.setBucketNo(bucketNo);
        bucketQo.setUserName(userName);
        PageHelper.startPage(pageNo, size);
        List<Bucket> bucketInfo = bucketService.getBucketLog(bucketQo);
        if (CollectionUtils.isEmpty(bucketInfo)) {
            return Result.success();
        }

        PageInfo<User> pageInfo = new PageInfo(bucketInfo);
        return Result.success(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    /**
     * 料罐管理list
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/addBucketList")
    public Result addBucketList(@RequestParam(value = "bucketNo", required = false) int bucketNo, @RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "size", defaultValue = "10") int size) {

        BucketQo bucketQo = new BucketQo();
        bucketQo.setBucketNo(bucketNo);
        PageHelper.startPage(pageNo, size);
        List<Bucket> bucketInfo = bucketService.getBucketInfo(bucketQo);
        if (CollectionUtils.isEmpty(bucketInfo)) {
            return Result.success();
        }

        PageInfo<User> pageInfo = new PageInfo(bucketInfo);
        return Result.success(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    /**
     * 新增料罐
     *
     * @param bucketQo
     * @return
     */
    @PostMapping("/addBucket")
    public Result addBucket(@RequestBody BucketQo bucketQo) {
        String userId = bucketQo.getUserId();
        BigDecimal capacity = bucketQo.getCapacity();
        int bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(userId) || capacity == null || bucketNo <= 0) {
            Result.error("输入参数有误");
        }

        bucketQo.setCapacity(BigDecimal.ZERO);
        bucketQo.setAbs(BigDecimal.ZERO);
        bucketService.insertBucket(bucketQo);
        return Result.success();
    }


    /**
     * 修改料罐
     *
     * @param bucketQo
     * @return
     */
    @PutMapping("/updateBucket")
    public Result updateBucket(@RequestBody BucketQo bucketQo) {
        String id = bucketQo.getId();
        String userId = bucketQo.getUserId();
        BigDecimal capacity = bucketQo.getCapacity();
        int bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(userId) || capacity == null || bucketNo <= 0) {
            Result.error("输入参数有误");
        }

        bucketService.updateBucketInfo(bucketQo);
        return Result.success();
    }

    /**
     * 删除料罐
     *
     * @param bucketQo
     * @return
     */
    @DeleteMapping("/deleteBucket")
    public Result deleteBucket(@RequestBody BucketQo bucketQo) {
        String id = bucketQo.getId();
        int bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(id) || bucketNo <= 0) {
            Result.error("输入参数有误");
        }

        bucketService.deleteBucket(bucketQo);
        return Result.success();
    }



}
