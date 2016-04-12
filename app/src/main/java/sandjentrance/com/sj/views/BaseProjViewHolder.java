package sandjentrance.com.sj.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sandjentrance.com.sj.R;

/**
 * Created by toidiu on 4/2/16.
 */
public class BaseProjViewHolder extends RecyclerView.ViewHolder {

    public final View containerView;
    public final View ownContainerView;
    public final TextView titleView;
    public final TextView ownUserView;

    public BaseProjViewHolder(View itemView) {
        super(itemView);

        containerView = itemView.findViewById(R.id.container);
        ownContainerView = itemView.findViewById(R.id.own_container);
        titleView = (TextView) itemView.findViewById(R.id.title);
        ownUserView = (TextView) itemView.findViewById(R.id.own_user);
    }
}
