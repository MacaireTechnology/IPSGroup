package com.mirrormind.ipsgroup;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mirrormind.ipsgroup.alarm_sec.RemindersIntentManager;
import com.mirrormind.ipsgroup.attendanceteam.TrackTeam;
import com.mirrormind.ipsgroup.fragment.AdminMng_frg;
import com.mirrormind.ipsgroup.fragment.Atten_frg;
import com.mirrormind.ipsgroup.fragment.ContactFrg;
import com.mirrormind.ipsgroup.fragment.Profile_frg;
import com.mirrormind.ipsgroup.fragment.TeamPerfm_frg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import db.DatabaseHelper;
import uihelper.Interface.RecyclerClick;
import uihelper.icomoon.Icomoon;
import uihelper.sharedPref.GlobalData;
import uihelper.sharedPref.SharedPreference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GlobalData {

    public static final String TAG = MainActivity.class.getSimpleName();
    TextView tv_announce_icon, tv_contact_icon, tv_home_icon, tv_team_icon, tv_profile_icon;

    private TextView tv_profile, tv_team, tv_home, tv_contact, tv_announce,
            tv_calendar_icon, tv_user_icon,tv_calendar,tv_user;
    Fragment mFragment;

    RecyclerView rv_list_dashboard;
    DashboardAdapter dashboardAdapter;

    String[] dashboardText = {"Attendance & Team", "Team Performance", "Travel & Reimbrushment",
            "Admin Management"};

    int[] dashboardIcon = {
            R.string.attendance_icon_, R.string.teamicon_color,
            R.string.travelicon_color, R.string.adminicon_color,
    };
    static final int WAKE_LOCK_REQUEST_CODE = 49488;  // random number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initID();
        initListener();
        initIcon();

        tv_home_icon.setTextColor(getResources().getColor(R.color.lightblue));
        tv_home_icon.setTypeface(tv_home_icon.getTypeface(), Typeface.BOLD);
        tv_home.setTypeface(tv_home.getTypeface(), Typeface.BOLD);

        checkWakeLockPermission();
    }

    void checkWakeLockPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED) {
            setUpNotification();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WAKE_LOCK}, WAKE_LOCK_REQUEST_CODE);
        }
    }
    private void initID() {
        tv_announce = findViewById(R.id.tv_announce);
        tv_contact = findViewById(R.id.tv_contact);
        tv_home = findViewById(R.id.tv_home);
        tv_team = findViewById(R.id.tv_team);
        tv_profile = findViewById(R.id.tv_profile);
        tv_calendar = findViewById(R.id.tv_calendar);
        tv_user = findViewById(R.id.tv_user);

        tv_announce_icon = findViewById(R.id.tv_announce_icon);
        tv_contact_icon = findViewById(R.id.tv_contact_icon);
        tv_home_icon = findViewById(R.id.tv_home_icon);
        tv_team_icon = findViewById(R.id.tv_team_icon);
        tv_profile_icon = findViewById(R.id.tv_profile_icon);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        tv_calendar.setText(df.format(c));

        if (SharedPreference.getDefaults(this,TAG_USER_NAME)!=null &&
                !SharedPreference.getDefaults(this,TAG_USER_NAME).equals("") &&
                !SharedPreference.getDefaults(this,TAG_USER_NAME).isEmpty()){
            tv_user.setText(SharedPreference.getDefaults(this,TAG_USER_NAME));
        }else {
            tv_user.setText("-");
        }
        findViewById(R.id.ll_contact).setOnClickListener(this);
        findViewById(R.id.ll_home).setOnClickListener(this);
        findViewById(R.id.ll_team).setOnClickListener(this);
        findViewById(R.id.ll_profile).setOnClickListener(this);
        findViewById(R.id.ll_announce).setOnClickListener(this);

        tv_calendar_icon = findViewById(R.id.tv_calendar_icon);
        tv_user_icon = findViewById(R.id.tv_user_icon);
        rv_list_dashboard = findViewById(R.id.rv_list_dashboard);
        rv_list_dashboard.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        rv_list_dashboard.setAdapter(dashboardAdapter = new DashboardAdapter(getApplicationContext(),
                dashboardText, dashboardIcon));

    }

    private void initIcon() {

        Icomoon.imageLogo.apply(this, tv_announce_icon);
        Icomoon.imageLogo.apply(this, tv_contact_icon);
        Icomoon.imageLogo.apply(this, tv_home_icon);
        Icomoon.imageLogo.apply(this, tv_team_icon);
        Icomoon.imageLogo.apply(this, tv_profile_icon);

    }

    private void initListener() {

        Icomoon.imageLogo.apply(this, tv_calendar_icon);
        Icomoon.imageLogo.apply(this, tv_user_icon);

        rv_list_dashboard.addOnItemTouchListener(new RecyclerClick(getApplicationContext(), rv_list_dashboard,
                new RecyclerClick.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        switch (position) {
                            case 0:
                                mFragment = new Atten_frg();
                                displaySelectedFragment(mFragment);
                                break;
                            case 1:
                                mFragment = new TeamPerfm_frg();
                                displaySelectedFragment(mFragment);
                                break;
                            case 3:
                                mFragment = new AdminMng_frg();
                                displaySelectedFragment(mFragment);
                                break;
                            case 4:

                                break;
                            default:

                                break;
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                })
        );

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Log.e(TAG,"size "+databaseHelper.getClockInOut().size());

        new OnDBSync(this,1).execute();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_announce:
                tv_announce.setTextColor(getResources().getColor(R.color.lightblack));
                tv_announce_icon.setTextColor(getResources().getColor(R.color.lightblue));
                tv_contact.setTextColor(getResources().getColor(R.color.lightblack));
                tv_contact_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_home.setTextColor(getResources().getColor(R.color.lightblack));
                tv_home_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_team.setTextColor(getResources().getColor(R.color.lightblack));
                tv_team_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_profile.setTextColor(getResources().getColor(R.color.lightblack));
                tv_profile_icon.setTextColor(getResources().getColor(R.color.lightblack));

                tv_announce.setTypeface(tv_announce.getTypeface(), Typeface.BOLD);
                tv_announce_icon.setTypeface(tv_announce_icon.getTypeface(), Typeface.BOLD);
                tv_contact.setTypeface(Typeface.DEFAULT);
                tv_contact_icon.setTypeface(Typeface.DEFAULT);
                tv_home.setTypeface(Typeface.DEFAULT);
                tv_home_icon.setTypeface(Typeface.DEFAULT);
                tv_team.setTypeface(Typeface.DEFAULT);
                tv_team_icon.setTypeface(Typeface.DEFAULT);
                tv_profile.setTypeface(Typeface.DEFAULT);
                tv_profile_icon.setTypeface(Typeface.DEFAULT);
                initIcon();
                break;
            case R.id.ll_contact:
                mFragment = new ContactFrg();
                displaySelectedFragment(mFragment);
                tv_announce.setTextColor(getResources().getColor(R.color.lightblack));
                tv_announce_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_contact.setTextColor(getResources().getColor(R.color.lightblack));
                tv_contact_icon.setTextColor(getResources().getColor(R.color.lightblue));
                tv_home.setTextColor(getResources().getColor(R.color.lightblack));
                tv_home_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_team.setTextColor(getResources().getColor(R.color.lightblack));
                tv_team_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_profile.setTextColor(getResources().getColor(R.color.lightblack));
                tv_profile_icon.setTextColor(getResources().getColor(R.color.lightblack));

                tv_announce.setTypeface(Typeface.DEFAULT);
                tv_announce_icon.setTypeface(Typeface.DEFAULT);
                tv_contact.setTypeface(tv_contact.getTypeface(), Typeface.BOLD);
                tv_contact_icon.setTypeface(tv_contact_icon.getTypeface(), Typeface.BOLD);
                tv_home.setTypeface(Typeface.DEFAULT);
                tv_home_icon.setTypeface(Typeface.DEFAULT);
                tv_team.setTypeface(Typeface.DEFAULT);
                tv_team_icon.setTypeface(Typeface.DEFAULT);
                tv_profile.setTypeface(Typeface.DEFAULT);
                tv_profile_icon.setTypeface(Typeface.DEFAULT);
                initIcon();

                break;
            case R.id.ll_home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                tv_announce.setTextColor(getResources().getColor(R.color.lightblack));
                tv_announce_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_contact.setTextColor(getResources().getColor(R.color.lightblack));
                tv_contact_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_home.setTextColor(getResources().getColor(R.color.lightblack));
                tv_home_icon.setTextColor(getResources().getColor(R.color.lightblue));
                tv_team.setTextColor(getResources().getColor(R.color.lightblack));
                tv_team_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_profile.setTextColor(getResources().getColor(R.color.lightblack));
                tv_profile_icon.setTextColor(getResources().getColor(R.color.lightblack));

                tv_announce.setTypeface(Typeface.DEFAULT);
                tv_announce_icon.setTypeface(Typeface.DEFAULT);
                tv_contact.setTypeface(Typeface.DEFAULT);
                tv_contact_icon.setTypeface(Typeface.DEFAULT);
                tv_home.setTypeface(tv_home.getTypeface(), Typeface.BOLD);
                tv_home_icon.setTypeface(tv_home_icon.getTypeface(), Typeface.BOLD);
                tv_team.setTypeface(Typeface.DEFAULT);
                tv_team_icon.setTypeface(Typeface.DEFAULT);
                tv_profile.setTypeface(Typeface.DEFAULT);
                tv_profile_icon.setTypeface(Typeface.DEFAULT);
                initIcon();

                break;
            case R.id.ll_team:
                startActivity(new Intent(getApplicationContext(), TrackTeam.class));
                tv_announce.setTextColor(getResources().getColor(R.color.lightblack));
                tv_announce_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_contact.setTextColor(getResources().getColor(R.color.lightblack));
                tv_contact_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_home.setTextColor(getResources().getColor(R.color.lightblack));
                tv_home_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_team.setTextColor(getResources().getColor(R.color.lightblack));
                tv_team_icon.setTextColor(getResources().getColor(R.color.lightblue));
                tv_profile.setTextColor(getResources().getColor(R.color.lightblack));
                tv_profile_icon.setTextColor(getResources().getColor(R.color.lightblack));

                tv_announce.setTypeface(Typeface.DEFAULT);
                tv_announce_icon.setTypeface(Typeface.DEFAULT);
                tv_contact.setTypeface(Typeface.DEFAULT);
                tv_contact_icon.setTypeface(Typeface.DEFAULT);
                tv_home.setTypeface(Typeface.DEFAULT);
                tv_home_icon.setTypeface(Typeface.DEFAULT);
                tv_team.setTypeface(tv_team.getTypeface(), Typeface.BOLD);
                tv_team_icon.setTypeface(tv_team_icon.getTypeface(), Typeface.BOLD);
                tv_profile.setTypeface(Typeface.DEFAULT);
                tv_profile_icon.setTypeface(Typeface.DEFAULT);
                initIcon();
                break;
            case R.id.ll_profile:
                mFragment = new Profile_frg();
                displaySelectedFragment(mFragment);
                tv_announce.setTextColor(getResources().getColor(R.color.lightblack));
                tv_announce_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_contact.setTextColor(getResources().getColor(R.color.lightblack));
                tv_contact_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_home.setTextColor(getResources().getColor(R.color.lightblack));
                tv_home_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_team.setTextColor(getResources().getColor(R.color.lightblack));
                tv_team_icon.setTextColor(getResources().getColor(R.color.lightblack));
                tv_profile.setTextColor(getResources().getColor(R.color.lightblack));
                tv_profile_icon.setTextColor(getResources().getColor(R.color.lightblue));

                tv_announce.setTypeface(Typeface.DEFAULT);
                tv_announce_icon.setTypeface(Typeface.DEFAULT);
                tv_contact.setTypeface(Typeface.DEFAULT);
                tv_contact_icon.setTypeface(Typeface.DEFAULT);
                tv_home.setTypeface(Typeface.DEFAULT);
                tv_home_icon.setTypeface(Typeface.DEFAULT);
                tv_team.setTypeface(Typeface.DEFAULT);
                tv_team_icon.setTypeface(Typeface.DEFAULT);
                tv_profile.setTypeface(tv_profile.getTypeface(), Typeface.BOLD);
                tv_profile_icon.setTypeface(tv_profile_icon.getTypeface(), Typeface.BOLD);
                initIcon();
                break;
        }
    }

    public void displaySelectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStack();
    }
    void setUpNotification() {
        Long time = new GregorianCalendar().getTimeInMillis() + 60 * 1000;
        RemindersIntentManager remindersIntentManager = RemindersIntentManager.getInstance(this);

        String toParse ="31-3-2017 16:44"; // set any date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm",Locale.getDefault()); // I assume d-M, you may refer to M-d for month-day instead.
        Date date = null; // You will need try/catch around this
        try {
            date = formatter.parse(toParse);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        long timeFromDay = date.getTime();

        final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_HALF_HOUR,
                remindersIntentManager.getChristmasIntent());
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeFromDay, remindersIntentManager.getDotaIntent());
    }
}

