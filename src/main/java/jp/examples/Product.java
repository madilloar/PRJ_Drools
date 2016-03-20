package jp.examples;

import jp.examples.utils.StringUtils;

public class Product {
  private String productId = null;
  private double height = 0.0;
  private double width = 0.0;
  private double depth = 0.0;

  @SuppressWarnings("unused")
  private Product() {

  }

  public Product(String productId) {
    this.setProductId(productId);
  }

  public String getProductId() {
    return productId;
  }

  private void setProductId(String productId) {
    this.productId = productId;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getDepth() {
    return depth;
  }

  public void setDepth(double depth) {
    this.depth = depth;
  }

  public String toString() {
    return StringUtils.toJson(this);
  }
}
