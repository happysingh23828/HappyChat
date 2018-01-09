package happysingh.thehappychat;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import javax.crypto.spec.DESedeKeySpec;

/**
 * Created by Happy-Singh on 12/30/2017.
 */

class FragmenAdapter extends FragmentPagerAdapter {
    public FragmenAdapter(FragmentManager fm) {
        super(fm);
    }


    // Switching Between Fragments for main Activity
    @Override
    public android.support.v4.app.Fragment getItem(int position) {

        switch (position) {
            case 2:
                requestfragment requestfragment = new requestfragment();
                return  requestfragment;

            case 0:
                chatsfragment chatsfragment = new chatsfragment();
                return  chatsfragment;

            case 1:
                friendsfragment friendsfragment = new friendsfragment();
                return  friendsfragment;

             default:
                 return  null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }


    //Setting Tab Heading On Them To Work With Fragments
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 2:
                return "REQUESTS";

            case 0 :
                return "CHATS";
            case  1:
                return  "FRIENDS";
            default:
                return null;

        }
    }
}
