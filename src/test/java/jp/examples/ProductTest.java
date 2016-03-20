package jp.examples;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import jp.examples.utils.StringUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProductTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test01() {
    Product t = new Product("id01");
    t.setHeight(10);
    t.setWidth(20);
    t.setDepth(30);
    String exp = "{\"productId\":\"id01\",\"height\":10.0,\"width\":20.0,\"depth\":30.0}";
    String act = t.toString();
    assertEquals(exp, act);
  }

  @Test
  public void test02() {
    String productId = "id02";
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

    String exp = "{\"productId\":\"id02\",\"beforeProduct\":{\"productId\":\"id02\",\"height\":10.0,\"width\":20.0,\"depth\":30.0},\"afterProcduct\":{\"productId\":\"id02\",\"height\":10.0,\"width\":20.0,\"depth\":30.5},\"differenceReasons\":[{\"productId\":\"id02\",\"itemId\":\"Depth\",\"reasonId\":\"奥行のサイズが異なります。\"}],\"decision\":null}";

    ProductDifference pd = new ProductDifference(productId, b, a, r);
    String act = pd.toString();

    assertEquals(exp, act);
  }

  @Test
  public void test03() {
    String exp ="null";
    String act =StringUtils.toJson(null);
    assertEquals(exp, act);
  }
}
