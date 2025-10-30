package edu.hust.marketflow.model.olistsrc;

public class OlistOrderItem {
    private String orderId;
    private String orderItemId;
    private String productId;
    private String sellerId;
    private String shippingLimitDate;
    private String price;
    private String freightValue;

    public static int getFieldCount() {
        return 7;
    }

    public static OlistOrderItem fromArray(String [] data) {
        OlistOrderItem orderItem = new OlistOrderItem();
        orderItem.orderId = data[0];
        orderItem.orderItemId = data[1];
        orderItem.productId = data[2];
        orderItem.sellerId = data[3];
        orderItem.shippingLimitDate = data[4];
        orderItem.price = data[5];
        orderItem.freightValue = data[6];
        return orderItem;
    }

    public String getFreightValue() {
        return freightValue;
    }

    public void setFreightValue(String freightValue) {
        this.freightValue = freightValue;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getShippingLimitDate() {
        return shippingLimitDate;
    }

    public void setShippingLimitDate(String shippingLimitDate) {
        this.shippingLimitDate = shippingLimitDate;
    }
}
