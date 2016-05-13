package net.zyexpress.site.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

public class IdCard{

    @NotEmpty
    private Integer id;

    @NotEmpty
    @Length(max = 16)
    private String accountName;

    @NotEmpty
    @Length(max = 16)
    private String idCardName;

    @NotEmpty
    @Length(max = 128)
    private String idCardNumber;

    @NotNull
    private Boolean isApproved = false;

    public IdCard() {
    }

    public IdCard(Integer id, String accountName, String idCardName, String idCardNumber, Boolean isApproved) {
        this.id = id;
        this.accountName = accountName;
        this.idCardName = idCardName;
        this.idCardNumber = idCardNumber;
        this.isApproved = isApproved;
    }

    public IdCard(Integer id, String accountName, String idCardName, String idCardNumber) {
        this.id = id;
        this.accountName = accountName;
        this.idCardName = idCardName;
        this.idCardNumber = idCardNumber;
    }

    @JsonProperty
    public int getId() {
        return id;
    }
    @JsonProperty
    public String getAccountName() {
        return accountName;
    }
    @JsonProperty
    public String getIdCardName() {
        return idCardName;
    }
    @JsonProperty
    public String getIdCardNumber() {
        return idCardNumber;
    }
    @JsonProperty
    public Boolean getApproved() {
        return isApproved;
    }
}

