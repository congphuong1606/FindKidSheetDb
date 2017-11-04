package com.congp.finkids.common;

import android.app.Activity;
import android.content.Context;
import android.icu.text.UnicodeSetIterator;
import android.widget.EditText;

import com.congp.finkids.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by MyPC on 05/08/2017.
 */

public class CheckInput {


    public static boolean isEmpty(EditText etText) {
        if (etText != null && etText.getText().toString().trim().length() > 0) {
            return true;
        } else {
            etText.requestFocus();
            etText.setError("Vui lòng điền thông tin!");
            return false;
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "[a-zA-Z0-9._-]+@[a-z]+(\\.+[a-z]+)+";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean checkInPutLogin(EditText edtEmail, EditText edtPass, Context context) {
        if (isEmpty(edtEmail) && isEmpty(edtPass)) {
            String email = edtEmail.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();
            if (!isEmailValid(email)) {
                edtEmail.requestFocus();
                edtEmail.setError(context.getResources().getString(R.string.email_erorr));
                return false;
            } else {
                if (pass.length() < 6) {
                    edtPass.requestFocus();
                    edtPass.setError(context.getResources().getString(R.string.pass_error));
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkInPutRegis(EditText edtEmail, EditText edtPass,
                                          EditText edtConfrimPass, Activity activity) {

        if (isEmpty(edtEmail) && isEmpty(edtPass) && isEmpty(edtConfrimPass)) {
            String email = edtEmail.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();
            String repeatPass = edtConfrimPass.getText().toString().trim();
            if (!isEmailValid(email)) {
                edtEmail.requestFocus();
                edtEmail.setError(activity.getResources().getString(R.string.email_erorr));
                return false;
            } else {

                if (pass.length() < 6) {
                    edtPass.requestFocus();
                    edtPass.setError(activity.getResources().getString(R.string.pass_error));
                    return false;
                } else {
                    if (!repeatPass.equals(pass)) {
                        edtConfrimPass.requestFocus();
                        edtConfrimPass.setError(activity.getResources().getString(R.string.confim_pass_erorr));
                        return false;
                    }
                }

            }
            return true;
        } else {
            return false;
        }
    }


    private static boolean isNumber(String number) {

        try {

            double d = Double.parseDouble(number);
            return true;
        } catch (NumberFormatException nfe) {

            return false;
        }

    }
}
