package net.zyexpress.site.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class User {
    @NotEmpty
    @Length(max = 16)
    private String userName;

    @NotEmpty
    @Length(max = 128)
    private String password;

    public User() {
        // jackson deserialization
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @JsonProperty
    public String getUserName() {
        return userName;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }
}
