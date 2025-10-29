package edu.hust.marketflow.model.hnmdatamodel;

public class HnMArticle {
    public String article_id;
    public String product_code;
    public String prod_name;
    public String product_type_no;
    public String product_type_name;
    public String product_group_name;
    public String graphical_appearance_no;
    public String graphical_appearance_name;
    public String colour_group_code;
    public String colour_group_name;
    public String perceived_colour_value_id;
    public String perceived_colour_value_name;
    public String perceived_colour_master_id;
    public String perceived_colour_master_name;
    public String department_no;
    public String department_name;
    public String index_code;
    public String index_name;
    public String index_group_no;
    public String index_group_name;
    public String section_no;
    public String section_name;
    public String garment_group_no;
    public String garment_group_name;
    public String detail_desc;

    public static int getFieldCount() {
        return 25;
    }

    public static HnMArticle fromArray(String [] p) {
        HnMArticle a = new HnMArticle();
        a.article_id = p[0];
        a.product_code = p[1];
        a.prod_name = p[2];
        a.product_type_no = p[3];
        a.product_type_name = p[4];
        a.product_group_name = p[5];
        a.graphical_appearance_no = p[6];
        a.graphical_appearance_name = p[7];
        a.colour_group_code = p[8];
        a.colour_group_name = p[9];
        a.perceived_colour_value_id = p[10];
        a.perceived_colour_value_name = p[11];
        a.perceived_colour_master_id = p[12];
        a.perceived_colour_master_name = p[13];
        a.department_no = p[14];
        a.department_name = p[15];
        a.index_code = p[16];
        a.index_name = p[17];
        a.index_group_no = p[18];
        a.index_group_name = p[19];
        a.section_no = p[20];
        a.section_name = p[21];
        a.garment_group_no = p[22];
        a.garment_group_name = p[23];
        a.detail_desc = p[24];
        return a;
    }
}
