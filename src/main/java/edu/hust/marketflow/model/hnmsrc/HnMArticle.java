package edu.hust.marketflow.model.hnmsrc;

public class HnMArticle {
    private String articleId;
    private String productCode;
    private String prodName;
    private String productTypeNo;
    private String productTypeName;
    private String productGroupName;
    private String graphicalAppearanceNo;
    private String graphicalAppearanceName;
    private String colourGroupCode;
    private String colourGroupName;
    private String perceivedColourValueId;
    private String perceivedColourValueName;
    private String perceivedColourMasterId;
    private String perceivedColourMasterName;
    private String departmentNo;
    private String departmentName;
    private String indexCode;
    private String indexName;
    private String indexGroupNo;
    private String indexGroupName;
    private String sectionNo;
    private String sectionName;
    private String garmentGroupNo;
    private String garmentGroupName;
    private String detailDesc;

    public static int getFieldCount() {
        return 25;
    }

    public String getColourGroupName() {
        return colourGroupName;
    }

    public void setColourGroupName(String colourGroupName) {
        this.colourGroupName = colourGroupName;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getColourGroupCode() {
        return colourGroupCode;
    }

    public void setColourGroupCode(String colourGroupCode) {
        this.colourGroupCode = colourGroupCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentNo() {
        return departmentNo;
    }

    public void setDepartmentNo(String departmentNo) {
        this.departmentNo = departmentNo;
    }

    public String getDetailDesc() {
        return detailDesc;
    }

    public void setDetailDesc(String detailDesc) {
        this.detailDesc = detailDesc;
    }

    public String getGarmentGroupName() {
        return garmentGroupName;
    }

    public void setGarmentGroupName(String garmentGroupName) {
        this.garmentGroupName = garmentGroupName;
    }

    public String getGarmentGroupNo() {
        return garmentGroupNo;
    }

    public void setGarmentGroupNo(String garmentGroupNo) {
        this.garmentGroupNo = garmentGroupNo;
    }

    public String getGraphicalAppearanceName() {
        return graphicalAppearanceName;
    }

    public void setGraphicalAppearanceName(String graphicalAppearanceName) {
        this.graphicalAppearanceName = graphicalAppearanceName;
    }

    public String getGraphicalAppearanceNo() {
        return graphicalAppearanceNo;
    }

    public void setGraphicalAppearanceNo(String graphicalAppearanceNo) {
        this.graphicalAppearanceNo = graphicalAppearanceNo;
    }

    public String getIndexCode() {
        return indexCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }

    public String getIndexGroupName() {
        return indexGroupName;
    }

    public void setIndexGroupName(String indexGroupName) {
        this.indexGroupName = indexGroupName;
    }

    public String getIndexGroupNo() {
        return indexGroupNo;
    }

    public void setIndexGroupNo(String indexGroupNo) {
        this.indexGroupNo = indexGroupNo;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getPerceivedColourMasterId() {
        return perceivedColourMasterId;
    }

    public void setPerceivedColourMasterId(String perceivedColourMasterId) {
        this.perceivedColourMasterId = perceivedColourMasterId;
    }

    public String getPerceivedColourMasterName() {
        return perceivedColourMasterName;
    }

    public void setPerceivedColourMasterName(String perceivedColourMasterName) {
        this.perceivedColourMasterName = perceivedColourMasterName;
    }

    public String getPerceivedColourValueId() {
        return perceivedColourValueId;
    }

    public void setPerceivedColourValueId(String perceivedColourValueId) {
        this.perceivedColourValueId = perceivedColourValueId;
    }

    public String getPerceivedColourValueName() {
        return perceivedColourValueName;
    }

    public void setPerceivedColourValueName(String perceivedColourValueName) {
        this.perceivedColourValueName = perceivedColourValueName;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductGroupName() {
        return productGroupName;
    }

    public void setProductGroupName(String productGroupName) {
        this.productGroupName = productGroupName;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getProductTypeNo() {
        return productTypeNo;
    }

    public void setProductTypeNo(String productTypeNo) {
        this.productTypeNo = productTypeNo;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionNo() {
        return sectionNo;
    }

    public void setSectionNo(String sectionNo) {
        this.sectionNo = sectionNo;
    }

    public static HnMArticle fromArray(String [] p) {
        HnMArticle a = new HnMArticle();
        a.articleId = p[0];
        a.productCode = p[1];
        a.prodName = p[2];
        a.productTypeNo = p[3];
        a.productTypeName = p[4];
        a.productGroupName = p[5];
        a.graphicalAppearanceNo = p[6];
        a.graphicalAppearanceName = p[7];
        a.colourGroupCode = p[8];
        a.colourGroupName = p[9];
        a.perceivedColourValueId = p[10];
        a.perceivedColourValueName = p[11];
        a.perceivedColourMasterId = p[12];
        a.perceivedColourMasterName = p[13];
        a.departmentNo = p[14];
        a.departmentName = p[15];
        a.indexCode = p[16];
        a.indexName = p[17];
        a.indexGroupNo = p[18];
        a.indexGroupName = p[19];
        a.sectionNo = p[20];
        a.sectionName = p[21];
        a.garmentGroupNo = p[22];
        a.garmentGroupName = p[23];
        a.detailDesc = p[24];
        return a;
    }
}
