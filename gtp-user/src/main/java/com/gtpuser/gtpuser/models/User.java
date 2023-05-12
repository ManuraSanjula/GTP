package com.gtpuser.gtpuser.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.Serializable;
import java.util.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document
public class User implements Serializable, UserDetails, CredentialsContainer {
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
    @Builder.Default
    private String pic = "user/profile/pic/defaultPic.png";
    @Builder.Default
    private Boolean active = true;
    @Builder.Default
    private List<String> roles = new ArrayList<>();
    @Builder.Default
    private List<String> authorities = new ArrayList<>();
    @Builder.Default
    private List<Friends> friends =  new ArrayList<>();
    @Builder.Default
    private List<Fans> fans =  new ArrayList<>();
    private List<FriendReq> friendReqs =  new ArrayList<>();
    @Override
    public void eraseCredentials() {
        // TODO Auto-generated method stub
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> user_authorities = new ArrayList<>();
        roles.forEach(role->{
            user_authorities.add(new SimpleGrantedAuthority(role));
        });
        authorities.forEach(auth->{
            user_authorities.add(new SimpleGrantedAuthority(auth));
        });
        return user_authorities;
    }
    @Override
    public String getPassword() {
        return null;
    }
    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return this.phoneNumber;
    }
    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return this.phoneNumberVerify;
    }
    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return this.active;
    }
}

