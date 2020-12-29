package fortesp.babysheep.preferences;

import fortesp.babysheep.Application;
import lombok.Getter;

import static android.content.Context.MODE_PRIVATE;

public class ApplicationPreferences extends AbstractApplicationPreferences {

    @Getter
    private static ApplicationPreferences instance = new ApplicationPreferences();

    private ApplicationPreferences() {
        sharedPreferences = Application.getContext().getSharedPreferences(ApplicationPreferencesResources.PREFERENCES_FILENAME, MODE_PRIVATE);
    }

}
