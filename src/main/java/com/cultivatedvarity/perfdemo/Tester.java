package com.cultivatedvarity.perfdemo;

/**
 * Created by robch on 07/12/2016.
 */
public class Tester {
    public static void main(String[] arg){
        int QUEUE_SIZE = 64 * 1024;
        for (int i = QUEUE_SIZE; i < QUEUE_SIZE * 2; i++){
            System.out.println("" + (i & (QUEUE_SIZE - 1)));
        }
    }
}