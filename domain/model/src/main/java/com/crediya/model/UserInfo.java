package com.crediya.model;

import java.math.BigDecimal;
import java.util.Objects;

public class UserInfo {
    private final String email;
    private final String name;
    private final BigDecimal baseSalary;

    public UserInfo(String email, String name, BigDecimal baseSalary) {
        this.email = email;
        this.name = name;
        this.baseSalary = baseSalary;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(email, userInfo.email) &&
                Objects.equals(name, userInfo.name) &&
                Objects.equals(baseSalary, userInfo.baseSalary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, baseSalary);
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", baseSalary=" + baseSalary +
                '}';
    }
}