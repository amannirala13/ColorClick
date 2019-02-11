package asdev.amansoft.com.colorclick;

import android.widget.EditText;

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





    public boolean isValidPhone(String phone) {
        boolean validity = false;
        if(phone.length()<=15 && phone.length()>=5)
            validity=true;
        return validity;
    }







    public boolean countryCodeExisting(EditText editText) {
        String currentValue = editText.getText().toString();
        if(currentValue.equals(""))
            return false;

        else if (currentValue.charAt(0) == '+')
        {

            if (currentValue.length() < 3)
                return false;

            else
            {
                int endindex=-1;
                for(int x = 0; x<currentValue.length();x++)
                {
                    if(currentValue.charAt(x)==' ')
                    {endindex = x; break;}
                }
                if(endindex==-1)
                    return false;
                else
                {
                    String dialCode = currentValue.substring(1, endindex);
                    // Toast.makeText(this, dialCode+ endindex, Toast.LENGTH_SHORT).show();
                    if (new CountryCode().isValidDialCode(dialCode))

                        return true;

                    else

                        return false;

                }
            }
        }
        else
            return false;

    }


}
