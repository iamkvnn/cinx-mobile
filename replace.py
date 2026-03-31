import re

with open("app/src/main/java/com/app/cinx/activity/CourseOutlineActivity.java", "r", encoding="utf-8") as f:
    content = f.read()

new_func = """private void showAddLessonDialog(SectionResponse section) {
        String[] types = {"Video", "Article", "Quiz", "Assignment"};
        new AlertDialog.Builder(this)
                .setTitle("Chon loai bai hoc")
                .setItems(types, (dialog1, which1) -> {
                    String selectedType = "VIDEO";
                    if(which1 == 1) selectedType = "ARTICLE";
                    if(which1 == 2) selectedType = "QUIZ";
                    if(which1 == 3) selectedType = "ASSIGNMENT";
                    String finalSelectedType = selectedType;
                    
                    EditText input = new EditText(this);
                    input.setHint("Ten bai hoc");
                    new AlertDialog.Builder(this)
                            .setTitle("Them bai hoc (" + finalSelectedType + ")")
                            .setView(input)
                            .setPositiveButton("Them", (dialog2, which2) -> {
                                String title = input.getText().toString().trim();
                                if (!title.isEmpty()) {
                                    UpdateCourseRequest req = buildUpdateReq();
                                    for (UpdateSectionRequest us : req.getSections()) {
                                        if (us.getId() != null && us.getId().equals(section.getId())) {
                                            UpdateLessonRequest newLess = new UpdateLessonRequest();
                                            newLess.setTitle(title);
                                            newLess.setLessonType(finalSelectedType);
                                            newLess.setOrderIndex(us.getLessons() == null ? 0 : us.getLessons().size());
                                            if (us.getLessons() == null) {
                                                us.setLessons(new java.util.ArrayList<>());
                                            }
                                            us.getLessons().add(newLess);
                                        }
                                    }
                                    saveToServer(req);
                                }
                            })
                            .setNegativeButton("Huy", null)
                            .show();
                }).show();
    }"""

# A simple regex to replace the function
content = re.sub(r"private void showAddLessonDialog\(SectionResponse section\) \{.*?.show\(\);\n    \}", new_func, content, flags=re.DOTALL)

with open("app/src/main/java/com/app/cinx/activity/CourseOutlineActivity.java", "w", encoding="utf-8") as f:
    f.write(content)

