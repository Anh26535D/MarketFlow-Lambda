package edu.hust.marketflow.model.olistsrc;

public class OlistOrderPayment {
    private String orderId;
    private String paymentSequential;
    private String paymentType;
    private String paymentInstallments;
    private String paymentValue;

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
