package com.dongsamo.dongsamo;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateForm {

    public static boolean validateEditText(EditText...editTexts){
        boolean valid = true;
        String string;
        for(EditText editText:editTexts){
            string = editText.getText().toString();
            if (TextUtils.isEmpty(string)) {
                editText.setError("Required.");
                valid = false;
            } else {
                editText.setError(null);
            }
        }

        return valid;
    }

    public static boolean validateId(String str){
        return str.matches("^[a-z0-9_]{4,20}$");
    }

    public static boolean validatePw(String str){
        int len = str.length();
        if(len != 6)
            return false;
        else
            return true;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

}
