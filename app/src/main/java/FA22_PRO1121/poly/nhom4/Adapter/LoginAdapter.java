package FA22_PRO1121.poly.nhom4.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import FA22_PRO1121.poly.nhom4.Fragment.LoginTabFragment;
import FA22_PRO1121.poly.nhom4.Fragment.SignUpTabFragment;

public class LoginAdapter extends FragmentStateAdapter {

    public LoginAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @Override
    public int getItemCount() {
        return 2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new SignUpTabFragment();
            default:
                return new LoginTabFragment();
        }
    }
}
