package sandjentrance.com.sj.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sandjentrance.com.sj.R;
import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.ui.extras.FileAddInterface;

/**
 * Created by toidiu on 4/2/16.
 */
public class AddFileViewHolder extends RecyclerView.ViewHolder {

    public final ImageView imageView;
    public final TextView textView;
    public final View container;

    public AddFileViewHolder(View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.add_container);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        textView = (TextView) itemView.findViewById(R.id.text);
    }

    public void bind(final String item, final FileAddInterface fileAddInterface) {
        switch (item) {
            case BaseAction.PURCHASE_FOLDER_NAME:
                textView.setText(BaseAction.PURCHASE_FOLDER_NAME);
                Picasso.with(SJApplication.appContext).load(R.drawable.purchase_order).into(imageView);
                break;
            case BaseAction.FAB_FOLDER_NAME:
                textView.setText(BaseAction.FAB_FOLDER_NAME);
                Picasso.with(SJApplication.appContext).load(R.drawable.fab_sheet).into(imageView);
                break;
            case BaseAction.LABOUR_FOLDER_NAME:
                textView.setText(BaseAction.LABOUR_FOLDER_NAME);
                Picasso.with(SJApplication.appContext).load(R.drawable.project_labor_request).into(imageView);
                break;
            case BaseAction.PHOTOS_FOLDER_NAME:
                textView.setText(BaseAction.PHOTOS_FOLDER_NAME);
                Picasso.with(SJApplication.appContext).load(R.drawable.camera).into(imageView);
                break;
            case BaseAction.NOTES_FOLDER_NAME:
                textView.setText(BaseAction.NOTES_FOLDER_NAME);
                Picasso.with(SJApplication.appContext).load(R.drawable.note).into(imageView);
                break;
            default:
                break;
        }

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileAddInterface.itemClicked(item);
            }
        });
    }

}
