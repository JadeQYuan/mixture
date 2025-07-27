package cn.domain.serial;

import cn.domain.config.SerialConfig;
import cn.domain.exception.AppException;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class SerialService {

    @Autowired
    private SerialConfig serialConfig;

    private SerialPort serialPort;
    private boolean isConnected = false;
    private boolean isReading = false;
    private LocalDateTime readingTime;

    @PostConstruct
    public void init() {
        connect();
    }

    @PreDestroy
    public void destroy() {
        disconnect();
    }

    private byte[] readBuffer;
    private Double weight;

    private final Executor executor = Executors.newSingleThreadExecutor();

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
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
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
                isReading = false;
                weight = null;
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

    public void stopReading() {
        isReading = false;
        weight = null;
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
                throw new AppException("串口连接失败");
            }
        }
        readingTime = LocalDateTime.now();
        if (!isReading) {
            isReading = true;
            read();
        }
        return weight;
    }

    private void read() {
        executor.execute(() -> {
            while (isConnected && isReading && Duration.between(readingTime, LocalDateTime.now()).getSeconds() < 15) {
                try {
                    int i = serialPort.readBytes(readBuffer, 1, 0);
                    if (i < 0) {
                        log.error("串口读取失败: i = {}", i);
                        disconnect();
                        continue;
                    }
                    log.info("读取重量数据: i = {}", i);
                    if (i > 0) {
                        log.info("读取重量数据1: {}", new String(readBuffer, 0, 1));
                    }
                    if (i < 1 || readBuffer[0] != 'w') {
                        continue;
                    }
                    i = serialPort.readBytes(readBuffer, 1, 1);
                    if (i > 0) {
                        log.info("读取重量数据2: {}", new String(readBuffer, 1, 1));
                    }
                    if (i < 1 || readBuffer[1] != 'n') {
                        continue;
                    }
                    serialPort.readBytes(readBuffer, 9, 2);
                    weight = parse(readBuffer);
                } catch (Exception e) {
                    log.error("串口读取异常: ",  e);
                    weight = 0.0;
                }
            }
        });
    }

    public double parse(byte[] bytes) {
        log.info("读取重量数据all: {}", new String(bytes));
        if (bytes[0] != 'w' || bytes[1] != 'n' || bytes[9] != 'k' || bytes[10] != 'g') {
            return 0.0;
        }
        return Double.parseDouble(new String(bytes, 2, 7));
    }
}