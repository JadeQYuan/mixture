package cn.domain.service;

import cn.domain.entity.Mixture;
import cn.domain.mapper.MixtureMapper;
import cn.domain.serial.SerialService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import cn.domain.pojo.MixtureQo;
import cn.domain.pojo.MixtureVo;
import cn.domain.pojo.PageVo;
import cn.domain.util.TokenUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 混合料信息服务实现类
 */
@Service
public class MixtureService {

    @Autowired
    private MixtureMapper mixtureMapper;

    @Autowired
    private TankService tankService;

    @Autowired
    private SerialService serialService;

    public PageVo<MixtureVo> getMixesList(MixtureQo mixtureQo) {
        mixtureQo.setStatus(Collections.singletonList(0));
        PageHelper.startPage(mixtureQo.getPageNo(), mixtureQo.getPageSize());
        Page<MixtureVo> mixesList = (Page<MixtureVo>) mixtureMapper.selectByCondition(mixtureQo);
        return new PageVo<>(mixesList);
    }

    @Transactional
    public void applyMixes(Mixture mixture) {
        Integer userId = TokenUtil.getToken();
        mixture.setApplyUserId(userId);
        mixtureMapper.insert(mixture);
        tankService.updateUser(mixture.getTankId(), userId);
    }

    public void executeMixes(Mixture mixture) {
        mixture.setFeedingUserId(TokenUtil.getToken());
        mixtureMapper.executeMixes(mixture);
    }

    @Transactional
    public void executeReturn(Mixture mixture) {
        mixture.setReturnUserId(TokenUtil.getToken());
        Mixture info = mixtureMapper.selectById(mixture.getId());
        mixture.setActualWeight(info.getFullWeight() + info.getFlameRetardantWeight() - mixture.getReturnWeight());
        mixtureMapper.executeReturn(mixture);
        tankService.updateUser(mixture.getTankId(), null);
    }

    public PageVo<MixtureVo> getMixesRecordList(MixtureQo mixtureQo) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        mixtureQo.setStatus(Arrays.asList(1, 2));
        mixtureQo.setApplyStartTime(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        mixtureQo.setApplyEndTime(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        PageHelper.startPage(mixtureQo.getPageNo(), mixtureQo.getPageSize());
        Page<MixtureVo> mixesList = (Page<MixtureVo>) mixtureMapper.selectByCondition(mixtureQo);
        return new PageVo<>(mixesList);
    }

    public PageVo<MixtureVo> getMixesStatsList(MixtureQo mixtureQo) {
        mixtureQo.setStatus(Collections.singletonList(2));
        PageHelper.startPage(mixtureQo.getPageNo(), mixtureQo.getPageSize());
        Page<MixtureVo> mixesList = (Page<MixtureVo>) mixtureMapper.selectByCondition(mixtureQo);
        return new PageVo<>(mixesList);
    }

    public Double getWeightData() {
//        double weight = Math.random() * 100 + 50;
//        return Math.round(weight * 100.0) / 100.0; // 保留两位小数
        return serialService.readWeight();
    }

    public List<Mixture> getTankForReturn() {
        return mixtureMapper.getTankForReturn(TokenUtil.getToken());
    }

    public void remark(Mixture mixture) {
        mixtureMapper.updateRemark(mixture);
    }
}