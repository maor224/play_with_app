package co.il.example.play_with_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    Activity activity;
    ArrayList<Contact> arrayList;

    OnItemClickListener onItemClickListener1;

    @SuppressLint("NotifyDataSetChanged")
    public ContactsAdapter(Activity activity, ArrayList<Contact> arrayList, OnItemClickListener onItemClickListener1) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.onItemClickListener1 = onItemClickListener1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);

        return new ViewHolder(view, onItemClickListener1);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // create contact view
        Contact contact = arrayList.get(position);

        holder.tvName.setText(contact.getName());
        holder.tvNumber.setText(contact.getNumber());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName, tvNumber;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_number);

            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // send to on item click function
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
