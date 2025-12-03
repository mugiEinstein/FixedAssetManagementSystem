package ztt.fixedassetmanagement.common;

import lombok.extern. slf4j. Slf4j;
import org.springframework. http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind. annotation.ExceptionHandler;
import org. springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web. bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log. error("业务异常: {}", e.getMessage());
        return Result. error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e. getBindingResult(). getFieldError().getDefaultMessage()
                : "参数验证失败";
        return Result.error(400, message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError(). getDefaultMessage()
                : "参数绑定失败";
        return Result.error(400, message);
    }

    @ExceptionHandler(Exception. class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error("系统内部错误，请联系管理员");
    }
}