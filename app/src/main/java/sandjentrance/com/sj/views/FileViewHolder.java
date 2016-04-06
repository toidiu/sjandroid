package sandjentrance.com.sj.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sandjentrance.com.sj.R;

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
}
