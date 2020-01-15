package org.financer.client.app.ui.main.transactions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import org.financer.client.app.R;
import org.financer.client.app.format.AndroidFormatter;
import org.financer.client.app.local.LocalStorageImpl;
import org.financer.client.format.Formatter;
import org.financer.shared.model.transactions.Attachment;

public class AttachmentListViewAdapter extends ArrayAdapter<Attachment> {
    private final Formatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), getContext());

    public AttachmentListViewAdapter(Context context, List<Attachment> attachments) {
        super(context, R.layout.list_item_transaction, attachments);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Attachment attachment = getItem(position);

        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_attachment, parent, false);
        }

        if (attachment != null) {
            TextView nameTextView = listItem.findViewById(R.id.tv_list_item_attachment_name);
            nameTextView.setText(attachment.getName());

            TextView dateTextView = listItem.findViewById(R.id.tv_list_item_attachment_date);
            dateTextView.setText(formatter.formatDate(attachment.getUploadDate()));

            // TODO add size as attribute to attachment
            TextView valueDateTextView = listItem.findViewById(R.id.tv_list_item_attachment_size);
            valueDateTextView.setText("1 MB");
        }

        return listItem;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        int totalHeight = 0;
        for (int i = 0; i < this.getCount(); i++) {
            View listItem = this.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (this.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
