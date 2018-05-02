package kjsce.stuart;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private List<ScheduleCard> cards;
    private FragmentActivity fragmentActivity;
    private SharedPreferences preferences;

    ScheduleAdapter(final FragmentActivity fragmentActivity){
        this.fragmentActivity = fragmentActivity;
        String server = fragmentActivity.getString(R.string.server);
        preferences = fragmentActivity.getSharedPreferences("Stuart", Context.MODE_PRIVATE);
        cards = new ArrayList<>();

        if(isNetworkAvailable()){
            // Get courses from server
            String url = "/schedule?branch="+ preferences.getString("BRANCH", "Information Technology");
            url += "&year="+ preferences.getString("YEAR", "LY");
            url += "&div="+ preferences.getString("DIV", "A");
            url += "&sem="+ preferences.getString("SEM", "EVEN");

            new AsyncHttpClient().get(server +url, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("SCHEDULE", responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject schedule = new JSONObject(responseString);
                        JSONArray monday = sortTime(schedule.getJSONArray("monday"));
                        JSONArray tuesday = sortTime(schedule.getJSONArray("tuesday"));
                        JSONArray wednesday = sortTime(schedule.getJSONArray("wednesday"));
                        JSONArray thursday = sortTime(schedule.getJSONArray("thursday"));
                        JSONArray friday = sortTime(schedule.getJSONArray("friday"));
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("MONDAY", monday.toString());
                        editor.putString("TUESDAY", tuesday.toString());
                        editor.putString("WEDNESDAY", wednesday.toString());
                        editor.putString("THURSDAY", thursday.toString());
                        editor.putString("FRIDAY", friday.toString());
                        editor.apply();
                        displaySchedule();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            displaySchedule();
        }
    }

    private void displaySchedule(){
        try {
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.US);
            String weekDay = simpleDateformat.format(new Date());
            JSONArray day = new JSONArray();
            if(weekDay.equalsIgnoreCase("MONDAY")){
                day = new JSONArray(preferences.getString("MONDAY", "[]"));
            }
            else if(weekDay.equalsIgnoreCase("TUESDAY")){
                day = new JSONArray(preferences.getString("TUESDAY", "[]"));
            }
            else if(weekDay.equalsIgnoreCase("WEDNESDAY")){
                day = new JSONArray(preferences.getString("WEDNESDAY", "[]"));
            }
            else if(weekDay.equalsIgnoreCase("THURSDAY")){
                day = new JSONArray(preferences.getString("THURSDAY", "[]"));
            }
            else if(weekDay.equalsIgnoreCase("FRIDAY")){
                day = new JSONArray(preferences.getString("FRIDAY", "[]"));
            }
            for(int i=0; i<day.length(); i++){
                JSONObject currentClass = day.getJSONObject(i);
                if(currentClass.getString("class_type").equalsIgnoreCase("lab")){
                    JSONArray labs = currentClass.getJSONArray("lab");
                    for(int j=0; j<labs.length(); j++){
                        JSONObject lab = labs.getJSONObject(j);
                        String myBatch = preferences.getString("DIV", "A").concat(preferences.getString("BATCH","4"));
                        if(lab.getString("batch").equalsIgnoreCase(myBatch)){
                            cards.add(new ScheduleCard(currentClass.getString("start_time"),
                                    currentClass.getString("end_time"),
                                    lab.getString("subject"),
                                    lab.getString("location"),
                                    currentClass.getString("class_type"),
                                    lab.getString("faculty")));
                            break;
                        }
                    }
                }
                else if(currentClass.getString("class_type").equalsIgnoreCase("lecture")){
                    cards.add(new ScheduleCard(currentClass.getString("start_time"),
                            currentClass.getString("end_time"),
                            currentClass.getString("subject"),
                            currentClass.getString("location"),
                            currentClass.getString("class_type"),
                            currentClass.getString("faculty")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    private JSONArray sortTime(JSONArray dayArray){
        ArrayList<JSONObject> day = new ArrayList<>();
        for(int i=0; i<dayArray.length(); i++){
            try {
                day.add(dayArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(day, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);
                int result = 0;
                try {
                    String stA = a.getString("start_time");
                    String stB = b.getString("start_time");
                    Date date1 = format.parse(stA);
                    Date date2 = format.parse(stB);
                    result = Integer.compare(date1.compareTo(date2), 0);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                return result;
            }
        });
        JSONArray sortedDayArray = new JSONArray();
        for(int i=0; i<day.size(); i++){
            sortedDayArray.put(day.get(i));
        }
        return sortedDayArray;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) fragmentActivity.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_schedule, parent, false);
        return new ScheduleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ScheduleViewHolder holder, final int position) {
        String time = cards.get(position).startTime+"\n"+cards.get(position).endTime;
        holder.time.setText(time);
        holder.subjectName.setText(cards.get(position).subjectName);
        String desc = cards.get(position).classType.toUpperCase()+" in "+cards.get(position).room+" by "+cards.get(position).faculty;
        holder.desc.setText(desc);
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout layout;
        TextView time, subjectName, desc;

        ScheduleViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.scheduleCard);
            layout = itemView.findViewById(R.id.scheduleCardLayout);
            time = itemView.findViewById(R.id.time);
            subjectName = itemView.findViewById(R.id.subjectName);
            desc = itemView.findViewById(R.id.desc);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class ScheduleCard {
        String startTime, endTime, subjectName, room, classType, faculty;

        ScheduleCard(String startTime, String endTime, String subjectName, String room, String classType, String faculty) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.subjectName = subjectName;
            this.room = room;
            this.classType = classType;
            this.faculty = faculty;
        }
    }

}
