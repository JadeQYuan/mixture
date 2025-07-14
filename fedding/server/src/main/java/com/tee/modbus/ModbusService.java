package com.tee.modbus;

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortFactoryPJC;
import com.intelligt.modbus.jlibmodbus.serial.SerialUtils;
import com.tee.config.ModbusConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Service
public class ModbusService {

    @Autowired
    private ModbusConfig modbusConfig;

    private ModbusMaster master;
    private boolean isConnected = false;

    @PostConstruct
    public void init() {
        connect();
    }

    @PreDestroy
    public void destroy() {
        disconnect();
    }

    /**
     * 连接到Modbus设备
     */
    public void connect() {
        try {
            SerialParameters sp = new SerialParameters();
            sp.setDevice(modbusConfig.getSerialPort());
            // these parameters are set by default
            sp.setBaudRate(SerialPort.BaudRate.getBaudRate(modbusConfig.getBaudRate()));
            sp.setDataBits(modbusConfig.getDataBits());
            sp.setParity(SerialPort.Parity.getParity(modbusConfig.getParity()));
            sp.setStopBits(modbusConfig.getStopBits());

            SerialUtils.setSerialPortFactory(new SerialPortFactoryPJC());
            master = ModbusMasterFactory.createModbusMasterRTU(sp);
            master.connect();
            isConnected = true;
            log.info("Modbus连接成功: {}", modbusConfig.getSerialPort());
        } catch (Exception e) {
            log.error("Modbus连接失败: {} ", modbusConfig.getSerialPort(),  e);
            isConnected = false;
        }
    }

    /**
     * 断开Modbus连接
     */
    public void disconnect() {
        if (master != null && isConnected) {
            try {
                master.disconnect();
                isConnected = false;
                log.info("Modbus连接已断开");
            } catch (Exception e) {
                log.error("断开Modbus连接时发生错误: ", e);
            }
        }
    }

    /**
     * 重新连接
     */
    public void reconnect() {
        disconnect();
        connect();
    }

    /**
     * 检查连接状态
     */
    public boolean isConnected() {
        return isConnected && master != null;
    }

    /**
     * 读取重量数据
     * @return 重量值（千克）
     */
    public Double readWeight() {
        if (!isConnected()) {
            log.warn("Modbus未连接，尝试重新连接");
            reconnect();
            if (!isConnected()) {
                log.error("Modbus重连失败，无法读取重量数据");
                return null;
            }
        }

        try {
            int[] registerValues = master.readHoldingRegisters(modbusConfig.getSlaveId(), modbusConfig.getOffset(), modbusConfig.getQuantity());
//            // print values
//            for (int value : registerValues) {
//                System.out.println("Address: " + offset++ + ", Value: " + value);
//            }
            return ((double) registerValues[0]);
        } catch (Exception e) {
            isConnected = false;
            log.error("读取重量数据时发生未知错误: ", e);
            return null;
        }
    }

}