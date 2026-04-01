package com.app.cinx.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.EditableLesson;

import java.util.Collections;
import java.util.List;

public class EditLessonAdapter extends RecyclerView.Adapter<EditLessonAdapter.LessonViewHolder> {

    private final Context context;
    private final List<EditableLesson> lessonList;
    private ItemTouchHelper itemTouchHelper;

    public EditLessonAdapter(Context context, List<EditableLesson> lessonList) {
        this.context = context;
        this.lessonList = lessonList;
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lesson_edit, parent, false);
        return new LessonViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        EditableLesson lesson = lessonList.get(position);
        holder.tvLessonTitle.setText(lesson.title);
        holder.tvLessonType.setText(lesson.lessonType);

        holder.imgLessonDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && itemTouchHelper != null) {
                itemTouchHelper.startDrag(holder);
            }
            return false;
        });

        holder.btnEditLesson.setOnClickListener(v -> showEditLessonDialog(position));
        holder.btnDeleteLesson.setOnClickListener(v -> {
            lessonList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    private void showEditLessonDialog(int position) {
        EditableLesson lesson = lessonList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa bài học");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(lesson.title);
        layout.addView(input);

        final android.widget.Spinner typeSpinner = new android.widget.Spinner(context);
        String[] types = {"Video", "Quiz", "Bài viết", "Bài tập"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(context, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 20;
        typeSpinner.setLayoutParams(params);
        layout.addView(typeSpinner);

        String currentType = lesson.lessonType != null ? lesson.lessonType.toUpperCase() : "VIDEO";
        if (currentType.equals("VIDEO")) typeSpinner.setSelection(0);
        else if (currentType.equals("QUIZ")) typeSpinner.setSelection(1);
        else if (currentType.equals("ARTICLE")) typeSpinner.setSelection(2);
        else if (currentType.equals("ASSIGNMENT")) typeSpinner.setSelection(3);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                lesson.title = newTitle;
                
                int selectedType = typeSpinner.getSelectedItemPosition();
                if (selectedType == 0) lesson.lessonType = "VIDEO";
                else if (selectedType == 1) lesson.lessonType = "QUIZ";
                else if (selectedType == 2) lesson.lessonType = "ARTICLE";
                else if (selectedType == 3) lesson.lessonType = "ASSIGNMENT";

                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Tên bài học không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(lessonList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(lessonList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLessonDragHandle, btnEditLesson, btnDeleteLesson;
        TextView tvLessonType, tvLessonTitle;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLessonDragHandle = itemView.findViewById(R.id.imgLessonDragHandle);
            tvLessonType = itemView.findViewById(R.id.tvLessonType);
            tvLessonTitle = itemView.findViewById(R.id.tvLessonTitle);
            btnEditLesson = itemView.findViewById(R.id.btnEditLesson);
            btnDeleteLesson = itemView.findViewById(R.id.btnDeleteLesson);
        }
    }
}