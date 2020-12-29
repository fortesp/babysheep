package fortesp.babysheep;

import android.content.Context;

import lombok.Getter;

public final class Application extends android.app.Application {

    @Getter
    private static Context context;

    public void onCreate() {
        super.onCreate();
        Application.context = getApplicationContext();
    }

}