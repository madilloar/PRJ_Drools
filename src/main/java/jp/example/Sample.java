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

    // ���[���t�@�C�����R���p�C������KnowledgeBuilder�֒ǉ�����
    kbuilder.add(ResourceFactory.newClassPathResource("DroolsTest1.drl", Sample.class), ResourceType.DRL);

    // ��L�R���p�C�����̃G���[����
    if (kbuilder.hasErrors()) {
      System.out.println(kbuilder.getErrors().toString());
      throw new RuntimeException("Unable to compile \"DroolsTest1.drl\".");
    }

    // KnowledgeBuilder����R���p�C���ς݂̃p�b�P�[�W���擾����
    final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

    // �擾�����p�b�P�[�W��Knowledgebase�ɒǉ�����(�p�b�P�[�W�̃f�v���C)
    final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
    kbase.addKnowledgePackages(pkgs);

    // �@Stateful�Z�b�V�������쐬
    final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

    // Agenda,WorkingMemory��View�\������
    ksession.addEventListener(new DebugAgendaEventListener());
    ksession.addEventListener(new org.kie.api.event.rule.DebugAgendaEventListener());

    // Audit���O�̃Z�b�g
    KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "log/DroolsTest1");

    // ���[���̎��s
    final Sales sale = new Sales();
    sale.setSales(6000);
    sale.setStatus(Sales.NOT_APPLIED);

    ksession.insert(sale);
    ksession.fireAllRules();

    // �@Audit���O�����
    logger.close();

    // �@Stateful�Z�b�V�����Ȃ̂ŁA���s���I������Ƃ����dispose���ė̈���J������
    ksession.dispose();
  }

  public static class Sales {

    public static final int NOT_APPLIED = 0; // �@���K�p
    public static final int APPLIED = 1; // �@�K�p��

    private long sales; // �@����グ
    private int status; // �@���(���K�p�A�K�p��)

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