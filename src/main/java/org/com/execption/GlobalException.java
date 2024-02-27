package org.com.execption;

import org.com.entity.Result;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    @ResponseBody
    public Result sqlIntegrityConstraintViolationException(HttpServletRequest request, SQLIntegrityConstraintViolationException e) {
        return Result.error("用户名重复");
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result handleValidationException(BindException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(value = MyException.class)
    @ResponseBody
    public Result myException(HttpServletRequest request, MyException e) {
        return Result.error(e.getMessage());
    }
}
