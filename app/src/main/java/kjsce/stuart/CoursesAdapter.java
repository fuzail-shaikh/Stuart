package kjsce.stuart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Fuzail Shaikh on 14-Mar-18.
 */

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder> {
    private List<CourseCard> cards;
    private FragmentActivity fragmentActivity;

    CoursesAdapter(FragmentActivity fragmentActivity){
        this.fragmentActivity = fragmentActivity;
        int sessionID = new Random().nextInt(9999999) + 1;
        String server = fragmentActivity.getString(R.string.server);
        SharedPreferences preferences = fragmentActivity.getSharedPreferences("Stuart", Context.MODE_PRIVATE);
        cards = new ArrayList<>();

        // Get courses from server
        String url = "/courses?sessionID="+sessionID+"&operation=getCourseList";
        url += "&branch="+ preferences.getString("BRANCH", "Information Technology");
        url += "&year="+ preferences.getString("YEAR", "LY");
        url += "&sem="+ preferences.getString("SEM", "EVEN");

        new AsyncHttpClient().get(server +url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("COURSES", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("COURSES", responseString);
                try {
                    JSONArray subjectsArray = new JSONArray(responseString);
                    for(int i=0; i<subjectsArray.length(); i++){
                        JSONObject subject = subjectsArray.getJSONObject(i);
                        cards.add(new CourseCard(subject.getString("subj_code"),
                                subject.getString("subj_name"),
                                subject.getString("subj_type").toUpperCase(),
                                subject.getJSONArray("experiments").length()));
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public CoursesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_courses, parent, false);
        return new CoursesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CoursesViewHolder holder, final int position) {
        holder.name.setText(cards.get(position).name);
        holder.type.setText(cards.get(position).type);
        holder.writeupCount.setText(String.valueOf(cards.get(position).writeupCount));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subjectIntent = new Intent(fragmentActivity, Subject.class);
                subjectIntent.putExtra("SUBJECT_ID", cards.get(holder.getAdapterPosition())._id);
                v.getContext().startActivity(subjectIntent);
            }
        });
    }

    static class CoursesViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout layout;
        TextView name, type, writeupCount;

        CoursesViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.courseCard);
            layout = itemView.findViewById(R.id.courseCardLayout);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            writeupCount = itemView.findViewById(R.id.writeupCount);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CourseCard {
        String _id, name, type;
        int writeupCount;

        CourseCard(String _id, String name, String type, int writeupCount) {
            this._id = _id;
            this.name = name;
            this.type = type;
            this.writeupCount = writeupCount;
        }
    }

}
