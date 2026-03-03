package com.app.cinx.data;

import com.app.cinx.model.Chapter;
import com.app.cinx.model.Lesson;
import com.app.cinx.model.LessonType;
import com.app.cinx.model.QuizOption;
import com.app.cinx.model.QuizQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides sample course curriculum data.
 * In a real app, replace this with a Repository + API/Room data source.
 */
public class SampleCourseData {

    /**
     * Returns a flat, ordered list of all lessons in the sample Figma course.
     * Use {@link #getChapters()} for grouped/chapter display.
     */
    public static List<Lesson> getLessons() {
        List<Lesson> all = new ArrayList<>();
        for (Chapter c : getChapters()) {
            all.addAll(c.getLessons());
        }
        return all;
    }

    /** Returns chapters with their lessons for the demo Figma design course. */
    public static List<Chapter> getChapters() {
        // ---- Chapter 1: Nhập môn ----
        List<Lesson> ch1Lessons = new ArrayList<>();
        ch1Lessons.add(new Lesson.Builder(1, "UI và UX là gì?", LessonType.VIDEO)
                .chapterNumber(1).chapterTitle("Chương 1: Nhập môn")
                .lessonNumber(1).duration("12:30").completed(true).preview(true)
                .videoThumbnailUrl("https://images.unsplash.com/photo-1611162617474-5b21e879e113?q=80&w=800")
                .build());

        ch1Lessons.add(new Lesson.Builder(2, "Tư duy thiết kế (Design Thinking)", LessonType.DOCUMENT)
                .chapterNumber(1).chapterTitle("Chương 1: Nhập môn")
                .lessonNumber(2).duration("8 phút").completed(true)
                .documentHtml(buildDocumentHtml())
                .build());

        // ---- Chapter 2: Làm quen Figma ----
        List<Lesson> ch2Lessons = new ArrayList<>();
        ch2Lessons.add(new Lesson.Builder(3, "Giao diện cơ bản của Figma", LessonType.VIDEO)
                .chapterNumber(2).chapterTitle("Chương 2: Làm quen với Figma")
                .lessonNumber(1).duration("12:30").active(true)
                .videoThumbnailUrl("https://images.unsplash.com/photo-1611162617474-5b21e879e113?q=80&w=800")
                .build());

        ch2Lessons.add(new Lesson.Builder(4, "Nguyên tắc phối màu trong UI Design", LessonType.DOCUMENT)
                .chapterNumber(2).chapterTitle("Chương 2: Làm quen với Figma")
                .lessonNumber(2).duration("5 phút")
                .documentHtml(buildDocumentHtml())
                .build());

        ch2Lessons.add(new Lesson.Builder(5, "Quiz: Kiểm tra Chương 2", LessonType.QUIZ)
                .chapterNumber(2).chapterTitle("Chương 2: Làm quen với Figma")
                .lessonNumber(3).duration("10 câu")
                .questions(buildQuizQuestions())
                .build());

        // ---- Chapter 3: Auto Layout (locked) ----
        List<Lesson> ch3Lessons = new ArrayList<>();
        ch3Lessons.add(new Lesson.Builder(6, "Căn bản Auto Layout", LessonType.VIDEO)
                .chapterNumber(3).chapterTitle("Chương 3: Auto Layout")
                .lessonNumber(1).duration("15:00").locked(true)
                .videoThumbnailUrl("https://images.unsplash.com/photo-1611162617474-5b21e879e113?q=80&w=800")
                .build());

        ch3Lessons.add(new Lesson.Builder(7, "Auto Layout nâng cao", LessonType.VIDEO)
                .chapterNumber(3).chapterTitle("Chương 3: Auto Layout")
                .lessonNumber(2).duration("18:00").locked(true)
                .videoThumbnailUrl("https://images.unsplash.com/photo-1611162617474-5b21e879e113?q=80&w=800")
                .build());

        ch3Lessons.add(new Lesson.Builder(8, "Quiz: Auto Layout", LessonType.QUIZ)
                .chapterNumber(3).chapterTitle("Chương 3: Auto Layout")
                .lessonNumber(3).duration("5 câu").locked(true)
                .questions(buildQuizQuestions())
                .build());

        return Arrays.asList(
                new Chapter(1, "CHƯƠNG 1: NHẬP MÔN", ch1Lessons),
                new Chapter(2, "CHƯƠNG 2: LÀM QUEN FIGMA", ch2Lessons),
                new Chapter(3, "CHƯƠNG 3: AUTO LAYOUT", ch3Lessons)
        );
    }

