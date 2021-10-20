package com.coocaa.websocket.api.config;

import com.coocaa.websocket.common.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class CustomExceptionHandler {
    public static Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public R errorHandler(Exception e) {
        log.error("",e);
        return R.fail(e.getMessage());
    }

}
