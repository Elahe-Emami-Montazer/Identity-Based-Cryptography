/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cryptography;

import Model.CT;
import Model.ISK;
import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import org.apache.commons.math3.linear.*;
import Utilities.DoubleToInt;
import Utilities.Mahalanobis;

/**
 *
 * @author Elahe <e.emami1375@gmail.com>
 */
public class PKG {

  private final int rBits = 160;
  private final int qBits = 512;
  private final int i_dimension; //n-dimension vectors for IPE (dimension + 2)
  private final int dimension; //n-dimension vectors as input
  private final int scale_factor; //scale factor to make vector data to int
  private final int[][] F_inv; //invert of covariance matrix
  private final int threshold = 30; //threshold t according to F and n

  private Pairing pairing;

  private Field G1;
  private Field GT;
  private Field Zr;

  private Element generator;  //generator of G1

  private Element lambda, beta, eta;  //random element of Zr
  private Element[] alpha;

  private Element[] g;
  private Element v;
  private Element u;
  private Element g_eta;
  private Element g_lambda;

  public PKG(int dimension, int scaleFactor, RealMatrix covarianceMatrix) {
    this.dimension = dimension;
    this.i_dimension = dimension + 2;
    this.scale_factor = scaleFactor;
    
    RealMatrix invCovMat = MatrixUtils.inverse(covarianceMatrix);
    this.F_inv = DoubleToInt.matToIntWithScale(invCovMat, 1); //change it after you underestand

  }

  public void setup() {
    System.out.println("-----------------------------SETUP-----------------------------");

    //step a)
    iSetup();

    //step b)
    //Choose a reasonable threshold t according to F and n
    //step c) 
    //return master secret key: (g_lambda) and master public key: (i_dimension, G1, GT, generator, p(group order), e, g[i], v, u, g_eta)
  }

  
  public ISK[] keyGen_offlineMode(RealVector vecY) {
    System.out.println("-----------------------------KeyGen_offlineMode-----------------------------");
    ISK[] sk = new ISK[threshold];

    if (vecY.getDimension() != dimension) {
      System.err.println("wrong dimension!");

    } else {
      int[] y = DoubleToInt.vecToIntWithScale(vecY, scale_factor);

      int[] z = new int[dimension + 2];
      ISK isk;

      for (int i = 0; i < threshold; i++) {
        z = Mahalanobis.transformToZ_delta(F_inv, y, i);
//        System.out.println("z:");
//        Mahalanobis.prt(z);
        isk = iKeyGen(z);
        sk[i] = isk;
//        System.out.println("sk[:" + i + "]1: " + sk[i].getIsk1());

      }

    }

    return sk;
  }
   

  public ISK keyGen(RealVector vecY, int delta) {
    System.out.println("-----------------------------KeyGen-----------------------------");
    ISK isk = null;
    if (vecY.getDimension() != dimension) {
      System.err.println("wrong dimension!");

    } else {
      int[] y = DoubleToInt.vecToIntWithScale(vecY, scale_factor);

      int[] z = new int[dimension + 2];

      z = Mahalanobis.transformToZ_delta(F_inv, y, delta);
      System.out.println("z:");
      
      isk = iKeyGen(z);

    }

    return isk;
  }

  public CT enc(RealVector vecX, String mes) {
    System.out.println("-----------------------------Enc-----------------------------");
    CT ict = null;

    if (vecX.getDimension() != dimension) {
      System.err.println("wrong dimension!");

    } else {
      int[] w = new int[dimension + 2];
      int[] x = DoubleToInt.vecToIntWithScale(vecX, scale_factor);

      w = Mahalanobis.transformToW(F_inv, x);
//      System.out.println("w:");
//      Mahalanobis.prt(w);

      ict = iEnc(w, mes);

    }

    return ict;
  }

  
  public String dec_offlineMode(ISK[] sk, CT ct, RealVector vecX, RealVector vecY) {
    System.out.println("-----------------------------Dec-----------------------------");
    String message = null;

    if ((vecX.getDimension() != dimension) || (vecY.getDimension() != dimension)) {
      System.err.println("wrong dimension!");

    } else {
      int[] x = DoubleToInt.vecToIntWithScale(vecX, scale_factor);
      int[] y = DoubleToInt.vecToIntWithScale(vecY, scale_factor);

      int delta = Mahalanobis.getDistance(F_inv, x, y);
      System.out.println("d(x, y): " + delta);

      if ((delta < 0) || (delta >= threshold)) {
        System.err.println("thereshhold error!!!");

      } else {
        ISK isk_delta = sk[delta];
        int[] w = Mahalanobis.transformToW(F_inv, x);
        message = iDec(w, isk_delta, ct);
//        System.out.println("message: " + message);

      }

    }

    return message;
  }

  
  public String dec(CT ct, RealVector vecX, RealVector vecY) {
    System.out.println("-----------------------------Dec-----------------------------");
    String message = null;

    if ((vecX.getDimension() != dimension) || (vecY.getDimension() != dimension)) {
      System.err.println("wrong dimension!");

    } else {
      int[] x = DoubleToInt.vecToIntWithScale(vecX, scale_factor);
      int[] y = DoubleToInt.vecToIntWithScale(vecY, scale_factor);

      int delta = Mahalanobis.getDistance(F_inv, x, y);
      System.out.println("d(x, y): " + delta);

      if ((delta < 0) || (delta >= threshold)) {
        System.err.println("thereshhold error!!!");

      } else {
        ISK isk_delta = keyGen(vecY, delta);
        int[] w = Mahalanobis.transformToW(F_inv, x);
        message = iDec(w, isk_delta, ct);
//        System.out.println("message: " + message);

      }

    }

    return message;
  }

