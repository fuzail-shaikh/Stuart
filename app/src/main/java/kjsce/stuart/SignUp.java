package kjsce.stuart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void signUp(View view){
        EditText email = findViewById(R.id.email);
        EditText pass = findViewById(R.id.pass);
        EditText cpass = findViewById(R.id.cpass);
        String server = getString(R.string.server)+":"+getString(R.string.port);
        String url = "accounts?operation=signUp&email="+email.getText().toString()+
                "&pass="+pass.getText().toString()+"&cpass="+cpass.getText().toString();

        new AsyncHttpClient().get(server+"/"+url, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
    }

    public void signIn(View view) {
        Intent intent = new Intent(SignUp.this, SignIn.class);
        startActivity(intent);
        finish();
    }
}
