package vn.propzy.sipphone

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import vn.propzy.sipphone.libraries.linphone.LinphoneManager
import vn.propzy.sipphone.libraries.linphone.my_custom.LinphoneContact
import vn.propzy.sipphone.libraries.linphone.my_custom.SimpleLinphone
import vn.propzy.sipphone.libraries.linphone.service.LinphoneService
import vn.propzy.sipphone.libraries.linphone.service.ServiceWaitThread
import vn.propzy.sipphone.libraries.linphone.service.ServiceWaitThreadListener

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

        SimpleLinphone.instance().setListContact(demoContacts())

        // Once the service is ready, we can move on in the application
        // We'll forward the intent action, type and extras so it can be handled
        // by the next activity if needed, it's not the launcher job to do that
        val intent = Intent(this, VoipMainActivity::class.java)
        startActivity(intent)
    }

    private fun demoContacts(): List<LinphoneContact> {

        val uri = Uri.parse("https://i.picsum.photos/id/300/300/300.jpg")

        val contact1 = LinphoneContact(826, 826, "tel 826")
        contact1.photoUri = uri
        val contact2 = LinphoneContact(827, 827, "tel 827")
        contact2.photoUri = uri
        val contact3 = LinphoneContact(1, "0988818597", "tel Duc Bui")
        contact3.photoUri = uri
        val contact4 = LinphoneContact(1, "0855562000", "tel Tin Luong")
        contact4.photoUri = uri

        return arrayListOf(contact1, contact2, contact3, contact4)
    }


}
