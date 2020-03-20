package vn.propzy.biphone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import vn.propzy.biphone.libraries.linphone.LinphoneManager
import vn.propzy.biphone.libraries.linphone.service.LinphoneService
import vn.propzy.biphone.libraries.linphone.service.ServiceWaitThread
import vn.propzy.biphone.libraries.linphone.service.ServiceWaitThreadListener

class MainActivity : AppCompatActivity(), ServiceWaitThreadListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if (LinphoneService.isReady()) {
            onServiceReady()
        } else {
            // -- Start Service --
            startService(
                Intent().setClass(this@MainActivity, LinphoneService::class.java)
            )
            ServiceWaitThread(this).start()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onServiceReady() {

        Toast.makeText(this,"onServiceReady", Toast.LENGTH_SHORT).show()

        LinphoneManager.getInstance().changeStatusToOnline()

        // Once the service is ready, we can move on in the application
        // We'll forward the intent action, type and extras so it can be handled
        // by the next activity if needed, it's not the launcher job to do that
        val intent = Intent(this, VoipMainActivity::class.java)
        startActivity(intent)
    }
}
