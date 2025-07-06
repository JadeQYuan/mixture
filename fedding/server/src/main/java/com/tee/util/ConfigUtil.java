package com.tee.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConfigUtil implements EnvironmentAware {
    private static Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        ConfigUtil.environment = environment;
    }

    public static String getMyProperty() {
        return getPropertyFromEnvironment("spring.profiles.active");
    }

    public static String getPropertyFromEnvironment(String key) {
        return environment.getProperty(key);
    }
}
