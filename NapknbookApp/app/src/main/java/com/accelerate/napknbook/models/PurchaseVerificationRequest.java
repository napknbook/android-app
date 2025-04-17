package com.accelerate.napknbook.models;

public class PurchaseVerificationRequest {
    private String product_id;
    private String purchase_token;
    private String purchase_type  ;

    public PurchaseVerificationRequest(String product_id, String purchase_token, String purchase_type) {
        this.product_id = product_id;
        this.purchase_token = purchase_token;
        this.purchase_type = purchase_type ;

    }

    public String getProduct_id() {
        return product_id;
    }

    public String getPurchase_token() {
        return purchase_token;
    }

    public String getPurchase_type() {
        return purchase_type;
    }

}
