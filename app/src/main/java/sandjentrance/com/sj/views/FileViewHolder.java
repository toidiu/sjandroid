package sandjentrance.com.sj.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.extras.FileClickInterface;

/**
 * Created by toidiu on 4/2/16.
 */
public class FileViewHolder extends RecyclerView.ViewHolder {

    public final View containerView;
    public final TextView titleView;

    public FileViewHolder(View itemView) {
        super(itemView);

        containerView = itemView.findViewById(R.id.container);
        titleView = (TextView) itemView.findViewById(R.id.title);
    }

    public void bind(final FileObj item, final FileClickInterface fileClickInterface) {
        this.titleView.setText(item.title);
        this.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.mime.equals(BaseAction.FOLDER_MIME)) {
                    fileClickInterface.folderClicked(item);
                } else {
                    fileClickInterface.fileClicked(item);
                }
            }
        });

        this.containerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fileClickInterface.fileLongClicked(item);
                return true;
            }
        });
    }
}
