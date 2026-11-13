package com.example.studyloopandroid.ui.learning;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.ui.common.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.studyloopandroid.data.model.container.ContainerResponse;


import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;


public class HomeActivity extends BaseActivity {

    private HomeViewModel homeViewModel;
    private CreateContainerViewModel createContainerViewModel;
    private ContainerAdapter containerAdapter;

    private ProgressBar progressBar;
    private TextView tvHomeError;
    private TextView tvEmptyLearning;
    private View emptyLearningContainer;
    private MaterialButton btnNewLearningSpace;

    private AlertDialog createDialog;
    private EditText etContainerName;
    private EditText etContainerDescription;
    private TextView tvCreateError;
    private ProgressBar createProgressBar;
    private MaterialButton btnCreate;
    private MaterialButton btnCancel;
    private EditContainerViewModel editContainerViewModel;
    private DeleteContainerViewModel deleteContainerViewModel;

    private AlertDialog editDialog;
    private AlertDialog deleteDialog;

    private boolean containersLoaded = false;
    private boolean containerLoadInProgress = false;
    private boolean containersEmpty = true;

    private EditText etEditContainerName;
    private EditText etEditContainerDescription;

    private TextView tvEditError;
    private ProgressBar editProgressBar;

    private MaterialButton btnSaveEdit;
    private MaterialButton btnCancelEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupBottomNavigation(R.id.nav_learn);

        progressBar = findViewById(R.id.homeProgressBar);
        tvHomeError = findViewById(R.id.tvHomeError);
        tvEmptyLearning = findViewById(R.id.tvEmptyLearning);
        emptyLearningContainer = findViewById(R.id.emptyLearningContainer);
        btnNewLearningSpace = findViewById(R.id.btnNewLearningSpace);

        RecyclerView rvLearningSpaces =
                findViewById(R.id.rvLearningSpaces);

        containerAdapter = new ContainerAdapter( new ContainerAdapter.OnContainerClickListener()
        {
            @Override
            public void onContainerClick(ContainerResponse container)
            {
                android.content.Intent intent = new android.content.Intent( HomeActivity.this,
                        LessonsActivity.class );
                intent.putExtra("containerId", container.getId());
                intent.putExtra("containerName", container.getName());
                startActivity(intent);
            }
            @Override
            public void onEditContainer(ContainerResponse container)
            {
                showEditContainerDialog(container);
            }
            @Override
            public void onDeleteContainer(ContainerResponse container) {
                showDeleteContainerDialog(container);
            }
        } );


        rvLearningSpaces.setAdapter(containerAdapter);

        homeViewModel = new ViewModelProvider(this)
                .get(HomeViewModel.class);

        createContainerViewModel = new ViewModelProvider(this)
                .get(CreateContainerViewModel.class);

        editContainerViewModel = new ViewModelProvider(this)
                .get(EditContainerViewModel.class);

        deleteContainerViewModel = new ViewModelProvider(this)
                .get(DeleteContainerViewModel.class);
        btnNewLearningSpace.setOnClickListener(v ->
                showCreateContainerDialog()
        );

        observeContainers();
        observeCreateContainer();
        observeEditContainer();
        observeDeleteContainer();

