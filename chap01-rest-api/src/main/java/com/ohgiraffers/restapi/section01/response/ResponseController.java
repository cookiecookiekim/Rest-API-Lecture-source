package com.ohgiraffers.restapi.section01.response;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller
@RestController
/* RestController = @Controller + @ResponseBody
 *   → 더이상 컨트롤러는 view를 반환하지 않고 Data만 응답할 것 */
@RequestMapping("/response")
public class ResponseController {

//    /* 문자열 응답 */ // @Controller 방식으로
//    @GetMapping("/hello")
//    @ResponseBody // 이거 붙이기 전에 response/hello 입장 시 404 에러
//                // ResponseBody 붙이니까 hello world!!! 출력
//    public String helloWorld(){
//        System.out.println("/hello 기존 방식 : 안녕하세요~!");
//        return "hello world!!!"; // view아닌 값(데이터)로 취급
//    }

    /* 문자열 응답 */ // @RestController 방식으로
    @GetMapping("/hello") //@ResponseBody 입력 안 해도 hello world!!! 출력
    public String helloWorld() {
        System.out.println("/hello 기존 방식 : 안녕하세요~!");
        return "hello world!!!"; // view아닌 값(데이터)로 취급
    }

    ///////////////////////////////
    /* 원시타입 Test */
    @GetMapping("/random")
    public int getRandom() {
        return (int) (Math.random() * 10) + 1;
    } // 페이지 들어가면 랜덤숫자 출력

    /* Object 타입 응답 */
    @GetMapping("/object")
    public Message getMessage() {
        return new Message(200, "정상 응답 성공");
        // join 타입으로 페이지에 출력 {"httpStatusCode":200,"message":"정상 응답 성공"}
    }

    /* List 타입 */
    @GetMapping("/list")
    public List<String> getList() {
        return List.of(new String[]{"햄버거", "피자", "치킨"});
    }

    /* Map 타입 응답 */
    @GetMapping("/map")
    public Map<Integer, String> getMap(){
        Map<Integer,String> map = new HashMap<>();
        map.put(200, "200 정상 응답");
        map.put(404, "404 찾을 수가 없습니다.");
        map.put(500, "500 개발자의 실수입니다.");
        return map;
    }

    /* comment. image 파일은 produce 설정하지 않으면 텍스트 형식으로 전송.
    *   produces 설정은 response header의 content-type(데이터 제공 형식)
    *   에 대한 설정이다. */
    /* 이미지 타입 응답 */
    @GetMapping(value = "/images", produces = MediaType.IMAGE_PNG_VALUE)
                                        // spring 제공 MediaType (이거 해야 사진 출력)
                                        // MediaType 설정하지 않으면 텍스트로 출력
    public byte[] getImage() throws IOException {
        // 이미지는 byte 배열 형식으로 이루어져 있음
        return getClass().getResourceAsStream("/images/cookie.png")
                        .readAllBytes(); // 예외처리 필수
    }
}
