package nat.pink.base.ui.home;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import nat.pink.base.MainActivity;
import nat.pink.base.R;
import nat.pink.base.adapter.ListUserAdapter;
import nat.pink.base.base.BaseFragment;
import nat.pink.base.dao.DatabaseController;
import nat.pink.base.databinding.FragmentListUserBinding;

public class FragmentListUser extends BaseFragment<FragmentListUserBinding, HomeViewModel> {

    @NonNull
    @Override
    public HomeViewModel getViewModel() {
        return new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    private ListUserAdapter listUserAdapter;

    @Override
    public void initView() {
        super.initView();
        binding.layoutActionBar.ivCreateNewMessenger.setVisibility(View.VISIBLE);
        binding.layoutActionBar.txtAction.setVisibility(View.GONE);
        binding.layoutActionBar.txtTitle.setText(getString(R.string.fake_messenger_title));
        binding.rcvContent.setLayoutManager(new LinearLayoutManager(requireContext()));
//        listUserAdapter = new ListUserAdapter(requireContext(), objectUser -> ((MainActivity) getActivity()).addChildFragment(new FragmentChat(objectUser), FragmentChat.class.getSimpleName(), ));
        listUserAdapter = new ListUserAdapter(requireContext(), objectUser -> {
            addFragment(new FragmentChat(objectUser), FragmentChat.class.getSimpleName());
        }, objectUser -> {
        });
        binding.rcvContent.setAdapter(listUserAdapter);
    }

    @Override
    public void initData() {
        super.initData();
        loadData();
        getViewModel().reloadDataUser.observe(requireActivity(), aBoolean -> {
            if (aBoolean && isAdded())
                loadData();
        });
    }

    private void loadData() {
        getViewModel().getAllUser(requireContext(), o -> {
            listUserAdapter.setListObject(getViewModel().objectUsers);
            binding.rcvContent.setVisibility(getViewModel().objectUsers.size() > 0 ? View.VISIBLE : View.GONE);
            binding.llEmpty.setVisibility(getViewModel().objectUsers.size() > 0 ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void initEvent() {
        super.initEvent();
        binding.llContent.setOnClickListener(v -> {
        });
        binding.layoutActionBar.ivCreateNewMessenger.setOnClickListener(v -> addFragment(new HomeFragment(), HomeFragment.class.getSimpleName()));
    }
}

