package com.fcastro.accountservice.accountgroupmember;

public enum AccountGroupMemberRole {
    ADMIN("ADMIN"),
    USER("USER"),
    OWNER("OWNER");

    public final String value;

    AccountGroupMemberRole(String value) {
        this.value = value;
    }
}
