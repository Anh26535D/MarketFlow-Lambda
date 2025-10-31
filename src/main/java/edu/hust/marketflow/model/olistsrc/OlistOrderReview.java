package edu.hust.marketflow.model.olistsrc;

public class OlistOrderReview {
    private String reviewId;
    private String orderId;
    private String reviewScore;
    private String reviewCommentTitle;
    private String reviewCommentMessage;
    private String reviewCreationDate;
    private String reviewAnswerTimestamp;

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
