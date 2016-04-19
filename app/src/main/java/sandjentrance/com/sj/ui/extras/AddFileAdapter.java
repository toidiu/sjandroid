package sandjentrance.com.sj.ui.extras;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sandjentrance.com.sj.R;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.DialogAddFile;
import sandjentrance.com.sj.views.AddFileViewHolder;
import sandjentrance.com.sj.views.ProjViewHolder;


/**
 * Created by toidiu on 4/2/16.
 */
public class AddFileAdapter extends RecyclerView.Adapter {

    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private final FileAddInterface fileAddInterface;
    private List<String> list = new ArrayList<>();

    public AddFileAdapter(FileAddInterface addInterface) {
        fileAddInterface = addInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_file, parent, false);
        return new AddFileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final String item = list.get(position);
        AddFileViewHolder view = (AddFileViewHolder) holder;
        view.bind(item, fileAddInterface);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refreshView(@NonNull List<String> data) {
        list = data;
        notifyDataSetChanged();
    }

}
