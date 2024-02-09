package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RestControllerAdvice
public class RestExceptionHandler
{
    @ExceptionHandler(RuntimeErrorCodedException.class)
    public ResponseEntity<ErrorDetails> handleAllCodedErrors(RuntimeErrorCodedException exception) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCode(exception.getErrorCode().getCode());
        errorDetails.setTimeStamp(new Date().getTime());


        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions( MethodArgumentNotValidException ex)
    {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCode(ErrorCode.INVALID_REQUEST_BODY.getCode());
        errorDetails.setTimeStamp(new Date().getTime());
        errorDetails.setDetails(errors);





        return new ResponseEntity<>(errorDetails,HttpStatus.BAD_REQUEST);
    }






    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> handleUnknownErrors(RuntimeErrorCodedException exception)
    {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCode(ErrorCode.UNKNOWN_SERVER_ERROR.getCode());
        errorDetails.setTimeStamp(new Date().getTime());


        return new ResponseEntity<>(errorDetails,HttpStatus.BAD_REQUEST);
    }
}
