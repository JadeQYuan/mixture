package com.tee.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.tee.config.SerialConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Service
public class SerialService {

    @Autowired
    private SerialConfig serialConfig;

    private SerialPort serialPort;
    private boolean isConnected = false;

    @PostConstruct
    public void init() {
        connect();
    }

    @PreDestroy
    public void destroy() {
        disconnect();
    }

    private byte[] readBuffer;
    private byte[] writeBuffer = new byte[1];

    /**
     * 连接到串口设备
     */
    public void connect() {
        try {
            serialPort = SerialPort.getCommPort(serialConfig.getSerialPort());
            serialPort.setBaudRate(serialConfig.getBaudRate());
            serialPort.setNumDataBits(serialConfig.getDataBits());
            serialPort.setParity(serialConfig.getParity());
            serialPort.setNumStopBits(serialConfig.getStopBits());
            serialPort.openPort(1000);
            isConnected = true;
            readBuffer = new byte[12];
            log.info("串口连接成功: {}", serialConfig.getSerialPort());
        } catch (Exception e) {
            log.error("串口连接失败: {} ", serialConfig.getSerialPort(),  e);
            isConnected = false;
        }
    }

    /**
     * 断开串口连接
     */
    public void disconnect() {
        if (serialPort != null && isConnected) {
            try {
                serialPort.closePort();
                isConnected = false;
                log.info("串口连接已断开");
            } catch (Exception e) {
                log.error("断开串口连接时发生错误: ", e);
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
        return isConnected && serialPort != null;
    }

    /**
     * 读取重量数据
     * @return 重量值（千克）
     */
    public Double readWeight() {
        if (!isConnected()) {
            log.warn("串口未连接，尝试重新连接");
            reconnect();
            if (!isConnected()) {
                log.error("串口重连失败，无法读取重量数据");
                return null;
            }
        }

        try {
            if (serialConfig.isSend()) {
                serialPort.writeBytes(readBuffer, 1);
            }
            serialPort.readBytes(readBuffer, 11);
            return parse(new String(readBuffer).trim());
        } catch (Exception e) {
            isConnected = false;
            log.error("读取重量数据时发生未知错误: ", e);
            return null;
        }
    }

    public double parse(String value) {
        if (value.startsWith("ww") || value.startsWith("wn") || value.startsWith("wt")) {
            if (value.endsWith("kg")) {
                return Double.parseDouble(value.substring(2, 9));
            } else if (value.endsWith("lb")) {
                return Double.parseDouble(value.substring(2, 9)) * 0.453592;
            }
        }
        return 0.0;
    }
}