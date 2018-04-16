package kjsce.stuart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class SignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    public void signIn(View view){
        EditText email = findViewById(R.id.email);
        EditText pass = findViewById(R.id.pass);
        String server = getString(R.string.server)+":"+getString(R.string.port);
        String url = "accounts?operation=signIn&email="+email.getText().toString()+"&pass="+pass.getText().toString();

        new AsyncHttpClient().get(server+"/"+url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if(responseString.equalsIgnoreCase("Verified")){
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
                    if(responseString.equalsIgnoreCase("NoAccount")){
                        error.setText("* Account does not exist, Sign up");
                    }
                    else if(responseString.equalsIgnoreCase("WrongPassword")){
                        error.setText("* Please check your credentials");
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
}
