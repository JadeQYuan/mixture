package cn.domain.service;

import cn.domain.config.AppConfig;
import cn.domain.entity.Check;
import cn.domain.entity.Mixture;
import cn.domain.mapper.MixtureMapper;
import cn.domain.pojo.MixtureBottomVo;
import cn.domain.pojo.MixtureQo;
import cn.domain.pojo.MixtureVo;
import cn.domain.pojo.PageVo;
import cn.domain.serial.SerialService;
import cn.domain.util.TokenUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 混合料信息服务实现类
 */
@Service
@Slf4j
public class MixtureService {

    @Autowired
    private MixtureMapper mixtureMapper;

    @Autowired
    private TankService tankService;

    @Autowired
    private SerialService serialService;

    @Autowired
    private CheckService checkService;

    @Autowired
    private AppConfig appConfig;

    public List<MixtureVo> getTodoList(MixtureQo mixtureQo) {
        mixtureQo.setStatus(Arrays.asList(0, 3, 6));
        List<MixtureVo> list = mixtureMapper.selectByCondition(mixtureQo);
        int thresholdMinutes = appConfig.getPickingTimeThreshold();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (MixtureVo vo : list) {
            if (vo.getApplyTime() != null) {
                try {
                    LocalDateTime applyTime = LocalDateTime.parse(vo.getApplyTime(), fmt);
                    long diffMinutes = Duration.between(applyTime, now).toMinutes();
                    vo.setOverdue(diffMinutes > thresholdMinutes);
                } catch (Exception e) {
                    vo.setOverdue(false);
                }
            } else {
                vo.setOverdue(false);
            }
        }
        return list;
    }

    public PageVo<MixtureVo> getMixesList(MixtureQo mixtureQo) {
        mixtureQo.setStatus(Arrays.asList(0, 3));
        PageHelper.startPage(mixtureQo.getPageNo(), mixtureQo.getPageSize()).setOrderBy(" f.apply_time ASC ");
        Page<MixtureVo> mixesList = (Page<MixtureVo>) mixtureMapper.selectByCondition(mixtureQo);
        return new PageVo<>(mixesList);
    }

    public PageVo<MixtureVo> getMixesRecordList(MixtureQo mixtureQo) {
        mixtureQo.setStatus(Arrays.asList(1, 2, 4, 5));
        PageHelper.startPage(mixtureQo.getPageNo(), mixtureQo.getPageSize()).setOrderBy(" f.apply_time DESC ");
        Page<MixtureVo> mixesList = (Page<MixtureVo>) mixtureMapper.selectByCondition(mixtureQo);
        return new PageVo<>(mixesList);
    }

    public PageVo<MixtureVo> getMixesStatsList(MixtureQo mixtureQo) {
        mixtureQo.setStatus(Collections.singletonList(2));
        PageHelper.startPage(mixtureQo.getPageNo(), mixtureQo.getPageSize()).setOrderBy(" f.apply_time ASC ");;
        Page<MixtureVo> mixesList = (Page<MixtureVo>) mixtureMapper.selectByCondition(mixtureQo);
        return new PageVo<>(mixesList);
    }


    @Transactional
    public void apply(Mixture mixture) {
        Integer userId = TokenUtil.getToken();
        mixture.setApplyUserId(userId);
        mixture.setPickingUserId(userId);
        mixtureMapper.apply(mixture);
        tankService.updateUser(mixture.getTankId(), userId);
    }

    @Transactional
    public void prepare(Mixture mixture) {
        Integer userId = TokenUtil.getToken();
        mixture.setApplyUserId(userId);
        mixtureMapper.prepare(mixture);
        tankService.updateUser(mixture.getTankId(), userId);
    }

    public Mixture getLastReturnWeight(Integer tankId) {
        return mixtureMapper.getReturnWeight(tankId);
    }

    @Transactional
    public void bottom(MixtureBottomVo bottomVo) {
        mixtureMapper.bottom(bottomVo);
        serialService.stopReading();
        if (bottomVo.getCheck()) {
            Mixture info = mixtureMapper.selectById(bottomVo.getId());
            Check check = new Check();
            check.setTankId(info.getTankId());
            check.setTankNo(info.getTankNo());
            check.setReturnId(bottomVo.getReturnId());
            check.setReturnWeight(bottomVo.getReturnWeight());
            check.setBottomId(bottomVo.getId());
            check.setBottomWeight(bottomVo.getBottomWeight());
            check.setOpinion(bottomVo.getOpinion());
            checkService.check(check);
        }
    }

