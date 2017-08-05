package org.icememo.api

import org.icememo.Result
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletResponse

@ControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler(value = Throwable)
    @ResponseBody
    public ResponseEntity<Result> globalHandler(Throwable e, HttpServletResponse response){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));

       return new ResponseEntity<Result>(Result.getError(errors.toString()),HttpStatus.BAD_REQUEST);
    }
}