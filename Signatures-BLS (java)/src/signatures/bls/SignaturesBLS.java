/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signatures.bls;

/**
 *
 * @author Elahe
 * Short Signatures from the Weil Pairing
 * based on "Dan Boneh, Ben Lynn, and Hovav Shacham" paper
 */

import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.*;

public class SignaturesBLS {
  
  Pairing pairing;
  Field G1, G2;
  Field Zr;
  Element g;

  public SignaturesBLS() {
    int rbits = 160;
    int qbits = 512;
    
    PairingParametersGenerator generator = new TypeACurveGenerator(rbits, qbits);
    PairingParameters parameters = generator.generate();
    pairing = PairingFactory.getPairing(parameters);
    //System.out.println("pairing parameters: " + parameters);
    
    G1 = pairing.getG1();
    G2 = pairing.getG2();
    Zr = pairing.getZr();
    
    g = G2.newRandomElement();
    //System.out.println("g: " + g);
    
  }
  
  public Keys genKey() {
    System.out.println("---generating keys---");
    
    Element publicKey;
    Element privateKey;
    
    privateKey = Zr.newRandomElement();
    publicKey = g.duplicate();
    publicKey.powZn(privateKey);
    
    System.out.println("privateKey: " + privateKey);
    System.out.println("publicKey: " + publicKey);
     
    return new Keys(publicKey, privateKey);
  }
  
  public Element sign(Element privateKey, String message) {
    System.out.println("---sign---");
    
    Element sigma = G1.newZeroElement();
    Element h = G1.newElementFromHash(message.getBytes(), 0, message.getBytes().length);
    //System.out.println("h: " + h);
    
    sigma = h.duplicate();
    sigma.powZn(privateKey);
    System.out.println("sigma: " + sigma);
    
    return sigma;
  }
  
  public boolean verify(Element sigma, Element publicKey, String message) {
    System.out.println("---verify---");
    boolean ret = false;
    
    Element h = G1.newElementFromHash(message.getBytes(), 0, message.getBytes().length);
    
    Element tmp1, tmp2;
    tmp1 = pairing.pairing(sigma, g);
    tmp2 = pairing.pairing(h, publicKey);
    
    /*
    System.out.println("sigma: " + sigma);
    System.out.println("g: " + g);
    System.out.println("h: " + h);
    System.out.println("publicKey: " + publicKey);
    */
    
    if (tmp1.isEqual(tmp2)) {
      ret = true;
      System.out.println("signature verifies...");
      
    } else {
      System.err.println("signature does not verify!!!");
      
    }
    
    
    
    return ret;
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    SignaturesBLS sig = new SignaturesBLS();
    
    Keys keys = sig.genKey();
    Element publicKey = keys.publicKey;
    Element privateKey = keys.PrivateKey;
    
    long start = System.currentTimeMillis();
    Element sigma = sig.sign(privateKey, "hello world!");
    long stop = System.currentTimeMillis();
    
    System.out.println("sign time: " + (stop-start));
    
    start = System.currentTimeMillis();
    boolean b = sig.verify(sigma, publicKey, "hello world!");
    stop = System.currentTimeMillis();
    
    System.out.println("verify time: " + (stop-start));
    
    //b = sig.verify(sigma, publicKey, "another message");
    
    //System.out.println("g size: " + sigma.getLengthInBytes());
    
  }
  
}


























