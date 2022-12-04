package com.pentaware.doorish;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterDeliverySlots extends RecyclerView.Adapter<AdapterDeliverySlots.ViewHolder> {

    private List<String> slotList;
    private List<String> availability;
    private Context m_Context;
    private int selectedSlot = -1;
    private boolean selectByDefault = true;
    private IDeliverySlotOperations iDeliverySlotOperations;


    public AdapterDeliverySlots(Context ctx, List<String> slotList, List<String> availability, IDeliverySlotOperations iDeliverySlotOperations) {
        this.slotList = slotList;
        this.availability = availability;
        m_Context = ctx;
        this.iDeliverySlotOperations = iDeliverySlotOperations;
    }


    @NonNull
    @Override
    public AdapterDeliverySlots.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_slot_list_item, parent, false);
        return new AdapterDeliverySlots.ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDeliverySlots.ViewHolder holder, int position) {
        String timeSlot = slotList.get(position);
        String available = availability.get(position);
        holder.txtSlotTime.setText(timeSlot);
        
        if(timeSlot.contains("Anytime") && selectByDefault){
            selectedSlot = position;
            iDeliverySlotOperations.selectedTimeSlot(timeSlot);
        }

        if(available.equals("NA")){
            holder.txtAvailability.setVisibility(View.VISIBLE);
            holder.txtSlotTime.setTextColor(Color.parseColor("#66000000"));
            holder.radioButton.setEnabled(false);
        }
        else {
            if (selectedSlot == position) {
                holder.radioButton.setChecked(true);
            }
            else {
                holder.radioButton.setChecked(false);
            }
        }


    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RadioButton radioButton;
        private TextView txtSlotTime, txtAvailability;


        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_button);
            txtSlotTime = itemView.findViewById(R.id.txt_slot_time);
            txtAvailability = itemView.findViewById(R.id.txt_availability);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedSlot = getAdapterPosition();
                    if(!availability.get(getAdapterPosition()).equals("NA")){
                        Log.d("value_here", String.valueOf(selectedSlot));
                        radioButton.setChecked(true);
                        iDeliverySlotOperations.selectedTimeSlot(slotList.get(getAdapterPosition()));
                        selectByDefault = false;
                        notifyDataSetChanged();
                    }

                }
            });

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedSlot = getAdapterPosition();
                    if(radioButton.isChecked()){
                        selectedSlot = getAdapterPosition();
                        Log.d("value_here", String.valueOf(selectedSlot));
                        radioButton.setChecked(true);
                        iDeliverySlotOperations.selectedTimeSlot(slotList.get(getAdapterPosition()));
                        selectByDefault = false;
                        notifyDataSetChanged();
                    }
                }
            });

        }
        @Override
        public void onClick(View view) { }
    }
}
