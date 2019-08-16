package com.company;

public class bitmap {
    private int bitMapSize = 1000;
    int bitMapArray[] = new int[bitMapSize];
    int openAddress = 0;

    void printBitmap(){
        System.out.println();
        System.out.println("Final Bitmap Address:");
        for(int i = 0; i < 200; i+=10){
            for(int j = i; j < i+10; j++){
                System.out.print(bitMapArray[j] + " ");
            }
            System.out.println();
        }
    }

}
