package com.github.klefstad_teaching.cs122b.gateway.config;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

public class MyCustomResult {
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public MyCustomResult setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MyCustomResult setMessage(String message) {
        this.message = message;
        return this;
    }
}
