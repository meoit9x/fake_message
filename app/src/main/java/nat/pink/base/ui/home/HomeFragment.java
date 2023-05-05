package nat.pink.base.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.util.Consumer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nat.pink.base.MainActivity;
import nat.pink.base.R;
import nat.pink.base.base.BaseFragment;
import nat.pink.base.customView.ExtTextView;
import nat.pink.base.dao.DatabaseController;
import nat.pink.base.databinding.HomeFragmentBinding;
import nat.pink.base.dialog.DialogErrorLink;
import nat.pink.base.dialog.DialogLoading;
import nat.pink.base.dialog.DialogShowError;
import nat.pink.base.dialog.DialogShowTimer;
import nat.pink.base.model.ObjectCalling;
import nat.pink.base.model.ObjectSpin;
import nat.pink.base.model.ObjectUser;
import nat.pink.base.model.ObjectsContentSpin;
import nat.pink.base.utils.Config;
import nat.pink.base.utils.Const;
import nat.pink.base.utils.PreferenceUtil;
import nat.pink.base.utils.StringUtils;
import nat.pink.base.utils.UriUtils;
import nat.pink.base.utils.Utils;

public class HomeFragment extends BaseFragment<HomeFragmentBinding, HomeViewModel> implements View.OnClickListener{
    public static final String TAG = "HomeFragment";

    @NonNull
    @Override
    public HomeViewModel getViewModel() {
        return new ViewModelProvider(getActivity()).get(HomeViewModel.class);
    }

    private boolean isEdit = false;
    private ObjectUser objectUser = new ObjectUser();
    private int checkedId = 0;

    @Override
    public void initView() {
        super.initView();
        binding.swStatus.setChecked(true);
        binding.layoutActionBar.txtAction.setText(getResources().getText(R.string.done));
        binding.layoutActionBar.txtTitle.setText(getResources().getText(R.string.make_message_title));
    }

    @Override
    public void initData() {
        super.initData();
        if (objectUser != null && objectUser.getId() != 0) {
            // set name
            binding.edtName.setText(objectUser.getName());
            // set lives
            binding.edtLives.setText(objectUser.getLiveIn());
            // check uri image
            Uri image = Uri.parse(objectUser.getAvatar());
            if (image != null)
                Glide.with(requireContext()).load(image).into(binding.ivChangeAva);
            else
                Glide.with(requireContext()).load(R.drawable.ic_change_ava).into(binding.ivChangeAva);
        } else {
            checkedId = binding.rb5Min.getId();
            getIndexChecked();
            objectUser.setStatus(0);
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();
        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                objectUser.setName(charSequence.toString());
                binding.edtError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.edtLives.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                objectUser.setLiveIn(charSequence.toString());
                binding.edtErrorLive.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.extChangeAva.setOnClickListener(view -> {
            Utils.hiddenKeyboard(requireActivity(), binding.edtName);
            try {
                Utils.askPermissionStorage(getActivity(), () -> {
                    Utils.requestGetGallery(requireActivity());
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.frChangeAva.setOnClickListener(view -> {
            Utils.hiddenKeyboard(requireActivity(), binding.edtName);
            try {
                Utils.askPermissionStorage(getActivity(), () -> {
                    Utils.requestGetGallery(requireActivity());
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.swStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.txtStatus.setCompoundDrawablesWithIntrinsicBounds(isChecked ? R.drawable.ic_online : R.drawable.ic_offline, 0, 0, 0);
            binding.lineView1.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            binding.radios.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            if (isChecked)
                objectUser.setStatus(Utils.getIndexSelected(requireContext(), ""));
            else {
                getIndexChecked();
            }
        });

        binding.radios.setOnCheckedChangeListener((group, checkedId) -> {
            this.checkedId = checkedId;
            getIndexChecked();
        });

        binding.layoutActionBar.txtAction.setOnClickListener(v -> {
            onclickSave();
        });
        binding.rb5Min.setOnClickListener(this);
        binding.rb30Min.setOnClickListener(this);
        binding.rb1Hour.setOnClickListener(this);
        binding.rb1Day.setOnClickListener(this);
    }

    private void getIndexChecked() {
        RadioButton radioButton = binding.radios.findViewById(checkedId);
        if (radioButton == null)
            return;
        objectUser.setStatus(Utils.getIndexSelected(requireContext(), radioButton.getText().toString()));
    }

    private void onclickSave() {
        if (checkName() || checkLive()) {
            return;
        }
        if (!checkDone()) {
            new DialogShowError(requireContext(), R.style.MaterialDialogSheet, o -> {
            }).show();
            return;
        }
        if (isEdit) {
            getViewModel().updateUser(requireContext(), objectUser);
            backStackFragment();
        } else {
            if (getViewModel().insertUser(requireContext(), objectUser))
                backStackFragment();
        }
    }

    private boolean checkName() {
        if (objectUser.getName() == null || objectUser.getName().trim().equals("")) {
            binding.edtError.setText(getResources().getText(R.string.please_fill_in_the_information));
            binding.edtError.setVisibility(View.VISIBLE);
            return true;
        } else if (objectUser.getName().trim().length() > 25) {
            binding.edtError.setText(getResources().getText(R.string.maxium_characters));
            binding.edtError.setVisibility(View.VISIBLE);
            return true;
        } else {
            binding.edtError.setVisibility(View.GONE);
            return false;
        }
    }

    private boolean checkLive() {
        if (objectUser.getLiveIn() == null || objectUser.getLiveIn().trim().equals("")) {
            binding.edtErrorLive.setText(getResources().getText(R.string.please_fill_in_the_information));
            binding.edtErrorLive.setVisibility(View.VISIBLE);
            return true;
        }
        if (objectUser.getLiveIn().trim().length() > 25) {
            binding.edtErrorLive.setText(getResources().getText(R.string.maxium_characters));
            binding.edtErrorLive.setVisibility(View.VISIBLE);
            return true;
        } else {
            binding.edtErrorLive.setVisibility(View.GONE);
            return false;
        }
    }

    private boolean checkDone() {
        if (objectUser.getAvatar() == null || objectUser.equals("")) {
            return false;
        }
        if (objectUser.getLiveIn() == null || objectUser.getLiveIn().equals("") || objectUser.getLiveIn().length() > 25) {
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        Utils.hiddenKeyboard(requireActivity(), binding.edtName);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Utils.hiddenKeyboard(requireActivity(), binding.edtName);
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Const.REQUEST_CODE_GALLERY) {
            Utils.openGallery(requireActivity(), false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.ALBUM_REQUEST_CODE && data != null && data.getData() != null) {
            objectUser.setAvatar(data.getData().toString());
            Glide.with(requireContext()).load(data.getData()).into(binding.ivChangeAva);
        }
        if (requestCode == Const.PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            CropImage.activity(data.getData())
                    .start(requireActivity());
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            objectUser.setAvatar(resultUri.toString());
            Glide.with(requireContext()).load(resultUri).into(binding.ivChangeAva);
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rb_5_min:
                if (checked)
                    break;
            case R.id.rb_30_min:
                if (checked)
                    break;
            case R.id.rb_1_hour:
                if (checked)
                    break;
            case R.id.rb_1_day:
                if (checked)
                    break;
        }
    }

    @Override
    public void onClick(View v) {
        onRadioButtonClicked(v);
    }
}
