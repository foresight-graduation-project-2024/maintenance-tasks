package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception;

import lombok.Data;

@Data
public class RuntimeErrorCodedException extends RuntimeException{
    ErrorCode errorCode;
    public RuntimeErrorCodedException(ErrorCode errorCode)
    {
        super();
        this.errorCode = errorCode;
    }
}
