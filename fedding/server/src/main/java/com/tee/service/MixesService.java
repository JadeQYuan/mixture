package com.tee.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tee.entity.Mixes;
import com.tee.entity.Tank;
import com.tee.mapper.MixesMapper;
import com.tee.pojo.MixesVo;
import com.tee.pojo.PageQo;
import com.tee.pojo.PageVo;
import com.tee.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 加料信息服务实现类
 */
@Service
public class MixesService {

    @Autowired
    private MixesMapper mixesMapper;

    @Autowired
    private TankService tankService;

    public PageVo<MixesVo> getMixesList(String userKey, String tankNo, String shiftType, String materialName, PageQo pageQo) {
        PageHelper.startPage(pageQo.getPageNo(), pageQo.getPageSize());
        Page<MixesVo> mixesList = (Page<MixesVo>) mixesMapper.selectByCondition(userKey, tankNo, shiftType, materialName, 0);
        return new PageVo<>(mixesList);
    }

    @Transactional
    public void applyMixes(Mixes mixes) {
        mixes.setApplyUserId(TokenUtil.getToken());
        mixesMapper.insert(mixes);
        tankService.updateUser(mixes.getTankId(), TokenUtil.getToken());
    }

    public void executeMixes(Mixes mixes) {
        mixes.setFeedingUserId(TokenUtil.getToken());
        mixesMapper.executeMixes(mixes);
    }

    @Transactional
    public void executeReturn(Mixes mixes) {
        mixes.setReturnUserId(TokenUtil.getToken());
        mixesMapper.executeReturn(mixes);
        tankService.updateUser(mixes.getTankId(), null);
    }

    public PageVo<MixesVo> getMixesRecordList(String userKey, String tankNo, String shiftType, String materialName, String startTime, String endTime, PageQo pageQo) {
        PageHelper.startPage(pageQo.getPageNo(), pageQo.getPageSize());
        Page<MixesVo> mixesList = (Page<MixesVo>)mixesMapper.selectByCondition(userKey, tankNo, shiftType, materialName, 2);
        return new PageVo<>(mixesList);
    }

    public Double getWeightData() {
        double weight = Math.random() * 100 + 50;
        return weight;
    }

    public List<Mixes> getTankForReturn() {
        // 获取所有可用的料罐（没有被占用的）
        return mixesMapper.getTankForReturn(TokenUtil.getToken());
    }

    public List<Mixes> getMixesList(String userKey, String tankNo, String shiftType, String materialName) {
        // 这里应该根据条件查询，暂时返回所有数据
        List<Mixes> mixesList = mixesMapper.selectAll();
        return mixesList;
    }

    public List<Mixes> getPendingMixesByTankNo(String tankNo) {
        // 查询待处理的加料申请（status为0或null）
        List<Mixes> mixesList = mixesMapper.selectByTankNo(tankNo);
//        return mixesList.stream()
//                .filter(mixes -> mixes.getStatus() == null || "0".equals(mixes.getStatus()))
//                .toList();
        return mixesList;
    }
}