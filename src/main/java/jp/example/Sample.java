package jp.example;
import java.util.Collection;

import org.drools.core.event.DebugAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLogger;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

@SuppressWarnings("deprecation")
public class Sample {

  public static final void main(final String[] args) throws Exception {
    final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

    // ルールファイルをコンパイルしてKnowledgeBuilderへ追加する
    kbuilder.add(ResourceFactory.newClassPathResource("DroolsTest1.drl", Sample.class), ResourceType.DRL);

    // 上記コンパイル時のエラー処理
    if (kbuilder.hasErrors()) {
      System.out.println(kbuilder.getErrors().toString());
      throw new RuntimeException("Unable to compile \"DroolsTest1.drl\".");
    }

    // KnowledgeBuilderからコンパイル済みのパッケージを取得する
    final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

    // 取得したパッケージをKnowledgebaseに追加する(パッケージのデプロイ)
    final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
    kbase.addKnowledgePackages(pkgs);

    // 　Statefulセッションを作成
    final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

    // Agenda,WorkingMemoryのView表示準備
    ksession.addEventListener(new DebugAgendaEventListener());
    ksession.addEventListener(new org.kie.api.event.rule.DebugAgendaEventListener());

    // Auditログのセット
    KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "log/DroolsTest1");

    // ルールの実行
    final Sales sale = new Sales();
    sale.setSales(6000);
    sale.setStatus(Sales.NOT_APPLIED);

    ksession.insert(sale);
    ksession.fireAllRules();

    // 　Auditログを閉じる
    logger.close();

    // 　Statefulセッションなので、実行が終わったところでdisposeして領域を開放する
    ksession.dispose();
  }

  public static class Sales {

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

  }

}