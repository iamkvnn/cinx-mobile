import sys

with open("app/src/main/java/com/app/cinx/adapter/EditSectionAdapter.java", "r", encoding="utf-8") as f:
    content = f.read()

old_dialog = """    private void showAddLessonDialog(EditableSection section, SectionViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thêm bài học mới");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Tên bài học");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                EditableLesson newLesson = new EditableLesson();
                newLesson.title = newTitle;
                newLesson.lessonType = "video"; // Default
                section.lessons.add(newLesson);
                if (holder.rvLessons.getAdapter() != null) {
                    holder.rvLessons.getAdapter().notifyItemInserted(section.lessons.size() - 1);
                }
            } else {
                Toast.makeText(context, "Tên bài học không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());   

        builder.show();
    }"""

new_dialog = """    private void showAddLessonDialog(EditableSection section, SectionViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thêm bài học mới");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Tên bài học");
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

        builder.setView(layout);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                EditableLesson newLesson = new EditableLesson();
                newLesson.title = newTitle;
                
                int selectedType = typeSpinner.getSelectedItemPosition();
                if (selectedType == 0) newLesson.lessonType = "VIDEO";
                else if (selectedType == 1) newLesson.lessonType = "QUIZ";
                else if (selectedType == 2) newLesson.lessonType = "ARTICLE";
                else if (selectedType == 3) newLesson.lessonType = "ASSIGNMENT";

                section.lessons.add(newLesson);
                if (holder.rvLessons.getAdapter() != null) {
                    holder.rvLessons.getAdapter().notifyItemInserted(section.lessons.size() - 1);
                }
            } else {
                Toast.makeText(context, "Tên bài học không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());   

        builder.show();
    }"""

content = content.replace(old_dialog, new_dialog)

with open("app/src/main/java/com/app/cinx/adapter/EditSectionAdapter.java", "w", encoding="utf-8") as f:
    f.write(content)
print("Done")