package jp.examples;

import jp.examples.utils.StringUtils;

/**
 * 差異理由。 差異項目ＩＤと差異理由ＩＤのペア。
 */
public class Reason {
  private String productId;
  private String itemId;
  private String reasonId;

  public Reason(String productId, String itemId, String reasonId) {
    this.setProductId(productId);
    this.setItemId(itemId);
    this.setReasonId(reasonId);
  }

  public String getItemId() {
    return this.itemId;
  }

  private void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getReasonId() {
    return this.reasonId;
  }

  private void setReasonId(String reasonId) {
    this.reasonId = reasonId;
  }

  public String getProductId() {
    return productId;
  }

  private void setProductId(String productId) {
    this.productId = productId;
  }

  public String toString() {
    return StringUtils.toJson(this);
  }

}