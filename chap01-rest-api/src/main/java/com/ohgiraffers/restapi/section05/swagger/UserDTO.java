package com.ohgiraffers.restapi.section05.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Schema(description = "회원 정보 관련 DTO")
public class UserDTO {

    @Schema(description = "회원정보(PK)")
    private int no;

    @Schema(description = "회원 ID", example = "user123")
    private String id;

    @Schema(description = "회원 비밀번호")
    private String pwd;

    @Schema(description = "회원 성명" , example = "김규남")
    private String name;

    @Schema(description = "회원 등록 일시", example = "2025-01-16")
    private LocalDate enrollDate;

}
