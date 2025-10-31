package edu.hust.marketflow.model.olistsrc;

public class OlistSeller {
    private String sellerId;
    private String sellerZipCodePrefix;
    private String sellerCity;
    private String sellerState;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerCity() {
        return sellerCity;
    }

    public void setSellerCity(String sellerCity) {
        this.sellerCity = sellerCity;
    }

    public String getSellerState() {
        return sellerState;
    }

    public void setSellerState(String sellerState) {
        this.sellerState = sellerState;
    }

    public String getSellerZipCodePrefix() {
        return sellerZipCodePrefix;
    }

    public void setSellerZipCodePrefix(String sellerZipCodePrefix) {
        this.sellerZipCodePrefix = sellerZipCodePrefix;
    }
}
