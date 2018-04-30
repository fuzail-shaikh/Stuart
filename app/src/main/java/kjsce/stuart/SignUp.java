package kjsce.stuart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class SignUp extends AppCompatActivity {
    SharedPreferences preferences;
    EditText email, pass, cpass, otp;
    LinearLayout signUpLayout, verifyOTPLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        preferences = getSharedPreferences("Stuart", MODE_PRIVATE);
        signUpLayout = findViewById(R.id.signUpLayout);
        verifyOTPLayout = findViewById(R.id.verifyOTPLayout);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        cpass = findViewById(R.id.cpass);
        otp = findViewById(R.id.verifyOTP);

        if(preferences.getBoolean("OTP_REQUIRED", false)){
            //Change view for OTP
            signUpLayout.setVisibility(View.GONE);
            verifyOTPLayout.setVisibility(View.VISIBLE);
        }
    }

    public void signUp(View view){
        if(!email.getText().toString().endsWith("@somaiya.edu")){
            final TextView error = findViewById(R.id.error);
            error.setText("* Only Somaiya account allowed");
            error.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    error.setVisibility(View.GONE);
                }
            }, 5000);
        }
        else if(pass.getText().toString().equals(cpass.getText().toString())){
            String url = "/accounts?operation=signUp";
            url += "&email="+email.getText().toString();
            url += "&password="+pass.getText().toString();
            new AsyncHttpClient().get(getString(R.string.server)+url, new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("SIGN_UP", "Error in signing up");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    if(responseString.equalsIgnoreCase("ACK")){
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("EMAIL", email.getText().toString());
                        editor.putBoolean("OTP_REQUIRED", true);
                        editor.apply();
                        //Change view for OTP
                        signUpLayout.setVisibility(View.GONE);
                        verifyOTPLayout.setVisibility(View.VISIBLE);
                        TextView chosenEmail = findViewById(R.id.chosenEmail);
                        chosenEmail.setText(email.getText().toString());
                    }
                    else{
                        final TextView error = findViewById(R.id.error);
                        if(responseString.equalsIgnoreCase("ACCOUNT_EXISTS")){
                            error.setText("* Account already exists, Sign In");
                        }
                        else{
                            error.setText(responseString);
                        }
                        error.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                error.setVisibility(View.GONE);
                            }
                        }, 5000);
                    }
                }
            });
        }
        else{
            final TextView error = findViewById(R.id.error);
            error.setText("Password did not match");
            error.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    error.setVisibility(View.GONE);
                }
            }, 5000);
        }
    }

    public void verifyOTP(View view) {
        String url = "/accounts?operation=verifyOTP";
        url += "&email="+email.getText().toString();
        url += "&otp="+otp.getText().toString();
        new AsyncHttpClient().get(getString(R.string.server)+url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("SIGN_UP", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if(responseString.equalsIgnoreCase("ACK")){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("OTP_REQUIRED", false);
                    editor.putBoolean("ACCOUNT_DETAILS_REQUIRED", true);
                    editor.putBoolean("LOGGED_IN", true);
                    editor.putString("BRANCH", "Information Technology");
                    editor.putString("YEAR", "LY");
                    editor.putString("DIV", "A");
                    editor.putString("SEM", "EVEN");
                    editor.apply();
                    Intent startApp = new Intent(SignUp.this, MainActivity.class);
                    startActivity(startApp);
                    finish();
                }
                else{
                    final TextView error = findViewById(R.id.error);
                    if(responseString.equalsIgnoreCase("INVALID_OTP")){
                        error.setText("* OTP is invalid, Sign up again.");
                    }
                    else{
                        error.setText(responseString);
                    }
                    error.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            error.setVisibility(View.GONE);
                        }
                    }, 5000);
                }
            }
        });
    }

    public void signIn(View view) {
        Intent intent = new Intent(SignUp.this, SignIn.class);
        startActivity(intent);
        finish();
    }

    public void trySignUpAgain(View view) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("EMAIL", "");
        editor.putBoolean("OTP_REQUIRED", false);
        editor.apply();

        email.setText("");
        pass.setText("");
        cpass.setText("");
        otp.setText("");

        //Change view for Sign Up
        signUpLayout.setVisibility(View.VISIBLE);
        verifyOTPLayout.setVisibility(View.GONE);
    }
}
