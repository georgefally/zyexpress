package net.zyexpress.site.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class User {
    @NotEmpty
    @Length(max = 16)
    private String name;

    @NotEmpty
    @Length(max = 128)
    private String password;

    public User() {
        // jackson deserialization
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }
}
