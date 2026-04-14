package com.nhantic.trelloapi.exception;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.response.Response;
import com.nhantic.trelloapi.helper.MessageResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalHandleException {
    private final MessageResolver mr;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Response response = Response.builder()
                .success(false)
                .cause(errors)
                .code(ErrorMessageCode.BAD_REQUEST)
                .message(mr.resolve(ErrorMessageCode.BAD_REQUEST))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> handleApplicationException(ApplicationException ex) {
        Response res = Response.builder()
                .code(ex.getCode())
                .message(mr.resolve(ex.getCode()))
                .cause(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.getStatus()).body(res);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        Response res = Response.builder()
                .code(ErrorMessageCode.INTERNAL_SERVER_ERROR)
                .message(mr.resolve(ErrorMessageCode.INTERNAL_SERVER_ERROR))
                .cause(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}
