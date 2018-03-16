package kjsce.stuart;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fuzail Shaikh on 14-Mar-18.
 */

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder> {
    private List<CourseCard> cards;
    private FragmentActivity fragmentActivity;

    CoursesAdapter(FragmentActivity fragmentActivity){
        this.fragmentActivity = fragmentActivity;
        cards = new ArrayList<>();

        String id="CC", title="Cloud Computing", faculty="Purnima Ahirao";
        int writeupCount = 8;
        cards.add(new CourseCard(id,title,faculty,writeupCount));
        id="SC";
        title="Soft Computing";
        faculty="Sonali Patil";
        cards.add(new CourseCard(id,title,faculty,writeupCount));
        id="SPM";
        title="Software Project Management";
        faculty="Khushi Khanchandani";
        writeupCount = 5;
        cards.add(new CourseCard(id,title,faculty,writeupCount));
        id="DM";
        title="Digital Marketing";
        faculty="Era Johri";
        writeupCount = 8;
        cards.add(new CourseCard(id,title,faculty,writeupCount));
    }

    @Override
    public CoursesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_courses, parent, false);
        return new CoursesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CoursesViewHolder holder, final int position) {
        holder.title.setText(cards.get(position).title);
        holder.faculty.setText(cards.get(position).faculty);
        holder.writeupCount.setText(String.valueOf(cards.get(position).writeupCount));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subjectIntent = new Intent(fragmentActivity, Subject.class);
                subjectIntent.putExtra("SUBJECT_NAME", cards.get(holder.getAdapterPosition())._id);
                v.getContext().startActivity(subjectIntent);
            }
        });
    }

    public static class CoursesViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout layout;
        TextView title, faculty, writeupCount;

        CoursesViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.courseCard);
            layout = itemView.findViewById(R.id.courseCardLayout);
            title = itemView.findViewById(R.id.title);
            faculty = itemView.findViewById(R.id.faculty);
            writeupCount = itemView.findViewById(R.id.writeupCount);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CourseCard {
        String _id, title, faculty;
        int writeupCount;

        CourseCard(String _id, String title, String faculty, int writeupCount) {
            this._id = _id;
            this.title = title;
            this.faculty = faculty;
            this.writeupCount = writeupCount;
        }
    }

}
