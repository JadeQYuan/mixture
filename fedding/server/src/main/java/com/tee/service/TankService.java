package com.tee.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tee.entity.Tank;
import com.tee.mapper.TankMapper;
import com.tee.pojo.PageQo;
import com.tee.pojo.PageVo;
import com.tee.util.Result;
import com.tee.exception.AppException;
import com.tee.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 料罐信息服务实现类
 */
@Service
public class TankService {

    @Autowired
    private TankMapper tankMapper;

    public PageVo<Tank> getTankList(String tankNo, PageQo pageQo) {
        PageHelper.startPage(pageQo.getPageNo(), pageQo.getPageSize());
        Page<Tank> tankList = (Page<Tank>)tankMapper.selectByCondition(tankNo);
        return new PageVo<>(tankList);
    }

    public void addTank(Tank tank) {
        if (StringUtils.isEmpty(tank.getTankNo())) {
            throw new AppException("料罐编号不能为空");
        }
        List<Tank> tanks = tankMapper.selectByTankNo(tank.getTankNo());
        if (!tanks.isEmpty()) {
            throw new AppException("料罐编号已存在");
        }
        insertTank(tank);
    }

    public void updateTank(Tank tank) {
        if (tank.getId() == null) {
            throw new AppException("料罐ID不能为空");
        }
        Tank existingTank = tankMapper.selectById(tank.getId());
        if (existingTank == null) {
            throw new AppException("料罐不存在");
        }
        tankMapper.updateById(tank);
    }

    public List<Tank> getTanksByUserId(String userId) {
        return tankMapper.selectByUserId(userId);
    }

    public List<Tank> getAvailableTanks() {
        // 获取所有可用的料罐（没有被占用的）
        return tankMapper.selectAvailableTanks();
    }

    public List<Tank> getMyTanks() {
        // 获取所有可用的料罐（没有被占用的）
        return tankMapper.selectMyTanks(TokenUtil.getToken());
    }

    public void insertTank(Tank tank) {
        // 设置创建时间和更新时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        tank.setCreateTime(now);
        tank.setUpdateTime(now);
        
        tankMapper.insert(tank);
    }

    public void deleteTank(Integer id) {
        if (id == null) {
            throw new AppException("料罐ID不能为空");
        }
        Tank existingTank = tankMapper.selectById(id);
        if (existingTank == null) {
            throw new AppException("料罐不存在");
        }
        if (existingTank.getUserId() != null) {
            throw new AppException("料罐正在使用中，无法删除");
        }
        tankMapper.deleteById(id);
    }
} 