  private void iSetup() {

    //step a)
    PairingParametersGenerator gen = new TypeACurveGenerator(rBits, qBits);
    PairingParameters parameters = gen.generate();
    pairing = PairingFactory.getPairing(parameters);

    if (!pairing.isSymmetric()) {
      System.err.println("error: pairing must be symmetric!");
    }

    G1 = pairing.getG1();
    GT = pairing.getGT();
    Zr = pairing.getZr();

    generator = G1.newRandomElement();
//    System.out.println("generator: " + generator);

    //step b)
    lambda = Zr.newRandomElement();
    beta = Zr.newRandomElement();
    eta = Zr.newRandomElement();

    alpha = new Element[i_dimension];
    for (int i = 0; i < i_dimension; i++) {
      alpha[i] = Zr.newRandomElement();
      //System.out.println("alpha[" + i + "]: " + alpha[i]);
    }

//    System.out.println("lambda: " + lambda);
//    System.out.println("beta: " + beta);
//    System.out.println("eta: " + eta);
    //step c)
    g = new Element[i_dimension];
    for (int i = 0; i < i_dimension; i++) {
      g[i] = G1.newElement();
      g[i] = generator.duplicate().powZn(alpha[i]);
//      System.out.println("g[" + i + "]: " + g[i]);
    }

    v = G1.newElement();
    v = generator.duplicate().powZn(beta);
//    System.out.println("v: " + v);

    u = GT.newElement();
    u = pairing.pairing(generator, generator).duplicate().powZn(lambda);
//    System.out.println("u: " + u);

    g_eta = G1.newElement();
    g_eta = generator.duplicate().powZn(eta);
//    System.out.println("g_eta: " + g_eta);

    g_lambda = G1.newElement();
    g_lambda = generator.duplicate().powZn(lambda);
//    System.out.println("g_lambda: " + g_lambda);

//    System.out.println("gen: " + generator);
    //step d) return master secret key: (g_lambda) and master public key: (i_dimension, G1, GT, generator, p(group order), e, g[i], v, u, g_eta)
  }

  private ISK iKeyGen(int[] z) {

    Element isk1;
    Element[] isk2;
    Element isk3;
    //step a)
    Element t;
    t = Zr.newRandomElement();
//    System.out.println("t: " + t);

    //step b)
    isk1 = G1.newElement();
    isk1 = generator.duplicate().powZn(t);
//    System.out.println("isk1: " + isk1);

    isk2 = new Element[i_dimension];
    Element g_pow_etaz;
    g_pow_etaz = G1.newElement();
    Element etaz;
    etaz = Zr.newElement();
    Element tmp;
    tmp = G1.newElement();

    for (int i = 0; i < i_dimension; i++) {
      etaz = Zr.newElement();
      etaz = eta.duplicate().mul(z[i]); //etaz = eta . z[i]
      g_pow_etaz = G1.newElement();
      g_pow_etaz = generator.duplicate().powZn(etaz); // g_pow_etaz = generator ^ (eta . z[i])
      tmp = G1.newElement();
      tmp = g_pow_etaz.duplicate().mul(g[i]); //  tmp = (generator ^ (eta . z[i])) . g[i]
      isk2[i] = G1.newElement();
      isk2[i] = tmp.duplicate().powZn(t); //isk2[i] = ((generator ^ (eta . z[i])) . g[i]) ^ t
//      System.out.println("isk2[" + i + "]: " + isk2[i]);

    }

    Element v_pow_t;
    v_pow_t = G1.newElement();
    v_pow_t = v.duplicate().powZn(t);
//    System.out.println("v ^ t: " + v_pow_t);
    isk3 = G1.newElement();
    isk3 = g_lambda.duplicate().mul(v_pow_t); //isk3 = (generator ^ lambda) . (v ^ t)
//    System.out.println("isk3: " + isk3);

    ISK isk = new ISK(isk1, isk2, isk3);

    return isk;
  }

