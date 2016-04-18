package sandjentrance.com.sj.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sandjentrance.com.sj.R;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.extras.FileClickInterface;

/**
 * Created by toidiu on 4/2/16.
 */
public class FileViewHolder extends RecyclerView.ViewHolder {

    public final View containerView;
    public final View claimContainerView;
    public final TextView titleView;
    public final TextView claimUserView;

    public FileViewHolder(View itemView) {
        super(itemView);

        containerView = itemView.findViewById(R.id.container);
        claimContainerView = itemView.findViewById(R.id.claim_container);
        titleView = (TextView) itemView.findViewById(R.id.title);
        claimUserView = (TextView) itemView.findViewById(R.id.claim_user);
    }

    public void bind(final FileObj item, final FileClickInterface fileClickInterface) {
        this.titleView.setText(item.title);
        this.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.mime.equals(FileObj.FOLDER_MIME)) {
                    fileClickInterface.folderClicked(item);
                }
                else {
                    fileClickInterface.fileClicked(item);
                }
            }
        });

        if (item.claimUser != null) {
            this.claimUserView.setText(item.claimUser);
            this.claimContainerView.setVisibility(View.VISIBLE);
        } else {
            this.claimContainerView.setVisibility(View.GONE);
        }
        this.containerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fileClickInterface.fileLongClicked(item);
                return true;
            }
        });
    }
}
