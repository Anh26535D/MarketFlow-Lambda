package edu.hust.marketflow.model.olistsrc;

public class OlistCustomer {
    private String customerId;
    private String customerUniqueId;
    private String customerZipCodePrefix;
    private String customerCity;
    private String customerState;

    public static int getFieldCount() {
        return 5;
    }

    public static OlistCustomer fromArray(String [] data) {
        OlistCustomer customer = new OlistCustomer();
        customer.customerId = data[0];
        customer.customerUniqueId = data[1];
        customer.customerZipCodePrefix = data[2];
        customer.customerCity = data[3];
        customer.customerState = data[4];
        return customer;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public String getCustomerUniqueId() {
        return customerUniqueId;
    }

    public void setCustomerUniqueId(String customerUniqueId) {
        this.customerUniqueId = customerUniqueId;
    }

    public String getCustomerZipCodePrefix() {
        return customerZipCodePrefix;
    }

    public void setCustomerZipCodePrefix(String customerZipCodePrefix) {
        this.customerZipCodePrefix = customerZipCodePrefix;
    }
}
