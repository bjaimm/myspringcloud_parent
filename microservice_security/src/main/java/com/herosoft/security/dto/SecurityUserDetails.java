package com.herosoft.security.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Setter
public class SecurityUserDetails implements UserDetails {

    //@JSONField(deserializeUsing = GrantedAuthorityDeserialize.class)
    private List<GrantedAuthority> authorities;
    private String username;
    private String password;
    //customized的信息 start
    private Double balance;
    private Integer userId;
    //customized的信息 end

    private boolean accountNonExpired ;
    private boolean accountNonLocked ;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public Double getBalance() {
        return balance;
    }

    public Integer getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
