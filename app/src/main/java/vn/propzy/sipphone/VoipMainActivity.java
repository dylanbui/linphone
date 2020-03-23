package vn.propzy.sipphone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.linphone.core.Address;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.tools.Log;

import java.util.ArrayList;

import vn.propzy.sipphone.libraries.linphone.LinphoneManager;
import vn.propzy.sipphone.libraries.linphone.my_custom.CustomCallIncomingActivity;
import vn.propzy.sipphone.libraries.linphone.my_custom.CustomCallOutgoingActivity;
import vn.propzy.sipphone.libraries.linphone.my_custom.LinphoneContact;
import vn.propzy.sipphone.libraries.linphone.my_custom.SimpleLinphone;


public class VoipMainActivity extends AppCompatActivity {

    private String testPhone = "0988818597";
    //private String testPhone = "826";

    private ImageView mLed;
    private CoreListenerStub mCoreListener;

    private EditText mSipAddressToCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voip_main);

        mLed = findViewById(R.id.led);

        // Monitors the registration state of our account(s) and update the LED accordingly
        mCoreListener = new CoreListenerStub() {
            @Override
            public void onRegistrationStateChanged(Core core, ProxyConfig cfg, RegistrationState state, String message) {
                updateLed(state);
            }
        };

        mSipAddressToCall = findViewById(R.id.address_to_call);
        mSipAddressToCall.setText(testPhone);



        Button btnCallSip = findViewById(R.id.btnCallSip);
        btnCallSip.setOnClickListener(v -> {
            String value = mSipAddressToCall.getText().toString();
            int finalValue = Integer.parseInt(value);
            LinphoneContact contact = new LinphoneContact(finalValue , finalValue, "Tel " + value);
            SimpleLinphone.instance().startSingleCallingTo(contact);

//            Core core = LinphoneManager.getCore();
//            Address addressToCall = core.interpretUrl(mSipAddressToCall.getText().toString());
//            CallParams params = core.createCallParams(null);
//
//            //Switch videoEnabled = findViewById(R.id.call_with_video);
//            // params.enableVideo(videoEnabled.isChecked());
//            params.enableVideo(false);
//
//            if (addressToCall != null) {
//                core.inviteAddressWithParams(addressToCall, params);
//            }
        });

        Button btnCallPhoneNumber = findViewById(R.id.btnCallPhoneNumber);
        btnCallPhoneNumber.setOnClickListener(v -> {
            String value = mSipAddressToCall.getText().toString();
            LinphoneContact contact = new LinphoneContact(123, value, "Tel " + value);
            SimpleLinphone.instance().startSingleCallingTo(contact);
        });

        Button btnShowOutcoming = findViewById(R.id.btnShowOutcoming);
        btnShowOutcoming.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomCallOutgoingActivity.class);
            startActivity(intent);
        });

        Button btnShowIncoming = findViewById(R.id.btnShowIncoming);
        btnShowIncoming.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomCallIncomingActivity.class);
            startActivity(intent);
        });

        // Remove old config
        // LinphoneService.getCore().removeProxyConfig(LinphoneService.getCore().getDefaultProxyConfig());
        SimpleLinphone.instance().removeAccount(); //removeAuthConfig();

        // So 1
//        String strName = "826";
//        String strPw = "!Wbs9ZuYZu";
        // So 2
        String strName = "827";
        String strPw = "Z37.W!nNWZ";

        String strHost = "propzyhcm160.ccall.vn";
        String strPropxy = "sbcwrtchcm.ccall.vn:5060";

        SimpleLinphone.instance().registerUserAuth(strName, strPw, strHost, strPropxy);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Ask runtime permissions, such as record audio and camera
        // We don't need them here but once the user has granted them we won't have to ask again
        checkAndRequestCallPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // The best way to use Core listeners in Activities is to add them in onResume
        // and to remove them in onPause
        LinphoneManager.getCore().addListener(mCoreListener);

        // Manually update the LED registration state, in case it has been registered before
        // we add a chance to register the above listener
        ProxyConfig proxyConfig = LinphoneManager.getCore().getDefaultProxyConfig();
        if (proxyConfig != null) {
            updateLed(proxyConfig.getState());
        } else {
            // No account configured, we display the configuration activity
            // startActivity(new Intent(this, VoipConfigureAccountActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Like I said above, remove unused Core listeners in onPause
        LinphoneManager.getCore().removeListener(mCoreListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        // Callback for when permissions are asked to the user
        for (int i = 0; i < permissions.length; i++) {
            Log.i(
                    "[Permission] "
                            + permissions[i]
                            + " is "
                            + (grantResults[i] == PackageManager.PERMISSION_GRANTED
                            ? "granted"
                            : "denied"));
        }
    }

    private void updateLed(RegistrationState state) {
        switch (state) {
            case Ok: // This state means you are connected, to can make and receive calls & messages
                mLed.setImageResource(R.drawable.ic_voip_led_connected);
                break;
            case None: // This state is the default state
            case Cleared: // This state is when you disconnected
                mLed.setImageResource(R.drawable.ic_voip_led_disconnected);
                break;
            case Failed: // This one means an error happened, for example a bad password
                mLed.setImageResource(R.drawable.ic_voip_led_error);
                break;
            case Progress: // Connection is in progress, next state will be either Ok or Failed
                mLed.setImageResource(R.drawable.ic_voip_led_inprogress);
                break;
        }
    }

    private void checkAndRequestCallPermissions() {
        ArrayList<String> permissionsList = new ArrayList<>();

        // Some required permissions needs to be validated manually by the user
        // Here we ask for record audio and camera to be able to make video calls with sound
        // Once granted we don't have to ask them again, but if denied we can
        int recordAudio =
                getPackageManager()
                        .checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName());
        Log.i(
                "[Permission] Record audio permission is "
                        + (recordAudio == PackageManager.PERMISSION_GRANTED
                        ? "granted"
                        : "denied"));
        int camera =
                getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName());
        Log.i(
                "[Permission] Camera permission is "
                        + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for record audio");
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (camera != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for camera");
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            String[] permissions = new String[permissionsList.size()];
            permissions = permissionsList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }
}
