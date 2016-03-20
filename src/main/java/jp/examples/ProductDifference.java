package jp.examples;

import java.util.List;

import jp.examples.utils.StringUtils;

/**
 * 製品差異を表現するクラス。
 * 
 * 旧製品、新製品、新旧製品差異理由、差異判定結果を状態として保持します。
 *
 */
public class ProductDifference {
  /**
   * 製品ID。
   */
  private String productId = null;
  /**
   * 旧製品。
   */
  private Product beforeProduct = null;
  /**
   * 新製品。
   */
  private Product afterProcduct = null;
  /**
   * 新旧製品差異理由。
   */
  private List<Reason> differenceReasons = null;
  /**
   * 差異判定結果
   */
  private String decision = null;

  /**
   * 引数無しコンストラクタは、不可視。
   */
  @SuppressWarnings("unused")
  private ProductDifference() {
  }

  /**
   * コンストラクタ。
   * 
   * @param productId 製品ID。
   * @param beforeProduct 旧製品。
   * @param afterProduct 新製品。
   * @param differenceReasons 差異理由。
   */
  public ProductDifference(String productId, Product beforeProduct, Product afterProduct, List<Reason> differenceReasons) {
    this.setProductId(productId);
    this.setBeforeProduct(beforeProduct);
    this.setAfterProcduct(afterProduct);
    this.setDifferenceReasons(differenceReasons);
  }

  /**
   * 文字列化します。
   * JSON形式です。
   */
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