package com.metsakuur.totpdemo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotpEncRequest {
    private String uuid;
    private String random;
    private String data;
    private Integer length;
}