package com.example.android.bluetoothlegatt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noxa on 12/06/14.
 */
public class SignalUtils{
    public static SerializableGraphView[] derivative(List<SerializableGraphView> array){
        SerializableGraphView[] ret_array = new SerializableGraphView[array.size()-1];

        for(int i=0;i<array.size()-1;i++){
            double deriv = (array.get(i+1).getY()-array.get(i).getY())/(array.get(i+1).getX()-array.get(i).getX());
            ret_array[i] = new SerializableGraphView((double)i,deriv);
        }

        return ret_array;
    }
//
//    public static double [] lpf(double [] arr, double [] cof){
//
//        if(arr.size()<cof.size()){
//
//            double [] ret_array= new double[1];
//            return [];
//        }
//        else{
//            Integer count=arr.size()-cof.size();
//
//            double [] ret_array= new double[count];
//            for(i=0;i<=count;i++){
//                double sum=0;
//
//                for(j=0;j<cof.size();j++){
//                    sum+=arr[j+i]*cof[j];
//                }
//
//                ret_array[i]=sum;
//            }
//            return ret_array;
//        }
//    }
//
//    public static double [] obnal(double [] arr){
//        double [] ret_array= new double[arr.size()];
//
//        for(i=0;i<arr.size()){
//            if(arr[i]<0){
//                ret_array[i]=0;
//            }
//            else{
//                ret_array[i]=arr[i];
//            }
//        }
//        return ret_array;
//    }
//
//    public static double [] ssl(double [] arr, Integer num){
//        Integer count=arr.size()-num;
//
//        double [] ret_array= new double[count];
//
//        for(i=0;i<=count;i++){
//            double sum=0;
//            for(j=0;j<num;j++){
//                sum+=arr[j+i];
//            }
//            ret_array[i]=sum/num;
//        }
//        return ret_array;
//    }
}