    // ------------------------------------------------------------------ //
    //  Sample quiz questions
    // ------------------------------------------------------------------ //
    private static List<QuizQuestion> buildQuizQuestions() {
        return Arrays.asList(
                new QuizQuestion(1,
                        "Trong Figma, phím tắt nào được sử dụng để chuyển đối tượng thành một Component?",
                        Arrays.asList(
                                new QuizOption("Ctrl + G / Cmd + G", false),
                                new QuizOption("Ctrl + Alt + K / Cmd + Option + K", true),
                                new QuizOption("Shift + A", false),
                                new QuizOption("Ctrl + Shift + K / Cmd + Shift + K", false)
                        )),

                new QuizQuestion(2,
                        "Panel nào trong Figma hiển thị các lớp (layers) của bản thiết kế?",
                        Arrays.asList(
                                new QuizOption("Right Sidebar", false),
                                new QuizOption("Toolbar", false),
                                new QuizOption("Left Sidebar", true),
                                new QuizOption("Bottom Bar", false)
                        )),

                new QuizQuestion(3,
                        "Tỷ lệ màu trong quy tắc 60-30-10 là gì?",
                        Arrays.asList(
                                new QuizOption("60% Primary, 30% Accent, 10% Secondary", false),
                                new QuizOption("60% Primary, 30% Secondary, 10% Accent", true),
                                new QuizOption("60% White, 30% Primary, 10% Secondary", false),
                                new QuizOption("50% Primary, 30% Secondary, 20% Accent", false)
                        )),

                new QuizQuestion(4,
                        "WCAG là viết tắt của gì?",
                        Arrays.asList(
                                new QuizOption("Web Color Accessibility Guidelines", false),
                                new QuizOption("Web Content Accessibility Guidelines", true),
                                new QuizOption("World Content Adjustment Guide", false),
                                new QuizOption("Web Component Accessibility Guide", false)
                        )),

                new QuizQuestion(5,
                        "Để nhóm các đối tượng trong Figma, bạn dùng tổ hợp phím nào?",
                        Arrays.asList(
                                new QuizOption("Ctrl + K / Cmd + K", false),
                                new QuizOption("Ctrl + Alt + G / Cmd + Option + G", false),
                                new QuizOption("Ctrl + G / Cmd + G", true),
                                new QuizOption("Ctrl + Shift + G / Cmd + Shift + G", false)
                        ))
        );
    }

    // ------------------------------------------------------------------ //
    //  Sample document HTML (rich-text reading content)
    // ------------------------------------------------------------------ //
    private static String buildDocumentHtml() {
        return "<h1>Nguyên tắc phối màu trong UI Design</h1>"
                + "<p>Màu sắc là một trong những yếu tố quan trọng nhất trong thiết kế giao diện. "
                + "Nó không chỉ tạo ra tính thẩm mỹ mà còn điều hướng hành vi của người dùng và "
                + "truyền tải thông điệp thương hiệu.</p>"

                + "<h2>1. Quy tắc 60-30-10</h2>"
                + "<p>Đây là quy tắc kinh điển trong thiết kế nội thất và hoàn toàn áp dụng tốt "
                + "cho UI Design. Tỷ lệ này giúp giao diện cân bằng, không bị rối mắt.</p>"
                + "<ul>"
                + "<li><b>60% Màu chủ đạo (Primary Color):</b> Thường là màu nền, màu của các mảng lớn.</li>"
                + "<li><b>30% Màu thứ cấp (Secondary Color):</b> Hỗ trợ màu chủ đạo, dùng cho các card.</li>"
                + "<li><b>10% Màu nhấn (Accent Color):</b> Dùng cho các nút bấm (Call to action), thông báo.</li>"
                + "</ul>"

                + "<h2>2. Tương phản (Contrast)</h2>"
                + "<p>Đảm bảo độ tương phản giữa chữ và nền đạt chuẩn WCAG (Web Content Accessibility "
                + "Guidelines). Text tối trên nền sáng hoặc ngược lại để đảm bảo khả năng đọc tốt nhất.</p>"
                + "<blockquote>💡 <b>Mẹo:</b> Sử dụng các công cụ như Coolors hoặc plugin Contrast "
                + "trong Figma để kiểm tra nhanh tỷ lệ tương phản của bạn.</blockquote>"

                + "<h2>3. Tâm lý màu sắc</h2>"
                + "<p>Mỗi màu sắc mang một cảm xúc và thông điệp khác nhau:</p>"
                + "<ul>"
                + "<li><b>Xanh dương:</b> Tin tưởng, chuyên nghiệp, bình tĩnh.</li>"
                + "<li><b>Đỏ:</b> Khẩn cấp, năng lượng, cảnh báo.</li>"
                + "<li><b>Xanh lá:</b> Tự nhiên, thành công, phát triển.</li>"
                + "<li><b>Tím:</b> Sáng tạo, sang trọng, thần bí.</li>"
                + "<li><b>Cam:</b> Thân thiện, nhiệt tình, sáng tạo.</li>"
                + "</ul>"

                + "<h2>4. Chế độ tối (Dark Mode)</h2>"
                + "<p>Khi thiết kế dark mode, tránh dùng màu đen thuần (#000000) làm nền vì sẽ gây "
                + "cảm giác khó chịu. Thay vào đó, dùng các sắc độ tối của màu neutral như #121212 hay #1E1E1E.</p>";
    }
}
