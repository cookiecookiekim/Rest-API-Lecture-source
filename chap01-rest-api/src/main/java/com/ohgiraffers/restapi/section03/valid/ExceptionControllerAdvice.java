package com.ohgiraffers.restapi.section03.valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // 예전에 배웠는데 이런 게 있다..!
public class ExceptionControllerAdvice {

    // 사용자 정의의 예외가 발생했을 때 낚아채는 Handler
    // 낚아 채서 커스터마이징 가능하다.
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UserNotFoundException e){
                    // ErrorResponse 클래스 생성

        String code = "ERROR_CODE_001";
        String description = "없는 회원 정보입니다. 다시 확인해 주세요.";
        String detail = e.getMessage();

        return new ResponseEntity<>(new ErrorResponse(code, description, detail), HttpStatus.NOT_FOUND);
    }

    /* 아까 id 입력 안 하고 insert 했을 때 유효성 검사가 잘 들어 맞았고,
       MethodArgumentNotValidException가 출력 됐으니 얘도 예외처리 가능 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodValid(MethodArgumentNotValidException e){
        // 클래스에서 제공하는 데이터로 넣기 위해 빈 값 처리
        String code = "";
        String description = "";
        String detail = "";

        /* 유효성 검사 시 Error 발생하면? */
        if (e.getBindingResult().hasErrors()){ // 만약 bindingResult가 에러를 발생시킨다면
            detail = e.getBindingResult().getFieldError().getDefaultMessage();
            // 발생한 ErrorCode - ex) NotNull, NotBlank 등등
            String bindCode = e.getBindingResult().getFieldError().getCode();

            switch (bindCode){
                case "NotBlank" :
                    code = "ERROR_CODE_002";
                    description = "필수 값이 누락됐습니다."; break;
                case "Size" :
                    code = "ERROR_CODE_003";
                    description = "글자 수를 확인해 주세요."; break;
            }
        }
        return new ResponseEntity<>(new ErrorResponse(code, description,detail), HttpStatus.BAD_REQUEST);
    }

}
