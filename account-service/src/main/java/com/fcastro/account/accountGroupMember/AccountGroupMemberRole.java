package com.fcastro.account.accountGroupMember;

public enum AccountGroupMemberRole {
    ADMIN("ADMIN"),
    USER("USER");

    public final String value;

    AccountGroupMemberRole(String value) {
        this.value = value;
    }
}
