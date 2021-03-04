package com.pegasusgroup.riarewards.model;

import java.io.Serializable;

public class SingleCategoryMd implements Serializable {

    private String id;
    private String merchant_id;
    private String name;
    private String highlight;
    private String offer;
    private String image_extension;
    private String display_image;
    private String logo_extension;
    private String strip_image;
    private String details;
    private String redeem_button_img;
    private String web_coupon;
    private String used_count;
    private String is_favourite;
    private String user_used_count;
    private String product_quantity;
    private String pay_type;
    private String points;
    private String price;
    private String out_of_order;

    @Override
    public String toString() {
        return "SingleCategoryMd{" +
                "id='" + id + '\'' +
                ", merchant_id='" + merchant_id + '\'' +
                ", name='" + name + '\'' +
                ", highlight='" + highlight + '\'' +
                ", offer='" + offer + '\'' +
                ", image_extension='" + image_extension + '\'' +
                ", display_image='" + display_image + '\'' +
                ", logo_extension='" + logo_extension + '\'' +
                ", strip_image='" + strip_image + '\'' +
                ", details='" + details + '\'' +
                ", redeem_button_img='" + redeem_button_img + '\'' +
                ", web_coupon='" + web_coupon + '\'' +
                ", used_count='" + used_count + '\'' +
                ", is_favourite='" + is_favourite + '\'' +
                ", user_used_count='" + user_used_count + '\'' +
                ", product_quantity='" + product_quantity + '\'' +
                ", pay_type='" + pay_type + '\'' +
                ", points='" + points + '\'' +
                ", price='" + price + '\'' +
                ", out_of_order='" + out_of_order + '\'' +
                '}';
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIs_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(String is_favourite) {
        this.is_favourite = is_favourite;
    }

    public String getDisplay_image() {
        return display_image;
    }

    public void setDisplay_image(String display_image) {
        this.display_image = display_image;
    }

    public String getLogo_extension() {
        return logo_extension;
    }

    public void setLogo_extension(String logo_extension) {
        this.logo_extension = logo_extension;
    }

    public String getStrip_image() {
        return strip_image;
    }

    public void setStrip_image(String strip_image) {
        this.strip_image = strip_image;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getImage_extension() {
        return image_extension;
    }

    public void setImage_extension(String image_extension) {
        this.image_extension = image_extension;
    }

    public String getRedeem_button_img() {
        return redeem_button_img;
    }

    public void setRedeem_button_img(String redeem_button_img) {
        this.redeem_button_img = redeem_button_img;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getWeb_coupon() {
        return web_coupon;
    }

    public void setWeb_coupon(String web_coupon) {
        this.web_coupon = web_coupon;
    }

    public String getUser_used_count() {
        return user_used_count;
    }

    public void setUser_used_count(String user_used_count) {
        this.user_used_count = user_used_count;
    }

    public String getUsed_count() {
        return used_count;
    }

    public void setUsed_count(String used_count) {
        this.used_count = used_count;
    }

    public String getOut_of_order() {
        return out_of_order;
    }

    public void setOut_of_order(String out_of_order) {
        this.out_of_order = out_of_order;
    }


    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

}