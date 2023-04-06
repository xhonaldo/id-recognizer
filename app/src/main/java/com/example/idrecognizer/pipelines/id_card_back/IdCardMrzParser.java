package com.example.idrecognizer.pipelines.id_card_back;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.List;

import com.example.idrecognizer.common.helpers.Utils;
import com.example.idrecognizer.common.mrz_parser.ErrorFixer;
import com.example.idrecognizer.common.mrz_parser.MrzParserBase;

public class IdCardMrzParser extends MrzParserBase {

    public IdCardMrzParser(List<FirebaseVisionText.Line> lines) {
        super(lines);
    }

    @Override
    public String extractPersonalNo() {
        String line = mrzTextLines[0];
        if (line.length() < 2) { return null; }

        StringBuilder personalNoBuilder = new StringBuilder();

        for (int i = line.length() - 1; i >= 1 && personalNoBuilder.length() < 10; i--){
            char currentChar = line.charAt(i);
            char nextChar = line.charAt(i - 1);

            if (currentChar == '<' || nextChar == '<'){
                continue;
            }else if (currentChar == 'K' && Character.isLetter(nextChar)){
                continue;
            }

            boolean isCharacterValid;

            if (personalNoBuilder.length() == 0 || personalNoBuilder.length() == 9){
                isCharacterValid = Character.isLetter(currentChar);
            }else{
                isCharacterValid = Character.isDigit(currentChar);
            }

            if (isCharacterValid){
                personalNoBuilder.append(currentChar);
            }else{
                Character fixed = ErrorFixer.attemptFixCharacterError(currentChar);
                if (fixed == null) { break; }
                personalNoBuilder.append(fixed);
            }
        }

        if (personalNoBuilder.length() == 10){
            return personalNoBuilder.reverse().toString();
        }

        return null;
    }

    @Override
    public String extractBirthdate() {
        String line = mrzTextLines[1];
        StringBuilder birthdayBuilder = new StringBuilder();

        for (int i = 0; i < line.length() && birthdayBuilder.length() < 6; i++){
            char currentChar = line.charAt(i);
            if (Character.isDigit(currentChar)){
                birthdayBuilder.append(currentChar);
            }else{
                Character fixed = ErrorFixer.attemptFixCharacterError(currentChar);
                if (fixed == null) { return null; }
                birthdayBuilder.append(fixed);
            }
        }

        return formatDate(birthdayBuilder.toString(),false);
    }

    @Override
    public String extractGender() {
        String line = mrzTextLines[1];
        int mCharCount = Utils.getCharCountInString(line,'M');
        int fCharCount = Utils.getCharCountInString(line,'F');

        if (mCharCount > 0 && fCharCount == 0){
            return "M";
        }else if (mCharCount == 0 && fCharCount > 0){
            return "F";
        }else if (mCharCount > 0 && fCharCount > 0){
            int mIndex = line.indexOf('M');
            int fIndex = line.indexOf('F');
            return mIndex < fIndex ? "M" : "F";
        }

        return null;
    }

    @Override
    public String extractExpiryDate() {
        String line = mrzTextLines[1];
        StringBuilder expiryDateBuilder = new StringBuilder();

        for (int i = 8; i < line.length() && expiryDateBuilder.length() < 6; i++){
            char currentChar = line.charAt(i);
            if (Character.isDigit(currentChar)){
                expiryDateBuilder.append(currentChar);
            }else{
                Character fixed = ErrorFixer.attemptFixCharacterError(currentChar);
                if (fixed == null) { return null; }
                expiryDateBuilder.append(fixed);
            }
        }

        return formatDate(expiryDateBuilder.toString(),true);
    }

    @Override
    public String extractFirstName() {
        String rawNameAndLastName = getRawNameAndSurname();

        String[] split;

        if (rawNameAndLastName.contains("<<")){
            split = rawNameAndLastName.split("<<");
        }else if (rawNameAndLastName.contains("<K")){
            split = rawNameAndLastName.split("<K");
        }else if (rawNameAndLastName.contains("K<")){
            split = rawNameAndLastName.split("K<");
        }else{
            return null;
        }

        if (split.length != 2){
            return null;
        }

        return split[1];
    }

    @Override
    public String extractLastName() {
        String rawNameAndLastName = getRawNameAndSurname();

        String[] split;

        if (rawNameAndLastName.contains("<<")){
            split = rawNameAndLastName.split("<<");
        }else if (rawNameAndLastName.contains("<K")){
            split = rawNameAndLastName.split("<K");
        }else if (rawNameAndLastName.contains("K<")){
            split = rawNameAndLastName.split("K<");
        }else{
            return null;
        }

        if (split.length != 2){
            return null;
        }

        return split[0];
    }
}
