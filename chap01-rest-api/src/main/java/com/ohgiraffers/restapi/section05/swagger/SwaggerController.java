package com.ohgiraffers.restapi.section05.swagger;

import com.ohgiraffers.restapi.section02.responseentity.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

/* comment. @Tag : 관련있는 API들의 그룹을 짓기 위한 어노테이션 */
// 스웨거 의존성 주입하면 @Tag 사용 가능
// 메서드 이름만 봐도 알 수 있지만, 더 상세한 설명을 위해 사용
@Tag(name = "Spring Boot Swagger 연동 (USER 관련 기능)")
@RestController
@RequestMapping("/swagger")
public class SwaggerController {

    private List<UserDTO> users;

    public SwaggerController() { // 가짜 DB에 들어있는 데이터들
        users = new ArrayList<>();

        users.add(new UserDTO(1, "user01", "pass01", "푸바오", LocalDate.now()));
        users.add(new UserDTO(2, "user02", "pass02", "후이오", LocalDate.now()));
        users.add(new UserDTO(3, "user03", "pass03", "루이바오", LocalDate.now()));
    }

    /* comment. @Operation란?
    *   해당하는 API의 정보를 기술하는 어노테이션
    *        <속성>
            - summary : 해당 API의 간단한 요약을 제공
            - description : 해당 API의 상세한 설명 제공 */
    @Operation(summary = "전체 회원 조회", description = "우리 사이트 전체 회원 목록 조회입니다.")
    @GetMapping("/users") // title 여기부터 section02 꺼 다 복사.
    public ResponseEntity<ResponseMessage> findAllUsers(){

        HttpHeaders headers = new HttpHeaders();

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

    @Operation(summary = "회원 번호로 회원 조회",
               description = "회원 번호로 특정 회원 정보를 조회합니다.",
               parameters = {@Parameter(name = "userNo", description = "사용자 화면에서 넘어오는 user의 PK")})
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

    @Operation(summary = "신규 회원 등록",
               description = "사용자 화면에서 받는 데이터로 회원 등록",
               parameters = {@Parameter(name = "newUser",description = "회원가입 관련 정보 DTO")})
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
                .created(URI.create("/swagger/users/" + users.get(users.size() - 1).getNo()))
                .build();
    }

    /* 수정 해보기 */
    @Operation(summary = "회원 정보 수정")
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

        return ResponseEntity.created(URI.create("/swagger/users/" + userNo)).build();
    }

    /* comment. @ApiResponses
    *       응답에 따라 상태코드와 상태에 대한 설명을 추가할 수 있다. */
    @Operation(summary = "회원 정보 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 정보 삭제 성공!"),
            @ApiResponse(responseCode = "400", description = "잘못된 파라미터")
    })
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
    }
}
