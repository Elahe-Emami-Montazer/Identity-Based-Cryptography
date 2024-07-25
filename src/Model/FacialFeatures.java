/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author Elahe <e.emami1375@gmail.com>
 */
public class FacialFeatures {
  
  private final double EYE_WIDTH;
  private final double NOSE_HEIGHT;
  private final double MOUNTH_WIDTH;

  public FacialFeatures(double EYE_WIDTH, double NOSE_HEIGHT, double MOUNTH_WIDTH) {
    this.EYE_WIDTH = EYE_WIDTH;
    this.NOSE_HEIGHT = NOSE_HEIGHT;
    this.MOUNTH_WIDTH = MOUNTH_WIDTH;
    
  }

  public double getEYE_WIDTH() {
    return EYE_WIDTH;
  }

  public double getNOSE_HEIGHT() {
    return NOSE_HEIGHT;
  }

  public double getMOUNTH_WIDTH() {
    return MOUNTH_WIDTH;
  }

  @Override
  public String toString() {
    return "FacialFeatures: <" + "EYE_WIDTH = " + EYE_WIDTH + ", NOSE_HEIGHT = " + NOSE_HEIGHT + ", MOUNTH_WIDTH = " + MOUNTH_WIDTH + ">";
  }

  public double[] getVector() {
    double[] vector = {EYE_WIDTH, NOSE_HEIGHT, MOUNTH_WIDTH};
    return vector;
  }
  
}
