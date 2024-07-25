/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;
import org.apache.commons.math3.linear.*;

/**
 *
 * @author Elahe <e.emami1375@gmail.com>
 */
public class DoubleToInt {
  private DoubleToInt() {
    
  }
  
  public static int[][] matToIntWithScale (RealMatrix input, int scaleFactor) {
    input = input.scalarMultiply(scaleFactor);
    int[][] ret = new int[input.getRowDimension()][input.getColumnDimension()];
    
    for (int i = 0; i < input.getRowDimension(); i++) {
      for (int j = 0; j < input.getColumnDimension(); j++) {
        ret[i][j] = (int) input.getData()[i][j];
      }
    }
    
    return ret;
  }
  
  public static int[] vecToIntWithScale (RealVector input, int scaleFactor) {
    input = input.mapMultiply(scaleFactor);
    int[] ret = new int[input.getDimension()];

    for (int i = 0; i < input.getDimension(); i++) {
      ret[i] = (int) input.getEntry(i);
    }
    
    return ret;
  }
}
