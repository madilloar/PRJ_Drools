package jp.examples;

import java.util.List;

import jp.examples.utils.StringUtils;

public class ProductDifference {
  private String productId = null;
  private Product beforeProduct = null;
  private Product afterProcduct = null;
  private List<Reason> differenceReasons = null;
  private String decision = null;

  @SuppressWarnings("unused")
  private ProductDifference() {
  }

  public ProductDifference(String productId, Product beforeProduct, Product afterProduct, List<Reason> differenceReasons) {
    this.setProductId(productId);
    this.setBeforeProduct(beforeProduct);
    this.setAfterProcduct(afterProduct);
    this.setDifferenceReasons(differenceReasons);
  }

  public String toString() {
    return StringUtils.toJson(this);
  }

  public String getProductId() {
    return this.productId;
  }

  private void setProductId(String productId) {
    this.productId = productId;
  }

  public Product getBeforeProduct() {
    return this.beforeProduct;
  }

  public void setBeforeProduct(Product beforeProduct) {
    this.beforeProduct = beforeProduct;
  }

  public Product getAfterProcduct() {
    return this.afterProcduct;
  }

  public void setAfterProcduct(Product afterProcduct) {
    this.afterProcduct = afterProcduct;
  }

  public List<Reason> getDifferenceReasons() {
    return this.differenceReasons;
  }

  public void setDifferenceReasons(List<Reason> differenceReasons) {
    this.differenceReasons = differenceReasons;
  }

  public String getDecision() {
    return decision;
  }

  public void setDecision(String decision) {
    this.decision = decision;
  }

}