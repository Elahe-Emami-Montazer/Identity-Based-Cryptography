/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.cpp
 * Author: Elahe <e.emami1375@gmail.com>
 *
 * Created on April 5, 2019, 9:40 PM
 */

#include <cstdlib>
#include <ostream>
#include <iostream>
#include <pbc/pbc.h>

#include "Signature.h"

using namespace std;

/*
 * 
 */
int main(int argc, char** argv) {
    cout << "lets start..." << endl;
    Signature signature;
    
    char* id = "elahe";
    
    element_t publicKey, private_key, R, S;
    bool verified;
    
    signature.get_public_key(publicKey, id);
    signature.get_private_key(private_key, id);
    signature.sign(R, S, private_key, "hello world");
    signature.verify(verified, publicKey, "hello world", R, S);
    
    return 0;
}

