package kjsce.stuart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class Subject extends AppCompatActivity {
    private String subjectID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        final String server = getString(R.string.server);
        final ListView expListView = findViewById(R.id.expList);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            subjectID = getIntent().getExtras().getString("SUBJECT_ID");
            getSupportActionBar().setTitle(subjectID);
        }

        // Get courses from server
        String url = "/courses?operation=getSubjectDetails&subjectID="+subjectID;
        new AsyncHttpClient().get(server+url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("SUBJECT", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONArray subjectList = new JSONArray(responseString);
                    JSONObject subject = subjectList.getJSONObject(0);
                    JSONArray experiments = subject.getJSONArray("experiments");
                    Experiment[] expList = new Experiment[experiments.length()];
                    for(int i=0; i<experiments.length(); i++){
                        expList[i] = new Experiment(experiments.getJSONObject(i).getString("exp_no"),
                                subjectID, experiments.getJSONObject(i).getString("exp_file"));
                    }
                    Arrays.sort(expList);

                    final ArrayAdapter<Experiment> expAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,
                            android.R.id.text1, expList);
                    expListView.setAdapter(expAdapter);
                    expListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Experiment exp = expAdapter.getItem(position);
                            new DownloadFile().execute(server+"/files/"+exp.path, exp.toString());
                            Toast.makeText(getApplicationContext(), exp.toString()+" downloading...", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    class Experiment implements Comparable<Experiment>
    {
        private String exp_no, subjectCode, path;
        Experiment(String exp_no, String subjectCode, String path){
            this.exp_no = exp_no;
            this.subjectCode = subjectCode;
            this.path = path;
        }

        @Override
        public String toString() {
            return subjectCode+"_"+exp_no+".pdf";
        }

        @Override
        public int compareTo(@NonNull Experiment exp) {
            return exp_no.compareTo(exp.exp_no);
        }
    }
}
