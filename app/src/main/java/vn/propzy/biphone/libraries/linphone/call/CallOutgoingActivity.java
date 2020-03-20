package vn.propzy.biphone.libraries.linphone.call;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Call.State;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Reason;
import org.linphone.core.tools.Log;

import java.util.ArrayList;

import vn.propzy.biphone.R;
import vn.propzy.biphone.libraries.linphone.LinphoneContext;
import vn.propzy.biphone.libraries.linphone.LinphoneManager;
import vn.propzy.biphone.libraries.linphone.activities.LinphoneGenericActivity;
import vn.propzy.biphone.libraries.linphone.my_custom.ContactsManager;
import vn.propzy.biphone.libraries.linphone.my_custom.LinphoneContact;
import vn.propzy.biphone.libraries.linphone.settings.LinphonePreferences;
import vn.propzy.biphone.libraries.linphone.utils.LinphoneUtils;
import vn.propzy.biphone.libraries.linphone.views.ContactAvatar;


public class CallOutgoingActivity extends AppCompatActivity {

}
