package com.tee.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tee.pojo.qo.BucketQo;
import com.tee.pojo.vo.Bucket;
import com.tee.pojo.vo.User;
import com.tee.service.BucketService;
import com.tee.service.UserService;
import com.tee.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private UserService userService;

    @Value("${user.default.password}")
    private String defaultPassword;


    /**
     * 加料管理列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/bucketList")
    public Result bucketList(@RequestParam(value = "bucketNo", required = false) String bucketNo, @RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "size", defaultValue = "10") int size) {
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
    public Result delBucketList(@RequestParam(value = "bucketNo", required = false) String bucketNo, @RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "size", defaultValue = "10") int size) {
        BucketQo bucketQo = new BucketQo();
        bucketQo.setBucketNo(bucketNo);
        bucketQo.setType("del");
        PageHelper.startPage(pageNo, size);
        List<Bucket> bucketInfo = bucketService.getBucketApplyLog(bucketQo);
        if (CollectionUtils.isEmpty(bucketInfo)) {
            return Result.success();
        }

        PageInfo<Bucket> pageInfo = new PageInfo(bucketInfo);
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
        String bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(bucketNo)) {
            Result.error("料罐号不能为空！");
        }
        bucketQo.setType("add");
        List<Bucket> bucketInfo = bucketService.getBucketApplyLog(bucketQo);
        if (!CollectionUtils.isEmpty(bucketInfo)) {
            Result.error("改料罐已经申请加料，请先处理上一笔申请！");
        }
        bucketQo.setType("add");
        bucketQo.setUserId(userService.getCurrentUser().getUserId());
        // 增加校验当天申请一次，待优化
        bucketService.insertBucketApply(bucketQo);
        // 修改当前料罐责任人为申请人
        bucketService.updateBucketNum(bucketQo);
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
        String bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(bucketNo)) {
            Result.error("料罐号不能为空！");
        }
        bucketQo.setType("del");
        List<Bucket> bucketInfo = bucketService.getBucketApplyLog(bucketQo);
        if (!CollectionUtils.isEmpty(bucketInfo)) {
            Result.error("改料罐已经申请退料，请先处理上一笔申请！");
        }

        bucketQo.setType("del");
        bucketQo.setUserId(userService.getCurrentUser().getUserId());
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
        BigDecimal capacity = bucketQo.getCapacity();
        BigDecimal capacityAdd = bucketQo.getCapacityAdd();
        BigDecimal abs = bucketQo.getAbs();
        String bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(bucketNo) || capacity == null || capacityAdd == null || abs == null) {
            Result.error("输入参数有误");
        }
        List<Bucket> bucketInfo = bucketService.getBucketApplyLog(bucketQo);
        if (CollectionUtils.isEmpty(bucketInfo)) {
            Result.error("加料申请不存在！");
        }
        Bucket bucket = bucketInfo.get(0);
        String status = bucket.getStatus();
        if ("1".equals(status)) {
            Result.error("加料申请已处理！");
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
        String bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(userId) || capacity == null || StringUtils.isEmpty(bucketNo)) {
            Result.error("输入参数有误");
        }
        List<Bucket> bucketInfo = bucketService.getBucketApplyLog(bucketQo);
        if (CollectionUtils.isEmpty(bucketInfo)) {
            Result.error("退料申请不存在！");
        }
        Bucket bucket = bucketInfo.get(0);
        String status = bucket.getStatus();
        if ("1".equals(status)) {
            Result.error("退料申请已处理！");
        }

        bucketQo.setUserId("EMPTY");
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
    public Result bucketLogList(@RequestParam(value = "bucketNo", required = false) String bucketNo,
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

        PageInfo<Bucket> pageInfo = new PageInfo(bucketInfo);
        return Result.success(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    /**
     * 料罐管理list
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/addBucketList")
    public Result addBucketList(String bucketNo, @RequestParam(defaultValue = "1") int pageNo, @RequestParam(defaultValue = "10") int size) {

        BucketQo bucketQo = new BucketQo();
        bucketQo.setBucketNo(bucketNo);
        PageHelper.startPage(pageNo, size);
        List<Bucket> bucketInfo = bucketService.getBucketInfo(bucketQo);
        if (CollectionUtils.isEmpty(bucketInfo)) {
            return Result.success();
        }

        PageInfo<Bucket> pageInfo = new PageInfo(bucketInfo);
        return Result.success(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    /**
     * 在自己名下的料罐列表
     *
     * @return
     */
    @GetMapping("/myBucketList")
    public Result myBucketList() {
        BucketQo bucketQo = new BucketQo();
        bucketQo.setUserId(userService.getCurrentUser().getUserId());
        List<Bucket> bucketInfo = bucketService.getBucketInfo(bucketQo);
        if (CollectionUtils.isEmpty(bucketInfo)) {
            return Result.success();
        }
        List<String> bucketList = bucketInfo.stream().map(Bucket::getBucketNo).collect(Collectors.toList());
        return Result.success(bucketList);
    }

    /**
     * 可申请料罐列表
     *
     * @return
     */
    @GetMapping("/bucketAvailableList")
    public Result bucketAvailableList() {
        BucketQo bucketQo = new BucketQo();
        bucketQo.setUserId("EMPTY");
        List<Bucket> bucketInfo = bucketService.getBucketInfo(bucketQo);
        if (CollectionUtils.isEmpty(bucketInfo)) {
            return Result.success();
        }
        List<String> bucketList = bucketInfo.stream().map(Bucket::getBucketNo).collect(Collectors.toList());
        return Result.success(bucketList);
    }

    /**
     * 称重
     *
     * @return
     */
    @GetMapping("/weigh")
    public Result weigh() {


        return Result.success(Math.random());
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
        String bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(userId) || capacity == null || StringUtils.isEmpty(bucketNo)) {
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
        String bucketNo = bucketQo.getBucketNo();
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(userId) || capacity == null || StringUtils.isEmpty(bucketNo)) {
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
        if (StringUtils.isEmpty(id)) {
            Result.error("输入参数有误");
        }

        bucketService.deleteBucket(bucketQo);
        return Result.success();
    }



}
