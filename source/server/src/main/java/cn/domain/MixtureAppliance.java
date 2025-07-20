package cn.domain;

import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@MapperScan("cn.domain.mapper")
@EnableCaching
@Log4j2
@SpringBootApplication
public class MixtureAppliance {

    public static void main(String[] args) {
        SpringApplication.run(MixtureAppliance.class, args);

    }
}
