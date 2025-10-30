package edu.hust.marketflow.model.olistsrc;

public class OlistOrder {
    private String orderId;
    private String customerId;
    private String orderStatus;
    private String orderPurchaseTimestamp;
    private String orderApprovedAt;
    private String orderDeliveredCarrierDate;
    private String orderDeliveredCustomerDate;
    private String orderEstimatedDeliveryDate;

    public static int getFieldCount() {
        return 8;
    }

    public static OlistOrder fromArray(String [] data) {
        OlistOrder order = new OlistOrder();
        order.orderId = data[0];
        order.customerId = data[1];
        order.orderStatus = data[2];
        order.orderPurchaseTimestamp = data[3];
        order.orderApprovedAt = data[4];
        order.orderDeliveredCarrierDate = data[5];
        order.orderDeliveredCustomerDate = data[6];
        order.orderEstimatedDeliveryDate = data[7];
        return order;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderApprovedAt() {
        return orderApprovedAt;
    }

    public void setOrderApprovedAt(String orderApprovedAt) {
        this.orderApprovedAt = orderApprovedAt;
    }

    public String getOrderDeliveredCarrierDate() {
        return orderDeliveredCarrierDate;
    }

    public void setOrderDeliveredCarrierDate(String orderDeliveredCarrierDate) {
        this.orderDeliveredCarrierDate = orderDeliveredCarrierDate;
    }

    public String getOrderDeliveredCustomerDate() {
        return orderDeliveredCustomerDate;
    }

    public void setOrderDeliveredCustomerDate(String orderDeliveredCustomerDate) {
        this.orderDeliveredCustomerDate = orderDeliveredCustomerDate;
    }

    public String getOrderEstimatedDeliveryDate() {
        return orderEstimatedDeliveryDate;
    }

    public void setOrderEstimatedDeliveryDate(String orderEstimatedDeliveryDate) {
        this.orderEstimatedDeliveryDate = orderEstimatedDeliveryDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderPurchaseTimestamp() {
        return orderPurchaseTimestamp;
    }

    public void setOrderPurchaseTimestamp(String orderPurchaseTimestamp) {
        this.orderPurchaseTimestamp = orderPurchaseTimestamp;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
