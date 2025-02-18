package me.adamoflynn.dynalarm.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import me.adamoflynn.dynalarm.R;
import me.adamoflynn.dynalarm.model.Sleep;

public class SleepAdapter extends RealmBaseAdapter<Sleep> implements ListAdapter {

    // Relevant date formats for printing the time and dates correctly
    private final DateFormat format = new SimpleDateFormat("HH:mm");
    private final DateFormat formatGMT = new SimpleDateFormat("HH:mm");
    private final DateFormat dateFormat = new SimpleDateFormat("E MMM dd", Locale.ENGLISH);
    private Context context = null;
    private final boolean autometicUpdate;
    private final RealmResults<Sleep> realmresults;
    RealmResults<Sleep> realmResults;

    // Get an instance of the user settings
    private SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);

    public static class ViewHolder {
        TextView date;
        TextView time;
        TextView duration;
        TextView sleepPercent;
        ProgressBar sleepDesired;
        ImageView starIcon;
    }


    // Constructor to pass the required
    public SleepAdapter(Context context, int resId, RealmResults<Sleep> realmResults, boolean automaticUpdate) {
        super(realmResults);
        this.context = context;
        this.realmresults = realmResults;
        this.autometicUpdate= automaticUpdate;
    }



    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            // Load custom layout file
            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.sleep_item, parent, false);
            viewHolder = new ViewHolder();

            // Get references to the required UI
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.sleepPercent = (TextView) convertView.findViewById(R.id.percent);
            viewHolder.sleepDesired = (ProgressBar) convertView.findViewById(R.id.sleepDesired);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);
            viewHolder.starIcon = (ImageView) convertView.findViewById(R.id.starIcon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the sleep object from the database results
        RealmResults<Sleep> realmResults = null;
        final Sleep s = realmResults.get(position);

        // Calculate the start, end, and duration of the sleep.
        long start = s.getStartTime();
        long end = s.getEndTime();
        long lengthOfsleep = end - start;

        // Print the correct time formats in the list.
        formatGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
        viewHolder.time.setText(formatGMT.format(new Date(lengthOfsleep)) + " hrs");
        viewHolder.duration.setText(format.format(new Date(start)) + " to " + format.format(new Date(end)));
        viewHolder.date.setText(dateFormat.format(s.getDate()));

        // Get value of desired sleep from settings
        String desired = mSettings.getString("desiredSleep", "07:00");

        try {

            // Calculate the percentage of the current sleep time over the desired sleep time
            formatGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date timeSelected = formatGMT.parse(desired);
            long desiredTime = timeSelected.getTime();

            double percent = ((double) lengthOfsleep/desiredTime) * 100;

            // Round the score up/down
            int progress = (int) Math.round(percent);

            // If percent is 100% or over, give a star and only say 100%. Otherwise, print the actual percentage
            viewHolder.sleepDesired.setMax(100);
            viewHolder.sleepDesired.setProgress(progress);
            if(progress >= 100){
                viewHolder.sleepPercent.setText("100%");
                viewHolder.starIcon.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.sleepPercent.setText(" "+Integer.toString(progress)+"%");
                viewHolder.starIcon.setVisibility(View.INVISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }


    public RealmResults<Sleep> getRealmResults() {
        RealmResults<Sleep> realmResults = null;
        return realmResults;
    }

}
