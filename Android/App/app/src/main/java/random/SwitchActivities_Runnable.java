package random;

import android.app.Activity;
import android.content.Intent;

public class SwitchActivities_Runnable implements Runnable {
    public Activity from;
    public Class<? extends Activity> to;

    public SwitchActivities_Runnable(Activity from, Class<? extends Activity> to){
        this.to = to;
        this.from = from;
    }

    @Override
    public void run() {
        from.runOnUiThread(() -> {
            from.startActivity(new Intent(from, to));
        });
    }
}
