package asdev.amansoft.com.colorclick;

import com.google.firebase.auth.FirebaseAuth;

public class check {


    //Checks if the User is Signed in or not
    public boolean isValidUser()
    {
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            return false;
        else
            return true;
    }

}
