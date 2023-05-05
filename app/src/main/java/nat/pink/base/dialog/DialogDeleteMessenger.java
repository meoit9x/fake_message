package nat.pink.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import nat.pink.base.databinding.DialogDeleteMessBinding;

public class DialogDeleteMessenger extends Dialog {
    private Consumer consumer;
    private DialogDeleteMessBinding binding;

    public DialogDeleteMessenger(@NonNull Context context, int themeResId, Consumer consumer) {
        super(context, themeResId);
        this.consumer = consumer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding = DialogDeleteMessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.llAdd.setOnClickListener(v -> {
            consumer.accept(new Object());
            dismiss();
        });
    }
}