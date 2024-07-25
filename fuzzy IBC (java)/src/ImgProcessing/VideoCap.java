/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImgProcessing;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author Elahe <e.emami1375@gmail.com>
 */
public class VideoCap {
  private VideoCapture capture;
  private Mat frame;
  
  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }
          
  public void startCap() {
    //System.out.println("capture started");
    frame = new Mat();
    capture = new VideoCapture(0);
    capture.grab();
    
  }
  
  public void stopCap() {
    capture.release();
    
  }
  
  public BufferedImage matToImg(Mat mat) {
    BufferedImage img;
    
    img = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
    WritableRaster raster = img.getRaster();
    DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
    byte[] data = dataBuffer.getData();
    mat.get(0, 0, data);
    
    return img;
  }
  
  public Mat getMat() {
    capture.read(frame);
    return frame;
    
  }
  
}
