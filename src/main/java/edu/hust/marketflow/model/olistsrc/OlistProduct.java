package edu.hust.marketflow.model.olistsrc;

public class OlistProduct {
    private String productId;
    private String productCategoryName;
    private String productNameLenght;
    private String productDescriptionLenght;
    private String productPhotosQty;
    private String productWeightG;
    private String productLengthCm;
    private String productHeightCm;
    private String productWidthCm;

    public static int getFieldCount() {
        return 9;
    }

    public static OlistProduct fromArray(String [] data) {
        OlistProduct product = new OlistProduct();
        product.productId = data[0];
        product.productCategoryName = data[1];
        product.productNameLenght = data[2];
        product.productDescriptionLenght = data[3];
        product.productPhotosQty = data[4];
        product.productWeightG = data[5];
        product.productLengthCm = data[6];
        product.productHeightCm = data[7];
        product.productWidthCm = data[8];
        return product;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public String getProductDescriptionLenght() {
        return productDescriptionLenght;
    }

    public void setProductDescriptionLenght(String productDescriptionLenght) {
        this.productDescriptionLenght = productDescriptionLenght;
    }

    public String getProductHeightCm() {
        return productHeightCm;
    }

    public void setProductHeightCm(String productHeightCm) {
        this.productHeightCm = productHeightCm;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductLengthCm() {
        return productLengthCm;
    }

    public void setProductLengthCm(String productLengthCm) {
        this.productLengthCm = productLengthCm;
    }

    public String getProductNameLenght() {
        return productNameLenght;
    }

    public void setProductNameLenght(String productNameLenght) {
        this.productNameLenght = productNameLenght;
    }

    public String getProductPhotosQty() {
        return productPhotosQty;
    }

    public void setProductPhotosQty(String productPhotosQty) {
        this.productPhotosQty = productPhotosQty;
    }

    public String getProductWeightG() {
        return productWeightG;
    }

    public void setProductWeightG(String productWeightG) {
        this.productWeightG = productWeightG;
    }

    public String getProductWidthCm() {
        return productWidthCm;
    }

    public void setProductWidthCm(String productWidthCm) {
        this.productWidthCm = productWidthCm;
    }
}
