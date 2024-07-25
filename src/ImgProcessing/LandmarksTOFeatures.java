/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImgProcessing;

import Model.FacialFeatures;
import org.opencv.core.MatOfPoint2f;

/**
 *
 * @author Elahe <e.emami1375@gmail.com>
 */
public class LandmarksTOFeatures {
  
  public static FacialFeatures getFeatures(MatOfPoint2f landmark) {
    FacialFeatures features;
    
    double x_left_eye_left_point = landmark.get(36, 0)[0];
    double x_left_eye_right_point = landmark.get(49, 0)[0];
    
    double y_nose_up_point = landmark.get(27, 0)[1];
    double y_nose_down_point = landmark.get(33, 0)[1];
    
    double x_mounth_left_point = landmark.get(48, 0)[0];
    double x_mounth_right_point = landmark.get(54, 0)[0];
    
    double face_width = landmark.get(16, 0)[0] - landmark.get(0, 0)[0];
    double face_height = landmark.get(8, 0)[1] - landmark.get(19, 0)[1];
    
    double eye_width = (x_left_eye_right_point - x_left_eye_left_point) / face_width;
    double nose_height = (y_nose_down_point - y_nose_up_point) / face_height;
    double mounth_width = (x_mounth_right_point - x_mounth_left_point) / face_width;
    
    features = new FacialFeatures(eye_width, nose_height, mounth_width);
    
    return features;
  }
  
}