class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.MyViewHolder> {

    private Context mActivity;
    private String[] dashboardText;
    private int[] dashboardIcon;

    DashboardAdapter(Context applicationContext, String[] dashboardText, int[] dashboardIcon) {
        this.mActivity = applicationContext;
        this.dashboardText = dashboardText;
        this.dashboardIcon = dashboardIcon;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_homeadp_card,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Shader myShader = new LinearGradient(
                -20, -20, 100, 100,
                mActivity.getResources().getColor(R.color.lightblue),
                mActivity.getResources().getColor(R.color.pink),
                Shader.TileMode.CLAMP);
        holder.tv_option_icon.getPaint().setShader(myShader);


        holder.tv_option.setText(dashboardText[position]);

        holder.tv_option_icon.setText(mActivity.getResources().getString(dashboardIcon[position]));

        Icomoon.imageLogo.apply(mActivity, holder.tv_option_icon);

    }

    @Override
    public int getItemCount() {
        return dashboardText.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_option;
        TextView tv_option_icon, tv_option;

        public MyViewHolder(View itemView) {
            super(itemView);
            ll_option = itemView.findViewById(R.id.ll_option);
            tv_option_icon = itemView.findViewById(R.id.tv_option_icon);
            tv_option = itemView.findViewById(R.id.tv_option);
        }
    }


}


