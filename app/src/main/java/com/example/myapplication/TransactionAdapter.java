package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private Context context;
    private List<Transaction> transactionList;
    private OnItemClickListener listener;

    // Interface untuk listener klik item
    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    // Metode untuk menetapkan listener klik
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;
        TextView amountTextView;
        TextView dateTextView; // Added TextView for date
        ImageView categoryIconImageView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView); // Initialize date TextView
            categoryIconImageView = itemView.findViewById(R.id.categoryIconImageView);

            // Set click listener on item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(transactionList.get(position));
                    }
                }
            });
        }

        public void bind(Transaction transaction) {
            categoryTextView.setText(transaction.getCategory());
            amountTextView.setText(transaction.getAmount());
            dateTextView.setText(transaction.getDate()); // Set date

            // Check transaction category and set icon accordingly
            if (transaction.getCategory().equals("Cash In")) {
                categoryIconImageView.setImageResource(R.drawable.receive);
            } else {
                categoryIconImageView.setImageResource(R.drawable.send);
            }
        }
    }
}
