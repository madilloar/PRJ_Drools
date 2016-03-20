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
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class ProductDifferenceFilter1 {
  Logger logger_ = Logger.getLogger(ProductDifferenceFilter1.class);

  public static final void main(final String[] args) throws Exception {
    ProductDifferenceFilter1 me = new ProductDifferenceFilter1();
    List<ProductDifference> products = me.makeProductDiffreDifferences();

    for (Iterator<ProductDifference> iterator = products.iterator(); iterator.hasNext();) {
      ProductDifference pd = (ProductDifference) iterator.next();
      me.logger_.debug("BEFORE:" + pd);
    }

    // ルールをメモリに展開
    KieSession kSession = me.build();

    KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newFileLogger(kSession, "log/ProductDifferenceFilter1");

    me.execute(kSession, products);
    logger.close();
    kSession.dispose();

    for (Iterator<ProductDifference> iterator = products.iterator(); iterator.hasNext();) {
      ProductDifference pd = (ProductDifference) iterator.next();
      me.logger_.debug("AFTER:" + pd);
    }
  }

  public void execute(KieSession kSession, List<ProductDifference> products) {
    // 製品単位のループ
    for (Iterator<ProductDifference> iterator = products.iterator(); iterator.hasNext();) {
      ProductDifference pd = (ProductDifference) iterator.next();
      List<Reason> reasons = pd.getDifferenceReasons();
      // 1製品にぶら下がる差異理由分ループ
      for (Iterator<Reason> iterator2 = reasons.iterator(); iterator2.hasNext();) {
        Reason reason = (Reason) iterator2.next();
        FactHandle f1 = kSession.insert(reason);
        FactHandle f2 = kSession.insert(pd);
        kSession.fireAllRules();
        // 同じルールにまたヒットしてしまうので、問題を削除する。
        kSession.delete(f1);
        kSession.delete(f2);
      }
    }
  }

  private List<ProductDifference> makeProductDiffreDifferences() {
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
    a.setHeight(10); // 差異ゼロ
    a.setWidth(20);
    a.setDepth(30);
    r = new ArrayList<Reason>();
    // r.add(new Reason(productId, "Height", ""));
    pd = new ProductDifference(productId, b, a, r);
    products.add(pd);

    return products;
  }

  public KieSession build() throws Exception {
    KieSession kieSession = null;

    KieServices kieServices = KieServices.Factory.get();
    KieFileSystem kfs = kieServices.newKieFileSystem();

    String path = "src/test/resources/jp/example/ProductDifferenceFilter1.xls";
    this.debugXlsToDrl(new File(path));

    FileInputStream fis = null;
    try {
      fis = new FileInputStream(path);
      // writeの第一引数のパスは実際のファイルのパスではない。メモリ上の仮想的なパス。
      // そして、KieModuleを自前で作っていないので、この時のパスは固定的なパスで"src/main/resources/"で始まる
      kfs.write("src/main/resources/ProductDifferenceFilter1.xls", kieServices.getResources().newInputStreamResource(fis));

      KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();

      Results results = kieBuilder.getResults();
      if (results.hasMessages(Message.Level.ERROR)) {
        System.out.println(StringUtils.toJson(results));
        throw new IllegalStateException(">>>ルールファイルの記述に問題があります。");
      }

      KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());

      KieBase kieBase = kieContainer.getKieBase();
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
   * ExcelディシジョンテーブルをDLR形式のテキストに変換し、デバッグ出力します。
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
}
