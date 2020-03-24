package vn.propzy.sipphone.libraries.linphone.my_custom;

import android.net.Uri;

import org.linphone.core.Friend;

import java.io.Serializable;

public class LinphoneContact implements Serializable, Comparable<LinphoneContact> {

    private static final long serialVersionUID = 9015568163905205244L;

    private transient Friend mFriend;
    private String mFullName, mFirstName, mLastName, mOrganization;
    private transient Uri mPhotoUri, mThumbnailUri;

    private boolean mHasSipAddress;
    private boolean mIsStarred;

    private Integer mUserId, mVoipId;
    private String mPhoneNumber;


    public LinphoneContact() {
        super();
        mUserId = 0;
        mVoipId = 0;
        mFullName = null;
        mFirstName = null;
        mLastName = null;
        mOrganization = null;
        //mAndroidId = null;
        mThumbnailUri = null;
        //mAddresses = new ArrayList<>();
        mPhotoUri = null;
        mHasSipAddress = false;
        mIsStarred = false;
    }

    public LinphoneContact(Integer userId, String phoneNumber, String fullName, Boolean hasSipAddress) {
        this();
        mUserId = userId;
        if (hasSipAddress) {
            mVoipId = Integer.parseInt(phoneNumber);
        } else {
            mPhoneNumber = phoneNumber;
        }
        mFullName = fullName;
        mHasSipAddress = hasSipAddress;
    }

    public LinphoneContact(Integer userId, Integer voipId, String fullName) {
        this();
        mUserId = userId;
        mVoipId = voipId;
        mFullName = fullName;
        mHasSipAddress = true;
    }

    public static LinphoneContact createContact() {
        LinphoneContact contact = new LinphoneContact();

//        if (ContactsManager.getInstance().hasReadContactsAccess()) {
//            contact.createAndroidContact();
//
//        } else {
//            contact.createFriend();
//        }
        return contact;
    }

    public String getContactId() {

//        if (isAndroidContact()) {
//            return getAndroidId();
//        } else {
//            // TODO
//        }
        return null;
    }

    @Override
    public int compareTo(LinphoneContact contact) {
        return mUserId.compareTo(contact.getUserId());
//        String fullName = getFullName() != null ? getFullName() : "";
//        String contactFullName = contact.getFullName() != null ? contact.getFullName() : "";
//        if (fullName.equals(contactFullName)) {
//            if (getAndroidId() != null) {
//                if (contact.getAndroidId() != null) {
//                    int idComp = getAndroidId().compareTo(contact.getAndroidId());
//                    if (idComp == 0) return 0;
//                    List<LinphoneNumberOrAddress> noas1 = getNumbersOrAddresses();
//
//                    List<LinphoneNumberOrAddress> noas2 = contact.getNumbersOrAddresses();
//
//                    if (noas1.size() == noas2.size()) {
//                        if (noas1.containsAll(noas2) && noas2.containsAll(noas1)) {
//                            return 0;
//                        }
//                        return -1;
//                    }
//                    return Integer.compare(noas1.size(), noas2.size());
//                }
//                return -1;
//            }
//            if (contact.getAndroidId() != null) return 1;
//            return 0;
//        }
//        return fullName.compareTo(contactFullName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != LinphoneContact.class) return false;
        LinphoneContact contact = (LinphoneContact) obj;
        return (this.compareTo(contact) == 0);
    }

    public Integer getUserId() {
        return mUserId;
    }

    public Integer getVoipId() {
        return mVoipId;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getCallValue() {
        if (mHasSipAddress) {
            return mVoipId.toString();
        }
        return mPhoneNumber;
    }

    public String getSipFormatAddress() {
        if (mHasSipAddress) {
            return "sip:" + mVoipId.toString();
        }
        return "sip:" + mPhoneNumber;
    }

    /*
       Name related
    */

    public String getFullName() {
        return mFullName == null ? "unknown" : mFullName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    /*
       Picture related
    */

    public Uri getPhotoUri() {
        return mPhotoUri;
    }

    public void setPhotoUri(Uri uri) {
        if (uri != null && uri.equals(mPhotoUri)) return;
        mPhotoUri = uri;
    }

    public Uri getThumbnailUri() {
        return mThumbnailUri;
    }

    public void setThumbnailUri(Uri uri) {
        if (uri != null && uri.equals(mThumbnailUri)) return;
        mThumbnailUri = uri;
    }

    public boolean isFavourite() {
        return false;
        //return mIsStarred;
    }

    public String getContactFromPresenceModelForUriOrTel(String uri) {
        if (mFriend != null && mFriend.getPresenceModelForUriOrTel(uri) != null) {
            return mFriend.getPresenceModelForUriOrTel(uri).getContact();
        }
        return null;
    }

    public boolean hasAddress() {
        return mHasSipAddress;
    }

}
