package com.ohgiraffers.restapi.section02.responseentity;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/entity")
public class ResponseEntityController {

    /* comment. ResponseEntity 란?
     *   결과 데이터 & HTTP 상태 코드를 직접 제어할 수 있는 클래스
     *   내부에 HttpStatus, HttpHeaders, HttpBody를 포함. */
    // 굳이 사용하지 않아도 되지만 정형화 한 것.... 그냥 객체만 보내도 되긴 하는데
    // 그러면 커스터마이징이 불가...

    // 테스트용 임시 DB 만들기
    private List<UserDTO> users;

    /* 임시 DB에서 조회한 값 설정 */
    public ResponseEntityController() {
        users = new ArrayList<>();
        // 해당 클래스 동작할 때 기본 생성자가 제일 먼저 동작
        // 동작할 때 해당 데이터가 삽입되게.
        users.add(new UserDTO(1, "user01", "pass01", "너구리", LocalDate.now()));
        users.add(new UserDTO(2, "user02", "pass02", "푸바오", LocalDate.now()));
        users.add(new UserDTO(3, "user03", "pass03", "러바오", LocalDate.now()));
    }

    @GetMapping("/users") // ResponseMessage 클래스 생성
    public ResponseEntity<ResponseMessage> findAllUsers(){

        // header : 데이터 타입 결정
        HttpHeaders headers = new HttpHeaders();

        // 응답할 데이터의 양식 지정 (json 객체 방식 , 인코딩 관련)
        headers.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        Map<String , Object> responseMap = new HashMap<>();
        responseMap.put("user", users);

        ResponseMessage responseMessage = new ResponseMessage(
                200,"조회 성공", responseMap
        );
        return new ResponseEntity<>(responseMessage, headers, HttpStatus.OK); // ok: 200
                     // 굳이 responseMessage 넣은 이유 : 확장성
                     // 예를 들어 상세조회 했을 때, 상세조회 성공이라는 메시지를 사용할 수 있음.
    }

    @GetMapping("/users/{userNo}")
    public ResponseEntity<ResponseMessage> findUserByNo(@PathVariable int userNo){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        // 지금은 가짜 db로 조회하는 것 나중에는 findById 로 가져오면 됨 (지금 로직 컷)
        UserDTO foundUser =
                users.stream().filter(user -> user.getNo() == userNo)// 받고있는 no와 db상 no일치 여부
                              .collect(Collectors.toList()) // 배열 형태로 만들기
                              .get(0);                     // get으로 0번 인텍스 꺼내주기

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("users", foundUser);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200,"조회성공",responseMap));
    }

        /* comment. form 태그로 데이터 전달 받는 것과
            javaScript로 데이터를 전달 받는 것은 다르다. */

    @PostMapping("/user/regist")
    // ?는 와일드 카드 처리로 아무 값이나 다 된다.
    public ResponseEntity<?> regist(@RequestBody UserDTO newUser){
        // js 방식으로 화면에서 넘어오는 데이터 받기 : @RequestBody
        System.out.println("Json 데이터 @RequestBody로 들어 오는지 확인 : " + newUser);

        // List에 들어있는 마지막 no 가져오기 (위에 3개 있으니까 3이라는 뜻)
        // 여기 코드는 신경쓰지 말기!! (db 연결 안 해서 이렇게 한 거!!)
        int lastNo = users.get(users.size() - 1).getNo();
        newUser.setNo(lastNo + 1);

        return ResponseEntity
                // 201 상태코드 -> 등록 관련(자원 생성 관련) 상태코드
                .created(URI.create("/entity/users/" + users.get(users.size() - 1).getNo()))
                .build();
    }

    /* 수정 해보기 */
    @PutMapping("/users/{userNo}")
    public ResponseEntity<?> modifyUser (@PathVariable int userNo,
                                         @RequestBody UserDTO modifyInfo){
                                    // postman에서 modifyInfo 입력
        System.out.println("userNo = " + userNo);
        System.out.println("modifyInfo = " + modifyInfo);
        // 1단계, 회원 정보 수정을 위한 유저 특정하기
        UserDTO foundUser =
                users.stream().filter(user -> user.getNo() == userNo)
                        .collect(Collectors.toList()).get(0);

        // id, pwd, name 수정하기
        foundUser.setId(modifyInfo.getId());
        foundUser.setPwd(modifyInfo.getPwd());
        foundUser.setName(modifyInfo.getName());

        return ResponseEntity.created(URI.create("/entity/users/" + userNo)).build();
    }

    @DeleteMapping("/users/{userNo}")
    public ResponseEntity<?> removeUser(@PathVariable int userNo){
        System.out.println("userNo = " + userNo);
        // userNo 1명 특정
        UserDTO foundUser =
                users.stream().filter(user -> user.getNo() == userNo)
                        .collect(Collectors.toList()).get(0);

        // 특정한 유저 객체 배열에서 삭제
        users.remove(foundUser);

        // 자원 삭제 관련 noContent()
        return ResponseEntity.noContent().build();
        // 정상 삭제 됐다면 postman에서 204 no Content 확인
    }
}
