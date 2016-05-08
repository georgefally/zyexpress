package net.zyexpress.site.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class User {
    @NotEmpty
    @Length(max = 16)
    private String userName;

    @NotEmpty
    @Length(max = 128)
    private String password;

    @NotNull
    private Boolean isAdmin = false;

    @NotNull
    private Boolean isApproved = false;

    public User() {
        // jackson deserialization
    }

    public User(String userName, String password, Boolean isAdmin, Boolean isApproved) {
        this.userName = userName;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    @JsonProperty
    public String getUserName() {
        return userName;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }
}
