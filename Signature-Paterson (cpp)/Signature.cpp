/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Signature.cpp
 * Author: Elahe <e.emami1375@gmail.com>
 * 
 * Created on April 5, 2019, 9:41 PM
 */

#include "Signature.h"

Signature::Signature() {
    printf("---constractor---\n");
    
    pbc_param_t par; //Parameter to generate the pairing
    pbc_param_init_a_gen(par, rbits, qbits); //Initial the parameter for the pairing
    pairing_init_pbc_param(pairing, par); //Initial the pairing


    //In our case, the pairing must be symmetric
    if (!pairing_is_symmetric(pairing))
        pbc_die("pairing must be symmetric");

    element_init_G1(P, pairing);
    element_init_G1(Ppub, pairing);
    element_init_Zr(s, pairing);
    element_random(P);
    element_random(s);
    element_mul_zn(Ppub, P, s);

    element_printf("P: %B\n--------------------\n", P);
    element_printf("Ppub: %B\n--------------------\n", Ppub);
    element_printf("s: %B\n--------------------\n", s);
}

void Signature::get_public_key(element_t public_key, char* ID) {
    element_init_G1(public_key, pairing);
    
    element_from_hash(public_key, ID, strlen(ID));
    element_printf("Public key: %B\n--------------------\n", public_key);
}

void Signature::get_private_key(element_t private_key, char* ID) {
    element_init_G1(private_key, pairing);
    
    element_t public_key;
    get_public_key(public_key, ID);
    
    element_mul_zn(private_key, public_key, s);
    element_printf("Private key: %B\n--------------------\n", private_key);
    
}

void Signature::sign(element_t R, element_t S, element_t private_key, char* message) {
    printf("\n----------------------------------------sign----------------------------------------\n");
    
    element_init_G1(R, pairing);
    element_init_G1(S, pairing);
    
    element_t k;
    element_init_Zr(k, pairing);
    element_random(k);
    element_printf("k: %B\n--------------------\n", k);
    
    element_mul_zn(R, P, k);
    element_printf("R: %B\n--------------------\n", R);
    
    element_t H2_m;
    element_init_Zr(H2_m, pairing);
    element_from_hash(H2_m, message, strlen(message));
    element_printf("H2_m: %B\n--------------------\n", H2_m);
    
    element_t tmp1;
    element_init_G1(tmp1, pairing);
    element_mul_zn(tmp1, P, H2_m); //tmp1 = H2(m).P
    element_printf("H2(m).P: %B\n--------------------\n", tmp1);
    
    element_t H3_R;
    element_init_Zr(H3_R, pairing);
    unsigned char data[200];
    element_to_bytes(data, R);
    element_from_hash(H3_R, data, 200);
    element_printf("H3_R: %B\n--------------------\n", H3_R);
    
    element_t tmp2;
    element_init_G1(tmp2, pairing);
    
    element_mul_zn(tmp2, private_key, H3_R); //tmp2 = H3(R).private_key
    element_printf("H3(R).private_key: %B\n--------------------\n", tmp2);
    
    element_t tmp3;
    element_init_G1(tmp3, pairing);
    element_add(tmp3, tmp1, tmp2); // tmp3 = H2(m).P + H3(R).private_key
    
    element_t k_inv;
    element_init_Zr(k_inv, pairing);
    element_invert(k_inv, k);
    
    element_mul_zn(S, tmp3, k_inv); //S = k_inv.(H2(m).P + H3(R).private_key)
    element_printf("S: %B\n--------------------\n", S);
    
}

void Signature::verify(bool verified, element_t public_key, char* message, element_t U, element_t V) {
    printf("\n----------------------------------------verify----------------------------------------\n");
    
    verified = false;
    
    element_t tmp1;
    element_init_GT(tmp1, pairing); 
    element_pairing(tmp1, U, V); //tmp1 = e(U, V)
    element_printf("e(U, V): %B\n--------------------\n", tmp1);
    
    element_t tmp2;
    element_init_GT(tmp2, pairing);    
    element_pairing(tmp2, P, P); //e(P, P)
    
    element_t H2_m;
    element_init_Zr(H2_m, pairing);
    element_from_hash(H2_m, message, strlen(message));
    element_printf("H2_m: %B\n--------------------\n", H2_m);
    
    element_pow_zn(tmp2, tmp2, H2_m); //tmp2 = e(P, P) ^ H2(m)
    element_printf("e(P, P) ^ H2(m): %B\n--------------------\n", tmp2);
    
    element_t tmp3;
    element_init_GT(tmp3, pairing);
    element_pairing(tmp3, Ppub, public_key); //e(Ppub, public_key)
    
    element_t H3_U;
    element_init_Zr(H3_U, pairing);
    unsigned char data[200];
    element_to_bytes(data, U);
    element_from_hash(H3_U, data, 200);
    element_printf("H3_U: %B\n--------------------\n", H3_U);
    
    element_pow_zn(tmp3, tmp3, H3_U); //tmp3 = e(Ppub, public_key) ^ H3(U)
    element_printf("e(Ppub, public_key) ^ H3(U): %B\n--------------------\n", tmp3);
    
    element_t tmp4;
    element_init_GT(tmp4, pairing);
    element_mul(tmp4, tmp2, tmp3);
    element_printf("tmp4: %B\n--------------------\n", tmp4);
    
    if(element_cmp(tmp1, tmp4) == 0) {
        printf("Signature is valid!\n");
        verified = true;
    } else {
        printf("Signature is invalid!\n");
        verified = false;
    }
    
}
