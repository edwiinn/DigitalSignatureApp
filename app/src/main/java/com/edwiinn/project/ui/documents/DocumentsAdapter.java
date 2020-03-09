package com.edwiinn.project.ui.documents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edwiinn.project.R;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.BaseViewHolder;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocumentsAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<DocumentsResponse.Document> mDocumentsResponseList;

    public DocumentsAdapter(List<DocumentsResponse.Document> documentsResponseList){
        mDocumentsResponseList = documentsResponseList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document_view, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (mDocumentsResponseList != null && mDocumentsResponseList.size() > 0){
            return mDocumentsResponseList.size();
        }
        return 0;
    }

    public void addItems(List<DocumentsResponse.Document> documentsList){
        mDocumentsResponseList.addAll(documentsList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.title_txt)
        TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
            titleTextView.setText("");
        }

        public void onBind(int position){
            super.onBind(position);

            final DocumentsResponse.Document document = mDocumentsResponseList.get(position);

            if (document.getName() != null){
                titleTextView.setText(document.getName());
            }
        }
    }
}


