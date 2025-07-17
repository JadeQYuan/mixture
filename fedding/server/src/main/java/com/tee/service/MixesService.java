package com.tee.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tee.entity.Mixes;
import com.tee.mapper.MixesMapper;
import com.tee.pojo.MixesQo;
import com.tee.pojo.MixesVo;
import com.tee.pojo.PageVo;
import com.tee.util.TokenUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PageVo<MixesVo> getMixesList(MixesQo mixesQo) {
        mixesQo.setStatus(0);
        PageHelper.startPage(mixesQo.getPageNo(), mixesQo.getPageSize());
        Page<MixesVo> mixesList = (Page<MixesVo>) mixesMapper.selectByCondition(mixesQo);
        return new PageVo<>(mixesList);
    }

    @Transactional
    public void applyMixes(Mixes mixes) {
        Integer userId = TokenUtil.getToken();
        mixes.setApplyUserId(userId);
        mixesMapper.insert(mixes);
        tankService.updateUser(mixes.getTankId(), userId);
    }

    public void executeMixes(Mixes mixes) {
        mixes.setFeedingUserId(TokenUtil.getToken());
        mixesMapper.executeMixes(mixes);
    }

    @Transactional
    public void executeReturn(Mixes mixes) {
        mixes.setReturnUserId(TokenUtil.getToken());
        Mixes info = mixesMapper.selectById(mixes.getId());
        mixes.setActualWeight(info.getFullWeight() + info.getFlameRetardantWeight() - mixes.getReturnWeight());
        mixesMapper.executeReturn(mixes);
        tankService.updateUser(mixes.getTankId(), null);
    }

    public PageVo<MixesVo> getMixesRecordList(MixesQo mixesQo) {
        mixesQo.setStatus(2);
        PageHelper.startPage(mixesQo.getPageNo(), mixesQo.getPageSize());
        Page<MixesVo> mixesList = (Page<MixesVo>) mixesMapper.selectByCondition(mixesQo);
        return new PageVo<>(mixesList);
    }

    public Double getWeightData() {
        double weight = Math.random() * 100 + 50;
        return Math.round(weight * 100.0) / 100.0; // 保留两位小数
    }

    public List<Mixes> getTankForReturn() {
        return mixesMapper.getTankForReturn(TokenUtil.getToken());
    }

    public void remark(Mixes mixes) {
        mixesMapper.updateRemark(mixes);
    }
}