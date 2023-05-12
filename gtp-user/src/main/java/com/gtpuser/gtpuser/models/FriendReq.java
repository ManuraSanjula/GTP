package com.gtpuser.gtpuser.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class FriendReq implements Serializable {
    private static final long serialVersionUID = 91939128L;
    @Id
    private UUID id;
    private UserForReq from;
    private UserForReq to;
}
