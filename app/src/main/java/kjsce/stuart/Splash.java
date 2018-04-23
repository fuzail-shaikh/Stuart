package kjsce.stuart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.animator.fade_in,R.animator.fade_out);
        setContentView(R.layout.activity_splash);
        SharedPreferences preferences = getSharedPreferences("Stuart", MODE_PRIVATE);

        if(preferences.getBoolean("LOGGED_IN", false)){
            //Start the app after some delay
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startApp = new Intent(Splash.this, MainActivity.class);
                    startActivity(startApp);
                    finish();
                }
            }, 1000);
        }
        else{
            //Start the app after some delay
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startApp = new Intent(Splash.this, SignUp.class);
                    startActivity(startApp);
                    finish();
                }
            }, 1000);
        }
    }
}
