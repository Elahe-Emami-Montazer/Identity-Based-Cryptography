/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Cryptography.PKG;
import GUI.dlgFaceLandmark;
import ImgProcessing.LandmarksTOFeatures;
import Model.CT;
import Model.FacialFeatures;
import Model.ISK;
import Utilities.DoubleToInt;
import Utilities.Mahalanobis;
import javax.swing.JOptionPane;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.opencv.core.MatOfPoint2f;

/**
 *
 * @author Elahe <e.emami1375@gmail.com>
 */
public class Test {
  public static void main(String args[]) {
    
    double[] Elahe = {0.22, 0.36, 0.34};
    RealVector vecElahe = MatrixUtils.createRealVector(Elahe);
    
    MatOfPoint2f landmark = dlgFaceLandmark.showAndGetFaceMark();
    FacialFeatures feature = LandmarksTOFeatures.getFeatures(landmark);
    System.out.println(feature);
    
    RealMatrix covarianceMatrix = MatrixUtils.createRealIdentityMatrix(3);

    PKG pkg = new PKG(3, 100, covarianceMatrix);
    pkg.setup();
    
    CT ct = pkg.enc(vecElahe, "hello Elahe!");
    
    RealVector vecElahe2 = MatrixUtils.createRealVector(feature.getVector());
    String message = pkg.dec(ct, vecElahe, vecElahe2);
    
    System.out.println("message decrypted to: " + message);
    
    if (message != null) {
      JOptionPane.showMessageDialog(null, ("message decrypted: " + message), "decryption", JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(null, "threshold error!!!", "decryption", JOptionPane.ERROR_MESSAGE);
    }
    
    // <editor-fold defaultstate="collapsed" desc="my tests">
    /*
    // Test FaceLandmarking
    
    MatOfPoint2f landmark = dlgFaceLandmark.showAndGetFaceMark();
    //System.out.println("landmark: " + landmark.dump());
    FacialFeatures features1 = LandmarksTOFeatures.getFeatures(landmark);
    System.out.println(features1);
    
    landmark = dlgFaceLandmark.showAndGetFaceMark();
    FacialFeatures features2 = LandmarksTOFeatures.getFeatures(landmark);
    System.out.println(features2);
    
    landmark = dlgFaceLandmark.showAndGetFaceMark();
    FacialFeatures features3 = LandmarksTOFeatures.getFeatures(landmark);
    System.out.println(features3);
    
    
    System.out.println("next person: ");
    
    landmark = dlgFaceLandmark.showAndGetFaceMark();
    FacialFeatures features11 = LandmarksTOFeatures.getFeatures(landmark);
    System.out.println(features11);
    
    landmark = dlgFaceLandmark.showAndGetFaceMark();
    FacialFeatures features22 = LandmarksTOFeatures.getFeatures(landmark);
    System.out.println(features22);
    
    landmark = dlgFaceLandmark.showAndGetFaceMark();
    FacialFeatures features33 = LandmarksTOFeatures.getFeatures(landmark);
    System.out.println(features33);
    
    
    
    
    
    RealMatrix covarianceMatrix = MatrixUtils.createRealIdentityMatrix(3);
    
    RealVector vecX = MatrixUtils.createRealVector(features1.getVector());
    RealVector vecY = MatrixUtils.createRealVector(features2.getVector());
    RealVector vecZ = MatrixUtils.createRealVector(features3.getVector());
    
    int dis1 = Mahalanobis.getDistance(DoubleToInt.matToIntWithScale(covarianceMatrix, 1), DoubleToInt.vecToIntWithScale(vecX, 100), DoubleToInt.vecToIntWithScale(vecY, 100));
    int dis2 = Mahalanobis.getDistance(DoubleToInt.matToIntWithScale(covarianceMatrix, 1), DoubleToInt.vecToIntWithScale(vecX, 100), DoubleToInt.vecToIntWithScale(vecZ, 100));
    int dis3 = Mahalanobis.getDistance(DoubleToInt.matToIntWithScale(covarianceMatrix, 1), DoubleToInt.vecToIntWithScale(vecY, 100), DoubleToInt.vecToIntWithScale(vecZ, 100));
    
    System.out.println("dist1: " + dis1);
    System.out.println("dist2: " + dis2);
    System.out.println("dist3: " + dis3);
    
    RealVector vecXx = MatrixUtils.createRealVector(features11.getVector());
    RealVector vecYy = MatrixUtils.createRealVector(features22.getVector());
    RealVector vecZz = MatrixUtils.createRealVector(features33.getVector());
    
    int dis11 = Mahalanobis.getDistance(DoubleToInt.matToIntWithScale(covarianceMatrix, 1), DoubleToInt.vecToIntWithScale(vecXx, 100), DoubleToInt.vecToIntWithScale(vecYy, 100));
    int dis22 = Mahalanobis.getDistance(DoubleToInt.matToIntWithScale(covarianceMatrix, 1), DoubleToInt.vecToIntWithScale(vecXx, 100), DoubleToInt.vecToIntWithScale(vecZz, 100));
    int dis33 = Mahalanobis.getDistance(DoubleToInt.matToIntWithScale(covarianceMatrix, 1), DoubleToInt.vecToIntWithScale(vecYy, 100), DoubleToInt.vecToIntWithScale(vecZz, 100));
    
    System.out.println("dist11: " + dis11);
    System.out.println("dist22: " + dis22);
    System.out.println("dist33: " + dis33);
    
    int dis111 = Mahalanobis.getDistance(DoubleToInt.matToIntWithScale(covarianceMatrix, 1), DoubleToInt.vecToIntWithScale(vecXx, 100), DoubleToInt.vecToIntWithScale(vecY, 100));
    int dis222 = Mahalanobis.getDistance(DoubleToInt.matToIntWithScale(covarianceMatrix, 1), DoubleToInt.vecToIntWithScale(vecXx, 100), DoubleToInt.vecToIntWithScale(vecZ, 100));
    int dis333 = Mahalanobis.getDistance(DoubleToInt.matToIntWithScale(covarianceMatrix, 1), DoubleToInt.vecToIntWithScale(vecYy, 100), DoubleToInt.vecToIntWithScale(vecZ, 100));
    
    System.out.println("dist111: " + dis111);
    System.out.println("dist222: " + dis222);
    System.out.println("dist333: " + dis333);
    */
    
    /*
    CT ct = pkg.enc(vecX, "hello bibe!");
    
    String decryptedMessage = pkg.dec(ct, vecX, vecY);
    System.out.println("decrypted message: " + decryptedMessage);
    */
    // Test PKG
    /*
    double[] x = {2.9, 4.0, 3};
    double[] y = {3, 3.8, 3};
        
    RealVector vecX = MatrixUtils.createRealVector(x);
    RealVector vecY = MatrixUtils.createRealVector(y);
    
    CT ct = pkg.enc(vecX, "hello bibe!");
    
    String decryptedMessage = pkg.dec(ct, vecX, vecY);
    System.out.println("decrypted message: " + decryptedMessage);
    
    
    // Test offline Mode
    /*
    ISK[] sk = pkg.keyGen_offlineMode(vecY);
    decryptedMessage = pkg.dec_offlineMode(sk, ct, vecX, vecY);
    System.out.println("decrypted message offline mode: " + decryptedMessage);
    */
    // </editor-fold>
  }
}
