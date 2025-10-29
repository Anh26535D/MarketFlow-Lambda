package edu.hust.marketflow.model.hnmsrc;

public class HnMTransaction {
    public String t_dat;
    public String customer_id;
    public String article_id;
    public double price;
    public String sales_channel_id;

    public static int getFieldCount() {
        return 5;
    }

    public static HnMTransaction fromArray(String[] p) {
        HnMTransaction t = new HnMTransaction();
        t.t_dat = p[0];
        t.customer_id = p[1];
        t.article_id = p[2];
        t.price = Double.parseDouble(p[3]);
        t.sales_channel_id = p[4];
        return t;
    }
}
