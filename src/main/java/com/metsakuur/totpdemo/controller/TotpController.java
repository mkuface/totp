package com.metsakuur.totpdemo.controller;

import com.metsakuur.totpdemo.dto.BaseResponse;
import com.metsakuur.totpdemo.dto.DataResponse;
import com.metsakuur.totpdemo.dto.TotpDecRequest;
import com.metsakuur.totpdemo.dto.TotpEncRequest;
import com.metsakuur.uface.totp.MkTotpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@Slf4j
public class TotpController {

    @PostMapping("/totp/encode")
    public BaseResponse encode(@RequestBody TotpEncRequest request) {
        try {
            log.debug("/totp/encode step: request received");
            if (request == null || request.getData() == null || request.getData().isBlank()) {
                log.debug("/totp/encode step: request validation failed");
                log.warn("/totp/encode result: code=400 message=Data is empty");
                return new BaseResponse("400", "Data is empty");
            }

            //Initialize Totp Object
            MkTotpUtil totp = new MkTotpUtil();

            log.debug("/totp/encode step: converting request data to bytes");
            byte[] source = request.getData().getBytes(StandardCharsets.UTF_8);
            log.debug("/totp/encode step: calling TotpUtil.encryptTotp");
            //Encrypt Data
            byte[] encrypted = totp.enc(request.getUuid(), request.getRandom(), source);
            if (encrypted == null || encrypted.length == 0) {
                log.debug("/totp/encode step: encryption returned empty result");
                log.error("/totp/encode result: code=500 message=Encryption failed");
                return new BaseResponse("500", "Encryption failed");
            }

            //Decrypt to verify
            byte[] decrypted = totp.dec(encrypted);
            if (decrypted == null || decrypted.length == 0) {
                log.debug("/totp/encode step: decryption returned empty result");
                log.error("/totp/encode result: code=500 message=Decryption failed");
            }
            log.debug("/totp/encode step: encoding encrypted bytes to base64");
            String encoded = Base64.getEncoder().encodeToString(encrypted);
            log.info("/totp/encode result: code=OK message=Encoded Data");
            return new DataResponse("OK", "Encoded Data", encoded);
        } catch (Exception e) {
            log.debug("/totp/encode step: exception path");
            log.error("Error in /totp/encode", e);
            log.error("/totp/encode result: code=500 message=Internal Server Error");
            return new BaseResponse("500", "Internal Server Error: " + e.getMessage());
        }
    }

    @PostMapping("/totp/decode")
    public BaseResponse decode(@RequestBody TotpDecRequest request) {
        try {
            log.debug("/totp/decode step: request received");
            if (request == null || request.getData() == null || request.getData().isBlank()) {
                log.debug("/totp/decode step: request validation failed");
                log.warn("/totp/decode result: code=400 message=Data is empty");
                return new BaseResponse("400", "Data is empty");
            }

            log.debug("/totp/decode step: decoding base64 request data");
            byte[] source = Base64.getDecoder().decode(request.getData().trim());
            log.debug("/totp/decode step: calling TotpUtil.decryptTotp");
            //Initialize Totp Object
            MkTotpUtil totp = new MkTotpUtil() ;
            //Decrypt Data
            byte[] decrypted =  totp.dec(source);
            if (decrypted == null || decrypted.length == 0) {
                log.debug("/totp/decode step: decryption returned empty result");
                log.warn("/totp/decode result: code=400 message=Decrypted Data is empty");
                return new BaseResponse("400", "Decrypted Data is empty");
            }

            log.debug("/totp/decode step: converting decrypted bytes to string");
            log.info("/totp/decode result: code=OK message=Decrypted");
            return new DataResponse("OK", "Decrypted", new String(decrypted, StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.debug("/totp/decode step: exception path");
            log.error("Error in /totp/decode", e);
            log.error("/totp/decode result: code=500 message=Internal Server Error");
            return new BaseResponse("500", "Internal Server Error: " + e.getMessage());
        }
    }
}
