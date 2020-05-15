package com.edwiinn.project.ui.documents;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.edwiinn.project.R;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.BaseViewHolder;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocumentsAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private DocumentsPresenter mDocumentsPresenter;
    private List<DocumentsResponse.Document> mDocumentsResponseList;

    public DocumentsAdapter(List<DocumentsResponse.Document> documentsResponseList){
        mDocumentsResponseList = documentsResponseList;
    }

    public void setPresenter(DocumentsPresenter presenter){
        mDocumentsPresenter = presenter;
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

        @BindView(R.id.item_document)
        ConstraintLayout documentItemLayout;

        @BindView(R.id.upload_btn)
        Button uploadButton;

        @BindView(R.id.icon_img)
        ImageView iconImage;

        @BindView(R.id.icon_signed_img)
        ImageView iconSignedImage;

        DocumentsResponse.Document document;
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

            this.document = mDocumentsResponseList.get(position);
            uploadButton.setVisibility(View.INVISIBLE);

            if (document.getSigned()) {
                iconSignedImage.setVisibility(View.VISIBLE);
                iconImage.setVisibility(View.INVISIBLE);
            } else {
                iconSignedImage.setVisibility(View.INVISIBLE);
                iconImage.setVisibility(View.VISIBLE);
                uploadButton.setVisibility(View.INVISIBLE);
            }

            if (document.getUserSigned()){
                uploadButton.setVisibility(View.VISIBLE);
                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDocumentsPresenter.uploadSignedDocument(document);
                    }
                });
            }

            if (document.getName() != null){
                titleTextView.setText(document.getName());
            }

            if (mDocumentsPresenter != null){
                documentItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDocumentsPresenter.onDocumentClicked(document);
                    }
                });

            }
        }
    }
}


