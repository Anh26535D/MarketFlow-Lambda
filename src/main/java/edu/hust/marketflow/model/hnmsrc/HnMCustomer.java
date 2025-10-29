package edu.hust.marketflow.model.hnmsrc;

public class HnMCustomer {
    public String customer_id;
    public String FN;
    public String Active;
    public String club_member_status;
    public String fashion_news_frequency;
    public String age;
    public String postal_code;

    public static int getFieldCount() {
        return 7;
    }

    public static HnMCustomer fromArray(String[] p) {
        HnMCustomer c = new HnMCustomer();
        c.customer_id = p[0];
        c.FN = p[1];
        c.Active = p[2];
        c.club_member_status = p[3];
        c.fashion_news_frequency = p[4];
        c.age = p[5];
        c.postal_code = p[6];
        return c;
    }
}
