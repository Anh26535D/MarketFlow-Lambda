package edu.hust.marketflow.model.hnmsrc;

public class HnMTransaction {
    private String tDat;
    private String customerId;
    private String articleId;
    private double price;
    private String salesChannelId;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSalesChannelId() {
        return salesChannelId;
    }

    public void setSalesChannelId(String salesChannelId) {
        this.salesChannelId = salesChannelId;
    }

    public String getTDat() {
        return tDat;
    }

    public void settDat(String tDat) {
        this.tDat = tDat;
    }
}
