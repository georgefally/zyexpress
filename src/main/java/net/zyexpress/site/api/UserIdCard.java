package net.zyexpress.site.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class UserIdCard {
    @NotEmpty
    @Length(max = 16)
    private String userName;

    @NotEmpty
    @Length(max = 128)
    private String idNumber;

    @NotEmpty
    @Length(max = 128)
    private String picFirst;

    @NotEmpty
    @Length(max = 128)
    private String picSecond;

    public UserIdCard() {
        // jackson deserialization
    }

    public UserIdCard(String userName, String idNumber, String picFirst, String picSecond) {
        this.userName = userName;
        this.idNumber = idNumber;
        this.picFirst = picFirst;
        this.picSecond = picSecond;
    }

    @JsonProperty
    public String getUserName() {
        return userName;
    }

    @JsonProperty
    public String getIdNumber() {
        return idNumber;
    }

    @JsonProperty
    public String getPicFirst() {
        return picFirst;
    }

    @JsonProperty
    public String getPicSecond() {
        return picSecond;
    }
}
