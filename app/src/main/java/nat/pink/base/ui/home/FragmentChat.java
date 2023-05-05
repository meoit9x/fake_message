package nat.pink.base.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.concurrent.Callable;

import nat.pink.base.R;
import nat.pink.base.adapter.MessageAdapter;
import nat.pink.base.base.BaseFragment;
import nat.pink.base.customView.SoftInputAssist;
import nat.pink.base.dao.DatabaseController;
import nat.pink.base.databinding.ActivityChatBinding;
import nat.pink.base.dialog.DialogDeleteMessenger;
import nat.pink.base.model.ObjectMessenge;
import nat.pink.base.model.ObjectUser;
import nat.pink.base.utils.Config;
import nat.pink.base.utils.Const;
import nat.pink.base.utils.FileUtil;
import nat.pink.base.utils.Toolbox;
import nat.pink.base.utils.Utils;

public class FragmentChat extends BaseFragment<ActivityChatBinding, HomeViewModel> implements View.OnClickListener {

    @NonNull
    @Override
    public HomeViewModel getViewModel() {
        return new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    private static final String TAG = "FragmentChat";
    private SoftInputAssist softInputAssist;
    private ObjectUser objectUser;
    private String pathImageSelect;
    private MessageAdapter messageAdapter;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1001;

    public FragmentChat() {
    }

    public FragmentChat(ObjectUser objectUser) {
        this.objectUser = objectUser;
    }

    @Override
    public void initView() {
        super.initView();
        softInputAssist = new SoftInputAssist(requireActivity());
        binding.tvBlock.setText(Html.fromHtml(getString(R.string.block_content), Html.FROM_HTML_MODE_COMPACT));
        binding.rcvMessage.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void initEvent() {
        super.initEvent();
        binding.llMessage.setOnClickListener(this);
        binding.layoutTop.imBack.setOnClickListener(this);
        binding.layoutTop.imCall.setOnClickListener(this);
        binding.layoutTop.imVideo.setOnClickListener(this);
        binding.layoutTop.imInfor.setOnClickListener(this);
        binding.layoutBottom.imMore.setOnClickListener(this);
        binding.layoutBottom.imSend.setOnClickListener(this);
        binding.layoutBottom.imPicture.setOnClickListener(this);
        binding.layoutBottom.edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setStatusItemBottom(count != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.layoutBottom.imMore.setOnClickListener(this);
        binding.layoutBottom.imEmoji.setOnClickListener(this);
        binding.layoutBottom.imSend.setOnClickListener(this);
        binding.layoutBottom.imPicture.setOnClickListener(this);
        binding.layoutBottom.imRecord.setOnClickListener(this);
        binding.layoutBottom.imTakePhoto.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        if (objectUser == null)
            return;

        loadUser();
        getViewModel().loadChatInfo.observe(requireActivity(), obj -> {
            if (isAdded()) {
                objectUser = obj;
                loadUser();
                loadData();
            }
        });

        binding.layoutBottom.imEmoji.setOnClickListener(this);
        getViewModel().reloadDataMessenger.observe(requireActivity(), aBoolean -> {
            if (aBoolean && isAdded()) {
                loadData();
            }
        });
        messageAdapter = new MessageAdapter(requireContext(), objectUser, getViewModel().objectMessenges);
        binding.rcvMessage.setAdapter(messageAdapter);
        messageAdapter.setItemLongClickListener((position, itemType, view) -> {
            DialogDeleteMessenger dialogDeleteMessenger = new DialogDeleteMessenger(requireContext(), R.style.MaterialDialogSheet, o -> {
                DatabaseController.getInstance(requireContext()).deleteMessenger(getViewModel().objectMessenges.get(position).getId());
                getViewModel().objectMessenges.remove(position);
                loadData();
            });
            dialogDeleteMessenger.show();
        });
        loadData();
//        scrollLastItemMessage();
    }

    private void loadUser() {
        getViewModel().objectMessenges.clear();
        if (objectUser.getAvatar() != null)
            Glide.with(requireContext()).load(Uri.parse(objectUser.getAvatar())).into(binding.layoutTop.imInline);
        if (objectUser.getName() != null)
            binding.layoutTop.tvName.setText(objectUser.getName());
        binding.layoutTop.tvContent.setText(Utils.getStringFromIndex(requireContext(), objectUser.getStatus()));
    }

    private void loadData() {
        getViewModel().getMessengerById(requireContext(), objectUser.getId(), o -> {
            if (getViewModel().objectMessenges.isEmpty() || getViewModel().objectMessenges.get(0).getType() != Config.TYPE_HEAEDER) {
                ObjectMessenge messageModel = new ObjectMessenge();
                messageModel.setType(Config.TYPE_HEAEDER);
                getViewModel().objectMessenges.add(0, messageModel);
            }

            if (getViewModel().objectMessenges.size() > 0) {
                if (messageAdapter == null)
                    return;
                messageAdapter.setList(getViewModel().objectMessenges);
                messageAdapter.notifyDataSetChanged();
            } else {
                Log.e("natruou", "2");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (softInputAssist != null)
            softInputAssist.onResume();
    }

    @Override
    public void onDestroy() {
        if (softInputAssist != null)
            softInputAssist.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onPause() {
        if (softInputAssist != null)
            softInputAssist.onPause();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.layoutTop.imBack.getId()) {
            Utils.hideKeyboard(binding.layoutBottom.edtInput);
            backStackFragment();
        }
        if (view.getId() == binding.layoutTop.imCall.getId()) {
        }
        if (view.getId() == binding.layoutTop.imVideo.getId()) {
        }
        if (view.getId() == binding.layoutTop.imInfor.getId()) {
            showPopup(view);
        }
        if (view.getId() == binding.layoutBottom.imEmoji.getId()) {
//            Intent intentEmoji = new Intent(requireContext(), StickerActivity.class);
//            startActivityForResult(intentEmoji, Config.REQUEST_CODE_ACT_STICKER);
        }
        if (view.getId() == binding.layoutBottom.imMore.getId()) {
            if (!binding.layoutBottom.llOpt.isShown()) {
                setStatusItemBottom(false);
            }
        }
        if (view.getId() == binding.layoutBottom.imSend.getId()) {
            showPopupOptSend();
        }
        if (view.getId() == binding.layoutBottom.imTakePhoto.getId()) {
            new AlertDialog.Builder(getContext())
                    .setMessage(getString(R.string.ask_save_image))
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        dialog.cancel();
                        saveImage();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        dialog.cancel();
                    })
                    .show();
        }
        if (view.getId() == binding.layoutBottom.imRecord.getId()) {

        }
        if (view.getId() == binding.layoutBottom.imPicture.getId()) {
            try {
                Utils.askPermissionStorage(getActivity(), () -> {
                    Utils.requestGetGallery(requireActivity());
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showPopup(View view) {
        Toolbox.hideSoftKeyboard(requireActivity());
        PopupMenu popup = new PopupMenu(requireContext(), view);
        popup.show();
    }

    private void showPopupOptSend() {

    }

    public void scrollLastItemMessage() {
        if (messageAdapter != null) {
            messageAdapter.notifyDataSetChanged();
        }
        binding.rcvMessage.scrollToPosition(messageAdapter.getItemCount() - 1);
        binding.layoutBottom.edtInput.setFocusableInTouchMode(true);
        binding.layoutBottom.edtInput.requestFocus(0);
    }

    private ObjectMessenge initDataSend(boolean isSend) {
        String data;
        int type;
        if (binding.layoutSelectImage.container.isShown()) {
            data = pathImageSelect;
            type = Config.TYPE_IMAGE;
            cleanImageAfterSend();
        } else if (!TextUtils.isEmpty(binding.layoutBottom.edtInput.getText().toString())) {
            data = binding.layoutBottom.edtInput.getText().toString();
            type = Config.TYPE_TEXT;
            cleanChatAfterSend();
        } else {
            data = Config.IC_LIKE_NAME;
            type = Config.TYPE_STICKER;
        }
        ObjectMessenge message = Config.createMessage(data, type);
        message.setSend(isSend);
        message.setUserOwn(objectUser.getId());
        return message;
    }

    public void cleanChatAfterSend() {
        binding.layoutBottom.edtInput.setText("");
    }

    public void cleanImageAfterSend() {
        binding.layoutSelectImage.container.setVisibility(View.GONE);
        pathImageSelect = "";
        if (!TextUtils.isEmpty(binding.layoutBottom.edtInput.getText().toString())) {
            binding.layoutBottom.imSend.setImageResource(R.drawable.ic_send);
        } else {
            binding.layoutBottom.imSend.setImageResource(R.drawable.ic_like);
        }
    }

    private void setStatusItemBottom(boolean isChating) {
        binding.layoutBottom.llOpt.setVisibility(isChating ? View.GONE : View.VISIBLE);
        binding.layoutBottom.imSend.setImageResource(isChating ? R.drawable.ic_send : R.drawable.ic_like);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.PICK_FROM_GALLERY && data != null && data.getData() != null) {
            CropImage.activity(data.getData())
                    .start(requireActivity());
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == -1 && null != data) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            pathImageSelect = resultUri.toString();
            binding.layoutSelectImage.container.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(resultUri).into(binding.layoutSelectImage.imPreview);
            binding.layoutBottom.imSend.setImageResource(R.drawable.ic_send);
        }
        if (requestCode == Config.REQUEST_CODE_ACT_STICKER) {
            if (data == null) {
                return;
            }
            String resName = data.getStringExtra(Config.KEY_CONTENT);
            boolean isSend = data.getBooleanExtra(Config.KEY_TYPE_SEND, true);
            ObjectMessenge messageModel = Config.createMessage(resName, Config.TYPE_STICKER);
            messageModel.setSend(isSend);
            messageModel.setUserOwn(objectUser.getId());
            getViewModel().inserMessenger(requireContext(), messageModel);
            getViewModel().objectMessenges.add(messageModel);
            scrollLastItemMessage();
        }
    }

    public void saveImage() {
        try {
            askPermissionStorage(() -> {
                File file = FileUtil.createFolder(requireActivity(), getString(R.string.app_name));
                Log.e("file_storage", file.getAbsolutePath());
                String path = FileUtil.saveImageToGallery(requireContext()
                        , file
                        , Toolbox.screenShortView(binding.container));
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void askPermissionStorage(Callable<Void> callable) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
            } else {
                callable.call();
            }
        } else {
            callable.call();
        }
    }
}