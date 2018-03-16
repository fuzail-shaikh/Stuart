package kjsce.stuart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

public class Settings extends AppCompatActivity {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private TextView name, email, server;
    private Spinner branch, year, div;
    private SwitchCompat notificationSwitch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = getSharedPreferences("STUART", Context.MODE_PRIVATE);
        editor = prefs.edit();

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        server = findViewById(R.id.server);
        branch = findViewById(R.id.branch);
        year = findViewById(R.id.year);
        div = findViewById(R.id.div);
        notificationSwitch = findViewById(R.id.notification);

        name.setText(prefs.getString("NAME",""));
        email.setText(prefs.getString("EMAIL",""));
        server.setText(prefs.getString("SERVER",""));

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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putString("NAME", name.getText().toString());
        editor.putString("EMAIL", email.getText().toString());
        editor.putString("SERVER", server.getText().toString());
        editor.apply();
    }
}
