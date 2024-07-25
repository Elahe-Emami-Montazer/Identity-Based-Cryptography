/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

/**
 *
 * @author Elahe <e.emami1375@gmail.com>
 */
public class Mahalanobis {
  
  private void Mahalanobis() {
    
  }
  
  public static int getXY(int[][] invCovMat, int[] x) {
    int X = 0;
    int n = x.length;
    
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        X += (invCovMat[i][j] * x[i] * x[j]);
               
      }
      
    }
    
    return X;
  }
  
  public static int getF_iY(int[][] invCovMat, int i, int[] y) {
    int f_i = 0;
    int n = y.length;
    
    for (int j = 0; j < n; j++) {
      f_i += (((-2) * invCovMat[i][j]) * y[j]);
      
    }
    
    return f_i;
  }
  
  public static int[] transformToW (int[][] invCovMat, int[] x) {
    int[] w = new int[x.length + 2];
    
    for (int i = 0; i < x.length; i++) {
      w[i] = x[i];
      
    }
    w[x.length] = getXY(invCovMat, x);
    w[x.length + 1] = 1;
    
    return w;
  }
  
  public static int[] transformToZ_delta (int[][] invCovMat, int[] y, int delta) {
    int[] z = new int[y.length + 2];
    
    for (int i = 0; i < y.length; i++) {
      z[i] = getF_iY(invCovMat, i, y);
      
    }
    z[y.length] = 1;
    z[y.length + 1] = getXY(invCovMat, y) - delta;
     
    return z;
  }
  
  public static int getDistance(int[][] invCovMat, int[] x, int[] y) {
    int distance = 0;
    int sum = 0;
    
    for (int i = 0; i < x.length; i++) {
      sum += (getF_iY(invCovMat, i, y) * x[i]);
      
    }
    
    distance = getXY(invCovMat, x) + getXY(invCovMat, y) + sum;
    
    return distance;
  }
  
    public static void prt(int[] a) {
    System.err.print("[");
    for (int i = 0; i < a.length; i++) {
      System.err.print(a[i] + ", ");
    }
    System.err.println("]");
  }
}
