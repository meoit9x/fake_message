package nat.pink.base.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import nat.pink.base.databinding.ItemUserMessengerBinding;
import nat.pink.base.model.ObjectUser;
import nat.pink.base.utils.Utils;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ViewHorder> {
    private List<ObjectUser> listObject = new ArrayList<>();
    private Context context;
    private Consumer<ObjectUser> consumer;
    private Consumer<ObjectUser> consumerDelete;

    public ListUserAdapter(Context context, Consumer<ObjectUser> consumer, Consumer<ObjectUser> consumerDelete) {
        this.context = context;
        this.consumer = consumer;
        this.consumerDelete = consumerDelete;
    }

    public void setListObject(List<ObjectUser> listObject) {
        this.listObject = listObject;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserMessengerBinding itemBinding =
                ItemUserMessengerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHorder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHorder holder, int position) {
        ItemUserMessengerBinding binding = holder.binding;
        ObjectUser item = listObject.get(position);
        if (item == null)
            return;
        binding.txtName.setText(item.getName());
        binding.txtLiveIn.setText(item.getLiveIn());
        binding.txtStatus.setText(Utils.getStringFromIndex(context, item.getStatus()));
        Glide.with(context).load(Uri.parse(item.getAvatar())).into(binding.ivContent);
        binding.llContent.setOnClickListener(v -> {
            consumer.accept(item);
        });
        binding.llContent.setOnLongClickListener(v -> {
            consumerDelete.accept(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listObject == null ? 0 : listObject.size();
    }

    public class ViewHorder extends RecyclerView.ViewHolder {
        private final ItemUserMessengerBinding binding;

        public ViewHorder(@NonNull ItemUserMessengerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
