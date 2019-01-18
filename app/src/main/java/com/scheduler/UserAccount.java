package com.scheduler;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class UserAccount {

    private Context context;
    private GoogleSignInAccount acct;

    public UserAccount(Context context) {
        this.context = context;
        getAccountInfo();
    }

    private void getAccountInfo() {
        acct = GoogleSignIn.getLastSignedInAccount(context);
    }

    public String getPersonEmail() {
        return acct.getEmail();
    }

    public String getName() {
        return acct.getGivenName();
    }

}
