package com.training.librarymanagementtraining.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;

}