        homeViewModel.loadContainers();
    }

    private void observeContainers() {

        homeViewModel.getContainers().observe(this, containers -> {

            containerAdapter.setContainers(containers);

            containersEmpty =
                    containers == null || containers.isEmpty();

            updateEmptyState();
        });

        homeViewModel.getLoading().observe(this, isLoading -> {

            boolean loading = Boolean.TRUE.equals(isLoading);

            progressBar.setVisibility(
                    loading ? View.VISIBLE : View.GONE
            );

            if (loading) {
                containerLoadInProgress = true;
                emptyLearningContainer.setVisibility(View.GONE);
            } else if (containerLoadInProgress) {
                // Only show the empty state after the first load finishes,
                // so it does not flash before the request starts.
                containerLoadInProgress = false;
                containersLoaded = true;
                updateEmptyState();
            }
        });

        homeViewModel.getError().observe(this, error -> {

            boolean hasError = error != null && !error.isEmpty();

            tvHomeError.setVisibility(
                    hasError ? View.VISIBLE : View.GONE
            );

            tvHomeError.setText(hasError ? error : "");

            updateEmptyState();
        });
    }

    private void updateEmptyState() {

        String error = homeViewModel.getError().getValue();

        boolean hasError = error != null && !error.isEmpty();

        boolean isLoading = Boolean.TRUE.equals(
                homeViewModel.getLoading().getValue()
        );

        emptyLearningContainer.setVisibility(
                containersLoaded
                        && containersEmpty
                        && !isLoading
                        && !hasError
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    private void observeCreateContainer() {

        createContainerViewModel.getLoading().observe(this, isLoading -> {

            boolean loading = Boolean.TRUE.equals(isLoading);

            if (createProgressBar != null) {
                createProgressBar.setVisibility(
                        loading ? View.VISIBLE : View.GONE
                );
            }

            if (btnCreate != null) {
                btnCreate.setEnabled(!loading);
                btnCreate.setText(
                        loading ? getString(R.string.create_space_creating) : getString(R.string.create_space_action)
                );
            }

            if (btnCancel != null) {
                btnCancel.setEnabled(!loading);
            }

            if (btnNewLearningSpace != null) {
                btnNewLearningSpace.setEnabled(!loading);
            }
        });

        createContainerViewModel.getError().observe(this, error -> {

            if (error == null || error.isEmpty()) {
                if (tvCreateError != null) {
                    tvCreateError.setVisibility(View.GONE);
                    tvCreateError.setText("");
                }
                return;
            }

            if (createDialog != null && createDialog.isShowing()
                    && tvCreateError != null) {

                tvCreateError.setText(error);
                tvCreateError.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(
                        this,
                        error,
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        createContainerViewModel.getCreatedContainer()
                .observe(this, container -> {

                    if (container == null) {
                        return;
                    }

                    // Consume the result before handling it.
                    createContainerViewModel.clearCreatedContainer();

                    if (createDialog != null && createDialog.isShowing()) {
                        createDialog.dismiss();
                    }

                    Toast.makeText(
                            this,
                            getString(R.string.create_space_success),
                            Toast.LENGTH_SHORT
                    ).show();

                    homeViewModel.loadContainers();
                });

    }

    private void showCreateContainerDialog() {

        if (createDialog != null && createDialog.isShowing()) {
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int horizontalPadding = dp(24);
        int verticalPadding = dp(8);

        layout.setPadding(
                horizontalPadding,
                verticalPadding,
                horizontalPadding,
                dp(4)
        );

        TextView description = new TextView(this);
        description.setText(
                getString(R.string.create_space_description)
        );
        description.setTextSize(14);
        description.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_home));
        description.setLineSpacing(dp(3), 1f);

        layout.addView(description);

        etContainerName = new EditText(this);
        etContainerName.setSingleLine(true);
        etContainerName.setTextSize(15);
        etContainerName.setHint(getString(R.string.create_space_name_hint));
        etContainerName.setHintTextColor(ContextCompat.getColor(this, R.color.text_hint));
        etContainerName.setTextColor(ContextCompat.getColor(this, R.color.text_primary_navy));
        etContainerName.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.brand_indigo))
        );
        etContainerName.setPadding(dp(4), dp(10), dp(4), dp(10));

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        nameParams.topMargin = dp(18);

        layout.addView(etContainerName, nameParams);

        etContainerDescription = new EditText(this);
        etContainerDescription.setTextSize(15);
        etContainerDescription.setHint(getString(R.string.description_optional_hint));
        etContainerDescription.setHintTextColor(ContextCompat.getColor(this, R.color.text_hint));
        etContainerDescription.setTextColor(ContextCompat.getColor(this, R.color.text_primary_navy));
        etContainerDescription.setGravity(
                android.view.Gravity.TOP | android.view.Gravity.START
        );
        etContainerDescription.setMinLines(2);
        etContainerDescription.setMaxLines(4);
        etContainerDescription.setPadding(dp(4), dp(10), dp(4), dp(10));
        etContainerDescription.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.border_input_soft))
        );

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        descriptionParams.topMargin = dp(12);

        layout.addView(etContainerDescription, descriptionParams);

        tvCreateError = new TextView(this);
        tvCreateError.setTextColor(ContextCompat.getColor(this, R.color.error_text_dark));
        tvCreateError.setTextSize(13);
        tvCreateError.setVisibility(View.GONE);
        tvCreateError.setPadding(0, dp(8), 0, 0);

        layout.addView(tvCreateError);

        createProgressBar = new ProgressBar(this);
        createProgressBar.setIndeterminate(true);
        createProgressBar.setIndeterminateTintList(
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.brand_indigo))
        );
        createProgressBar.setVisibility(View.GONE);

        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(
                        dp(28),
                        dp(28)
                );
        progressParams.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        progressParams.topMargin = dp(12);

        layout.addView(createProgressBar, progressParams);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.create_space_title))
                .setView(layout)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.create_space_action), null);

        createDialog = builder.create();

        createDialog.setOnShowListener(dialog -> {

            btnCreate = (MaterialButton) createDialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            btnCancel = (MaterialButton) createDialog.getButton(
                    AlertDialog.BUTTON_NEGATIVE
            );

            btnCreate.setTextColor(ContextCompat.getColor(this, R.color.brand_indigo));
            btnCancel.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_home));

            btnCancel.setOnClickListener(v -> createDialog.dismiss());

            btnCreate.setOnClickListener(v -> {

                String name = etContainerName.getText()
                        .toString()
                        .trim();

                String descriptionText =
                        etContainerDescription.getText()
                                .toString()
                                .trim();

                if (name.isEmpty()) {
                    etContainerName.setError(
                            getString(R.string.space_name_required)
                    );
                    etContainerName.requestFocus();
                    return;
                }

                createContainerViewModel.createContainer(
                        name,
                        descriptionText
                );
            });

            if (Boolean.TRUE.equals(
                    createContainerViewModel.getLoading().getValue()
            )) {
                btnCreate.setEnabled(false);
                btnCancel.setEnabled(false);
                createProgressBar.setVisibility(View.VISIBLE);
                btnCreate.setText(getString(R.string.create_space_creating));
            }

            etContainerName.requestFocus();

            createDialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
        });

        createDialog.show();
        createDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private void observeEditContainer() {

        editContainerViewModel.getLoading().observe(this, isLoading -> {

            boolean loading = Boolean.TRUE.equals(isLoading);

            if (editProgressBar != null) {
                editProgressBar.setVisibility(
                        loading ? View.VISIBLE : View.GONE
                );
            }

            if (btnSaveEdit != null) {
                btnSaveEdit.setEnabled(!loading);
                btnSaveEdit.setText(
                        loading ? getString(R.string.saving) : getString(R.string.save_changes)
                );
            }

            if (btnCancelEdit != null) {
                btnCancelEdit.setEnabled(!loading);
            }
        });

        editContainerViewModel.getError().observe(this, error -> {

            if (error == null || error.isEmpty()) {
                if (tvEditError != null) {
                    tvEditError.setVisibility(View.GONE);
                    tvEditError.setText("");
                }
                return;
            }

            if (editDialog != null && editDialog.isShowing()
                    && tvEditError != null) {

                tvEditError.setText(error);
                tvEditError.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(
                        this,
                        error,
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        editContainerViewModel.getUpdated().observe(this, updated -> {

            if (!Boolean.TRUE.equals(updated)) {
                return;
            }

            // Consume the result to prevent repeated success messages.
            editContainerViewModel.clearUpdated();

            if (editDialog != null && editDialog.isShowing()) {
                editDialog.dismiss();
            }

            Toast.makeText(
                    this,
                    getString(R.string.space_update_success),
                    Toast.LENGTH_SHORT
            ).show();

            homeViewModel.loadContainers();
        });

    }

    private void showEditContainerDialog(ContainerResponse container) {

        if (editDialog != null && editDialog.isShowing()) {
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int horizontalPadding = dp(24);

        layout.setPadding(
                horizontalPadding,
                dp(8),
                horizontalPadding,
                dp(4)
        );

        TextView description = new TextView(this);
        description.setText(getString(R.string.edit_space_description));
        description.setTextSize(14);
        description.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_home));

        layout.addView(description);

        etEditContainerName = new EditText(this);
        etEditContainerName.setSingleLine(true);
        etEditContainerName.setTextSize(15);
        etEditContainerName.setHint(getString(R.string.edit_space_name_hint));
        etEditContainerName.setTextColor(ContextCompat.getColor(this, R.color.text_primary_navy));
        etEditContainerName.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.brand_indigo))
        );
        etEditContainerName.setPadding(dp(4), dp(10), dp(4), dp(10));

        etEditContainerName.setText(container.getName());

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        nameParams.topMargin = dp(18);
        layout.addView(etEditContainerName, nameParams);

        etEditContainerDescription = new EditText(this);
        etEditContainerDescription.setTextSize(15);
        etEditContainerDescription.setHint(getString(R.string.description_optional_hint));
        etEditContainerDescription.setTextColor(ContextCompat.getColor(this, R.color.text_primary_navy));
        etEditContainerDescription.setGravity(
                android.view.Gravity.TOP | android.view.Gravity.START
        );
        etEditContainerDescription.setMinLines(2);
        etEditContainerDescription.setMaxLines(4);
        etEditContainerDescription.setPadding(
                dp(4), dp(10), dp(4), dp(10)
        );

        etEditContainerDescription.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.border_input_soft))
        );

        String currentDescription = container.getDescription();

        if (currentDescription != null) {
            etEditContainerDescription.setText(currentDescription);
        }

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        descriptionParams.topMargin = dp(12);
        layout.addView(
                etEditContainerDescription,
                descriptionParams
        );

        tvEditError = new TextView(this);
        tvEditError.setTextColor(ContextCompat.getColor(this, R.color.error_text_dark));
        tvEditError.setTextSize(13);
        tvEditError.setVisibility(View.GONE);
        tvEditError.setPadding(0, dp(8), 0, 0);

        layout.addView(tvEditError);

        editProgressBar = new ProgressBar(this);
        editProgressBar.setIndeterminate(true);
        editProgressBar.setIndeterminateTintList(
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.brand_indigo))
        );
        editProgressBar.setVisibility(View.GONE);

        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(dp(28), dp(28));

        progressParams.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        progressParams.topMargin = dp(12);

        layout.addView(editProgressBar, progressParams);

        editDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.edit_space_title))
                .setView(layout)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.save_changes), null)
                .create();

        editDialog.setOnShowListener(dialog -> {

            btnSaveEdit = (MaterialButton) editDialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            btnCancelEdit = (MaterialButton) editDialog.getButton(
                    AlertDialog.BUTTON_NEGATIVE
            );

            btnSaveEdit.setTextColor(ContextCompat.getColor(this, R.color.brand_indigo));
            btnCancelEdit.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_home));

            btnCancelEdit.setOnClickListener(v -> editDialog.dismiss());

            btnSaveEdit.setOnClickListener(v -> {

                String name = etEditContainerName.getText()
                        .toString()
                        .trim();

                String descriptionText =
                        etEditContainerDescription.getText()
                                .toString()
                                .trim();

                if (name.isEmpty()) {
                    etEditContainerName.setError(
                            getString(R.string.space_name_required)
                    );
                    etEditContainerName.requestFocus();
                    return;
                }

                editContainerViewModel.updateContainer(
                        container.getId(),
                        name,
                        descriptionText
                );
            });

            if (Boolean.TRUE.equals(
                    editContainerViewModel.getLoading().getValue()
            )) {
                btnSaveEdit.setEnabled(false);
                btnCancelEdit.setEnabled(false);
                editProgressBar.setVisibility(View.VISIBLE);
                btnSaveEdit.setText(getString(R.string.saving));
            }
        });

        editDialog.show();

        editDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void showDeleteContainerDialog(ContainerResponse container) {

        if (deleteDialog != null && deleteDialog.isShowing()) {
            return;
        }

        deleteDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_space_title))
                .setMessage(
                        getString(R.string.delete_space_message, container.getName())
                )
                .setNegativeButton(getString(R.string.action_cancel), (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(getString(R.string.action_delete), (dialog, which) -> {
                    deleteContainerViewModel.deleteContainer(
                            container.getId()
                    );
                })
                .create();

        deleteDialog.setOnShowListener(dialog -> {

            deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.error_text_dark));

            deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.text_secondary_home));
        });

        deleteDialog.show();
    }

    private void observeDeleteContainer() {

        deleteContainerViewModel.getError().observe(this, error -> {

            if (error == null || error.isEmpty()) {
                return;
            }

            Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
            ).show();
        });

        deleteContainerViewModel.getDeleted().observe(this, deleted -> {

            if (!Boolean.TRUE.equals(deleted)) {
                return;
            }

            // Consume the result to prevent repeated success messages.
            deleteContainerViewModel.clearDeleted();

            if (deleteDialog != null && deleteDialog.isShowing()) {
                deleteDialog.dismiss();
            }

            Toast.makeText(
                    this,
                    getString(R.string.space_delete_success),
                    Toast.LENGTH_SHORT
            ).show();

            homeViewModel.loadContainers();
        });

    }


}
