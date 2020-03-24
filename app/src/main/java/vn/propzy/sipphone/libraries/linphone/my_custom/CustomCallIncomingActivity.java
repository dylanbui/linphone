package vn.propzy.sipphone.libraries.linphone.my_custom;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.tools.Log;

import java.util.ArrayList;

import vn.propzy.sipphone.R;
import vn.propzy.sipphone.libraries.linphone.LinphoneContext;
import vn.propzy.sipphone.libraries.linphone.LinphoneManager;
import vn.propzy.sipphone.libraries.linphone.compatibility.Compatibility;
import vn.propzy.sipphone.libraries.linphone.utils.LinphoneUtils;


public class CustomCallIncomingActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mCallTitle, mName, mNumber;
    private ImageView mMicro, mSpeaker, mHangUp, mPicture;
    private Chronometer mCallTimer;
    private ViewGroup menuWaiting, menuAnswer;
    private boolean mIsMicMuted, mIsSpeakerEnabled;

    private Call mCall;
    private CoreListenerStub mListener;
    private boolean mAlreadyAcceptedOrDeniedCall; // Tranh cho bam nhieu lan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Compatibility.setShowWhenLocked(this, true);
        Compatibility.setTurnScreenOn(this, true);

        setContentView(R.layout.my_custom_call_incoming);

        menuWaiting = findViewById(R.id.menuWaiting);
        menuAnswer = findViewById(R.id.menuAnswer);

        mCallTitle = findViewById(R.id.tvCallTitle);
        mCallTitle.setText("Đang chờ ...");
        mName = findViewById(R.id.tvContactName);
        mNumber = findViewById(R.id.tvContactNumber);
        mPicture = findViewById(R.id.ivContactPicture);
        mCallTimer = findViewById(R.id.currentCallTimer);

        // For Answer
        mIsMicMuted = false;
        mIsSpeakerEnabled = false;

        mMicro = findViewById(R.id.ivMicro);
        mMicro.setOnClickListener(this);
        mSpeaker = findViewById(R.id.ivSpeaker);
        mSpeaker.setOnClickListener(this);

        mHangUp = findViewById(R.id.ivHangUp);
        mHangUp.setOnClickListener(this);

        // For Waiting

        Button mAccept = findViewById(R.id.btnAnswer);
        mAccept.setOnClickListener(this);
        Button mDecline = findViewById(R.id.btnDecline);
        mDecline.setOnClickListener(this);

        lookupCurrentCall();

        mListener = new CoreListenerStub() {
            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                if (state == Call.State.End || state == Call.State.Released) {
                    mCall = null;
                    finish();
                } else if (state == Call.State.Connected) {


                } else if (state == Call.State.StreamsRunning) {
                    // Da tra loi cuoc goi
                    // Cap nhat gia dien goi
                    // Cap nhat, bat dau thoi gian goi

                    mAlreadyAcceptedOrDeniedCall = false;

                    mCallTitle.setText("Đang gọi");

                    // Hide menu waiting
                    menuWaiting.setVisibility(View.GONE);
                    // Show menu answer
                    menuAnswer.setVisibility(View.VISIBLE);
                    // Da ket noi thanh cong cuoc goi, tinh gio goi
                    updateCurrentCallTimer();

                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO: Check permission here
    }

    @Override
    protected void onResume() {
        super.onResume();
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.addListener(mListener);
        }

        mAlreadyAcceptedOrDeniedCall = false;
        mCall = null;

        // Only one call ringing at a time is allowed
        lookupCurrentCall();
        if (mCall == null) {
            // The incoming call no longer exists.
            Log.d("Couldn't find incoming call");
            finish();
            return;
        }

        setCurrentCallContactInformation();

//        if (LinphonePreferences.instance().acceptIncomingEarlyMedia()) {
//            if (mCall.getCurrentParams() != null && mCall.getCurrentParams().videoEnabled()) {
//                findViewById(R.id.avatar_layout).setVisibility(View.GONE);
//                mCall.getCore().setNativeVideoWindowId(mVideoDisplay);
//            }
//        }
    }

    @Override
    protected void onPause() {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(mListener);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mName = null;
        mNumber = null;
        mCall = null;
        mListener = null;
        // mVideoDisplay = null;

        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnAnswer) {
            answer();
        }

        if (id == R.id.ivMicro) {
            mIsMicMuted = !mIsMicMuted;
            mMicro.setSelected(mIsMicMuted);
            LinphoneManager.getCore().enableMic(!mIsMicMuted);
        }

        if (id == R.id.ivSpeaker) {
            mIsSpeakerEnabled = !mIsSpeakerEnabled;
            mSpeaker.setSelected(mIsSpeakerEnabled);
            if (mIsSpeakerEnabled) {
                LinphoneManager.getAudioManager().routeAudioToSpeaker();
            } else {
                LinphoneManager.getAudioManager().routeAudioToEarPiece();
            }
        }

        if (id == R.id.ivHangUp || id == R.id.btnDecline) {
            decline();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (LinphoneContext.isReady()
                && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
                && mCall != null) {
            mCall.terminate();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void lookupCurrentCall() {
        if (LinphoneManager.getCore() != null) {
            for (Call call : LinphoneManager.getCore().getCalls()) {
                if (Call.State.IncomingReceived == call.getState()
                        || Call.State.IncomingEarlyMedia == call.getState()) {
                    mCall = call;
                    break;
                }
            }
        }
    }

    private void decline() {
        if (mAlreadyAcceptedOrDeniedCall) {
            return;
        }
        mAlreadyAcceptedOrDeniedCall = true;

        if (mCall != null) mCall.terminate();
        finish();
    }

    private void answer() {
        // Sau khi chap nhan cuoc goi, no se chuyen sang trang thai Connected va StreamsRunning
        // Sau do goi CallActivity
        // Ta phai xu ly luon ngay tai day, cho no nghe tai day luon
        // Ta se chuyen thằng CallActivity lai giong thang CallOutComming de co the dung chung cach xu ly

        if (mAlreadyAcceptedOrDeniedCall) {
            return;
        }
        mAlreadyAcceptedOrDeniedCall = true;

        if (!LinphoneManager.getCallManager().acceptCall(mCall)) {
            // the above method takes care of Samsung Galaxy S
            Toast.makeText(this, R.string.couldnt_accept_call, Toast.LENGTH_LONG).show();
        }
    }

    private void updateCurrentCallTimer() {
        if (mCall == null) return;

        mCallTimer.setBase(SystemClock.elapsedRealtime() - 1000 * mCall.getDuration());
        mCallTimer.start();
    }


    private void setCurrentCallContactInformation() {

        if (mCall == null) return;

        Address address = mCall.getRemoteAddress();
        LinphoneContact contact = ContactsManager.getInstance().findContactFromAddress(address);

        if (contact != null) {
            Uri photo = contact.getPhotoUri();
            if (photo != null) {
                Log.e("photo : " + photo.toString());
                Glide.with(this).load(photo.toString()).into(mPicture);
            }
//            ContactAvatar.displayAvatar(contact, mContactAvatar, true);
            mName.setText(contact.getFullName());
        } else {
            String displayName = LinphoneUtils.getAddressDisplayName(address);
//            ContactAvatar.displayAvatar(displayName, mContactAvatar, true);
            mName.setText(displayName);
        }
        // Display phone number at here
        // mNumber.setText(address.asStringUriOnly());
    }


}
