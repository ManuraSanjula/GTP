package com.gtpuser.gtpuser.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document
public class UserForReq {
    private static final long serialVersionUID = 6994849949494494L;
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
