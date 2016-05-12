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
    private Integer id;

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

    @NotEmpty
    @Length(max = 128)
    private String province;

    @NotEmpty
    @Length(max = 128)
    private String city;

    @NotEmpty
    @Length(max = 128)
    private String area;

    @NotEmpty
    @Length(max = 128)
    private String street;

    @NotNull
    private Boolean isDefault = false;

    public Address() {
    }

    public Address(Integer id, String accountName, String receiverName, String address, String phoneNumber, String postcode, Boolean isDefault,String province,String city,String area,String street) {
        this.id = id;
        this.accountName = accountName;
        this.receiverName = receiverName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.postcode = postcode;
        this.isDefault = isDefault;
        this.province = province;
        this.city = city;
        this.area = area;
        this.street = street;
    }

    public Address(String accountName, String receiverName, String address, String phoneNumber, String postcode, Boolean isDefault,String province,String city,String area,String street) {
        this.accountName = accountName;
        this.receiverName = receiverName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.postcode = postcode;
        this.isDefault = isDefault;
        this.province = province;
        this.city = city;
        this.area = area;
        this.street = street;
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
    @JsonProperty
    public String getProvince() {
        return province;
    }
    @JsonProperty
    public String getCity() {
        return city;
    }
    @JsonProperty
    public String getArea() {
        return area;
    }
    @JsonProperty
    public String getStreet() {
        return street;
    }
}

