package kjsce.stuart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class SignIn extends AppCompatActivity {
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        email = findViewById(R.id.email);
    }

    public void signIn(View view){
        EditText pass = findViewById(R.id.pass);
        String url = "/accounts?operation=signIn";
        url += "&email="+email.getText().toString();
        url += "&password="+pass.getText().toString();
        new AsyncHttpClient().get(getString(R.string.server)+url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("SIGN_IN", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if(responseString.equalsIgnoreCase("ACK")){
                    SharedPreferences preferences = getSharedPreferences("Stuart", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("LOGGED_IN", true);
                    editor.apply();
                    Intent startApp = new Intent(SignIn.this, MainActivity.class);
                    startActivity(startApp);
                    finish();
                }
                else{
                    final TextView error = findViewById(R.id.error);
                    if(responseString.equalsIgnoreCase("NO_ACCOUNT")){
                        error.setText("* Account does not exist, Sign up");
                    }
                    else if(responseString.equalsIgnoreCase("WRONG_PASSWORD")){
                        error.setText("* Please check your password");
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

    public void signUp(View view) {
        Intent intent = new Intent(SignIn.this, SignUp.class);
        startActivity(intent);
        finish();
    }

    public void forgotPassword(View view) {
        String url = "/accounts?operation=forgotPassword";
        url += "&email="+email.getText().toString();
        new AsyncHttpClient().get(getString(R.string.server)+url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("FORGOT_PASSWORD", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if(responseString.equalsIgnoreCase("ACK")){
                    Toast.makeText(SignIn.this, "New password was mailed", Toast.LENGTH_SHORT).show();
                }
                else{
                    final TextView error = findViewById(R.id.error);
                    if(responseString.equalsIgnoreCase("NO_ACCOUNT")){
                        error.setText("* Account does not exist, Sign up");
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
}
