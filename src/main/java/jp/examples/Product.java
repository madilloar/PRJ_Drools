package jp.examples;

import jp.examples.utils.StringUtils;

/**
 * 製品クラス。
 */
public class Product {
  // キー
  /**
   * 製品ID。
   */
  private String productId = null;

  // 属性
  /**
   * 高さ
   */
  private double height = 0.0;
  /**
   * 幅
   */
  private double width = 0.0;
  /**
   * 奥行
   */
  private double depth = 0.0;

  /**
   * 引数無しコンストラクタは、不可視。
   */
  @SuppressWarnings("unused")
  private Product() {

  }

  /**
   * コンストラクタ。
   * 
   * @param productId
   *          製品ID。
   */
  public Product(String productId) {
    this.setProductId(productId);
  }

  /**
   * 製品IDを返します。
   * @return 製品ID。
   */
  public String getProductId() {
    return productId;
  }

  /**
   * 製品IDのセッターは不可視。
   * @param productId
   */
  private void setProductId(String productId) {
    this.productId = productId;
  }

  /**
   * 高さを返します。
   * @return 高さ。
   */
  public double getHeight() {
    return height;
  }

  /**
   * 高さを設定します。
   * @param height 高さ。
   */
  public void setHeight(double height) {
    this.height = height;
  }

  /**
   * 幅を返します。
   * @return 幅。
   */
  public double getWidth() {
    return width;
  }

  /**
   * 幅を設定します。
   * @param width 幅。
   */
  public void setWidth(double width) {
    this.width = width;
  }

  /**
   * 奥行を返します。
   * @return 奥行。
   */
  public double getDepth() {
    return depth;
  }

  /**
   * 奥行を設定します。
   * @param depth 奥行。
   */
  public void setDepth(double depth) {
    this.depth = depth;
  }

  /**
   * 文字列化します。
   * JSON形式。
   */
  public String toString() {
    return StringUtils.toJson(this);
  }
}
