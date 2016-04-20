package sandjentrance.com.sj.views;

import android.support.v7.widget.CardView;
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
public class ProjDetailViewHolder extends RecyclerView.ViewHolder {

    public final View containerView;
    public final TextView titleView;
    private final CardView folderCard;

    public ProjDetailViewHolder(View itemView) {
        super(itemView);

        containerView = itemView.findViewById(R.id.container);
        titleView = (TextView) itemView.findViewById(R.id.title);
        folderCard = (CardView) itemView.findViewById(R.id.folder_card);
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


        titleView.setTextColor(itemView.getResources().getColor(R.color.white));
        if (item.title.matches("(?i)drawing.*|proposal.*|note.*")) {
            folderCard.setCardBackgroundColor(itemView.getResources().getColor(R.color.folder_red));
        } else if (item.title.matches("(?i).*mail.*|Insurance.*|Transmittal.*")) {
            folderCard.setCardBackgroundColor(itemView.getResources().getColor(R.color.folder_blue));
        } else if (item.title.matches("(?i).*contract.*|Fab.*|Close.*|Purchase.*")) {
            folderCard.setCardBackgroundColor(itemView.getResources().getColor(R.color.folder_green));
        } else if (item.title.matches("(?i)Approval.*|Certificate.*|Inventory.*|Photo.*")) {
            folderCard.setCardBackgroundColor(itemView.getResources().getColor(R.color.folder_teal));
            titleView.setTextColor(itemView.getResources().getColor(R.color.black));
        } else if (item.title.matches("(?i)Billing.*|Hardware.*|Quote.*")) {
            folderCard.setCardBackgroundColor(itemView.getResources().getColor(R.color.folder_yellow));
            titleView.setTextColor(itemView.getResources().getColor(R.color.black));
        } else {
            folderCard.setCardBackgroundColor(itemView.getResources().getColor(R.color.folder_green));
        }
    }
}
