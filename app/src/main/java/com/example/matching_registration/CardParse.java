package com.example.matching_registration;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardParse {

    public static ArrayList<String> parse(String text) {
        if (text.contains("POLYTECHNIC")) return parsePoly(text);
        else if (text.contains("The Chinese")) return parseCUHK(text);
        else if (text.contains("City")) return parseCity(text);
        else if (text.contains("THE UNIVERSITY OF HONG KONG")) return parseHKU(text);
        else if (text.contains("HKUST") || text.contains("TECHNOLOGY")) return parseUST(text);
        else if (text.contains("Education")) return parseEdU(text);
        else {
            return null;
        }
    }

    // For PolyU only
    public static ArrayList<String> parsePoly(String text) {
        int start = text.indexOf("THE HONG");
        if (start != -1)
            text = text.substring(start);
        String[] _lines = text.replace("Student No:\n", "").split("\n");
        ArrayList<String> info = new ArrayList<>();
        List<String> filteredList = new ArrayList<>();
        for (String line : _lines)
            if (line.length() > 5)
                filteredList.add(line);
        String[] lines = filteredList.toArray(new String[0]);

        if (lines.length != 6 || !lines[1].startsWith("POLYTECHNIC")) {
            // needs retaking
            return null;
        }
        try {
            info.add(lines[2]); // Name
            Pattern sid_pattern = Pattern.compile("\\d+\\s*[A-Za-z]");
            Matcher sid_matcher = sid_pattern.matcher(lines[3]);
            if (sid_matcher.find()) {
                String result = sid_matcher.group().replace(" ", "");
                info.add(result); // SID
            } else {
                // needs retaking
                return null;
            }
            Pattern date_pattern = Pattern.compile("(\\d{4})");
            Matcher date_matcher = date_pattern.matcher(lines[4]);

            if (date_matcher.find()) {
                String result = date_matcher.group();
                info.add(result); // Exp Year in String
            } else {
                // needs retaking
                return null;
            }
        } catch (Exception e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
            return null;
        }
        info.add("PolyU");
        return info;
    }

    // for CUHK only
    public static ArrayList<String> parseCUHK(String text) {
        ArrayList<String> info = new ArrayList<>();
        int start = text.indexOf("The Chinese");
        if (start == -1) // not found
            return null;
        text = text.replace("University of\n", "");
        String[] _lines = text.split("\n");
        List<String> filteredList = new ArrayList<>();
        for (String line : _lines)
            if (line.length() > 4)
                filteredList.add(line);
        String[] lines = filteredList.toArray(new String[0]);
//        if (lines.length!=8) return null;
        try {
            info.add(lines[1]); // Name
            info.add(lines[3]); // SID
            Pattern date_pattern = Pattern.compile("(\\d{4})");
            Matcher date_matcher = date_pattern.matcher(lines[4]);
            if (date_matcher.find()) {
                String result = date_matcher.group();
                info.add(result); // Exp Year in String
            } else return null; // needs retaking
        } catch (Exception e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
            return null;
        }
        info.add("CUHK");
        return info;
    }

    // for CityU only
    public static ArrayList<String> parseCity(String text) {
        ArrayList<String> info = new ArrayList<>();
        int start = text.indexOf("City");
        if (start == -1) // not found
            return null;
//        text = text.replace("University of\n","");
        String[] _lines = text.split("\n");
        List<String> filteredList = new ArrayList<>();
        for (String line : _lines)
            if (line.length() > 4)
                filteredList.add(line);
        String[] lines = filteredList.toArray(new String[0]);
//        if (lines.length != 10) return null;
        try {
            info.add(lines[4]); // Name
            info.add(lines[5]); // SID
            info.add(lines[9].substring(lines[9].length() - 4)); // Exp Year in String
        } catch (Exception e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
            return null;
        }
        info.add("CityU");
        return info;
    }

    // HKU only
    public static ArrayList<String> parseHKU(String text) {
        ArrayList<String> info = new ArrayList<>();
        int start = text.indexOf("THE UNIVERSITY OF HONG KONG");
        if (start == -1) // not found
            return null;
        else text = text.substring(start);

//        text = text.replace("", "");
        String[] _lines = text.split("\n");
        List<String> filteredList = new ArrayList<>();
        for (String line : _lines)
            if (line.length() > 1)
                filteredList.add(line);
        String[] lines = filteredList.toArray(new String[0]);

        if (lines.length != 9) return null;

        try {
            info.add(lines[1]); // Name
            info.add(lines[5]); // SID
            Pattern pattern = Pattern.compile("\\d{4}");
            Matcher matcher = pattern.matcher(lines[7]);
            List<String> matches = new ArrayList<>();
            while (matcher.find()) {
                matches.add(matcher.group());
            }
            String year = "";
            if (matches.size() >= 2) { // If there are at least two matches
                year = matches.get(matches.size() - 1); // The last match
            } else return null;
            info.add(year); // Exp Year in String
        } catch (Exception e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
            return null;
        }
        info.add("HKU");
        return info;
    }

    // UST only
    public static ArrayList<String> parseUST(String text) {

        ArrayList<String> info = new ArrayList<>();

        String[] _lines = text.split("\n");
        List<String> filteredList = new ArrayList<>();
        for (String line : _lines)
            if (line.length() > 4)
                filteredList.add(line);
        String[] lines = filteredList.toArray(new String[0]);
        if (lines.length != 8) return null;

        try {
            info.add(lines[1]); // Name
            info.add(lines[2]); // SID
            info.add(lines[4].substring(lines[4].length() - 4)); // Exp Year in String
        } catch (Exception e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
            return null;
        }
        info.add("HKUST");
        return info;
    }

    // EdU
    public static ArrayList<String> parseEdU(String text) {
        ArrayList<String> info = new ArrayList<>();
        int start = text.indexOf("Education");
        if (start == -1) // not found
            return null;
        text = text.substring(start);
        String[] _lines = text.split("\n");
        List<String> filteredList = new ArrayList<>();
        for (String line : _lines)
            if (line.length() > 2)
                filteredList.add(line);
        String[] lines = filteredList.toArray(new String[0]);
        if (lines.length != 9) return null;
        try {
            info.add(lines[4]);
            info.add(lines[3]);
            info.add("20" + lines[8].substring(lines[8].length() - 2));
        } catch (Exception e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
            return null;
        }
        info.add("EdU");
        return info;
    }


    // MU
    public static ArrayList<String> parseMU(String text) {
        ArrayList<String> info = new ArrayList<>();
        return info;
    }

    // BU
    public static ArrayList<String> parseBU(String text) {
        ArrayList<String> info = new ArrayList<>();
        return info;
    }

    // HSU
    public static ArrayList<String> parseHSU(String text) {
        ArrayList<String> info = new ArrayList<>();
        return info;
    }

    // SYU
    public static ArrayList<String> parseSYU(String text) {
        ArrayList<String> info = new ArrayList<>();
        return info;
    }

    // LU
    public static ArrayList<String> parseLU(String text) {
        ArrayList<String> info = new ArrayList<>();
        return info;
    }

}