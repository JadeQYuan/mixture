package com.tee.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "modbus")
public class ModbusConfig {

    /**
     * 串口设备名称，如COM1、COM2等
     */
    private String serialPort = "COM1";
    
    /**
     * 波特率，常用的有9600、19200、38400、57600、115200等
     */
    private int baudRate = 115200;

    /**
     * 串口通信校验方式
     */
    private int parity = 0;
    
    /**
     * 数据位，通常为8位
     */
    private int dataBits = 8;
    
    /**
     * 停止位，通常为1位
     */
    private int stopBits = 1;
    
    /**
     * 从站ID，Modbus从设备的地址
     */
    private int slaveId = 1;
    
    /**
     * 寄存器偏移地址，要读取的起始寄存器地址
     */
    private int offset = 0;
    
    /**
     * 读取寄存器数量，要读取的连续寄存器个数
     */
    private int quantity = 2;

}