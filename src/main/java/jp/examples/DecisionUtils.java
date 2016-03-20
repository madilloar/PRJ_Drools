package jp.examples;

public class DecisionUtils {
  public static boolean exec01(Product b, Product a) {
    return (Math.abs(a.getHeight() - b.getHeight()) < 1);
  }
}
