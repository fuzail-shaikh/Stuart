package kjsce.stuart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class Settings extends AppCompatActivity {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private TextView email;
    EditText name;
    private Spinner branch, year, div, sem;
    private SwitchCompat notificationSwitch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = getSharedPreferences("Stuart", Context.MODE_PRIVATE);
        editor = prefs.edit();

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        branch = findViewById(R.id.branch);
        year = findViewById(R.id.year);
        div = findViewById(R.id.div);
        sem = findViewById(R.id.sem);
        notificationSwitch = findViewById(R.id.notification);

        name.setText(prefs.getString("NAME",""));
        email.setText(prefs.getString("EMAIL",""));
        branch.setSelection(getSpinnerPositionByValue(branch, prefs.getString("BRANCH","Information Technology")));
        year.setSelection(getSpinnerPositionByValue(year, prefs.getString("YEAR","LY")));
        div.setSelection(getSpinnerPositionByValue(div, prefs.getString("DIV","A")));
        sem.setSelection(getSpinnerPositionByValue(sem, prefs.getString("SEM","EVEN")));

        if(prefs.getBoolean("NOTIFICATION",false)){
            notificationSwitch.setChecked(true);
        }
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean("NOTIFICATION",true);
                    notificationSwitch.setChecked(true);
                }
                else{
                    editor.putBoolean("NOTIFICATION",false);
                    notificationSwitch.setChecked(false);
                }
                editor.apply();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            updateDetails();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDetails(){
        editor.putString("NAME", name.getText().toString());
        editor.putString("BRANCH", branch.getSelectedItem().toString());
        editor.putString("YEAR", year.getSelectedItem().toString());
        editor.putString("DIV", div.getSelectedItem().toString());
        editor.putString("SEM", sem.getSelectedItem().toString());
        editor.putBoolean("NOTIFICATION", notificationSwitch.isChecked());
        editor.apply();

        String url = "/accounts?operation=updateAccountDetails";
        url += "&email="+email.getText().toString();
        url += "&name="+name.getText().toString();
        url += "&branch="+branch.getSelectedItem().toString();
        url += "&year="+year.getSelectedItem().toString();
        url += "&div="+div.getSelectedItem().toString();
        url += "&sem="+sem.getSelectedItem().toString();
        new AsyncHttpClient().get(getString(R.string.server)+url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("SETTINGS", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if(responseString.equalsIgnoreCase("ACK")){
                    Log.d("SETTINGS", "Account updated");
                }
                else{
                    Toast.makeText(Settings.this, "Problem updating account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void changePassword(View view) {

    }

    public void signOut(View view) {
        editor.putBoolean("LOGGED_IN", false);
        editor.apply();
        finish();
    }

    private int getSpinnerPositionByValue(Spinner spinner, String myString){
        for (int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }
}
