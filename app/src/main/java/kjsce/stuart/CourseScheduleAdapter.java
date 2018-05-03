package kjsce.stuart;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CourseScheduleAdapter extends RecyclerView.Adapter<CourseScheduleAdapter.ScheduleViewHolder> {
    private List<ScheduleCard> cards;
    private Context context;
    private SharedPreferences preferences;
    private String subjectID;

    CourseScheduleAdapter(final Context context, String subjectID){
        this.context = context;
        this.subjectID = subjectID;
        preferences = context.getSharedPreferences("Stuart", Context.MODE_PRIVATE);
        cards = new ArrayList<>();

        try {
            displayDay(new JSONArray(preferences.getString("MONDAY", "[]")), "MONDAY");
            displayDay(new JSONArray(preferences.getString("TUESDAY", "[]")), "TUESDAY");
            displayDay(new JSONArray(preferences.getString("WEDNESDAY", "[]")), "WEDNESDAY");
            displayDay(new JSONArray(preferences.getString("THURSDAY", "[]")), "THURSDAY");
            displayDay(new JSONArray(preferences.getString("FRIDAY", "[]")), "FRIDAY");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    private void displayDay(JSONArray day, String dayName){
        String time="", desc="";
        try {
            for(int i=0; i<day.length(); i++){
                JSONObject currentClass = day.getJSONObject(i);
                if(currentClass.getString("class_type").equalsIgnoreCase("lab")){
                    JSONArray labs = currentClass.getJSONArray("lab");
                    for(int j=0; j<labs.length(); j++){
                        JSONObject lab = labs.getJSONObject(j);
                        String myBatch = preferences.getString("DIV", "A").concat(preferences.getString("BATCH","4"));
                        if(lab.getString("batch").equalsIgnoreCase(myBatch) && lab.getString("subject").equalsIgnoreCase(subjectID)){
                            if(!(time.isEmpty() || desc.isEmpty())){
                                time += "\n\n";
                                desc += "\n\n";
                            }
                            time += currentClass.getString("start_time")+"\n"+currentClass.getString("end_time");
                            desc += currentClass.getString("class_type").toUpperCase()+" in "+lab.getString("location")+"\n"+lab.getString("faculty");
                            break;
                        }
                    }
                }
                else if(currentClass.getString("class_type").equalsIgnoreCase("lecture") &&
                        currentClass.getString("subject").equalsIgnoreCase(subjectID)){
                    if(!(time.isEmpty() || desc.isEmpty())){
                        time += "\n\n";
                        desc += "\n\n";
                    }
                    time += currentClass.getString("start_time")+"\n"+currentClass.getString("end_time");
                    desc += currentClass.getString("class_type").toUpperCase()+" in "+currentClass.getString("location")+"\n"+currentClass.getString("faculty");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!(time.isEmpty() || desc.trim().isEmpty())){
            cards.add(new ScheduleCard(time,dayName,desc));
        }
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_course_schedule, parent, false);
        return new ScheduleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ScheduleViewHolder holder, final int position) {
        holder.time.setText(cards.get(position).time);
        holder.day.setText(cards.get(position).day);
        holder.desc.setText(cards.get(position).desc);
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout layout;
        TextView time, day, desc;

        ScheduleViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.scheduleCard);
            layout = itemView.findViewById(R.id.scheduleCardLayout);
            time = itemView.findViewById(R.id.time);
            day = itemView.findViewById(R.id.day);
            desc = itemView.findViewById(R.id.desc);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class ScheduleCard {
        String time, day, desc;

        ScheduleCard(String time, String day, String desc) {
            this.time = time;
            this.day = day;
            this.desc = desc;
        }
    }

}
