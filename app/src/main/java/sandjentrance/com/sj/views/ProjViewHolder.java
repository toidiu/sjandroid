package sandjentrance.com.sj.views;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sandjentrance.com.sj.R;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.extras.FileClickInterface;
import sandjentrance.com.sj.ui.extras.ProjClickInterface;

/**
 * Created by toidiu on 4/2/16.
 */
public class ProjViewHolder extends RecyclerView.ViewHolder {

    public final View containerView;
    public final View claimContainerView;
    public final TextView titleView;
    public final TextView claimUserView;
    public final FloatingActionButton fabView;

    public ProjViewHolder(View itemView) {
        super(itemView);

        containerView = itemView.findViewById(R.id.container);
        claimContainerView = itemView.findViewById(R.id.claim_container);
        titleView = (TextView) itemView.findViewById(R.id.title);
        claimUserView = (TextView) itemView.findViewById(R.id.claim_user);
        fabView = (FloatingActionButton) itemView.findViewById(R.id.fab);
    }

    public void bind(final FileObj item, final ProjClickInterface projClickInterface) {
        titleView.setText(item.title);
        containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.mime.equals(FileObj.FOLDER_MIME)) {
                    projClickInterface.folderClicked(item);
                } else {
                    projClickInterface.fileClicked(item);
                }
            }
        });

        if (item.claimUser != null) {
            claimUserView.setText(item.claimUser);
            claimContainerView.setVisibility(View.VISIBLE);
        } else {
            claimContainerView.setVisibility(View.GONE);
        }
        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projClickInterface.addClicked(item);
            }
        });
    }
}
