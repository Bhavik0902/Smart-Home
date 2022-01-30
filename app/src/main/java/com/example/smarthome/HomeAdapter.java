package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomeAdapter extends FragmentStateAdapter {

    public HomeAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Door();
            case 1:
                return new Temperature();
        }
        return new Door();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
