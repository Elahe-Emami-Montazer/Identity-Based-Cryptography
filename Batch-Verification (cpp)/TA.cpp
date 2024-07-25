/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   TA.cpp
 * Author: Elahe <e.emami1997@gmail.com>
 * 
 * Created on May 27, 2019, 11:08 AM
 */

#include "TA.h"
#include <cstring>
#include <X11/Xlib.h>

TA::TA() {

}

/*
void TA::sha(char* sha_result, char target_string[])
{

   SHA1Context sha;
   int i;
   unsigned int g;
   SHA1Reset(&sha);
   SHA1Input(&sha, (const unsigned char *) target_string,
             strlen(target_string));

   if (!SHA1Result(&sha))
   {
     fprintf(stderr, "ERROR-- could not compute message digest\n");
   }else{
      //printf("\t");
      for (i = 0; i < 5; i++)
      {
         g = sha.Message_Digest[i];

      }

       sprintf(sha_result,
                "%08X%08X%08X%08X%08X", sha.Message_Digest[0], sha.Message_Digest[1], sha.Message_Digest[2], sha.Message_Digest[3], sha.Message_Digest[4]);

    }

}
 */

void TA::setup() {
    pbc_param_t par; //Parameter to generate the pairing
    pbc_param_init_a_gen(par, rbits, qbits); //Initial the parameter for the pairing
    pairing_init_pbc_param(pairing, par); //Initial the pairing

    //In our case, the pairing must be symmetric (G1 * G1 -> GT)
    if (!pairing_is_symmetric(pairing))
        pbc_die("pairing must be symmetric");

    element_init_Zr(s1, pairing);
    element_init_Zr(s2, pairing);
    element_init_G1(P_pub1, pairing);
    element_init_G1(P_pub2, pairing);
    element_init_G1(P, pairing);

    element_random(P);
    //element_printf("P: %B\n", P);

    //TA first randomly chooses s1, s2 ∈ Z*q as its two master keys
    element_random(s1);
    element_random(s2);

    //element_printf("s1: %B\n", s1);
    //element_printf("s2: %B\n", s2);

    element_mul_zn(P_pub1, P, s1); //P_pub1 = s1.P
    element_mul_zn(P_pub2, P, s2); //P_pub2 = s2.P

    //element_printf("P_pub1: %B\n", P_pub1);
    //element_printf("P_pub2: %B\n", P_pub2);

}

void TA::generate_Pseudo_Identity(element_t ID1, element_t ID2, char* RID) {
    element_t r;

    element_init_G1(ID1, pairing);
    element_init_G1(ID2, pairing);
    element_init_Zr(r, pairing);

    element_random(r); //choose a random r as a nonce

    element_mul_zn(ID1, P, r); //ID1 = r.P
    //element_printf("ID1: %B\n", ID1);

    element_t H_RID;
    element_init_G1(H_RID, pairing);
    element_from_hash(H_RID, RID, strlen(RID)); //H_RID = H(RID)
    //element_printf("H(RID): %B\n", H_RID);

    element_t tmp1;
    element_init_G1(tmp1, pairing);
    element_mul_zn(tmp1, P_pub1, r); //tmp1 = r.P_pub1

    element_add(ID2, H_RID, tmp1); //ID2 = H(RID) +. r.P_pub1 where +. is addition on elliptic curve
    //element_printf("ID2: %B\n", ID2);   

}

void TA::generate_private_key(element_t sk1, element_t sk2, element_t ID1, element_t ID2) {
    element_init_G1(sk1, pairing);
    element_init_G1(sk2, pairing);

    element_mul_zn(sk1, ID1, s1); //sk1 = s1.ID1
    //element_printf("sk1: %B\n", sk1);

    element_t tmp;
    element_init_G1(tmp, pairing);
    element_add(tmp, ID1, ID2); //tmp = ID1 +. ID2

    element_mul_zn(sk2, tmp, s2);
    //element_printf("sk2: %B\n", sk2);
}

void TA::sign(element_t sigma, element_t sk1, element_t sk2, char* message) {
    //element_printf("in sign: sk1: %B, sk2: %B\n", sk1, sk2);
    element_init_G1(sigma, pairing);

    element_t h_m;
    element_init_Zr(h_m, pairing);

    element_from_hash(h_m, message, strlen(message));
    //element_printf("h_m: %B\n", h_m);

    element_t tmp;
    element_init_G1(tmp, pairing);
    element_mul_zn(tmp, sk2, h_m);
    //element_printf("tmp: %B\n", tmp);

    element_add(sigma, sk1, tmp);
    //element_printf("sigma: %B\n", sigma);

}

