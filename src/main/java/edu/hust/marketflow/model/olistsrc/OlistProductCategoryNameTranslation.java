package edu.hust.marketflow.model.olistsrc;

public class OlistProductCategoryNameTranslation {
    private String productCategoryName;
    private String productCategoryNameEnglish;

    public static int getFieldCount() {
        return 2;
    }

    public static OlistProductCategoryNameTranslation fromArray(String [] data) {
        OlistProductCategoryNameTranslation translation = new OlistProductCategoryNameTranslation();
        translation.productCategoryName = data[0];
        translation.productCategoryNameEnglish = data[1];
        return translation;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public String getProductCategoryNameEnglish() {
        return productCategoryNameEnglish;
    }

    public void setProductCategoryNameEnglish(String productCategoryNameEnglish) {
        this.productCategoryNameEnglish = productCategoryNameEnglish;
    }
}
