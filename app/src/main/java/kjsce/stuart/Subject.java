package kjsce.stuart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class Subject extends AppCompatActivity {
    private String subjectID;
    private RelativeLayout layout = null;
    private RecyclerView recyclerView = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            subjectID = getIntent().getExtras().getString("SUBJECT_ID");
            getSupportActionBar().setTitle(subjectID);
        }

        layout = findViewById(R.id.scheduleLayout);
        if(recyclerView!=null){
            layout.removeView(recyclerView);
        }
        recyclerView = new RecyclerView(getApplicationContext());
        recyclerView.setPadding(0,(int)(2*getResources().getDisplayMetrics().density),
                0,(int)(6*getResources().getDisplayMetrics().density));
        recyclerView.setClipToPadding(false);
        layout.addView(recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.Adapter recyclerAdapter = new CourseScheduleAdapter(getApplicationContext(), subjectID);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
