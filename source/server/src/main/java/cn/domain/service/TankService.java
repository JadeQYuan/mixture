package cn.domain.service;

import cn.domain.entity.Mixture;
import cn.domain.mapper.MixtureMapper;
import cn.domain.mapper.TankMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import cn.domain.entity.Tank;
import cn.domain.exception.AppException;
import cn.domain.pojo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import cn.domain.pojo.TankQo;
import cn.domain.pojo.TankVo;

/**
 * 料罐信息服务实现类
 */
@Service
public class TankService {

    @Autowired
    private TankMapper tankMapper;

    @Autowired
    private MixtureMapper mixtureMapper;

    public PageVo<TankVo> getTankList(TankQo tankQo) {
        PageHelper.startPage(tankQo.getPageNo(), tankQo.getPageSize());
        Page<TankVo> tankList = (Page<TankVo>) tankMapper.selectByCondition(tankQo.getTankNo());
        return new PageVo<>(tankList);
    }

    public void addTank(Tank tank) {
        validateTankNo(tank.getTankNo());
        if (!tankMapper.selectByTankNo(tank.getTankNo()).isEmpty()) {
            throw new AppException("料罐编号已存在");
        }
        insertTank(tank);
    }

    public void updateTank(Tank tank) {
        if (tank.getId() == null) {
            throw new AppException("料罐ID不能为空");
        }
        Tank existingTank = getExistingTank(tank.getId());
        if (existingTank.getUserId() != null) {
            throw new AppException("料罐正在使用中，不能编辑");
        }
        tankMapper.updateById(tank);
    }

    public void updateUser(Integer id, Integer userId) {
        getExistingTank(id);
        tankMapper.updateUser(id, userId);
    }

    public List<Tank> getTanksByUserId(Integer userId) {
        return tankMapper.selectByUserId(userId);
    }

    public List<TankVo> getTanksForApply() {
        List<TankVo> tanks = tankMapper.getTanksForApply();
        for (TankVo tank : tanks) {
            Mixture mixture = mixtureMapper.tankStatus(tank.getId());
            if (mixture != null && mixture.getStatus() == 4) {
                tank.setMixtureId(mixture.getId());
                tank.setPicking(true);
                tank.setFullWeight(mixture.getFullWeight());
            } else {
                tank.setPicking(false);
            }
        }
        return tanks;
    }

    private void insertTank(Tank tank) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        tank.setCreateTime(now);
        tank.setUpdateTime(now);
        tankMapper.insert(tank);
    }

    public void deleteTank(Integer id) {
        if (id == null) {
            throw new AppException("料罐ID不能为空");
        }
        Tank existingTank = getExistingTank(id);
        if (existingTank.getUserId() != null) {
            throw new AppException("料罐正在使用中，无法删除");
        }
        tankMapper.deleteById(id);
    }

    private void validateTankNo(String tankNo) {
        if (StringUtils.isEmpty(tankNo)) {
            throw new AppException("料罐编号不能为空");
        }
    }

    private Tank getExistingTank(Integer id) {
        Tank tank = tankMapper.selectById(id);
        if (tank == null) {
            throw new AppException("料罐不存在");
        }
        return tank;
    }
} 