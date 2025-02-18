package me.adamoflynn.dynalarm.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import me.adamoflynn.dynalarm.SettingsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    // Get list of all the fragments to add to the adapter -> Alarm, Analysis, and Settings
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);

    }

    @Override
    public Fragment getItem(int position) {
        // If user is in the settings fragment, return the settings fragment.
        if(position == 2){
            return new SettingsFragment();
        }
        else {
            return mFragmentList.get(position);
        }
    }

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }


    public void addFragment(Fragment fragment, String one)
    {
        mFragmentList.add(fragment);
    }


    @Override
    public CharSequence getPageTitle(int position)
    {
        return null;
    }


}