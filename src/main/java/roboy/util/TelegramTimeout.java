package roboy.util;


import java.util.Timer;
import java.util.TimerTask;

public class TelegramTimeout {

    //TODO: Although this is not a observer design pattern, it's name conflicts with it,
    // TODO: Should be renamed for the sake of readability!
    public interface TimeoutObserver{
        void onTimeout();
    }

    private Runnable runnable;

    private long millis;
    private Timer timer;
    private TimerTask timerTask;

    public TelegramTimeout(long millis){
        this.millis = millis;
        timer = new Timer();
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }

    public void start(TimeoutObserver timeoutObserver){
        //cancel all the timer tasks
        stop();

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timeoutObserver.onTimeout();
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
