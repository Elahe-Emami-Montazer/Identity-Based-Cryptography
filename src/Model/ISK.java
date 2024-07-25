/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import it.unisa.dia.gas.jpbc.Element;

/**
 *
 * @author Elahe <e.emami1375@gmail.com>
 */
public class ISK {
  
  private final Element isk1;
  private final Element[] isk2;
  private final Element isk3;

  public ISK(Element isk1, Element[] isk2, Element isk3) {
    this.isk1 = isk1;
    this.isk2 = isk2;
    this.isk3 = isk3;
    
  }

  public Element getIsk1() {
    //System.out.println("getisk1: " + isk1);
    return isk1;
  }

  public Element[] getIsk2() {
    for (int i = 0; i < isk2.length; i++) {
      //System.out.println("getisk2[" + i + "]: " + isk2[i]);
    }
    return isk2;
  }

  public Element getIsk3() {
    //System.out.println("getisk3: " + isk3);    
    return isk3;
  }
  
  
  
}
