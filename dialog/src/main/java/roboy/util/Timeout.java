package roboy.util;


import java.util.Timer;
import java.util.TimerTask;

public class Timeout {

    public interface TimeoutObserver{
        void onTimeout(String unique);
    }

    private Runnable runnable;

    private long millis;
    private Timer timer;
    private TimerTask timerTask;

    // a unique string for timeout (E.g. chatID for telegram)
    private String unique;

    public Timeout(long millis){
        this.millis = millis;
        timer = new Timer();
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }

    public String getUnique(){
        return unique;
    }

    public void setUnique(String unique){
        this.unique = unique;
    }


    public void start(TimeoutObserver timeoutObserver){
        //cancel all the timer tasks
        stop();

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timeoutObserver.onTimeout(unique);
            }
        };

        timer.schedule(timerTask, millis);

    }

    //Completely cancels the timer.
    public void stop(){
        if(timerTask != null){
            timerTask.cancel();
        }

        if(timer != null){
            timer.cancel();
            timer.purge();
        }
    }
}