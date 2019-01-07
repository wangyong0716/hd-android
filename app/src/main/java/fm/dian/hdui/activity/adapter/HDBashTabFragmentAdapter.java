package fm.dian.hdui.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.fragment.HDTab1Fragment;
import fm.dian.hdui.fragment.HDTab2Fragment;
import fm.dian.hdui.fragment.HDTab3Fragment;

public class HDBashTabFragmentAdapter extends FragmentPagerAdapter {
    public final static int TAB_COUNT = 3;

    List<Fragment> tabsFragment = new ArrayList<Fragment>();

    public HDBashTabFragmentAdapter(FragmentManager fm, HDBaseTabFragmentActivity baseTab) {
        super(fm);
        tabsFragment.add(new HDTab1Fragment());
        tabsFragment.add(new HDTab2Fragment());
        tabsFragment.add(new HDTab3Fragment());
    }

    @Override
    public Fragment getItem(int id) {
        switch (id) {
            case HDBaseTabFragmentActivity.TAB_INDEX:
                return tabsFragment.get(0);
            case HDBaseTabFragmentActivity.TAB_HONGDIAN:
                return tabsFragment.get(1);
            case HDBaseTabFragmentActivity.TAB_ME:
                return tabsFragment.get(2);
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
