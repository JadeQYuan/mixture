package com.tee.service;

import com.github.pagehelper.PageHelper;
import com.tee.entity.Mixes;
import com.tee.entity.Tank;
import com.tee.entity.User;
import com.tee.exception.AppException;
import com.tee.mapper.MixesMapper;
import com.tee.pojo.PageQo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 加料信息服务实现类
 */
@Service
public class MixesService {

    @Autowired
    private MixesMapper feedingMapper;

    @Autowired
    private TankService tankService;

    public List<Mixes> getMixesList(String userKey, String tankNo, String shiftType, String materialName, PageQo pageQo) {
        PageHelper.startPage(pageQo.getPageNo(), pageQo.getPageSize());
        List<Mixes> mixesList = getMixesList(userKey, tankNo, shiftType, materialName);
        return mixesList;
    }

    public void applyMixes(Mixes mixes) {
        if (org.springframework.util.StringUtils.isEmpty(mixes.getTankNo())) {
            throw new AppException("料罐编号不能为空");
        }
        if (org.springframework.util.StringUtils.isEmpty(mixes.getShiftType())) {
            throw new AppException("班次不能为空");
        }
        if (org.springframework.util.StringUtils.isEmpty(mixes.getMaterialName())) {
            throw new AppException("材料名称不能为空");
        }
        if (org.springframework.util.StringUtils.isEmpty(mixes.getProductSpec())) {
            throw new AppException("产品规格型号不能为空");
        }
        if (mixes.getPlanWeight() == null || mixes.getPlanWeight() <= 0) {
            throw new AppException("计划加料重量必须大于0");
        }
//        Tank tank = tankService.geet(mixes.getTankNo());
//        if (tank == null) {
//            throw new AppException("料罐不存在");
//        }
//        List<Mixes> existingMixes = getPendingMixesByTankNo(mixes.getTankNo());
//        if (existingMixes != null && !existingMixes.isEmpty()) {
//            throw new AppException("该料罐已有未处理的申请，请先处理上一笔申请");
//        }
//        mixes.setApplyUserId(currentUser.getId());
        mixes.setStatus(0);
        mixes.setApplyTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        feedingMapper.insert(mixes);
    }

    public void executeMixes(Mixes mixes) {
        if (org.springframework.util.StringUtils.isEmpty(mixes.getTankNo())) {
            throw new AppException("料罐编号不能为空");
        }
        if (mixes.getBottomWeight() == null) {
            throw new AppException("底罐重量不能为空");
        }
        if (mixes.getFullWeight() == null) {
            throw new AppException("加料重量不能为空");
        }
        if (mixes.getFlameRetardantWeight() == null) {
            throw new AppException("阻燃粉重量不能为空");
        }
//        List<Mixes> applyMixes = getPendingMixesByTankNo(mixes.getTankNo());
//        if (applyMixes == null || applyMixes.isEmpty()) {
//            throw new AppException("加料申请不存在");
//        }
//        Mixes applyMixes = applyMixes.get(0);
//        if ("1".equals(applyMixes.getStatus())) {
//            throw new AppException("加料申请已处理");
//        }
//        mixes.setId(applyMixes.getId());
//        mixes.setFeedingUserId(currentUser.getId());
//        mixes.setStatus(1);
//        updateMixes(mixes);
    }

    public void executeReturn(Mixes mixes) {
//        if (org.springframework.util.StringUtils.isEmpty(mixes.getTankNo())) {
//            throw new AppException("料罐编号不能为空");
//        }
//        if (mixes.getCurrentWeight() == null) {
//            throw new AppException("当前重量不能为空");
//        }
//        List<Mixes> applyMixes = getPendingReturnMixesByTankNo(mixes.getTankNo());
//        if (applyMixes == null || applyMixes.isEmpty()) {
//            throw new AppException("退料申请不存在");
//        }
//        Mixes applyMixes = applyMixes.get(0);
//        if ("1".equals(applyMixes.getStatus())) {
//            throw new AppException("退料申请已处理");
//        }
//        mixes.setId(applyMixes.getId());
//        mixes.setReturnUserId(currentUser.getId());
//        mixes.setStatus("1");
//        updateReturnMixes(mixes);
    }

    public List<Mixes> getMixesRecordList(String userKey, String tankNo, String shiftType, String materialName, String startTime, String endTime, int pageNo, int size) {
        List<Mixes> mixesList = getMixesRecordList(userKey, tankNo, shiftType, materialName, startTime, endTime);
        return mixesList;
    }

    public Double getWeightData() {
        double weight = Math.random() * 100 + 50;
        return weight;
    }

    public List<Mixes> getMixesList(String userKey, String tankNo, String shiftType, String materialName) {
        // 这里应该根据条件查询，暂时返回所有数据
        List<Mixes> mixesList = feedingMapper.selectAll();

//        // 根据条件过滤
//        if (StringUtils.hasText(tankNo)) {
//            mixesList = mixesList.stream()
//                    .filter(mixes -> tankNo.equals(mixes.getTankNo()))
//                    .toList();
//        }
//
//        if (StringUtils.hasText(shiftType)) {
//            mixesList = mixesList.stream()
//                    .filter(mixes -> shiftType.equals(mixes.getShiftType()))
//                    .toList();
//        }
//
//        if (StringUtils.hasText(materialName)) {
//            mixesList = mixesList.stream()
//                    .filter(mixes -> materialName.equals(mixes.getMaterialName()))
//                    .toList();
//        }
//
        return mixesList;
    }

    public List<Mixes> getPendingMixesByTankNo(String tankNo) {
        // 查询待处理的加料申请（status为0或null）
        List<Mixes> mixesList = feedingMapper.selectByTankNo(tankNo);
//        return mixesList.stream()
//                .filter(mixes -> mixes.getStatus() == null || "0".equals(mixes.getStatus()))
//                .toList();
        return mixesList;
    }


    public List<Mixes> getMixesRecordList(String userKey, String tankNo, String shiftType, String materialName, String startTime, String endTime) {
        // 这里应该根据条件查询，暂时返回所有数据
        List<Mixes> mixesList = feedingMapper.selectAll();
        
//        // 根据条件过滤
//        if (StringUtils.hasText(tankNo)) {
//            mixesList = mixesList.stream()
//                    .filter(mixes -> tankNo.equals(mixes.getTankNo()))
//                    .toList();
//        }
//
//        if (StringUtils.hasText(shiftType)) {
//            mixesList = mixesList.stream()
//                    .filter(mixes -> shiftType.equals(mixes.getShiftType()))
//                    .toList();
//        }
//
//        if (StringUtils.hasText(materialName)) {
//            mixesList = mixesList.stream()
//                    .filter(mixes -> materialName.equals(mixes.getMaterialName()))
//                    .toList();
//        }
//
//        // 时间范围过滤
//        if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
//            mixesList = mixesList.stream()
//                    .filter(mixes -> {
//                        if (mixes.getApplyTime() == null) return false;
//                        return mixes.getApplyTime().compareTo(startTime) >= 0 &&
//                               mixes.getApplyTime().compareTo(endTime) <= 0;
//                    })
//                    .toList();
//        }
        
        return mixesList;
    }
} 