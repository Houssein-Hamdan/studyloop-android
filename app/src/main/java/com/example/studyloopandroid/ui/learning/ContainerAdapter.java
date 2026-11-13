package com.example.studyloopandroid.ui.learning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.data.model.container.ContainerResponse;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ContainerAdapter
        extends RecyclerView.Adapter<ContainerAdapter.ContainerViewHolder> {

    public interface OnContainerClickListener {

        void onContainerClick(ContainerResponse container);

        void onEditContainer(ContainerResponse container);

        void onDeleteContainer(ContainerResponse container);
    }

    private List<ContainerResponse> containers = new ArrayList<>();
    private final OnContainerClickListener listener;

    public ContainerAdapter(OnContainerClickListener listener) {
        this.listener = listener;
    }

    public void setContainers(List<ContainerResponse> containers) {
        this.containers = containers != null
                ? containers
                : new ArrayList<>();

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContainerViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_container, parent, false);

        return new ContainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ContainerViewHolder holder,
            int position
    ) {
        ContainerResponse container = containers.get(position);

        holder.tvContainerName.setText(container.getName());

        String description = container.getDescription();

        if (description == null || description.trim().isEmpty()) {
            description = holder.itemView.getContext()
                    .getString(R.string.container_default_description);
        }

        holder.tvContainerDescription.setText(description);

        // Open the learning space.
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContainerClick(container);
            }
        });

        // Edit the learning space.
        holder.btnEditContainer.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditContainer(container);
            }
        });

        // Delete the learning space.
        holder.btnDeleteContainer.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteContainer(container);
            }
        });
    }

    @Override
    public int getItemCount() {
        return containers.size();
    }

    static class ContainerViewHolder extends RecyclerView.ViewHolder {

        TextView tvContainerName;
        TextView tvContainerDescription;

        MaterialButton btnEditContainer;
        MaterialButton btnDeleteContainer;

        public ContainerViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContainerName =
                    itemView.findViewById(R.id.tvContainerName);

            tvContainerDescription =
                    itemView.findViewById(R.id.tvContainerDescription);

            btnEditContainer =
                    itemView.findViewById(R.id.btnEditContainer);

            btnDeleteContainer =
                    itemView.findViewById(R.id.btnDeleteContainer);
        }
    }
}