  private CT iEnc(int[] w, String m) {

    Element c1 = GT.newElement();
    Element c2 = G1.newElement();
    Element c3 = G1.newElement();;

    if (w.length != i_dimension) {
      System.err.println("wrong dimension!");

    } else {
//      System.out.println("m: " + m);
      Element element_m;
      element_m = GT.newElementFromBytes(m.getBytes());
//      System.out.println("element_m: " + element_m);

      //step a)
      Element s;
      s = Zr.newRandomElement();
//      System.out.println("s: " + s);

      Element u_pow_s;
      u_pow_s = GT.newElement();
      u_pow_s = u.duplicate().powZn(s);
//      System.out.println("u ^ s: " + u_pow_s);

      //Element c1;
      c1 = u_pow_s.duplicate().mul(element_m);
//      System.out.println("c1: " + c1);

      //Element c2;
      c2 = generator.duplicate().powZn(s);
//      System.out.println("c2: " + c2);

      //step b)
      Element[] gi_pow_wi = new Element[i_dimension];
      gi_pow_wi = powAllToBigInt(g, w);
      Element tmp;
      tmp = G1.newElement();
      tmp = mullAllGElements(gi_pow_wi);
//      System.out.println("mul(gi ^ wi): " + tmp);

      Element tmp2;
      tmp2 = G1.newElement();
      tmp2 = v.duplicate().mul(tmp);
//      System.out.println("v . (mul(gi ^ wi)): " + tmp2);

      //Element c3;
      c3 = tmp2.duplicate().powZn(s);
//      System.out.println("c3: " + c3);

    }

    CT ict = new CT(c1, c2, c3);

    return ict;
  }

  private String iDec(int[] w, ISK isk, CT ict) {

    Element isk1 = isk.getIsk1();
    Element[] isk2 = isk.getIsk2();
    Element isk3 = isk.getIsk3();

    Element c1 = ict.getC1();
    Element c2 = ict.getC2();
    Element c3 = ict.getC3();

    //step a)
    Element[] tmp = new Element[i_dimension];
    tmp = powAllToBigInt(isk2, w);

    Element tmp2;
    tmp2 = G1.newElement();
    tmp2 = mullAllGElements(tmp);
//    System.out.println("mul(isk2[i] ^ w[i]): " + tmp2);

    Element isk3_mul_tmp2;
    isk3_mul_tmp2 = G1.newElement();
    isk3_mul_tmp2 = isk3.duplicate().mul(tmp2);
//    System.out.println("isk3 . mul(isk2[i] ^ w[i]): " + isk3_mul_tmp2);

    Element e1;
    e1 = GT.newElement();
    e1 = pairing.pairing(isk3_mul_tmp2, c2);
//    System.out.println("e1: " + e1);

    Element e2;
    e2 = G1.newElement();
    e2 = pairing.pairing(isk1, c3);
//    System.out.println("e2: " + e2);

    Element e1_inv;
    e1_inv = GT.newElement();
    e1_inv = e1.duplicate().invert();
//    System.out.println("e1_inv: " + e1_inv);

    Element m_element;
    m_element = GT.newElement();
    m_element = c1.duplicate().mul(e1_inv).duplicate().mul(e2);
//    System.out.println("m_element: " + m_element);

    String message = new String(m_element.duplicate().toBytes(), 0, m_element.getLengthInBytes());
//    System.out.println("m_element: " + m_element);
//    System.out.println("message: " + message);

    return message;

  }

  private Element[] powAllToBigInt(Element[] base, int[] pow) {
    Element[] result;
    result = new Element[base.length];

    if (base.length != pow.length) {
      System.err.println("base length not equal to pow length");
      return null;

    } else {
      for (int i = 0; i < base.length; i++) {
        result[i] = base[i].duplicate().getField().newElement();
        result[i] = base[i].duplicate().powZn((Zr.newElement(pow[i])));

      }

    }

    return result;
  }

  private Element mullAllGElements(Element[] element) {
    Element result;
    result = element[0].duplicate().getField().newOneElement();

    for (int i = 0; i < element.length; i++) {
      result = result.duplicate().mul(element[i].duplicate());

    }

    return result;
  }

}
