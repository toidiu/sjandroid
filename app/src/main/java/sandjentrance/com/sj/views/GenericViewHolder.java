package sandjentrance.com.sj.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import de.hdodenhof.circleimageview.CircleImageView;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.extras.FileClickInterface;
import sandjentrance.com.sj.utils.UtilsDate;
import sandjentrance.com.sj.utils.UtilsView;

/**
 * Created by toidiu on 4/2/16.
 */
public class GenericViewHolder extends RecyclerView.ViewHolder {

    public final View containerView;
    public final TextView titleView;
    public final TextView modifiedView;
    public final CircleImageView fileIconView;
    private final View overflowView;

    public GenericViewHolder(View itemView) {
        super(itemView);

        containerView = itemView.findViewById(R.id.container);
        titleView = (TextView) itemView.findViewById(R.id.title);
        modifiedView = (TextView) itemView.findViewById(R.id.modified);
        fileIconView = (CircleImageView) itemView.findViewById(R.id.file_icon);
        overflowView = itemView.findViewById(R.id.overflow);
    }

    public void bind(final FileObj item, final FileClickInterface fileClickInterface) {
        this.titleView.setText(item.title);
        DateTime parse = DateTime.parse(item.lastModified);
        modifiedView.setText(UtilsDate.dateFormatter.print(parse));

        if (FileObj.isFolder(item.mime)) {
            Picasso.with(SJApplication.appContext).load(R.drawable.ic_content_folder).into(fileIconView);
            overflowView.setVisibility(View.GONE);
        } else if (item.mime.equals(BaseAction.MIME_DWG1) ) {
            overflowView.setVisibility(View.GONE);
        } else {
            Picasso.with(SJApplication.appContext).load(R.drawable.ic_document).into(fileIconView);
        }

        this.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.mime.equals(BaseAction.FOLDER_MIME)) {
                    fileClickInterface.folderClicked(item);
                } else {
                    fileClickInterface.editClicked(item);
                }
            }
        });

        overflowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsView.fileClickPopup(overflowView, item, fileClickInterface);
            }
        });

        this.containerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!item.mime.equals(BaseAction.FOLDER_MIME)) {
                    UtilsView.fileLongClickPopup(containerView, item, fileClickInterface);
                }
                return true;
            }
        });

    }
}
