package com.herosoft.movie.dto;

import java.io.Serializable;

public class UserDto implements Serializable {
    private Integer id;
    private String username;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", balance=" + balance +
                '}';
    }

    private String password;
    private String sex;
    private Double balance;

    public UserDto() {
    }

    public UserDto(Integer id, String username, String password, String sex, Double balance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.sex = sex;
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
