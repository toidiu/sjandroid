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
import sandjentrance.com.sj.views.FileViewHolder;


/**
 * Created by toidiu on 4/2/16.
 */
public class FileListAdapter extends RecyclerView.Adapter {

    private final FileListInterface fileListInterface;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private List<FileObj> list = new ArrayList<>();

    public FileListAdapter(FileListInterface fileListInterface) {
        this.fileListInterface = fileListInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proj_list, parent, false);
        return new FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FileObj item = list.get(position);
        FileViewHolder view = (FileViewHolder) holder;
        view.titleView.setText(item.title);
        view.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.mime.equals(FileObj.FOLDER_MIME)) {
                    fileListInterface.folderClicked(item);
                }
                else {
                    fileListInterface.fileClicked(item);
                }
                //// FIXME: 4/3/16 check if its a pdf  or picture and open it
            }
        });

        if (item.claimUser != null) {
            view.claimUserView.setText(item.claimUser);
            view.claimContainerView.setVisibility(View.VISIBLE);
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
