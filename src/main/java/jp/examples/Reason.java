package jp.examples;

import jp.examples.utils.StringUtils;

/**
 * 新旧製品の差異理由。
 */
public class Reason {
  /**
   * 製品ID。
   */
  private String productId;
  /**
   * 項目ID。
   */
  private String itemId;
  /**
   * 差異理由ID。
   */
  private String reasonId;

  /**
   * 引数無しコンストラクタは、不可視。
   */
  @SuppressWarnings("unused")
  private Reason() {

  }

  /**
   * コンストラクタ。
   * 
   * @param productId
   *          製品ID。
   * @param itemId
   *          項目ID。
   * @param reasonId
   *          差異理由ID。
   */
  public Reason(String productId, String itemId, String reasonId) {
    this.setProductId(productId);
    this.setItemId(itemId);
    this.setReasonId(reasonId);
  }

  /**
   * 項目IDを返します。
   * 
   * @return 項目ID。
   */
  public String getItemId() {
    return this.itemId;
  }

  /**
   * 項目IDのセッターは不可視。
   * 
   * @param itemId
   */
  private void setItemId(String itemId) {
    this.itemId = itemId;
  }

  /**
   * 差異理由IDを返します。
   * 
   * @return 差異理由ID。
   */
  public String getReasonId() {
    return this.reasonId;
  }

  /**
   * 差異理由IDのセッターは不可視。
   * 
   * @param reasonId
   */
  private void setReasonId(String reasonId) {
    this.reasonId = reasonId;
  }

  /**
   * 製品IDを返します。
   * 
   * @return 製品ID。
   */
  public String getProductId() {
    return productId;
  }

  /**
   * 製品IDのセッターは不可視。
   * 
   * @param productId
   */
  private void setProductId(String productId) {
    this.productId = productId;
  }

  /**
   * 文字列化します。 JSON形式。
   */
  public String toString() {
    return StringUtils.toJson(this);
  }

}