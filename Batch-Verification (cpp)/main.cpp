/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.cpp
 * Author: Elahe <e.emami1997@gmail.com>
 *
 * Created on May 27, 2019, 11:06 AM
 */

#include <cstdlib>

#include "TA.h"
#include <pbc/pbc.h>
#include <cstring>
#include <fstream>
#include <iostream>

#include <chrono>
#include <ctime> 

using namespace std;
using namespace std::chrono; 

/*
 * 
 */

char *mkrndstr(size_t length) { // const size_t length, supra

static char charset[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.-#'?!"; // could be const
char *randomString;

if (length) {
    randomString = (char*) malloc(length +1); // sizeof(char) == 1, cf. C99

    if (randomString) {
        int l = (int) (sizeof(charset) -1); // (static/global, could be const or #define SZ, would be even better)
        int key;  // one-time instantiation (static/global would be even better)
        for (int n = 0;n < length;n++) {        
            key = rand() % l;   // no instantiation, just assignment, no overhead from sizeof
            randomString[n] = charset[key];
        }

        randomString[length] = '\0';
    }
}

return randomString;
}

int main(int argc, char** argv) {
    
    int num = 10000;

    TA ta;   
    ta.setup();
    
    char* RID[num];
    
    for (int i = 0; i < num; i++) {
        RID[i] = mkrndstr(10);
        //cout << "RID[" << i << "]: " <<RID[i] << endl;
    }
    
    element_t ID1[num];
    element_t ID2[num];
    
    for (int i = 0; i < num; i++) {
        ta.generate_Pseudo_Identity(ID1[i], ID2[i], RID[i]);
    }
    
    element_t sk1[num];
    element_t sk2[num];
    

    for (int i = 0; i < num; i++) {
        ta.generate_private_key(sk1[i], sk2[i], ID1[i], ID2[i]);
    }
    
    element_t sigma[num];
    char* message[num];
    
    for (int i = 0; i < num; i++) {
        message[i] = mkrndstr(40);
    }
    
    
    for (int i = 0; i < num; i++) {
        ta.sign(sigma[i], sk1[i], sk2[i], message[i]);
    }
    
    
    bool verified;
    
    auto start = high_resolution_clock::now(); 
    ta.batch_verify(verified, ID1, ID2, message, sigma, 8);
    auto stop = high_resolution_clock::now(); 
    
    auto duration = duration_cast<microseconds>(stop - start); 
    cout << "duration of batch verification: ";
    cout << duration.count() << endl;
    
    auto start2 = high_resolution_clock::now(); 
    for (int i = 0; i < num; i++) {
        ta.verify(verified, ID1[i], ID2[i], message[i], sigma[i]);
    }
    auto stop2 = high_resolution_clock::now(); 
    
    auto duration2 = duration_cast<microseconds>(stop2 - start2); 
    cout << "duration of single verification: ";
    cout << duration2.count() << endl;
    
    return 0;
}

