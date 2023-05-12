package com.gtpuser.gtpuser.Controller.models;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UserRes implements Serializable {
    private static final long serialVersionUID = -78127;
    private UUID id;
    private String name;
    private String phoneNumber;
    private String pic = "user/profile/pic/defaultPic.png";
    private Boolean active;
}
