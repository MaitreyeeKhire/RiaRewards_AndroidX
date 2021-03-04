package com.pegasusgroup.riarewards.model;

public class CartMd {

    private int cart_item_id;
    private String categoryName;
    private String itemName;
    private String price;
    private int cart_id;
    private int shippingtype;
    private int max_group_ship_cost;
    private int pid;
    private int qty;
    private String discount;
    private String cart_product_id;
    private String payType;
    private float payEqualPoints;
    private float totalRequiredPoints;
    private float productShippingCost;
    private String warehouse_product_quantity;
    private String displayImage;
    private String merchant_id;
    private String productPoints;

    public CartMd(int cart_item_id, String categoryName, String itemName, String price, int cart_id, int shippingtype, int max_group_ship_cost, int pid, int qty,
                  String discount, String cart_product_id, String warehouse_product_quantity,
                  String payType, float payEqualPoints, float totalRequiredPoints, float productShippingCost, String displayImage, String merchant_id, String productPoints) {
        this.cart_item_id = cart_item_id;
        this.categoryName = categoryName;
        this.itemName = itemName;
        this.price = price;
        this.cart_id = cart_id;
        this.shippingtype = shippingtype;
        this.max_group_ship_cost = max_group_ship_cost;
        this.pid = pid;
        this.qty = qty;
        this.discount = discount;
        this.cart_product_id = cart_product_id;
        this.warehouse_product_quantity = warehouse_product_quantity;
        this.payType = payType;
        this.payEqualPoints = payEqualPoints;
        this.totalRequiredPoints = totalRequiredPoints;
        this.productShippingCost = productShippingCost;
        this.displayImage = displayImage;
        this.merchant_id = merchant_id;
        this.productPoints = productPoints;
    }

    public String getWarehouse_product_quantity() {
        return warehouse_product_quantity;
    }

    public void setWarehouse_product_quantity(String warehouse_product_quantity) {
        this.warehouse_product_quantity = warehouse_product_quantity;
    }

    public String getCart_product_id() {
        return cart_product_id;
    }

    public void setCart_product_id(String cart_product_id) {
        this.cart_product_id = cart_product_id;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getCart_item_id() {
        return cart_item_id;
    }

    public void setCart_item_id(int cart_item_id) {
        this.cart_item_id = cart_item_id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public int getShippingtype() {
        return shippingtype;
    }

    public void setShippingtype(int shippingtype) {
        this.shippingtype = shippingtype;
    }

    public int getMax_group_ship_cost() {
        return max_group_ship_cost;
    }

    public void setMax_group_ship_cost(int max_group_ship_cost) {
        this.max_group_ship_cost = max_group_ship_cost;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getPayType() {
        return payType;
    }

    public float getPayEqualPoints() {
        return payEqualPoints;
    }

    public float getTotalRequiredPoints() {
        return totalRequiredPoints;
    }

    public float getProductShippingCost() {
        return productShippingCost;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getProductPoints() {
        return productPoints;
    }

    public void setProductPoints(String productPoints) {
        this.productPoints = productPoints;
    }
}