package com.ohgiraffers.restapi.section03.valid;

public class UserNotFoundException extends Throwable {

    public UserNotFoundException(String message) {
        super(message); // 부모 생성자에게 출력 구문 생성 시키기?
    }
}
