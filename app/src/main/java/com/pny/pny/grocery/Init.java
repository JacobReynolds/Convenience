package com.pny.pny.grocery;

import com.parse.Parse;

/**
 * Created by pny on 7/26/15.
 */
public class Init extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "yB4j9eX9af5w15F1BKSjpBjL35nOJkVXYt2O6LKt", "OYqt0ok5md8whBRSpR8aswuUZz2zilsmRudWWarj");
    }
}
