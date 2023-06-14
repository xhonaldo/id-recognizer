package com.example.idrecognizer.common;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CommonSubstringExtractor {
    private ArrayList<String> values = new ArrayList<>();

    private static String getLongestCommonSubstring(String str1, String str2) {
        StringBuilder sb = new StringBuilder();
        if (str1 == null || str1.isEmpty() || str2 == null || str2.isEmpty())
            return "";

        // ignore case
        str1 = str1.toUpperCase();
        str2 = str2.toUpperCase();

        // java initializes them already with 0
        int[][] num = new int[str1.length()][str2.length()];
        int maxlen = 0;
        int lastSubsBegin = 0;

        for (int i = 0; i < str1.length(); i++) {
            for (int j = 0; j < str2.length(); j++) {
                if (str1.charAt(i) == str2.charAt(j)) {
                    if ((i == 0) || (j == 0))
                        num[i][j] = 1;
                    else
                        num[i][j] = 1 + num[i - 1][j - 1];

                    if (num[i][j] > maxlen) {
                        maxlen = num[i][j];
                        // generate substring from str1 => i
                        int thisSubsBegin = i - num[i][j] + 1;
                        if (lastSubsBegin == thisSubsBegin) {
                            //if the current getLongestCommonSubstring is the same as the last time this block ran
                            sb.append(str1.charAt(i));
                        } else {
                            //this block resets the string builder if a different getLongestCommonSubstring is found
                            lastSubsBegin = thisSubsBegin;
                            sb = new StringBuilder();
                            sb.append(str1.substring(lastSubsBegin, i + 1));
                        }
                    }
                }
            }
        }

        return sb.toString();
    }

    public void addValue(String value){
        values.add(value);
        Collections.sort(values, (o1, o2) -> o2.length() - o1.length());
    }

    public int getSampleSize(){
        return values.size();
    }

    public Pair<String,String> extract(){
        boolean allEqual = new HashSet<>(values).size() <= 1;

        if (allEqual){
            String value = values.get(0);

            if (value.contains("<<")){
                String[] split = value.split("<<");
                if (split.length == 2){
                    return new Pair<>(split[0], split[1]);
                }
            }
        }else{
            return extractViaLcs();
        }

        return new Pair<>("","");
    }


    private Pair<String,String> extractViaLcs() {
        String first = "";
        ArrayList<String> trimmed = new ArrayList<>();
        HashMap<String,Integer> nonTrimmedMap = new HashMap<>();

        for (int a = 0; a < values.size(); a++) {
            for (int i = 0; i < values.size(); i++) {
                if (a != i) {
                    String current = values.get(i);
                    String biggest = values.get(a);
                    if (!current.equals(biggest)) {
                        String lcs = getLongestCommonSubstring(current, biggest);
                        trimmed.add(current.replace(lcs, ""));
                        if (!nonTrimmedMap.containsKey(lcs)){
                            nonTrimmedMap.put(lcs,0);
                        }else{
                            nonTrimmedMap.put(lcs,nonTrimmedMap.get(lcs) + 1);
                        }
                    }
                }
            }
        }

        int maxValueInMap= Collections.max(nonTrimmedMap.values());
        for (Map.Entry<String, Integer> entry : nonTrimmedMap.entrySet()) {
            if (entry.getValue()==maxValueInMap) {
                String potential = entry.getKey();
                int positionInLongestValue = values.get(0).indexOf(potential);

                if (positionInLongestValue > 0){
                    first = values.get(0).substring(0,positionInLongestValue) + first;
                }else{
                    first = potential;
                }
                break;
            }
        }

        Collections.sort(trimmed, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });

        String second = "";
        HashMap<String,Integer> trimmedMap = new HashMap<>();

        for (int a = 0; a < trimmed.size(); a++) {
            for (int i = 0; i < trimmed.size(); i++) {
                String current = trimmed.get(i);
                String biggest = trimmed.get(a);
                String lcs = getLongestCommonSubstring(current, biggest);
                if (!trimmedMap.containsKey(lcs)){
                    trimmedMap.put(lcs,0);
                }else{
                    //noinspection ConstantConditions
                    trimmedMap.put(lcs,trimmedMap.get(lcs) + 1);
                }
            }
        }
        maxValueInMap =(Collections.max(trimmedMap.values()));
        for (Map.Entry<String, Integer> entry : trimmedMap.entrySet()) {
            if (entry.getValue()==maxValueInMap) {
                second = entry.getKey();
            }
        }

        first = first.replace("<","");
        second = second.replace("<", "");
        return new Pair<>(first, second);
    }
}
