package org.project.trandit.global.exception;

public class JwtValidationException extends RuntimeException{
    // 문자열 메시지만 받는 생성자
    public JwtValidationException(String message) {
        super(message);
    }
    public JwtValidationException(String message, Throwable cause){
        super(message, cause);
    }
}
