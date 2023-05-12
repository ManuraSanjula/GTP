package com.gtpuser.gtpuser.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Document
public class Friends implements Serializable {
    private static final long serialVersionUID = 7872277487321319L;
    @Id
    private UUID id;
    @Builder.Default
    private Boolean accountNonLocked = true;
    @Builder.Default
    private Boolean accountNonExpired = true;
    private String name;
    private String phoneNumber;
    private Boolean phoneNumberVerify;
    private String pic;
    private Boolean active;
}
