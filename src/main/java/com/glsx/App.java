package com.glsx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {


        HashSet<String> map = new HashSet<>();
        //113.5659,24.9058
        map.add("113.5659,24.9058");
        map.add("113.5659,24.9059");
        map.add("113.5659,24.9058");
        map.add("113.5658,24.9058");

        System.out.println(map);

    }
}
