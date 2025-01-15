package com.ohgiraffers.restapi.section03.valid;

import com.ohgiraffers.restapi.section02.responseentity.ResponseMessage;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/valid") // 유효성 검사 (선택사항)
public class ValidTestController {
    // 화면에서 검사할 것인가 / 백으로 가져와서 db와 매칭하며 검사할 것인가
    // → 사용자가 에러페이지 보지 못하게 처리하는...? 예외처리같은?

    // 테스트용 임시 DB 만들기
    private List<UserDTO> users;

    /* 임시 DB에서 조회한 값 설정 */
    public ValidTestController() {
        users = new ArrayList<>();
        // 해당 클래스 동작할 때 기본 생성자가 제일 먼저 동작
        // 동작할 때 해당 데이터가 삽입되게.
        users.add(new UserDTO(1, "user01", "pass01", "너구리", LocalDate.now()));
        users.add(new UserDTO(2, "user02", "pass02", "푸바오", LocalDate.now()));
        users.add(new UserDTO(3, "user03", "pass03", "러바오", LocalDate.now()));
    }

    @GetMapping("/user/{userNo}")// 예를 들어 db에 없는 10번을 넣는다고 가정 시 500 에러인데 이걸 컨트롤
    public ResponseEntity<ResponseMessage> findByNo(@PathVariable int userNo) throws UserNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        List<UserDTO> foundUserList =
                users.stream().filter(user -> user.getNo() == userNo)
                        .collect(Collectors.toList());

        UserDTO foundUser = null;
        // userNo 조회 시 조회됨
        if (foundUserList.size() > 0) {
            foundUser = foundUserList.get(0);
        } else {
            throw new UserNotFoundException("회원 정보를 찾을 수 없습니다.");
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("user", foundUser);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200, "조회 성공", responseMap));
    }

    @PostMapping("/user/regist")
    public ResponseEntity<?> registUser (@Valid @RequestBody UserDTO userDTO){
                                    //유효성 검사 하려면 @Valid 넣어 줘야 한다.
        System.out.println("userDTO 잘 받아 오는지 = " + userDTO);
        return ResponseEntity.created(URI.create("valid/user/" + userDTO.getNo())).build();
    }
}
