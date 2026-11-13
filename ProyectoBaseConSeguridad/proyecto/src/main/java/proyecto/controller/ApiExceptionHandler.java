package proyecto.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField()+": "+error.getDefaultMessage())
            .collect(Collectors.joining("; "));
        return response(HttpStatus.BAD_REQUEST,detail);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String,Object>> login(AuthenticationException ex) {
        return response(HttpStatus.UNAUTHORIZED,"Credenciales incorrectas o cuenta bloqueada");
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> conflict(DataIntegrityViolationException ex) {
        return response(HttpStatus.CONFLICT,"El usuario ya existe o la operación entra en conflicto con otros datos");
    }
    private ResponseEntity<Map<String,Object>> response(HttpStatus status,String message) {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("message",message);
        return ResponseEntity.status(status).body(body);
    }
}
