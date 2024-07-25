/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   TA.h
 * Author: Elahe <e.emami1997@gmail.com>
 *
 * Created on May 27, 2019, 11:08 AM
 */

#ifndef TA_H
#define TA_H

#include <gmp.h>
#include <pbc/pbc.h>

class TA {
public:
    TA();
    //void sha(char* sha_result, char target_string[]);
    void setup();
    void generate_Pseudo_Identity(element_t ID1, element_t ID2, char* RID);
    void generate_private_key(element_t sk1, element_t sk2, element_t ID1, element_t ID2);
    void sign(element_t sigma, element_t sk1, element_t sk2, char* message);
    void verify(bool verified, element_t ID1, element_t ID2, char* message, element_t sigma);
    void batch_verify(bool verified, element_t ID1[], element_t ID2[], char* message[], element_t sigma[], int num);
    void sum(element_t result, element_t e[], int num);
    
    pairing_t pairing;
    element_t P;
    element_t P_pub1;
    element_t P_pub2;
    
    element_t s1;
    element_t s2;
    
private: 
    int size = 40;
    int rbits = 160;
    int qbits = 160;
    
};

#endif /* TA_H */

