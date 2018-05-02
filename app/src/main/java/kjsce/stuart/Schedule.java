package kjsce.stuart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

public class Schedule extends Fragment {
    private RelativeLayout layout = null;
    private RecyclerView recyclerView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        layout = getActivity().findViewById(R.id.scheduleLayout);
        if(recyclerView!=null){
            layout.removeView(recyclerView);
        }
        recyclerView = new RecyclerView(getActivity());
        recyclerView.setPadding(0,(int)(6*getActivity().getResources().getDisplayMetrics().density),
                0,(int)(90*getActivity().getResources().getDisplayMetrics().density));
        recyclerView.setClipToPadding(false);
        recyclerView.setTranslationY(60);
        recyclerView.animate().translationY(0).setDuration(300).setInterpolator(new DecelerateInterpolator());
        layout.addView(recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.Adapter recyclerAdapter = new ScheduleAdapter(getActivity());
        recyclerView.setAdapter(recyclerAdapter);
    }

}
