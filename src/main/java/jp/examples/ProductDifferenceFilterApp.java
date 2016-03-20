package jp.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.examples.utils.DroolsUtils;

import org.apache.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 * 旧プロセスで生成された製品と新プロセスで生成された製品の差異をフィルタする。
 * 
 * 差異が沢山出たときに、それをフィルタする条件をディシジョンテーブルで表現する。
 * 
 * このディシジョンテーブルをDroolsルールエンジンに与え、ディシジョンテーブルに
 * ヒットした条件に従って、製品群差異に対して、判定結果として、OK(差異と見做さない)、NG(差異と見做す)の印をつける。
 * そもそもディシジョンテーブルにヒットしなかった場合は、判定結果は付与しない(製品群の生成方次第であるが、何もしなければnullのまま)。
 * 
 */
public class ProductDifferenceFilterApp {
  /**
   * 当クラスのロガー。
   */
  private Logger logger_ = Logger.getLogger(ProductDifferenceFilterApp.class);

  /**
   * ディシジョンテーブル名。
   */
  private static final String DECISION_TABLE_NAME = "ProductDifferenceFilter1.xlsx";

  /**
   * ディシジョンテーブルへのファイルパス。
   */
  private static final File DECISION_TABLE_PAHT = new File("data/" + DECISION_TABLE_NAME);

  /**
   * エントリーポイント。
   * 
   * @param args
   *          コマンドライン引数。
   */
  public static final void main(final String[] args) {
    ProductDifferenceFilterApp me = new ProductDifferenceFilterApp();

    // 製品群の差異を求める。
    List<ProductDifference> problems = me.createProblems();

    // ルール実行前の状態
    me.debugProductDifference(">>>BEFORE", problems);

    // ルール適用
    KieSession kieSession = null;
    KieRuntimeLogger kieRLogger = null;
    try {
      // ルールファイル(ディシジョンテーブルやDRL形式ルール)をエンジンに展開し、ルールとの接続(kieSession)を返す。
      kieSession = DroolsUtils.createSession(DECISION_TABLE_PAHT, DECISION_TABLE_NAME);
      // エンジンのロガー生成
      kieRLogger = KieServices.Factory.get().getLoggers()
          .newFileLogger(kieSession, "log/" + ProductDifferenceFilterApp.class.getSimpleName());

      // 製品群の差異をルールに適用
      me.execute(kieSession, problems);
    } finally {
      kieRLogger.close();
      kieSession.dispose();
    }

    // ルール実行結果の状態
    me.debugProductDifference(">>>AFTER", problems);
  }

  /**
   * 製品群をデバッグ出力。
   * 
   * @param prefix
   *          デバッグ出力メッセージの接頭辞。
   * @param products
   */
  private void debugProductDifference(String prefix, List<ProductDifference> products) {
    for (Iterator<ProductDifference> iterator = products.iterator(); iterator.hasNext();/* nop */) {
      ProductDifference pd = iterator.next();
      this.logger_.debug(prefix + pd);
    }
  }

  /**
   * ルール適用。
   * 
   * @param kieSession
   *          ルールエンジンとの接続。
   * @param products
   *          ルールエンジンに与える製品群差異。
   */
  public void execute(KieSession kieSession, List<ProductDifference> products) {
    // 製品単位のループ
    for (Iterator<ProductDifference> iterator = products.iterator(); iterator.hasNext();) {
      ProductDifference pd = (ProductDifference) iterator.next();
      List<Reason> reasons = pd.getDifferenceReasons();
      // 1製品にぶら下がる差異理由分ループ
      for (Iterator<Reason> iterator2 = reasons.iterator(); iterator2.hasNext();) {
        Reason reason = iterator2.next();
        FactHandle f1 = kieSession.insert(reason);
        FactHandle f2 = kieSession.insert(pd);
        kieSession.fireAllRules();
        // 同じルールにまたヒットしてしまうので、トランザクションを削除する。
        kieSession.delete(f1);
        kieSession.delete(f2);
      }
    }
  }

  /**
   * 製品群の差異を求める。
   * 
   * 別のAPで求めた差異の結果もしくは、ファイルやDBから取り出してもよいが、 テストアプリなので、ハードコーディングしている。
   * 
   * @return 製品群の差異
   */
  private List<ProductDifference> createProblems() {
    List<ProductDifference> products = new ArrayList<ProductDifference>();

    String productId = "P01";
    Product b = new Product(productId);
    b.setHeight(10);
    b.setWidth(20);
    b.setDepth(30);
    Product a = new Product(productId);
    a.setHeight(10);
    a.setWidth(20);
    a.setDepth(30.5);
    List<Reason> r = new ArrayList<Reason>();
    r.add(new Reason(productId, "Depth", "奥行のサイズが異なります。"));
    ProductDifference pd = new ProductDifference(productId, b, a, r);
    products.add(pd);

    productId = "P02";
    b = new Product(productId);
    b.setHeight(10);
    b.setWidth(20);
    b.setDepth(30);
    a = new Product(productId);
    a.setHeight(10.5); // 差異が出る
    a.setWidth(20);
    a.setDepth(30);
    r = new ArrayList<Reason>();
    r.add(new Reason(productId, "Height", "高さのサイズが異なります。"));
    pd = new ProductDifference(productId, b, a, r);
    products.add(pd);

    productId = "P03";
    b = new Product(productId);
    b.setHeight(10);
    b.setWidth(20);
    b.setDepth(30);
    a = new Product(productId);
    a.setHeight(10); // 差異なし
    a.setWidth(20);
    a.setDepth(30);
    r = new ArrayList<Reason>();
    // r.add(new Reason(productId, "Height", ""));
    pd = new ProductDifference(productId, b, a, r);
    products.add(pd);

    return products;
  }
}
