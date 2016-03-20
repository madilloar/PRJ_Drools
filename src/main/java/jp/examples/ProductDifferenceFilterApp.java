package jp.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.examples.utils.StringUtils;

import org.apache.log4j.Logger;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
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
  private static final String DECISION_TABLE_NAME = "ProductDifferenceFilter1.xls";

  /**
   * ディシジョンテーブルへのファイルパス。
   */
  private static final File DECISION_TABLE_PAHT = new File("data/" + DECISION_TABLE_NAME);

  /**
   * デフォルトのディシジョンテーブルへのファイルパス。
   */
  private static final String DEFAULT_DECISION_TABLE_PAHT_STRING = "src/main/resources/" + DECISION_TABLE_NAME;

  /**
   * エントリーポイント。
   * 
   * @param args
   *          コマンドライン引数。
   */
  public static final void main(final String[] args) {
    ProductDifferenceFilterApp me = new ProductDifferenceFilterApp();

    // 製品群の差異を求める。
    List<ProductDifference> products = me.diffProducts();

    // ルール実行前の状態
    me.debugProductDifference(">>>BEFORE", products);

    // ルール適用
    KieSession kieSession = null;
    KieRuntimeLogger kieRLogger = null;
    try {
      // ルールファイル(ディシジョンテーブルやDRL形式ルール)をエンジンに展開し、ルールとの接続(kieSession)を返す。
      kieSession = me.createSession();
      // エンジンのロガー生成
      kieRLogger = KieServices.Factory.get().getLoggers().newFileLogger(kieSession, "log/ProductDifferenceFilter1");

      // 製品群の差異をルールに適用
      me.execute(kieSession, products);
    } finally {
      kieRLogger.close();
      kieSession.dispose();
    }

    // ルール実行結果の状態
    me.debugProductDifference(">>>AFTER", products);
  }

  /**
   * Droolsルールエンジンにディシジョンテーブルを与え、その接続を返す。
   * 
   * @return Droolsルールエンジンへの接続。
   */
  public KieSession createSession() {
    // DEBUG:ディシジョンテーブルをDRL形式に変換してログ出力。
    this.debugXlsToDrl(DECISION_TABLE_PAHT);

    KieSession kieSession = null;

    KieServices kieServices = KieServices.Factory.get();

    // ルールエンジンのメモリファイルシステムの構築。
    KieFileSystem kfs = kieServices.newKieFileSystem();

    FileInputStream fis = null;
    try {
      fis = new FileInputStream(DECISION_TABLE_PAHT);

      // writeの第一引数のパスは実際のファイルのパスではない。メモリ上の仮想的なパス。
      // KieModuleを自前で作っていないので、デフォルトのパスで"src/main/resources/"で始まる。
      // http://stackoverflow.com/questions/24558451/cant-run-hello-world-on-drools-dlr-files-are-not-picked-from-classpath-by-kie
      kfs.write(DEFAULT_DECISION_TABLE_PAHT_STRING, kieServices.getResources().newInputStreamResource(fis));

      // ルールエンジン内にルールを展開。
      KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();

      // ルール展開結果の取得。
      Results results = kieBuilder.getResults();
      // ルール展開結果にエラーがあったら、例外を返す。
      if (results.hasMessages(Message.Level.ERROR)) {
        this.logger_.debug(StringUtils.toJson(results));
        throw new IllegalStateException(">>>ルールファイルの記述に問題があります。");
      }

      // ルールコンテナにルールを覚えさせます。
      KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());

      // ルールエンジンへの接続を生成。
      kieSession = kieContainer.newKieSession();

    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return kieSession;
  }

  /**
   * ExcelディシジョンテーブルをDRL形式のテキストに変換し、デバッグ出力します。
   * 
   * ExcelディシジョンテーブルだとKieBuilderでルールを展開したときにエラーが発生したとき、
   * エラー行番号がDRL形式の行番号なので、それを知るため。
   * 
   * @param path
   *          Excelディシジョンテーブルパス。
   */
  private void debugXlsToDrl(File path) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(path);
      SpreadsheetCompiler sc = new SpreadsheetCompiler();
      this.logger_.debug(sc.compile(fis, InputType.XLS));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
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
  private List<ProductDifference> diffProducts() {
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
