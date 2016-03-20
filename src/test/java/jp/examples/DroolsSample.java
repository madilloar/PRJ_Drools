package jp.examples;

import java.io.File;

import jp.examples.utils.DroolsUtils;
import jp.examples.utils.StringUtils;

import org.apache.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class DroolsSample {
  /**
   * 当クラスのロガー。
   */
  private Logger logger_ = Logger.getLogger(DroolsSample.class);

  public static final void main(final String[] args) {
    DroolsSample me = new DroolsSample();

    Object problems = me.createProblems();

    // ルール実行前の状態
    me.logger_.debug(">>>BEFORE:" + problems);

    // ルール適用
    KieSession kieSession = null;
    KieRuntimeLogger kieRLogger = null;
    try {
      // ルールファイル(ディシジョンテーブルやDRL形式ルール)をエンジンに展開し、ルールとの接続(kieSession)を返す。
      String fileName = "Discount.drl";
      kieSession = DroolsUtils.createSession(new File("data/" + fileName), fileName);
      // エンジンのロガー生成
      kieRLogger = KieServices.Factory.get().getLoggers().newFileLogger(kieSession, "log/" + DroolsSample.class.getSimpleName());

      // ルールに適用
      me.execute(kieSession, problems);
    } catch (Throwable t) {
      t.printStackTrace();
    } finally {
      kieRLogger.close();
      kieSession.dispose();
    }

    // ルール実行結果の状態
    me.logger_.debug(">>>AFTER:" + problems);
  }

  public Object createProblems() {
    Sales sale = new Sales();
    sale.setSales(6000);
    sale.setStatus(Sales.NOT_APPLIED);
    return sale;
  }

  /**
   * ルール適用。
   * 
   * @param kieSession
   *          ルールエンジンとの接続。
   * @param products
   *          ルールエンジンに与える製品群差異。
   */
  public void execute(KieSession kieSession, Object sales) {
    FactHandle f1 = kieSession.insert(sales);
    kieSession.fireAllRules();
    kieSession.delete(f1);
  }

  public class Sales {

    public static final int NOT_APPLIED = 0; // 　未適用
    public static final int APPLIED = 1; // 　適用済

    private long sales; // 　売り上げ
    private int status; // 　状態(未適用、適用済)

    public int getStatus() {
      return this.status;
    }

    public void setStatus(int status) {
      this.status = status;
    }

    public void setSales(long sales) {
      this.sales = sales;
    }

    public long getSales() {
      return sales;
    }

    public String toString() {
      return StringUtils.toJson(this);
    }

  }

}