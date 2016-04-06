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
import sandjentrance.com.sj.views.FileArchiveViewHolder;


/**
 * Created by toidiu on 4/2/16.
 */
public class FileArchiveListAdapter extends RecyclerView.Adapter {

    private final FileArchiveListInterface fileListInterface;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private List<FileObj> list = new ArrayList<>();

    public FileArchiveListAdapter(FileArchiveListInterface fileListInterface) {
        this.fileListInterface = fileListInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proj_archive_list, parent, false);
        return new FileArchiveViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FileObj item = list.get(position);
        FileArchiveViewHolder view = (FileArchiveViewHolder) holder;
        view.titleView.setText(item.title);
        view.unArchiveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileListInterface.unarchiveFile(item);
            }
        });


        if (item.claimUser != null) {
            view.claimUserView.setText(item.claimUser);
        } else {
            view.claimContainerView.setVisibility(View.GONE);
        }
        view.containerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fileListInterface.fileLongClicked(item);
                return true;
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

}
