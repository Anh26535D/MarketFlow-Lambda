package edu.hust.marketflow.model.hnmsrc;

public class HnMTransaction {
    private String tDat;
    private String customerId;
    private String articleId;
    private double price;
    private String salesChannelId;

    public static int getFieldCount() {
        return 5;
    }

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

    public String gettDat() {
        return tDat;
    }

    public void settDat(String tDat) {
        this.tDat = tDat;
    }

    public static HnMTransaction fromArray(String[] p) {
        HnMTransaction t = new HnMTransaction();
        t.tDat = p[0];
        t.customerId = p[1];
        t.articleId = p[2];
        t.price = Double.parseDouble(p[3]);
        t.salesChannelId = p[4];
        return t;
    }
}
