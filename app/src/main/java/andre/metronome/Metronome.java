package andre.metronome;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Andre on 31-Aug-17.
 */

public class Metronome {

    private final int MAX_BPM = 200;
    private int _bpm;
    private boolean _isRunning;
    private long _lastTickTime, _tickPeriodMs;
    private Timer _timer;
    private Context _currentContext;
    private MediaPlayer _mediaPlayer;
    private TextView _bpmTextView = null;

    public Metronome(){

        setBpm(80);
        _isRunning = false;
    }

    public void start(Context context){

        _currentContext = context;

        if(!_isRunning){

            _isRunning = true;
            _mediaPlayer = MediaPlayer.create(context, R.raw.low_seiko_sq50);
            _tickPeriodMs =  (long) ((60.0/(float) getBpm())*1000.0);;
            _timer = new Timer();
            Log.v("Metronome", "Starting metronome at " + getBpm() + " BPM ( " + _tickPeriodMs + " miliseconds per beat)");

            _timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    tick();
                }
            }, 0, _tickPeriodMs);
        }
    }

    public void stop(){

        _mediaPlayer.release();
        _timer.cancel();
        _isRunning = false;
    }

    private void tick(){

        long currentPeriodMs = System.currentTimeMillis() - _lastTickTime;

        Log.v("Metronome", "Time since last tick is " + currentPeriodMs + "ms (" + getBpm() + " BPM)");
        Log.v("Metronome", "Precision of " + calculatePrecision(currentPeriodMs) + "%");
        _lastTickTime = System.currentTimeMillis();
        try{
            _mediaPlayer.start();
        }catch(Exception e) {
            Log.e("Metronome", e.toString());
        }
    }

    private void refreshBpmTextView(){

        if(_bpmTextView != null){
            _bpmTextView.setText(Integer.toString(getBpm()) + " BPM");
        }
    }

    private float calculatePrecision(long currentPeriodMs){

        return (float) (((float) (currentPeriodMs - _tickPeriodMs)) / ((float)_tickPeriodMs/100.0));
    }

    //--------------------------------------------------SETTERS
    public void setBpmTextView(TextView textView){
        _bpmTextView = textView;
        refreshBpmTextView();
    }

    public void setBpm(int bpm){

        if(bpm <= MAX_BPM){
            _bpm = bpm;
            refreshBpmTextView();
        }
        else{
            Log.v("Metronome", "BPM = " + bpm + " not allowed.");
        }
    }

    public void increaseBpm(int value){

        if(_bpm + value <= MAX_BPM){
            _bpm = _bpm + value;
            Log.v("Metronome", "BPM set to " + getBpm());
            if(_isRunning){
                stop();
                start(_currentContext);
            }
        }
        refreshBpmTextView();
    }

    public void decreaseBpm(int value){

        if(_bpm - value >= 1){
            _bpm = _bpm - value;
            Log.v("Metronome", "BPM set to " + getBpm());
            if(_isRunning){
                stop();
                start(_currentContext);
            }
            refreshBpmTextView();
        }
    }

    //--------------------------------------------------GETTERS

    int getBpm(){ return _bpm; }
    boolean getIsRunning(){ return _isRunning; }
}
