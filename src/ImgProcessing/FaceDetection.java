/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImgProcessing;

import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.face.Face;
import org.opencv.face.Facemark;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * @author Elahe <e.emami1375@gmail.com>
 */
public class FaceDetection {
  private static final CascadeClassifier cascade;
  private static final MatOfRect faces;
  private static final Facemark fm;
  private ArrayList<MatOfPoint2f> landmarks;
  

  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    
//    cascade = new CascadeClassifier("imgProc-data/haarcascades/haarcascade_frontalface_alt.xml");
//    cascade = new CascadeClassifier("imgProc-data/lbpcascades/lbpcascade_frontalface_improved.xml");
//    fm = Face.createFacemarkKazemi();
//    fm.loadModel("imgProc-data/face_landmark_model.dat");

    cascade = new CascadeClassifier("imgProc-data/haarcascades_cuda/haarcascade_frontalface_alt2.xml");
    faces = new MatOfRect();

    fm = Face.createFacemarkLBF();
    fm.loadModel("imgProc-data/lbfmodel.yaml");
    
  }

  public Mat startDetection(Mat mat) {

    // detect faces
    cascade.detectMultiScale(mat, faces);

    if (!faces.empty()) {
      // fit landmarks for each found face
      landmarks = new ArrayList<MatOfPoint2f>();
      fm.fit(mat, faces, landmarks);
      
      drawOutlines(landmarks, mat);
      
    }

    return mat;
  }
  
  public MatOfPoint2f getLandmark() {
    return landmarks.get(0);
    
  }
  
  private void  drawOutlines(ArrayList<MatOfPoint2f> landmarks, Mat mat) {
    // draw face border
      draw(landmarks, mat, 0, 17, new Scalar(0, 0, 255));

      // draw nose border
      draw(landmarks, mat, 27, 36, new Scalar(0, 255, 0));

      // draw eyebrows
      draw(landmarks, mat, 17, 22, new Scalar(255, 255, 0));
      draw(landmarks, mat, 22, 27, new Scalar(255, 255, 0));

      // draw mounth
      draw(landmarks, mat, 48, 65, new Scalar(0, 255, 255));

      // draw left eye
      draw(landmarks, mat, 36, 42, new Scalar(255, 0, 255));

      // draw right eye
      draw(landmarks, mat, 42, 48, new Scalar(255, 0, 255));
      
  }

  private void draw(ArrayList<MatOfPoint2f> landmarks, Mat img, int start, int end, Scalar color) {
    for (int i = 0; i < landmarks.size(); i++) {
      MatOfPoint2f listOfPoints = landmarks.get(i);
      Point previousPoint = new Point(listOfPoints.get(start, 0)[0], listOfPoints.get(start, 0)[1]);
      //Imgproc.circle(img, previousPoint, 1, new Scalar(222), 5);

      for (int j = start + 1; j < end; j++) {
        Point p = new Point(listOfPoints.get(j, 0)[0], listOfPoints.get(j, 0)[1]);
        //Imgproc.circle(img, p, 1, new Scalar(222), 3);

        Imgproc.line(img, previousPoint, p, color);

        previousPoint = p.clone();
      }
    }
  }
}
