package net.zyexpress.site.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

public class Address {

    @NotEmpty
    @Length(max = 16)
    private String accountName;

    @NotEmpty
    @Length(max = 16)
    private String receiverName;

    @NotEmpty
    @Length(max = 128)
    private String phoneNumber;

    @NotEmpty
    @Length(max = 128)
    private String address;

    @NotEmpty
    @Length(max = 128)
    private String postcode;

    @NotNull
    private Boolean isDefault = false;

    public Address() {
    }

    public Address(String accountName, String receiverName, String address, String phoneNumber, String postcode, Boolean isDefault) {
        this.accountName = accountName;
        this.receiverName = receiverName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.postcode = postcode;
        this.isDefault = isDefault;
    }
    @JsonProperty
    public String getAccountName() {
        return accountName;
    }
    @JsonProperty
    public String getAddress() {
        return address;
    }
    @JsonProperty
    public String getPhoneNumber() {
        return phoneNumber;
    }
    @JsonProperty
    public String getPostcode() {
        return postcode;
    }
    @JsonProperty
    public Boolean getDefault() {
        return isDefault;
    }
    @JsonProperty
    public String getReceiverName() {
        return receiverName;
    }
}

