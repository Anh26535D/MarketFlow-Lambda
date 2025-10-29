package edu.hust.marketflow.model.hnmsrc;

public class HnMCustomer {
    private String customerId;
    private String fn;
    private String active;
    private String clubMemberStatus;
    private String fashionNewsFrequency;
    private String age;
    private String postalCode;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getClubMemberStatus() {
        return clubMemberStatus;
    }

    public void setClubMemberStatus(String clubMemberStatus) {
        this.clubMemberStatus = clubMemberStatus;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFashionNewsFrequency() {
        return fashionNewsFrequency;
    }

    public void setFashionNewsFrequency(String fashionNewsFrequency) {
        this.fashionNewsFrequency = fashionNewsFrequency;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public static int getFieldCount() {
        return 7;
    }

    public static HnMCustomer fromArray(String[] p) {
        HnMCustomer c = new HnMCustomer();
        c.customerId = p[0];
        c.fn = p[1];
        c.active = p[2];
        c.clubMemberStatus = p[3];
        c.fashionNewsFrequency = p[4];
        c.age = p[5];
        c.postalCode = p[6];
        return c;
    }
}
