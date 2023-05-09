package com.yht.exerciseassist.exception.controller;

import com.yht.exerciseassist.exception.dto.ExceptionResponse;
import com.yht.exerciseassist.exception.error.ErrorCode;
import com.yht.exerciseassist.seoulAPI.controller.SeoulAPIController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.text.ParseException;

@Slf4j
@RestControllerAdvice(assignableTypes = SeoulAPIController.class)
public class APIExceptionController {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public ExceptionResponse ioExceptionHandle(IOException exception) {

        log.error("{} // {}", exception.getMessage(), ErrorCode.IO_FAIL_EXCEOPTION.getMessage());
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.IO_FAIL_EXCEOPTION.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ParseException.class)
    public ExceptionResponse parseExceptionHandle(ParseException exception) {

        log.error("{} // {}", exception.getMessage(), ErrorCode.JSON_PARSE_EXCEPTION.getMessage());
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.JSON_PARSE_EXCEPTION.getMessage());
    }
}