package edu.hust.marketflow.model.olistsrc;

public class OlistOrderPayment {
    private String orderId;
    private String paymentSequential;
    private String paymentType;
    private String paymentInstallments;
    private String paymentValue;

    public static int getFieldCount() {
        return 5;
    }

    public static OlistOrderPayment fromArray(String [] data) {
        OlistOrderPayment orderPayment = new OlistOrderPayment();
        orderPayment.orderId = data[0];
        orderPayment.paymentSequential = data[1];
        orderPayment.paymentType = data[2];
        orderPayment.paymentInstallments = data[3];
        orderPayment.paymentValue = data[4];
        return orderPayment;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentInstallments() {
        return paymentInstallments;
    }

    public void setPaymentInstallments(String paymentInstallments) {
        this.paymentInstallments = paymentInstallments;
    }

    public String getPaymentSequential() {
        return paymentSequential;
    }

    public void setPaymentSequential(String paymentSequential) {
        this.paymentSequential = paymentSequential;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentValue() {
        return paymentValue;
    }

    public void setPaymentValue(String paymentValue) {
        this.paymentValue = paymentValue;
    }
}
