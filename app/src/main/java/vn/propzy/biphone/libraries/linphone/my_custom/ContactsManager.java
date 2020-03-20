package vn.propzy.biphone.libraries.linphone.my_custom;

import android.content.Context;

import org.linphone.core.Address;
import org.linphone.core.Friend;
import org.linphone.core.FriendList;
import org.linphone.core.FriendListListener;
import org.linphone.core.MagicSearch;

import java.util.ArrayList;
import java.util.List;

import vn.propzy.biphone.libraries.linphone.LinphoneContext;


public class ContactsManager implements FriendListListener, LinphoneContext.CoreStartedListener {

    private List<LinphoneContact> mContacts, mSipContacts;
    // private final ArrayList<ContactsUpdatedListener> mContactsUpdatedListeners;
    private MagicSearch mMagicSearch;
    private boolean mContactsFetchedOnce = false;
    private Context mContext;
    // private AsyncContactsLoader mLoadContactTask;
    private boolean mInitialized = false;

    public static ContactsManager getInstance() {
        return LinphoneContext.instance().getContactsManager();
    }

    public ContactsManager(Context context) {
//        super(new Handler(Looper.getMainLooper()));
//        mContext = context;
//        mContactsUpdatedListeners = new ArrayList<>();
        mContacts = new ArrayList<>();
        mSipContacts = new ArrayList<>();
//
//        if (LinphoneManager.getCore() != null) {
//            mMagicSearch = LinphoneManager.getCore().createMagicSearch();
//            mMagicSearch.setLimitedSearch(false); // Do not limit the number of results
//        }

        LinphoneContext.instance().addCoreStartedListener(this);
    }

    public void initializeContactManager() {
//        if (!mInitialized) {
//            if (mContext.getResources().getBoolean(R.bool.use_linphone_tag)) {
//                if (hasReadContactsAccess()
//                        && hasWriteContactsAccess()
//                        && hasWriteSyncPermission()) {
//                    if (LinphoneContext.isReady()) {
//                        initializeSyncAccount();
//                        mInitialized = true;
//                    }
//                }
//            }
//        }
    }

    public void setListSipContact(List<LinphoneContact> sipContacts) {
        mSipContacts = sipContacts;
    }

//    public void addContactsListener(ContactsUpdatedListener listener) {
//        mContactsUpdatedListeners.add(listener);
//    }
//
//    public void removeContactsListener(ContactsUpdatedListener listener) {
//        mContactsUpdatedListeners.remove(listener);
//    }
//
//    public ArrayList<ContactsUpdatedListener> getContactsListeners() {
//        return mContactsUpdatedListeners;
//    }

    public synchronized LinphoneContact findContactFromAndroidId(String androidId) {
        if (androidId == null) {
            return null;
        }

//        for (LinphoneContact c : getContacts()) {
//            if (c.getAndroidId() != null && c.getAndroidId().equals(androidId)) {
//                return c;
//            }
//        }
        return null;
    }

    public synchronized LinphoneContact findContactFromAddress(Address address) {
        if (address == null) return null;

        String[] arr = address.asStringUriOnly().split("@");

        for (LinphoneContact contact : mSipContacts) {
            if (contact.getVoipId() == Integer.getInteger(arr[0])) {
                return contact;
            }
        }
        return null;

        // address.asStringUriOnly()
//        Core core = LinphoneManager.getCore();
//
//        Friend lf = core.findFriend(address);
//        if (lf != null) {
//            return (LinphoneContact) lf.getUserData();
//        }

//        String username = address.getUsername();
//        if (username == null) {
//            Log.w("[Contacts Manager] Address ", address.asString(), " doesn't have a username!");
//            return null;
//        }
//
//        if (android.util.Patterns.PHONE.matcher(username).matches()) {
//            return findContactFromPhoneNumber(username);
//        }
//        return null;
    }

    public synchronized LinphoneContact findContactFromPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        return null;

//        if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
//            Log.w(
//                    "[Contacts Manager] Expected phone number but doesn't look like it: "
//                            + phoneNumber);
//            return null;
//        }
//
//        Core core = LinphoneManager.getCore();
//        ProxyConfig lpc = null;
//        if (core != null) {
//            lpc = core.getDefaultProxyConfig();
//        }
//        if (lpc == null) {
//            Log.i("[Contacts Manager] Couldn't find default proxy config...");
//            return null;
//        }
//
//        String normalized = lpc.normalizePhoneNumber(phoneNumber);
//        if (normalized == null) {
//            Log.w(
//                    "[Contacts Manager] Couldn't normalize phone number "
//                            + phoneNumber
//                            + ", default proxy config prefix is "
//                            + lpc.getDialPrefix());
//            normalized = phoneNumber;
//        }
//
//        Address addr = lpc.normalizeSipUri(normalized);
//        if (addr == null) {
//            Log.w("[Contacts Manager] Couldn't normalize SIP URI " + normalized);
//            return null;
//        }
//
//        // Without this, the hashmap inside liblinphone won't find it...
//        addr.setUriParam("user", "phone");
//        Friend lf = core.findFriend(addr);
//        if (lf != null) {
//            return (LinphoneContact) lf.getUserData();
//        }
//
//        Log.w("[Contacts Manager] Couldn't find friend...");
//        return null;
    }

    public void destroy() {
//        mContext.getContentResolver().unregisterContentObserver(this);
//        LinphoneContext.instance().removeCoreStartedListener(this);
//
//        if (mLoadContactTask != null) {
//            mLoadContactTask.cancel(true);
//        }
//        // LinphoneContact has a Friend field and Friend can have a LinphoneContact has userData
//        // Friend also keeps a ref on the Core, so we have to clean them
//        for (LinphoneContact c : mContacts) {
//            c.setFriend(null);
//        }
//        mContacts.clear();
//        for (LinphoneContact c : mSipContacts) {
//            c.setFriend(null);
//        }
//        mSipContacts.clear();
//
//        Core core = LinphoneManager.getCore();
//        if (core != null) {
//            for (FriendList list : core.getFriendsLists()) {
//                list.removeListener(this);
//            }
//        }
    }


    // -- FriendListListener --------------------

    @Override
    public void onCoreStarted() {

    }

    @Override
    public void onContactUpdated(FriendList friendList, Friend friend, Friend friend1) {

    }

    @Override
    public void onPresenceReceived(FriendList friendList, Friend[] friends) {

    }

    @Override
    public void onSyncStatusChanged(FriendList friendList, FriendList.SyncStatus syncStatus, String s) {

    }

    @Override
    public void onContactCreated(FriendList friendList, Friend friend) {

    }

    @Override
    public void onContactDeleted(FriendList friendList, Friend friend) {

    }
}
