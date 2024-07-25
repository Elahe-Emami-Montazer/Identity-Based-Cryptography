/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Signature.h
 * Author: Elahe <e.emami1997@gmail.com>
 * 
 * Paterson ID-based signature.
 * Based on "K. G. Paterson. ID-Based Signatures from Pairings on Elliptic Curvers. Electron. Lett., Vol. 38" paper
 *
 * Created on April 5, 2019, 9:41 PM
 */
#include <pbc/pbc.h>
#include <cstdlib>
#include <iostream>
#include <cstring>
#include <pbc/pbc_pairing.h>

#ifndef SIGNATURE_H
#define SIGNATURE_H

class Signature {
public:
    Signature();
    void get_public_key(element_t public_key, char* ID);
    void get_private_key(element_t private_key, char* ID);
    void sign(element_t R, element_t S, element_t private_key, char* message);
    void verify(bool verified, element_t public_key, char* message, element_t U, element_t V);
          
private:
    element_t P;  
    element_t Ppub;  
    element_t s;
    pairing_t pairing;
    
    int rbits = 160;
    int qbits = 512;
    int size = 100;
    int n = 40; //message size
    
};

#endif /* SIGNATURE_H */

