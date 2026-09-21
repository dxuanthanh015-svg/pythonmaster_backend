package com.thanhhandsome.pythonmaster.exception;

import com.thanhhandsome.pythonmaster.dto.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 400 — Lỗi bind dữ liệu lọc từ request (param ngày tháng, số, v.v.) */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException ex) {
        boolean hasDateError = ex.getFieldErrors().stream()
                .anyMatch(fe -> fe.getField().equalsIgnoreCase("from") || fe.getField().equalsIgnoreCase("to")
                        || (fe.getCode() != null && fe.getCode().contains("typeMismatch")));

        if (hasDateError) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "Định dạng ngày tháng không hợp lệ. Vui lòng sử dụng YYYY-MM-DD."));
        }

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, message.isBlank() ? "Dữ liệu yêu cầu không hợp lệ." : message));
    }

    /** 400 — Lỗi type mismatch (truyền sai kiểu date hoặc id trên request param) */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if ("from".equalsIgnoreCase(ex.getName()) || "to".equalsIgnoreCase(ex.getName())
                || ex.getRootCause() instanceof DateTimeParseException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "Định dạng ngày tháng không hợp lệ. Vui lòng sử dụng YYYY-MM-DD."));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "Tham số '" + ex.getName() + "' không đúng định dạng."));
    }

    /** 400 — Lỗi logic do code ném ra */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, ex.getMessage()));
    }

    /** 4xx/5xx — Lỗi có status tường minh từ service */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException ex) {
        int code = ex.getStatusCode().value();
        String message = ex.getReason() != null ? ex.getReason() : "Yêu cầu không hợp lệ.";
        return ResponseEntity.status(code).body(ApiResponse.error(code, message));
    }

    /** 401 — JWT không hợp lệ */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwt(JwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."));
    }

    /** 500 — Lỗi server không mong muốn */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau."));
    }
}
