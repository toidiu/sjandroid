package sandjentrance.com.sj.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sandjentrance.com.sj.R;

/**
 * Created by toidiu on 4/2/16.
 */
public class ProjectViewHolder extends RecyclerView.ViewHolder {

    public final View containerView;
    public final TextView titleView;

    public ProjectViewHolder(View itemView) {
        super(itemView);

        containerView = itemView.findViewById(R.id.container);
        titleView = (TextView) itemView.findViewById(R.id.title);
    }
}