    @Transactional
    public void executeFeed(Mixture mixture) {
        Mixture info = mixtureMapper.selectById(mixture.getId());
        mixture.setFeedingUserId(TokenUtil.getToken());
        if (info.getStatus() == 0) {
            mixture.setStatus(1);
        } else if (info.getStatus() == 3) {
            boolean needFlame = info.getProductSpec() != null && info.getProductSpec().toUpperCase().contains("V");
            mixture.setStatus(needFlame ? 6 : 4);
            tankService.updateUser(mixture.getTankId(), null);
        } else {
            log.info("加料记录状态未匹配，id={} status={}", mixture.getId(), info.getStatus());
            return;
        }
        mixtureMapper.executeFeed(mixture);
        serialService.stopReading();
    }

    public List<MixtureVo> getTankForPicking() {
        Integer userId = TokenUtil.getToken();
        return mixtureMapper.getTankForPicking(userId);
    }

    @Transactional
    public void addFlameRetardant(Mixture mixture) {
        Mixture info = mixtureMapper.selectById(mixture.getId());
        if (info.getStatus() != 6) {
            log.warn("阻燃粉操作状态不正确，id={} status={}", mixture.getId(), info.getStatus());
            return;
        }
        Integer userId = TokenUtil.getToken();
        if (!userId.equals(info.getApplyUserId())) {
            log.warn("非申请人尝试加阻燃粉，id={} applyUserId={} currentUserId={}", mixture.getId(), info.getApplyUserId(), userId);
            return;
        }
        mixture.setFlameRetardantUserId(userId);
        if (mixture.getFlameRetardantAbnormal() == null) {
            mixture.setFlameRetardantAbnormal(false);
        }
        mixtureMapper.executeFlameRetardant(mixture);
        serialService.stopReading();
    }

    @Transactional
    public void picking(Mixture mixture) {
        Integer userId = TokenUtil.getToken();
        mixture.setPickingUserId(userId);
        // 如果未设置阻燃粉重量，默认为0
        if (mixture.getFlameRetardantWeight() == null) {
            mixture.setFlameRetardantWeight(0.0);
        }
        // 阻燃粉异常标识由前端判断传入
        if (mixture.getFlameRetardantAbnormal() == null) {
            mixture.setFlameRetardantAbnormal(false);
        }
        mixtureMapper.executePicking(mixture);
        tankService.updateUser(mixture.getTankId(), userId);
        serialService.stopReading();
    }

    public List<Mixture> getTankForReturn() {
        return mixtureMapper.getTankForReturn(TokenUtil.getToken());
    }

    @Transactional
    public void executeReturn(Mixture mixture) {
        mixture.setReturnUserId(TokenUtil.getToken());
        Mixture info = mixtureMapper.selectById(mixture.getId());
        mixture.setActualWeight(BigDecimal.valueOf(info.getFullWeight())
                .add(BigDecimal.valueOf(info.getFlameRetardantWeight()))
                .add(BigDecimal.valueOf(-mixture.getReturnWeight())).doubleValue());
        mixtureMapper.executeReturn(mixture);
        tankService.updateUser(mixture.getTankId(), null);
        serialService.stopReading();
    }

    public void updateReturn(Integer id, Double bottomWeight) {
        Mixture info = mixtureMapper.selectById(id);
        double actualWeight = BigDecimal.valueOf(info.getFullWeight())
                .add(BigDecimal.valueOf(info.getFlameRetardantWeight()))
                .add(BigDecimal.valueOf(-bottomWeight)).doubleValue();
        mixtureMapper.updateReturn(id, bottomWeight, actualWeight);
    }

    @Transactional
    public void executeCancel(Mixture mixture) {
        mixtureMapper.executeCancel(mixture);
        tankService.updateUser(mixture.getTankId(), null);
    }

    public Double getWeightData() {
        return serialService.readWeight();
    }

    public void fakeWeight(Double weight) {
        serialService.fakeWeight(weight);
    }

    public void remark(Mixture mixture) {
        mixtureMapper.updateRemark(mixture);
    }
}