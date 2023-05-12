package com.gtpuser.gtpuser.Controller.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserReq implements Serializable {

    private static final long serialVersionUID = -728299;
    private String name;
    private String phoneNumber;
}
