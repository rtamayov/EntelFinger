package bio.insolutions.veridium.entel.piloto;

import android.os.Bundle;

import com.veridiumid.sdk.core.data.persistence.IKVStore;
import com.veridiumid.sdk.defaultdata.DataStorage;
import com.veridiumid.sdk.fourf.defaultui.activity.DefaultFourFBiometricsActivity;

public class CustomFourFBiometricsActivity extends DefaultFourFBiometricsActivity {
    private final int FOURF_TIMEOUT = 120000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimeout(FOURF_TIMEOUT);
    }

    @Override
    protected IKVStore openStorage() {
        return DataStorage.getDefaultStorage();
    }
}
