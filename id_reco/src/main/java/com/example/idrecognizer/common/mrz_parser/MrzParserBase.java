package com.example.idrecognizer.common.mrz_parser;

import android.util.Log;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.List;


@SuppressWarnings("WeakerAccess")
public abstract class MrzParserBase {
    protected String[] mrzTextLines;
    protected List<FirebaseVisionText.Line> mrzLines;

    public MrzParserBase(List<FirebaseVisionText.Line> lines){
        setMrzLines(lines);
    }

    public void setMrzLines(List<FirebaseVisionText.Line> mrzLines) {
        this.mrzLines = mrzLines;
        createMrzTextLines();
    }

    private void createMrzTextLines() {
        mrzTextLines = new String[mrzLines.size()];

        for (int i = 0; i < mrzLines.size(); i++){
            String rawLine = mrzLines.get(i).getText();
            rawLine = rawLine.replaceAll("\\s","");
            rawLine = rawLine.toUpperCase();
            mrzTextLines[i] = rawLine;
        }
    }

    protected static String formatDate(String unformatted, boolean isAlwaysTwentyFirstCentury){
        StringBuilder birthdayFormatted = new StringBuilder();
        if (unformatted.length() >= 6) {
            String year = unformatted.substring(0, 2);
            birthdayFormatted.append(unformatted.substring(2, 4));
            birthdayFormatted.append("/");
            birthdayFormatted.append(unformatted.substring(4, 6));
            birthdayFormatted.append("/");
            int iYear = Integer.valueOf(year);
            if (iYear < 18 || isAlwaysTwentyFirstCentury) {
                birthdayFormatted.append("20");
            } else {
                birthdayFormatted.append("19");
            }

            birthdayFormatted.append(year);
        }

        return birthdayFormatted.toString();
    }

    public String getRawNameAndSurname(){
        String lastLine = mrzTextLines[mrzTextLines.length - 1];
        return trimExtraArrows(lastLine);
    }

    private String trimExtraArrows(String input){
        int toTrimFrom = input.length() - 1;

        int skippedArrows = 0;

        for (int i = 1; i < input.length(); i++){
            if (skippedArrows == 2){
                if (input.charAt(i) == '<'){
                    if (input.charAt(i - 1) == '<'){
                        toTrimFrom = i - 1;
                    }else{
                        toTrimFrom = i;
                    }
                    break;
                }
            }else if (input.charAt(i) == '<'){
                skippedArrows++;
            }
        }

        String trimmed = input.substring(0,toTrimFrom);

        Log.d("trimExtraArrows",trimmed);
        return trimmed;
    }

    public abstract String extractPersonalNo();

    public abstract String extractBirthdate();

    public abstract String extractFirstName();

    public abstract String extractLastName();

    public abstract String extractGender();

    public abstract String extractExpiryDate();
}
