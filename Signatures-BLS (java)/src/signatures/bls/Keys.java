/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signatures.bls;

import it.unisa.dia.gas.jpbc.Element;

/**
 *
 * @author elahe
 */
public class Keys {
  
  Element publicKey;
  Element PrivateKey;

  public Keys(Element publicKey, Element PrivateKey) {
    this.publicKey = publicKey;
    this.PrivateKey = PrivateKey;
  }
  
  
  
}
