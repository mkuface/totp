package com.metsakuur.totpdemo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataResponse extends BaseResponse {

    private Object data;

    public DataResponse(String code, String message, Object data) {
        super(code, message);
        this.data = data;
    }
}
