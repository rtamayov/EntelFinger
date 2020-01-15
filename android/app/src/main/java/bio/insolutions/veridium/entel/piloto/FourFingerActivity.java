package bio.insolutions.veridium.entel.piloto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.digitalpersona.uareu.Compression;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.dpfj.CompressionImpl;
import com.veridiumid.sdk.BiometricResultsParser;
import com.veridiumid.sdk.IBiometricFormats;
import com.veridiumid.sdk.IBiometricResultsHandler;
import com.veridiumid.sdk.IVeridiumSDK;
import com.veridiumid.sdk.SDKInitializationException;
import com.veridiumid.sdk.VeridiumSDK;
import com.veridiumid.sdk.core.exception.LicenseException;
import com.veridiumid.sdk.defaultdata.VeridiumDataInitializer;
import com.veridiumid.sdk.fourf.FourFInterface;
import com.veridiumid.sdk.fourf.VeridiumFourFInitializer;
import com.veridiumid.sdk.fourfintegration.ExportConfig;
import com.veridiumid.sdk.support.help.ToastHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class FourFingerActivity extends Activity {
    public final String LOG_SEGUIMIENTO = "seguimientoLog";
    private static final int REQUEST_CAPTURE = 3;

    private static final int wsqWidth = 512;
    private static final int wsqHeigth = 512;

    private static final byte fillColor = (byte) 255;
    private static final int wsqBitRate = 200;
    private static final int wsqDPI = 500;
    private static final int wsqBPP = 8;

    private int indice = 0;
    public Button button_pulgar, button_indice, button_medio, button_alunar, button_menique;
    private IVeridiumSDK mBiometricSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourfinger);

        preInitSDK();
        verificarPermisosAplicacion();
        initSDK();


        button_pulgar = findViewById(R.id.pulgar);
        button_indice = findViewById(R.id.indice);
        button_medio = findViewById(R.id.medio);
        button_alunar = findViewById(R.id.anular);
        button_menique = findViewById(R.id.menique);

        button_pulgar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indice = 0;
                abrirPulgar();
            }
        });

        button_indice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indice = 0;
                abrir4Finger();
            }
        });

        button_medio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indice = 1;
                abrir4Finger();
            }
        });

        button_alunar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indice = 2;
                abrir4Finger();
            }
        });

        button_menique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indice = 3;
                abrir4Finger();
            }
        });
    }

    private void abrir4Finger(){
        ExportConfig.optimiseForIndexLittle(false);
        ExportConfig.optimiseForIndex(false);

        if (mBiometricSDK != null) {
            Intent captureIntent = mBiometricSDK.capture(new String[]{FourFInterface.UID});
            startActivityForResult(captureIntent, REQUEST_CAPTURE);
        }else{
            ToastHelper.showMessage(FourFingerActivity.this, "Engine null: Licence is invalid");
            Log.e(LOG_SEGUIMIENTO, "IVeridiumSDK object not initialised");
        }
    }
    private void abrirPulgar(){
        ExportConfig.optimiseForIndexLittle(false);
        ExportConfig.optimiseForIndex(false);

        if (mBiometricSDK != null) {
            Intent captureIntent = mBiometricSDK.captureTHUMB(new String[]{FourFInterface.UID});
            startActivityForResult(captureIntent, REQUEST_CAPTURE);
        }else{
            ToastHelper.showMessage(FourFingerActivity.this, "Engine null: Licence is invalid");
            Log.e(LOG_SEGUIMIENTO, "IVeridiumSDK object not initialised");
        }
    }

    private void preInitSDK() {
        Context appContext = getApplicationContext();

        try {
            VeridiumSDK.init(appContext,
                    new VeridiumFourFInitializer(),
                    new VeridiumDataInitializer()
            );
        } catch (SDKInitializationException e) {
            e.printStackTrace();
        }
    }

    private void verificarPermisosAplicacion() {
        int TODOS_LOS_PERMISOS = 1;

        String[] PERMISOS = {
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // La versión de Android es igual o superior a Marshmallow (API 23), se solicitan los permisos necesarios.
            Log.d(LOG_SEGUIMIENTO, "Se solicitarán los persmisos necesarios");

            if (!tienePermisos(this, PERMISOS)) {
                ActivityCompat.requestPermissions(this, PERMISOS, TODOS_LOS_PERMISOS);
            } else {
                // En una oportunidad anterior se mostró la ventana de solicitud de permisos y fueron aceptados los solicitados.
                Log.d(LOG_SEGUIMIENTO, "Permisos ya aceptados anteriormente, cambiando de pantalla");
            }
        } else {
            // Por ser una versión anterior a Android Marshmallow (API 23), no es necesario solicitar permisos
            Log.d(LOG_SEGUIMIENTO, "No es necesario solicitar permisos por la versión de Android");
        }
    }

    private static boolean tienePermisos(Context context, String... permisos) {
        if (context != null && permisos != null) {
            for (String permiso : permisos) {
                if (ActivityCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initSDK() {

        ExportConfig.setFormat(IBiometricFormats.TemplateFormat.FORMAT_JSON);
        ExportConfig.setCalculate_NFIQ(true);
        ExportConfig.setBackground_remove(true);
        ExportConfig.setPackDebugInfo(false);
        ExportConfig.setPackExtraScale(false);
        ExportConfig.setPackAuditImage(false);
        ExportConfig.setUseLiveness(false);
        ExportConfig.setLaxLiveness(false);

        ExportConfig.setPack_raw_scaled(true);
        ExportConfig.setPack_png_scaled(true);
        ExportConfig.setPack_wsq_scaled(false);

        ExportConfig.setUseNistType4(false);

        ExportConfig.optimiseForIndexLittle(false);
        ExportConfig.optimiseForIndex(false);


        try {
            mBiometricSDK = VeridiumSDK.getSingleton();
        } catch (LicenseException e) {
            ToastHelper.showMessage(this, R.string.license_is_invalid);
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        BiometricResultsParser.parse(resultCode, data, customCaptureHandler);
        // complete
        super.onActivityResult(requestCode, resultCode, data);
    }

    IBiometricResultsHandler customCaptureHandler = new IBiometricResultsHandler() {
        @Override
        public void handleSuccess(List<byte[]> results) {

            if (results != null && results.size() > 0) {
                convertirTrama(results);
                ToastHelper.showMessage(FourFingerActivity.this, R.string.save_success);

            }else {
                ToastHelper.showMessage(FourFingerActivity.this, R.string.write_fail);
            }
        }

        @Override
        public void handleFailure() {
            ToastHelper.showMessage(FourFingerActivity.this, R.string.failed);
        }

        @Override
        public void handleLivenessFailure() {
            ToastHelper.showMessage(FourFingerActivity.this, R.string.liveness_failed);
        }

        @Override
        public void handleCancellation() {
            ToastHelper.showMessage(FourFingerActivity.this, R.string.cancelled);
        }

        @Override
        public void handleError(String message) {
            ToastHelper.showMessage(FourFingerActivity.this, getString(R.string.error) + message);
        }
    };

    private void convertirTrama(List<byte[]> results) {
        try {
            JSONObject object = new JSONObject(new String(results.get(0)));
            JSONArray fingerprints = object.getJSONArray("Fingerprints");
            JSONObject currentFingerprint = fingerprints.getJSONObject(indice);
            JSONObject fingerImpressionImage = currentFingerprint.getJSONObject("FingerImpressionImage");

            String template = fingerImpressionImage.getString("BinaryBase64ObjectRAW");
            int height = fingerImpressionImage.getInt("Height");
            int width = fingerImpressionImage.getInt("Width");

            String respuestaWSQ = generateWsq(template, width, height).replace("\n","");
            regresar(respuestaWSQ);

        }catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void regresar(String respuestaWSQ) {
        Intent i = new Intent();
        i.putExtra("base64String", respuestaWSQ);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    String generateWsq(String rawVeridium, int widthVeridium, int heightVeridium) {
        return getWSQfromRawImage(rawVeridium, widthVeridium, heightVeridium);
    }

    public String getWSQfromRawImage(String rawImage, int widthVeridium, int heightVeridium) {

        byte[] input = Base64.decode(rawImage, Base64.DEFAULT);

        int diffW = wsqWidth - widthVeridium;
        int diffH = wsqHeigth - heightVeridium;

        int widhtL = (diffW / 2);
        int widhtR = (diffW / 2) + (diffW % 2);

        int heigthU = (diffH / 2);
        int heigthD = (diffH / 2) + (diffH % 2);

        return convertRawImageToWSQ(widthVeridium, heightVeridium, widhtL, widhtR, heigthU, heigthD, input, diffW % 2, diffH % 2);
    }


    private String convertRawImageToWSQ(int width, int heigth, int widhtL, int widhtR, int heigthU, int heigthD, byte[] rawImage, int modW, int modH) {
        byte[] resizedFPImage = new byte[262144];
        int posRowFPImage = 0;
        int posRawImage = 0;

        try {
            if (heigth > 512) {

                for (int i = 0; i < resizedFPImage.length; i++) {

                    if (posRowFPImage > (widhtL) && posRowFPImage < (width + widhtR + (modW == 0 ? 1 : 0))) {
                        resizedFPImage[i] = rawImage[posRawImage];
                        posRawImage++;
                    } else {
                        resizedFPImage[i] = fillColor;
                    }

                    if (posRowFPImage == 511)
                        posRowFPImage = 0;
                    else
                        posRowFPImage++;
                }
            } else {
                for (int i = 0; i < resizedFPImage.length; i++) {
                    if (i >= (wsqHeigth * heigthU) && i <= (wsqHeigth * (heigth + heigthD - (modH == 0 ? 0 : 1)))) {
                        if (posRowFPImage > (widhtL) && posRowFPImage < (width + widhtR + (modW == 0 ? 1 : 0))) {
                            resizedFPImage[i] = rawImage[posRawImage];
                            posRawImage++;
                        } else {
                            resizedFPImage[i] = fillColor;
                        }

                        if (posRowFPImage == 511)
                            posRowFPImage = 0;
                        else
                            posRowFPImage++;
                    } else {
                        resizedFPImage[i] = fillColor;
                    }
                }
            }

            CompressionImpl comp = new CompressionImpl();

            comp.Start();
            comp.SetWsqBitrate(wsqBitRate, 1);

            byte[] rawCompress = comp.CompressRaw(resizedFPImage, wsqWidth, wsqHeigth, wsqDPI, wsqBPP, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST);
            comp.Finish();

            return Base64.encodeToString(rawCompress, Base64.DEFAULT);

        } catch (UareUException ex) {
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } catch (Throwable ex) {
            return null;
        }
    }

}
