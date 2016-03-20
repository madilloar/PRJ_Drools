package jp.examples.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class DroolsUtils {
  /**
   * 当クラスのロガー。
   */
  private static Logger logger_ = Logger.getLogger(DroolsUtils.class);

  /**
   * Droolsルールエンジンにディシジョンテーブルを与え、その接続を返す。
   * 
   * ruleFileName: writeの第一引数のパスは実際のファイルのパスではない。メモリ上の仮想的なパス。
   * KieModuleを自前で作っていないので、デフォルトのパスで"src/main/resources/"で始まる。
   * 
   * @see <a href=
   *      "http://stackoverflow.com/questions/24558451/cant-run-hello-world-on-drools-dlr-files-are-not-picked-from-classpath-by-kie"
   *      >Can't run hello world on drools - dlr files are not picked from
   *      classpath by KieContainer</a>
   * 
   * @param ruleFilePath
   *          ルールファイルフルパス。例："data/Discount.drl"。
   * @param ruleFileName
   *          ルールファイル名。例："Discount.drl"。
   * @return Droolsルールエンジンへの接続。
   */
  public static KieSession createSession(File ruleFilePath, String ruleFileName) {
    // DEBUG:ディシジョンテーブルをDRL形式に変換してログ出力。
    if(isXlsxFile(ruleFilePath)){
      debugXlsxToDrl(ruleFilePath);
    }

    KieSession kieSession = null;

    KieServices kieServices = KieServices.Factory.get();

    // ルールエンジンのメモリファイルシステムの構築。
    KieFileSystem kfs = kieServices.newKieFileSystem();

    FileInputStream fis = null;
    try {
      fis = new FileInputStream(ruleFilePath);

      kfs.write("src/main/resources/" + ruleFileName, kieServices.getResources().newInputStreamResource(fis));

      // ルールエンジン内にルールを展開。
      KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();

      // ルール展開結果の取得。
      Results results = kieBuilder.getResults();
      // ルール展開結果にエラーがあったら、例外を返す。
      if (results.hasMessages(Message.Level.ERROR)) {
        logger_.debug(StringUtils.toJson(results));
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
  public static void debugXlsxToDrl(File path) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(path);
      SpreadsheetCompiler sc = new SpreadsheetCompiler();
      logger_.debug(sc.compile(fis, InputType.XLS));
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

  public static boolean isXlsxFile(File file) {
    return file.isFile() && file.canRead() && file.getPath().toLowerCase().endsWith(".xlsx");
  }

}
