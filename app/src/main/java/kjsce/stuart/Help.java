package kjsce.stuart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Help extends AppCompatActivity {
    TextView pageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        pageNo = findViewById(R.id.pageNo);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        HelpCard helpCard = new HelpCard();
        helpCard.setLayout(R.layout.help_writeup);
        adapter.addFragment(helpCard, "WRITEUP");
        helpCard = new HelpCard();
        helpCard.setLayout(R.layout.help_events);
        adapter.addFragment(helpCard, "EVENTS");
        helpCard = new HelpCard();
        helpCard.setLayout(R.layout.help_location);
        adapter.addFragment(helpCard, "LOCATIONS");

        final ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                String page = (position+1)+" of "+viewPager.getAdapter().getCount();
                pageNo.setText(page);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    public void closeButtonClicked(View view) {
        finish();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    public static class HelpCard extends Fragment {
        int layout;

        public void setLayout(int layout){
            this.layout = layout;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(layout, container, false);
            return v;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
    }
}
