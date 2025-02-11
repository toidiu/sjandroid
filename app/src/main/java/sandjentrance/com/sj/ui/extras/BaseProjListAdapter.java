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
import sandjentrance.com.sj.views.BaseProjViewHolder;


/**
 * Created by toidiu on 4/2/16.
 */
public class BaseProjListAdapter extends RecyclerView.Adapter {

    private final BaseProjClickInterface baseProjClickInterface;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private List<FileObj> list = new ArrayList<>();

    public BaseProjListAdapter(BaseProjClickInterface baseProjClickInterface) {
        this.baseProjClickInterface = baseProjClickInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_base_proj_list, parent, false);
        return new BaseProjViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FileObj item = list.get(position);
        BaseProjViewHolder view = (BaseProjViewHolder) holder;
        view.bind(item, baseProjClickInterface);
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
