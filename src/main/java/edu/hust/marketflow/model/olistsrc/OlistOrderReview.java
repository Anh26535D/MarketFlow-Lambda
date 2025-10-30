package edu.hust.marketflow.model.olistsrc;

public class OlistOrderReview {
    private String reviewId;
    private String orderId;
    private String reviewScore;
    private String reviewCommentTitle;
    private String reviewCommentMessage;
    private String reviewCreationDate;
    private String reviewAnswerTimestamp;

    public static int getFieldCount() {
        return 7;
    }

    public static OlistOrderReview fromArray(String [] data) {
        OlistOrderReview orderReview = new OlistOrderReview();
        orderReview.reviewId = data[0];
        orderReview.orderId = data[1];
        orderReview.reviewScore = data[2];
        orderReview.reviewCommentTitle = data[3];
        orderReview.reviewCommentMessage = data[4];
        orderReview.reviewCreationDate = data[5];
        orderReview.reviewAnswerTimestamp = data[6];
        return orderReview;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReviewAnswerTimestamp() {
        return reviewAnswerTimestamp;
    }

    public void setReviewAnswerTimestamp(String reviewAnswerTimestamp) {
        this.reviewAnswerTimestamp = reviewAnswerTimestamp;
    }

    public String getReviewCommentMessage() {
        return reviewCommentMessage;
    }

    public void setReviewCommentMessage(String reviewCommentMessage) {
        this.reviewCommentMessage = reviewCommentMessage;
    }

    public String getReviewCommentTitle() {
        return reviewCommentTitle;
    }

    public void setReviewCommentTitle(String reviewCommentTitle) {
        this.reviewCommentTitle = reviewCommentTitle;
    }

    public String getReviewCreationDate() {
        return reviewCreationDate;
    }

    public void setReviewCreationDate(String reviewCreationDate) {
        this.reviewCreationDate = reviewCreationDate;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(String reviewScore) {
        this.reviewScore = reviewScore;
    }
}
