package vn.propzy.sipphone.libraries.linphone.my_custom;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Call.State;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Reason;
import org.linphone.core.tools.Log;

import java.util.ArrayList;

import vn.propzy.sipphone.R;
import vn.propzy.sipphone.libraries.linphone.LinphoneContext;
import vn.propzy.sipphone.libraries.linphone.LinphoneManager;
import vn.propzy.sipphone.libraries.linphone.utils.LinphoneUtils;


public class CustomCallOutgoingActivity extends AppCompatActivity implements OnClickListener {

    private TextView mCallTitle, mName, mNumber;
    private ImageView mMicro, mSpeaker, mHangUp, mPicture;
    private Call mCall;
    private CoreListenerStub mListener;
    private boolean mIsMicMuted, mIsSpeakerEnabled;

    private Chronometer mCallTimer;
    private CountDownTimer mCallUpdateCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.my_custom_call_outgoing);

        mCallTitle = findViewById(R.id.tvCallTitle);
        mCallTitle.setText("Đang chờ ...");
        mName = findViewById(R.id.tvContactName);
        mNumber = findViewById(R.id.tvContactNumber);
        mPicture = findViewById(R.id.ivContactPicture);
        mCallTimer = findViewById(R.id.currentCallTimer);

        mIsMicMuted = false;
        mIsSpeakerEnabled = false;

        mMicro = findViewById(R.id.ivMicro);
        mMicro.setOnClickListener(this);
        mSpeaker = findViewById(R.id.ivSpeaker);
        mSpeaker.setOnClickListener(this);

        mHangUp = findViewById(R.id.ivHangUp);
        mHangUp.setOnClickListener(this);

        mListener =
                new CoreListenerStub() {
                    @Override
                    public void onCallStateChanged(
                            Core core, Call call, State state, String message) {
                        if (state == State.Error) {
                            // Convert Core message for internalization
                            if (call.getErrorInfo().getReason() == Reason.Declined) {
                                Toast.makeText(
                                        CustomCallOutgoingActivity.this,
                                        getString(R.string.error_call_declined),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else if (call.getErrorInfo().getReason() == Reason.NotFound) {
                                Toast.makeText(
                                        // User not found
                                        CustomCallOutgoingActivity.this,
                                        getString(R.string.error_user_not_found),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else if (call.getErrorInfo().getReason() == Reason.NotAcceptable) {
                                Toast.makeText(
                                        CustomCallOutgoingActivity.this,
                                        getString(R.string.error_incompatible_media),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else if (call.getErrorInfo().getReason() == Reason.Busy) {
                                Toast.makeText(
                                        CustomCallOutgoingActivity.this,
                                        getString(R.string.error_user_busy),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else if (message != null) {
                                Log.e("TAG", "Unknown error - " + message);
                                Toast.makeText(
                                        CustomCallOutgoingActivity.this,
                                        getString(R.string.error_unknown) + " - " + message,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else if (state == State.End) {
                            // Convert Core message for internalization
                            if (call.getErrorInfo().getReason() == Reason.Declined) {
                                Toast.makeText(
                                        CustomCallOutgoingActivity.this,
                                        getString(R.string.error_call_declined),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else if (state == State.Connected) {
                            // This is done by the LinphoneContext listener now
                            // startActivity(new Intent(CallOutgoingActivity.this,
                            // CallActivity.class));
                        } else if (state == State.StreamsRunning) {

                            mCallTitle.setText("Đang gọi");
                            // Cap nhat, bat dau thoi gian goi
                            updateCurrentCallTimer();
                            // Da ket noi thanh cong cuoc goi, tinh gio goi
                            setCurrentCallContactInformation();
                        }

                        if (state == State.End || state == State.Released) {
                            finish();
                        }
                    }
                };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO: Check permission here
        // O day xin them quyen READ_PHONE_STATE
        // Note lai coi co that su can quyen nay khong
    }

    @Override
    protected void onResume() {
        super.onResume();
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.addListener(mListener);
        }

        mCall = null;

        // Only one call ringing at a time is allowed
        if (LinphoneManager.getCore() != null) {
            for (Call call : LinphoneManager.getCore().getCalls()) {
                State cstate = call.getState();
                if (State.OutgoingInit == cstate
                        || State.OutgoingProgress == cstate
                        || State.OutgoingRinging == cstate
                        || State.OutgoingEarlyMedia == cstate) {
                    mCall = call;
                    break;
                }
            }
        }
        if (mCall == null) {
            Log.e("[Call Outgoing Activity] Couldn't find outgoing call");
            finish();
            return;
        }

        setCurrentCallContactInformation();
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
        mMicro = null;
        mSpeaker = null;
        mCall = null;
        mListener = null;

        mCallTimer.stop();
        mCallTimer = null;

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

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
        if (id == R.id.ivHangUp) {
            decline();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (LinphoneContext.isReady()
                && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
            mCall.terminate();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decline() {
        mCall.terminate();
        finish();
    }

    private void updateCurrentCallTimer() {
        if (mCall == null) return;

        mCallTimer.setBase(SystemClock.elapsedRealtime() - 1000 * mCall.getDuration());
        mCallTimer.start();
    }

    private void setCurrentCallContactInformation() {
        if (mCall == null) return;

        Address address = mCall.getRemoteAddress();
        LinphoneContact contact =
                ContactsManager.getInstance().findContactFromAddress(address);

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
        // mNumber.setText(LinphoneUtils.getDisplayableAddress(address));
    }

}

