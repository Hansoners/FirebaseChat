package hanson.android.firebasechat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionsAdapter extends FragmentPagerAdapter {

    public SectionsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int i) {

        switch (i) {
            case 0:
                return "Requests";
            case 1:
                return "Chats";
            case 2:
                return "Friends";
            default:
                return null;
        }
    }
}
