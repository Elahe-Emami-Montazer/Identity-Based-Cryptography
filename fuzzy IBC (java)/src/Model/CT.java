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
public class CT {
  
  private final Element c1;
  private final Element c2;
  private final Element c3;

  public CT(Element c1, Element c2, Element c3) {
    this.c1 = c1;
    this.c2 = c2;
    this.c3 = c3;
  }

  public Element getC1() {
    return c1;
  }

  public Element getC2() {
    return c2;
  }

  public Element getC3() {
    return c3;
  }
  
  
  
}
