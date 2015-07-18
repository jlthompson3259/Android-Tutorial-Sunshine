package com.example.android.sunshine.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by Jeffery on 7/14/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 1;
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecastStr;
    private String mForecastUri;
    private Uri mUri;
    private ShareActionProvider mShareActionProvider;

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_DEGREES = 7;
    private static final int COL_WEATHER_PRESSURE = 8;
    private static final int COL_WEATHER_DESC_ID = 9;

    public static final String DETAIL_URI = "Detail Uri";

    private TextView mDayView;
    private TextView mDateView;
    private TextView mHighView;
    private TextView mLowView;
    private TextView mHumidityView;
    private TextView mPressureView;
    private TextView mWindTextView;
    private TextView mWeatherView;
    private ImageView mIconView;
    private WindView mWindView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mDayView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mHighView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        mWindTextView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mWeatherView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mWindView = (WindView) rootView.findViewById(R.id.detail_windview);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mForecastStr != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    public void onLocationChanged(String newLocation){
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != mUri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }


    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null);
        }
        else return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // move cursor to first position and guarantee something was returned
        if(!data.moveToFirst()) {return;}

        Activity activity = getActivity();

        // set day and date text views
        long date = data.getLong(COL_WEATHER_DATE);
        mDayView.setText(Utility.getDayName(activity, date));
        mDateView.setText(Utility.getFormattedMonthDay(activity,date));

        boolean isMetric = Utility.isMetric(getActivity());

        // set the min/max temp text views
        double high = data.getDouble(COL_WEATHER_MAX_TEMP);

        mHighView.setText(Utility.formatTemperature(activity, high, isMetric));
        double low = data.getDouble(COL_WEATHER_MIN_TEMP);
        mLowView.setText(Utility.formatTemperature(activity, low, isMetric));

        //set the humidity text view
        double humidity = data.getDouble(COL_WEATHER_HUMIDITY);
        mHumidityView.setText(activity.getString(R.string.format_humidity, humidity));

        //set the wind text view
        float windSpd = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDir = data.getFloat(COL_WEATHER_DEGREES);
        mWindTextView.setText(Utility.getFormattedWind(activity, windSpd, windDir));
        mWindView.setWindDirection(windDir);

        //set the pressure text view
        double pressure = data.getDouble(COL_WEATHER_PRESSURE);
        mPressureView.setText(activity.getString(R.string.format_pressure,pressure));

        // set the weather icon - dummy for now
        int weatherId = data.getInt(COL_WEATHER_DESC_ID);
        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        // set the weather description text view
        String weather = data.getString(COL_WEATHER_DESC);
        mWeatherView.setText(weather);

        mForecastStr = activity.getString(R.string.format_share,
                Utility.getFriendlyDayString(activity,date),
                weather,
                Utility.formatTemperature(activity,high,isMetric),
                Utility.formatTemperature(activity,low,isMetric));

        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}