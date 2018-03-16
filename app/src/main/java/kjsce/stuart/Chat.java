package kjsce.stuart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Chat extends AppCompatActivity {
    private LinearLayout layout = null;
    private RecyclerView recyclerView = null;
    private ChatAdapter recyclerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = findViewById(R.id.messagesLayout);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        TextView message = findViewById(R.id.messageBox);
        recyclerAdapter.addMessage(message.getText().toString(), "text", true);
        message.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(recyclerView!=null){
            layout.removeView(recyclerView);
        }
        recyclerView = new RecyclerView(getApplicationContext());
        recyclerView.setPadding(0,(int)(6*getResources().getDisplayMetrics().density),
                0,(int)(90*getResources().getDisplayMetrics().density));
        recyclerView.setClipToPadding(false);
        recyclerView.setTranslationY(60);
        recyclerView.animate().translationY(0).setDuration(300).setInterpolator(new DecelerateInterpolator());
        layout.addView(recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerAdapter = new ChatAdapter(getApplicationContext());
        recyclerView.setAdapter(recyclerAdapter);
    }
}
