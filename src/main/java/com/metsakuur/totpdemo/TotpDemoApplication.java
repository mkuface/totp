package com.metsakuur.totpdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class TotpDemoApplication {

    static {
        String archType = System.getProperty("os.arch", "");
        String osName = System.getProperty("os.name", "");
        boolean isLinux = osName.toLowerCase().startsWith("linux");
        if (archType.equalsIgnoreCase("AMD64") && isLinux) {
            try {
                System.loadLibrary("MKtotp");
                log.info("MKtotp loaded successfully");
            } catch (Exception e) {
                log.error("MKtotp load failed: {}", e.getMessage());
            }
        } else {
            log.warn("Skipping MKtotp load for os={} arch={}", osName, archType);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TotpDemoApplication.class, args);
    }
}
