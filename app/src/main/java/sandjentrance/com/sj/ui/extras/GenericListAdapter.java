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
import sandjentrance.com.sj.views.GenericViewHolder;


/**
 * Created by toidiu on 4/2/16.
 */
public class GenericListAdapter extends RecyclerView.Adapter {

    private final FileClickInterface fileClickInterface;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private List<FileObj> list = new ArrayList<>();

    public GenericListAdapter(FileClickInterface fileClickInterface) {
        this.fileClickInterface = fileClickInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_generic_list, parent, false);
        return new GenericViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FileObj item = list.get(position);
        GenericViewHolder view = (GenericViewHolder) holder;
        view.bind(item, fileClickInterface);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refreshView(@NonNull List<FileObj> data) {
        list = data;
        notifyDataSetChanged();
    }

}
