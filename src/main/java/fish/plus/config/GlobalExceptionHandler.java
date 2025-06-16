package fish.plus.config;

import fish.plus.data.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> globalException(Exception exception) {
        log.error("业务异常:{},{}", exception.getMessage(), exception);
        return Result.fail(500, exception.getMessage());
    }
    /**
     * Method argument not valid handler object.
     *
     * @param exception the exception
     * @return the object
     * @throws Exception the exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);
        return Result.fail(500, Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
    }



    /**
     * Illegal argument exception wrapper.
     *
     * @param exception the exception
     * @return the wrapper
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> IllegalArgumentException(IllegalArgumentException exception) {
        log.error("参数非法异常:{},{}", exception.getMessage(), exception);
        return Result.fail(500, exception.getMessage());
    }

    /**
     * Not fount wrapper.
     *
     * @param exception the exception
     * @return the wrapper
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> businessException(BusinessException exception) {
        log.error("业务异常:{},{}", exception.getMessage(), exception);
        return Result.fail(exception.getCode(), exception.getMessage());
    }


    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> businessException(MultipartException exception) {
        log.error("业务异常:{},{}", exception.getMessage(), exception);
        return Result.fail(500, exception.getMessage());
    }
}
