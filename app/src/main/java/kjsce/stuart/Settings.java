package kjsce.stuart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class Settings extends AppCompatActivity {
    private SharedPreferences prefs;
    private TextView email;
    private EditText name;
    private Spinner branch, year, div, sem, batch;
    private SwitchCompat notificationSwitch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = getSharedPreferences("Stuart", Context.MODE_PRIVATE);

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        branch = findViewById(R.id.branch);
        year = findViewById(R.id.year);
        div = findViewById(R.id.div);
        sem = findViewById(R.id.sem);
        batch = findViewById(R.id.batch);
        notificationSwitch = findViewById(R.id.notification);

        name.setText(prefs.getString("NAME",""));
        email.setText(prefs.getString("EMAIL",""));
        branch.setSelection(getSpinnerPositionByValue(branch, prefs.getString("BRANCH","Information Technology")));
        year.setSelection(getSpinnerPositionByValue(year, prefs.getString("YEAR","LY")));
        div.setSelection(getSpinnerPositionByValue(div, prefs.getString("DIV","A")));
        sem.setSelection(getSpinnerPositionByValue(sem, prefs.getString("SEM","EVEN")));
        batch.setSelection(getSpinnerPositionByValue(batch, prefs.getString("BATCH","1")));
        if(prefs.getBoolean("NOTIFICATION",false)){
            notificationSwitch.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        else if(item.getItemId() == R.id.action_done){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("NAME", name.getText().toString());
            editor.putString("BRANCH", branch.getSelectedItem().toString());
            editor.putString("YEAR", year.getSelectedItem().toString());
            editor.putString("DIV", div.getSelectedItem().toString());
            editor.putString("SEM", sem.getSelectedItem().toString());
            editor.putString("BATCH", batch.getSelectedItem().toString());
            editor.putBoolean("NOTIFICATION", notificationSwitch.isChecked());
            editor.apply();

            String url = "/accounts?operation=updateAccountDetails";
            url += "&email="+email.getText().toString();
            url += "&name="+name.getText().toString();
            url += "&branch="+branch.getSelectedItem().toString();
            url += "&year="+year.getSelectedItem().toString();
            url += "&div="+div.getSelectedItem().toString();
            url += "&sem="+sem.getSelectedItem().toString();
            url += "&batch="+batch.getSelectedItem().toString();
            new AsyncHttpClient().get(getString(R.string.server)+url, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(Settings.this, "Problem updating account", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    if(responseString.equalsIgnoreCase("ACK")){
                        Toast.makeText(Settings.this, "Account updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(prefs.getBoolean("ACCOUNT_DETAILS_REQUIRED", false)){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("ACCOUNT_DETAILS_REQUIRED", false);
            editor.apply();
            Intent intent = new Intent(Settings.this, Help.class);
            startActivity(intent);
        }
    }

    public void changePassword(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
        alertDialog.setTitle("Change Password");

        LinearLayout dialogLayout = new LinearLayout(Settings.this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParamsLabel = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsLabel.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,12, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1, getResources().getDisplayMetrics()));

        LinearLayout.LayoutParams layoutParamsEditText = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsEditText.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1, getResources().getDisplayMetrics()));

        final TextView oldPassLabel = new TextView(Settings.this);
        final EditText oldPassword = new EditText(Settings.this);
        oldPassLabel.setText("Old Password:");
        oldPassLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        oldPassLabel.setLayoutParams(layoutParamsLabel);
        oldPassword.setLayoutParams(layoutParamsEditText);
        dialogLayout.addView(oldPassLabel);
        dialogLayout.addView(oldPassword);

        final TextView newPassLabel = new TextView(Settings.this);
        final EditText newPassword = new EditText(Settings.this);
        newPassLabel.setText("New Password:");
        newPassLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassLabel.setLayoutParams(layoutParamsLabel);
        newPassword.setLayoutParams(layoutParamsEditText);
        dialogLayout.addView(newPassLabel);
        dialogLayout.addView(newPassword);
        alertDialog.setView(dialogLayout);

        alertDialog.setPositiveButton("Update Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = "/accounts?operation=changePassword";
                url += "&email="+email.getText().toString();
                url += "&oldPassword="+oldPassword.getText().toString();
                url += "&newPassword="+newPassword.getText().toString();
                new AsyncHttpClient().get(getString(R.string.server)+url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("SETTINGS", responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if(responseString.equalsIgnoreCase("ACK")){
                            Toast.makeText(Settings.this, "Password changed", Toast.LENGTH_SHORT).show();
                        }
                        else if(responseString.equalsIgnoreCase("INCORRECT_PASSWORD")){
                            Toast.makeText(Settings.this, "Incorrect password entered", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    alertDialog.show();
    }

    public void signOut(View view) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("LOGGED_IN", false);
        editor.putString("NAME", "");
        editor.putString("EMAIL", "");
        editor.putString("BRANCH", "");
        editor.putString("YEAR", "");
        editor.putString("DIV", "");
        editor.putString("SEM", "");
        editor.putString("BATCH", "");
        editor.apply();
        finish();
    }

    public void help(View view){
        Intent help = new Intent(Settings.this, Help.class);
        startActivity(help);
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