void TA::verify(bool verified, element_t ID1, element_t ID2, char* message, element_t sigma) {
    verified = False;

    element_t tmp1;
    element_init_GT(tmp1, pairing);

    element_pairing(tmp1, sigma, P); //tmp1 = e(sigma, P)
    //element_printf("e(sigma, P): %B\n", tmp1);

    element_t tmp2;
    element_init_GT(tmp2, pairing);

    element_pairing(tmp2, ID1, P_pub1); //tmp2 = e(ID1, P_pub1)
    //element_printf("e(ID1, P_pub1): %B\n", tmp2);

    element_t h_m;
    element_init_Zr(h_m, pairing);

    element_from_hash(h_m, message, strlen(message));
    //element_printf("h_m: %B\n", h_m);

    element_t tmp;
    element_init_G1(tmp, pairing);
    element_add(tmp, ID1, ID2); //tmp = ID1 +. ID2

    element_t tmp3;
    element_init_G1(tmp3, pairing);
    element_mul_zn(tmp3, tmp, h_m); //tmp3 = H(m).(ID1 +. ID2)
    //element_printf("H(m).(ID1 +. ID2): %B\n", tmp3);

    element_t tmp4;
    element_init_GT(tmp4, pairing);
    element_pairing(tmp4, tmp3, P_pub2); //tmp4 = e(H(m).(ID1 +. ID2), P_pub2)
    //element_printf("e(H(m).(ID1 +. ID2), P_pub2): %B\n", tmp4);

    element_t tmp5;
    element_init_GT(tmp5, pairing);
    element_mul(tmp5, tmp2, tmp4); //tmp5 = e(ID1, P_pub1) . e(H(m).(ID1 +. ID2), P_pub2)
    //element_printf("tmp5: %B\n", tmp5);

    if (element_cmp(tmp1, tmp5) == 0) {
        verified = True;
//        printf("verified\n");
    } else {
        verified = False;
        printf("not verified\n");
    }

}

void TA::batch_verify(bool verified, element_t ID1[], element_t ID2[], char* message[], element_t sigma[], int num) {
    verified = false;

    element_t sum_sigma;
    element_init_G1(sum_sigma, pairing);

    sum(sum_sigma, sigma, num);
    //element_printf("sum_sigma: %B\n", sum_sigma);

    element_t sum_ID1;
    element_init_G1(sum_ID1, pairing);

    sum(sum_ID1, ID1, num);
    //element_printf("sum_ID1: %B\n", sum_ID1);

    element_t tmp3[num];
    int i = 0;
    element_t h_m;
    element_init_Zr(h_m, pairing);
    element_t tmp;
    element_init_G1(tmp, pairing);
    
    while (i < num) {
        element_from_hash(h_m, message[i], strlen(message[i]));
        //element_printf("h_m: %B\n", h_m);

        element_add(tmp, ID1[i], ID2[i]); //tmp = ID1 +. ID2
        element_init_G1(tmp3[i], pairing);
        element_mul_zn(tmp3[i], tmp, h_m); //tmp3 = H(m).(ID1 +. ID2)
        //element_printf("tmp3[%d]: %B\n", i, tmp3[i]);
        i++;
    }
    
    element_t sum_tmp3;
    element_init_G1(sum_tmp3, pairing);
    sum(sum_tmp3, tmp3, num);
    //element_printf("sum_tmp3: %B\n", sum_tmp3);
    
    element_t tmp1;
    element_init_GT(tmp1, pairing);
    element_pairing(tmp1, sum_sigma, P);
    //element_printf("tmp1: %B\n", tmp1);
    
    element_t tmp2;
    element_init_GT(tmp2, pairing);
    element_pairing(tmp2, sum_ID1, P_pub1);
    //element_printf("tmp2: %B\n", tmp2);
    
    element_t tmp4;
    element_init_GT(tmp4, pairing);
    element_pairing(tmp4, sum_tmp3, P_pub2); 
    //element_printf("tmp4: %B\n", tmp4);
    
    element_t tmp5;
    element_init_GT(tmp5, pairing);
    element_mul(tmp5, tmp2, tmp4); //tmp5 = e(ID1, P_pub1) . e(H(m).(ID1 +. ID2), P_pub2)
    //element_printf("tmp5: %B\n", tmp5);

    if (element_cmp(tmp1, tmp5) == 0) {
        verified = True;
//        printf("batch_verified\n");
    } else {
        verified = False;
        printf("not batch_verified\n");
    }

    
}

void TA::sum(element_t result, element_t e[], int num) {
    int i = 1;
    element_set(result, e[0]);
    element_t tmp;
    element_init_same_as(tmp, result); 
    
    while (i < num) {
        element_add(tmp, result, e[i]);
        element_set(result, tmp);
        i++;
        
    }
    //element_printf("sum: %B\n", result);
    
}



