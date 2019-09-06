package com.mirrormind.ipsgroup.alarm_sec;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class RemindersIntentManager {

    private static final int CHRISTMAS = 0;
    private static final int THE_INTERNATIONALS = 1;
    private static RemindersIntentManager remindersIntentManager;
    private PendingIntent[] reminderIntents;
    private Context mContext;

    private RemindersIntentManager( Context context ) {
        mContext = context;
        reminderIntents = new PendingIntent[3];
    }
    public static RemindersIntentManager getInstance( Context context ) {
        if ( remindersIntentManager == null ) {
            remindersIntentManager = new RemindersIntentManager( context );
        }
        return remindersIntentManager;
    }
    public PendingIntent getChristmasIntent() {
        Intent intentAlarm = new Intent( mContext, AlarmReceiver_sec.class );
        intentAlarm.putExtra( "reminder", "Christmas Happies!" );
        intentAlarm.putExtra( "code", ( CHRISTMAS + 1 ) * 133 );
        reminderIntents[ CHRISTMAS ] = PendingIntent.getBroadcast( mContext, CHRISTMAS,
                intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT );
        return reminderIntents[ CHRISTMAS ];

    }
    public PendingIntent getDotaIntent() {
        Intent intentAlarm = new Intent( mContext, AlarmReceiver_sec.class );
        intentAlarm.putExtra( "reminder", "Aegis is up for grabs! The Internationals starts now." );
        intentAlarm.putExtra( "code", ( THE_INTERNATIONALS + 1 ) * 133 );

        reminderIntents[ THE_INTERNATIONALS ] = PendingIntent.getBroadcast(
                mContext, THE_INTERNATIONALS, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT );

        return reminderIntents[ THE_INTERNATIONALS ];
    }
}