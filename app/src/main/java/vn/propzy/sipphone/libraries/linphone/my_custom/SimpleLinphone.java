package vn.propzy.sipphone.libraries.linphone.my_custom;

import org.linphone.core.AVPFMode;
import org.linphone.core.AccountCreator;
import org.linphone.core.Address;
import org.linphone.core.AuthInfo;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.ProxyConfig;
import org.linphone.core.TransportType;
import org.linphone.core.tools.Log;

import java.util.List;

import vn.propzy.sipphone.libraries.linphone.LinphoneManager;


public class SimpleLinphone {

    private static final String TAG = "SimpleLinphone";
    private static SimpleLinphone sInstance = null;
    // private Context mContext;

    private ProxyConfig mProxyConfig;
    private AuthInfo mAuthInfo;

    private LinphoneContact currentCallContact;

    public static SimpleLinphone instance() {
        if (sInstance == null) {
            sInstance = new SimpleLinphone();
        }
        return sInstance;
    }

    private SimpleLinphone() {

    }

    public void registerUserAuth(String name, String password, String host, String proxy) {
        Log.e(TAG, "registerUserAuth name = " + name);
        Log.e(TAG, "registerUserAuth pw = " + password);
        Log.e(TAG, "registerUserAuth host = " + host);

        Core mCore = LinphoneManager.getCore();
        if (mCore == null) {
            Log.e(TAG, "LinphoneManager core == null");
            return;
        }

//        String identify = "sip:" + name + "@" + host;
//        proxy = "sip:" + host;

        AccountCreator mAccountCreator = mCore.createAccountCreator(null);

        // At least the 3 below values are required
        mAccountCreator.setUsername(name);
        mAccountCreator.setPassword(password);
        mAccountCreator.setDomain(host);
        mAccountCreator.setTransport(TransportType.Udp);

        // This will automatically create the proxy config and auth info and add them to the Core
        ProxyConfig cfg = mAccountCreator.createProxyConfig();
        cfg.setAvpfMode(AVPFMode.Enabled);
        cfg.setAvpfRrInterval(0);
        cfg.enableQualityReporting(false);
        cfg.setQualityReportingCollector(null);
        cfg.setQualityReportingInterval(0);
        cfg.enableRegister(true);


        // String identify = "sip:" + strName + "@" + strHost;
        // String proxy = "sip:" + host;

        // Make identify
//        Address identifyAdd = Factory.instance().createAddress(identify);
//        cfg.setIdentityAddress(identifyAdd);
//
        cfg.setRoute(proxy);
        cfg.setServerAddr(proxy);

        // Make sure the newly created one is the default
        mCore.setDefaultProxyConfig(cfg);

        Log.i("getIdentityAddress = " + cfg.getIdentityAddress().asStringUriOnly());
        Log.i("getRoutes = " + cfg.getRoutes()[0]);
        Log.i("getServerAddr = " + cfg.getServerAddr());


//        LinphoneAddress proxyAddr = LinphoneCoreFactory.instance().createLinphoneAddress(proxy);
//        LinphoneAddress identifyAddr = LinphoneCoreFactory.instance().createLinphoneAddress(identify);
//        LinphoneAuthInfo authInfo = LinphoneCoreFactory.instance().createAuthInfo(name, null, password,
//                null, null, host);
//        LinphoneProxyConfig prxCfg = mLinphoneCore.createProxyConfig(identifyAddr.asString(),
//                proxyAddr.asStringUriOnly(), proxyAddr.asStringUriOnly(), true);
//        prxCfg.enableAvpf(false);
//        prxCfg.setAvpfRRInterval(0);
//        prxCfg.enableQualityReporting(false);
//        prxCfg.setQualityReportingCollector(null);
//        prxCfg.setQualityReportingInterval(0);
//        prxCfg.enableRegister(true);
//        mLinphoneCore.addProxyConfig(prxCfg);
//        mLinphoneCore.addAuthInfo(authInfo);
//        mLinphoneCore.setDefaultProxyConfig(prxCfg);
    }

//    public void removeAuthConfig() {
//        Core mCore = LinphoneManager.getCore();
//        if (mCore == null) return;
//        // Remove old config
//        mCore.removeProxyConfig(mCore.getDefaultProxyConfig());
//    }

    public void removeAccount() {
        mProxyConfig = null;
        mAuthInfo = null;
        Core mCore = LinphoneManager.getCore();
        if (mCore == null) return;

        ProxyConfig mProxyConfig = mCore.getDefaultProxyConfig();
        mAuthInfo = mProxyConfig.findAuthInfo();

        if (mProxyConfig != null) {
            mCore.removeProxyConfig(mProxyConfig);
        }
        if (mAuthInfo != null) {
            mCore.removeAuthInfo(mAuthInfo);
        }
    }

    public void startSingleCallingTo(LinphoneContact contact) {
        Core mCore = LinphoneManager.getCore();
        if (mCore == null) return;

        Address addressToCall = mCore.interpretUrl(contact.getCallValue());
        addressToCall.setDisplayName(contact.getFullName());

        CallParams params = mCore.createCallParams(null);
        params.enableVideo(false);

        if (addressToCall == null) {
            Log.e("addressToCall == null");
            return;
        }
        mCore.inviteAddressWithParams(addressToCall, params);


//        Core mCore = LinphoneManager.getCore();
//        if (mCore == null) return;
//
//        // Save current call
//        currentCallContact = contact;
//
//        Address address = mCore.interpretUrl("ten@domain.sip.com");
//        address.setDisplayName("display name");
////        Address address = mCore.interpretUrl(bean.getUserName() + "@" + bean.getHost());
////        address.setDisplayName(bean.getDisplayName());
//        LinphoneManager.getCallManager().inviteAddress(address, false);

//        Core mCore = LinphoneService.getCore();
//        if (mCore == null) return null;
//
//        Address address;
//
//        address = mCore.interpretUrl(bean.getUserName() + "@" + bean.getHost());
//
//        address.setDisplayName(bean.getDisplayName());
//        CallParams params = mCore.createCallParams(null);
//        return mCore.inviteAddressWithParams(address, params);
    }

    public void setListContact(List<LinphoneContact> contacts) {
        ContactsManager.getInstance().setListSipContact(contacts);
    }


}
