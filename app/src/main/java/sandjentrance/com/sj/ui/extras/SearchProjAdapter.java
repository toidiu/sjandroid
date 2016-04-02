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
import sandjentrance.com.sj.views.ProjectViewHolder;


/**
 * Created by toidiu on 4/2/16.
 */
public class SearchProjAdapter extends RecyclerView.Adapter {

    private final ProjListInterface projListInterface;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private List<FileObj> list = new ArrayList<>();

    public SearchProjAdapter(ProjListInterface projListInterface) {
        this.projListInterface = projListInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.proj_list_item, parent, false);
        return new ProjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FileObj item = list.get(position);
        ProjectViewHolder view = (ProjectViewHolder) holder;
        view.titleView.setText(item.title);
        view.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projListInterface.projClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refreshView(@NonNull List<FileObj> data) {
        list = data;
        notifyDataSetChanged();
    }


    public interface ProjListInterface {
        void projClicked(FileObj fileObj);
    }
}
