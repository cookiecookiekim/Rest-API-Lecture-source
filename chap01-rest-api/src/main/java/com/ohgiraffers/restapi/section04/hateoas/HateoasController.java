package com.ohgiraffers.restapi.section04.hateoas;

import com.ohgiraffers.restapi.section02.responseentity.ResponseMessage;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// 25-01-16 (목) 1교시
@RestController
@RequestMapping("/hateoas")
public class HateoasController {

    /* comment. Hateoas란?
     *   하나의 API를 동작시켰을 때, 그 전 상황이나 그 후 상황에
     *   대한 충분한 정보(링크)를 제공함으로써
     *   클라이언트가 리소스(자원)에 대한 이해를 더욱 잘 할 수 있게
     *   링크를 제공하는 것,
     *   RestFul API라고도 한다. -> 정보가 충분한.
     *   EX) 전체 조회할 때 상세조회 할 수 있는 로직을 링크로 남겨주는 것?!
     *          → localhost:8080/user/1    */

    // 가짜 DB 구축하기
    private List<UserDTO> users;

    public HateoasController() { // 가짜 DB에 들어있는 데이터들
        users = new ArrayList<>();

        users.add(new UserDTO(1, "user01", "pass01", "푸바오", LocalDate.now()));
        users.add(new UserDTO(2, "user02", "pass02", "후이오", LocalDate.now()));
        users.add(new UserDTO(3, "user03", "pass03", "루이바오", LocalDate.now()));
    }

    // 기본 방식으로 조회해보기
    @GetMapping("/users/{userNo}")
    public ResponseEntity<ResponseMessage> findUserByNo(@PathVariable int userNo){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        UserDTO foundUser =
                users.stream().filter(user -> user.getNo() == userNo)
                        .collect(Collectors.toList())
                        .get(0);

        Map<String, Object> response = new HashMap<>();
        response.put("user", foundUser);

        return ResponseEntity
                .ok() // 조회 성공 ok()
                .headers(headers) // 헤더 정보 전달
                .body(new ResponseMessage(200,"조회성공",response));
    }

    /* Hateoas 적용 전체 조회 API */
    @GetMapping("/users")
    public ResponseEntity<ResponseMessage> findAllUsers(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        /* comment. EntityModel이란?
        *   Hateoas에서 제공하는 클래스로 RestAPI를 구성할 때
        *   해당 리소스와 연관이 있는 링크를 포함할 수 있도록 함.*/
        // 전체 조회니까 List로 감싼 객체 보내야 함.
        // Hateoas 사용위해 EntityModel(Spring Hateoas) 의존성 받아야 함.
        List<EntityModel<UserDTO>> userLink // UserDTO에 대해서만 Link를 걸겠다.
                = users.stream().map( // 이번엔 filter 말고 map으로 설정
                   user -> EntityModel.of( // user라는 변수에 하나씩 담기
                           user,
                           // 임포트 받을 때 Builder()로 받기
                           /* withSelfRel() : 특정 리소스를 조회하는 URL 링크를 제공함으로써
                                특정 리소스에 대한 정보를 알 수 있다. (나 자신의 대한 정보) */
                           linkTo(methodOn(HateoasController.class).findUserByNo(user.getNo())).withSelfRel(),
                           /* withRel() : 현재 리소스와 관련된 관계를 나타낸다.
                           *    즉, user 한 명은 users라는 리소스에 포함 */
                           linkTo(methodOn(HateoasController.class).findAllUsers()).withRel("users")
                   )
        ).collect(Collectors.toList());

        // 위에서 만든 데이터 응답 양식에 맞춰 넣기
        Map<String ,Object> response = new HashMap<>();
        response.put("users" , userLink);

        ResponseMessage message =
                new ResponseMessage(200 , "hate 조회 성공", response);

        return new ResponseEntity<>(message,headers, HttpStatus.OK);
    }
}
