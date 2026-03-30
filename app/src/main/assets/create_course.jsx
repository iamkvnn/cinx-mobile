import React, { useState, useRef } from 'react';
import { 
  BookOpen, Plus, Edit2, Trash2, GripVertical, 
  ChevronLeft, Save, Video, FileText, ChevronUp, 
  ChevronDown, LayoutList, Info, PlayCircle, Image as ImageIcon, X
} from 'lucide-react';

// --- MOCK DATA ---
const initialCourses = [
  {
    id: 'c1',
    title: 'React Native thực chiến cho người mới',
    description: 'Học cách xây dựng ứng dụng di động đa nền tảng với React Native và Expo.',
    price: '1,200,000',
    status: 'published',
    thumbnail: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    chapters: [
      {
        id: 'ch1',
        title: 'Chương 1: Khởi đầu',
        lessons: [
          { id: 'l1', title: '1. Giới thiệu khóa học', type: 'video' },
          { id: 'l2', title: '2. Cài đặt môi trường', type: 'text' }
        ]
      },
      {
        id: 'ch2',
        title: 'Chương 2: Core Components',
        lessons: [
          { id: 'l3', title: '3. View, Text & Image', type: 'video' },
          { id: 'l4', title: '4. Xử lý sự kiện với Button', type: 'video' }
        ]
      }
    ]
  }
];

// --- COMPONENTS ---

// Modal đơn giản để nhập liệu (Thêm/Sửa Tên Chương, Bài học)
const PromptModal = ({ isOpen, title, defaultValue, placeholder, onConfirm, onCancel }) => {
  const [value, setValue] = useState(defaultValue || '');

  React.useEffect(() => {
    setValue(defaultValue || '');
  }, [defaultValue, isOpen]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-slate-900/50 z-50 flex items-center justify-center p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl w-full max-w-sm overflow-hidden shadow-xl">
        <div className="p-4 border-b border-slate-100 flex justify-between items-center">
          <h3 className="font-semibold text-slate-800">{title}</h3>
          <button onClick={onCancel} className="p-1 text-slate-400 hover:text-slate-600 rounded-full">
            <X size={20} />
          </button>
        </div>
        <div className="p-4">
          <input
            type="text"
            autoFocus
            className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-slate-800"
            placeholder={placeholder}
            value={value}
            onChange={(e) => setValue(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onConfirm(value)}
          />
        </div>
        <div className="p-4 bg-slate-50 flex justify-end gap-3">
          <button onClick={onCancel} className="px-4 py-2 font-medium text-slate-600 bg-white border border-slate-300 rounded-xl hover:bg-slate-50">
            Hủy
          </button>
          <button 
            onClick={() => onConfirm(value)} 
            disabled={!value.trim()}
            className="px-4 py-2 font-medium text-white bg-indigo-600 rounded-xl hover:bg-indigo-700 disabled:opacity-50"
          >
            Xác nhận
          </button>
        </div>
      </div>
    </div>
  );
};

export default function App() {
  const [courses, setCourses] = useState(initialCourses);
  const [currentView, setCurrentView] = useState('list'); // 'list' | 'edit'
  const [editingCourse, setEditingCourse] = useState(null);

  const handleEditCourse = (course) => {
    setEditingCourse(JSON.parse(JSON.stringify(course))); // Deep copy
    setCurrentView('edit');
  };

  const handleCreateCourse = () => {
    const newCourse = {
      id: `c_${Date.now()}`,
      title: 'Khóa học mới',
      description: '',
      price: '',
      status: 'draft',
      chapters: []
    };
    setEditingCourse(newCourse);
    setCurrentView('edit');
  };

  const handleSaveCourse = (updatedCourse) => {
    const isExisting = courses.some(c => c.id === updatedCourse.id);
    if (isExisting) {
      setCourses(courses.map(c => c.id === updatedCourse.id ? updatedCourse : c));
    } else {
      setCourses([updatedCourse, ...courses]);
    }
    setCurrentView('list');
  };

  const handleDeleteCourse = (id) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa khóa học này?')) {
      setCourses(courses.filter(c => c.id !== id));
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 font-sans text-slate-800 selection:bg-indigo-100 selection:text-indigo-900">
      <div className="max-w-md mx-auto bg-white min-h-screen shadow-2xl relative sm:max-w-2xl lg:max-w-4xl border-x border-slate-200">
        {currentView === 'list' ? (
          <CourseList 
            courses={courses} 
            onCreate={handleCreateCourse} 
            onEdit={handleEditCourse}
            onDelete={handleDeleteCourse}
          />
        ) : (
          <CourseEditor 
            course={editingCourse} 
            onBack={() => setCurrentView('list')}
            onSave={handleSaveCourse}
          />
        )}
      </div>
    </div>
  );
}

// --- VIEW: Danh sách khóa học ---
const CourseList = ({ courses, onCreate, onEdit, onDelete }) => {
  return (
    <div className="flex flex-col h-full min-h-screen pb-24">
      {/* Header */}
      <div className="bg-indigo-600 text-white p-6 pt-12 rounded-b-3xl shadow-md z-10 sticky top-0">
        <h1 className="text-2xl font-bold mb-1">Quản lý giảng dạy</h1>
        <p className="text-indigo-200 text-sm">Chào mừng bạn trở lại, Giảng viên!</p>
      </div>

      {/* Stats/Quick Info */}
      <div className="px-5 py-4 flex gap-4 -mt-6 z-20">
        <div className="bg-white p-4 rounded-2xl shadow-sm flex-1 border border-slate-100 flex items-center gap-3">
          <div className="bg-indigo-100 p-2.5 rounded-xl text-indigo-600">
            <BookOpen size={24} />
          </div>
          <div>
            <p className="text-2xl font-bold text-slate-800">{courses.length}</p>
            <p className="text-xs text-slate-500 font-medium uppercase tracking-wider">Khóa học</p>
          </div>
        </div>
      </div>

      {/* Course List */}
      <div className="px-5 py-2 flex-1 flex flex-col gap-4">
        <h2 className="text-lg font-bold text-slate-800 mb-2">Khóa học của bạn</h2>
        
        {courses.length === 0 ? (
          <div className="text-center py-12 px-4 bg-slate-50 rounded-2xl border-2 border-dashed border-slate-200">
            <BookOpen size={48} className="mx-auto text-slate-300 mb-3" />
            <p className="text-slate-500 mb-4">Bạn chưa có khóa học nào.</p>
            <button 
              onClick={onCreate}
              className="bg-indigo-600 text-white px-6 py-2.5 rounded-full font-medium shadow-sm hover:bg-indigo-700 active:scale-95 transition-all"
            >
              Tạo khóa học đầu tiên
            </button>
          </div>
        ) : (
          courses.map(course => (
            <div key={course.id} className="bg-white rounded-2xl p-4 shadow-sm border border-slate-100 flex flex-col gap-3 group relative overflow-hidden">
              <div className="flex gap-4">
                {/* Thumbnail */}
                <div className="w-24 h-24 rounded-xl bg-slate-100 overflow-hidden flex-shrink-0 relative border border-slate-100">
                  {course.thumbnail ? (
                    <img src={course.thumbnail} alt={course.title} className="w-full h-full object-cover" />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-slate-300">
                      <ImageIcon size={32} />
                    </div>
                  )}
                  <div className={`absolute top-0 right-0 m-1 w-3 h-3 rounded-full border-2 border-white ${course.status === 'published' ? 'bg-green-500' : 'bg-amber-400'}`}></div>
                </div>
                
                {/* Info */}
                <div className="flex-1">
                  <h3 className="font-bold text-slate-800 text-base leading-tight mb-1 line-clamp-2">{course.title}</h3>
                  <p className="text-sm text-slate-500 mb-2 line-clamp-1">{course.chapters?.length || 0} Chương</p>
                  <p className="text-indigo-600 font-semibold text-sm">{course.price ? `${course.price} ₫` : 'Miễn phí'}</p>
                </div>
              </div>

              {/* Actions */}
              <div className="flex gap-2 border-t border-slate-50 pt-3 mt-1">
                <button 
                  onClick={() => onEdit(course)}
                  className="flex-1 bg-indigo-50 text-indigo-700 py-2 rounded-xl text-sm font-semibold flex items-center justify-center gap-2 hover:bg-indigo-100 active:scale-95 transition-all"
                >
                  <Edit2 size={16} /> Chỉnh sửa
                </button>
                <button 
                  onClick={() => onDelete(course.id)}
                  className="p-2 text-rose-500 bg-rose-50 rounded-xl hover:bg-rose-100 active:scale-95 transition-all"
                >
                  <Trash2 size={20} />
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* FAB (Floating Action Button) for Mobile */}
      <button 
        onClick={onCreate}
        className="fixed bottom-6 right-6 lg:right-auto lg:ml-[780px] w-14 h-14 bg-indigo-600 text-white rounded-full shadow-lg shadow-indigo-200 flex items-center justify-center hover:bg-indigo-700 hover:scale-105 active:scale-95 transition-all z-50"
      >
        <Plus size={28} />
      </button>
    </div>
  );
};

// --- VIEW: Trình chỉnh sửa khóa học ---
const CourseEditor = ({ course, onBack, onSave }) => {
  const [editedCourse, setEditedCourse] = useState(course);
  const [activeTab, setActiveTab] = useState('curriculum'); // 'info' | 'curriculum'

  const handleUpdateInfo = (field, value) => {
    setEditedCourse(prev => ({ ...prev, [field]: value }));
  };

  const handleUpdateChapters = (newChapters) => {
    setEditedCourse(prev => ({ ...prev, chapters: newChapters }));
  };

  return (
    <div className="flex flex-col h-full min-h-screen bg-slate-50">
      {/* Sticky Header */}
      <div className="sticky top-0 z-40 bg-white border-b border-slate-200 shadow-sm px-4 pt-10 pb-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <button 
            onClick={onBack}
            className="p-2 -ml-2 text-slate-600 hover:bg-slate-100 rounded-full transition-colors"
          >
            <ChevronLeft size={24} />
          </button>
          <h2 className="font-bold text-lg text-slate-800 line-clamp-1 w-48 sm:w-auto">
            {editedCourse.title || 'Khóa học mới'}
          </h2>
        </div>
        <button 
          onClick={() => onSave(editedCourse)}
          className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-xl font-medium text-sm hover:bg-indigo-700 active:scale-95 transition-all shadow-sm"
        >
          <Save size={18} /> <span className="hidden sm:inline">Lưu lại</span>
        </button>
      </div>

      {/* Tabs */}
      <div className="bg-white px-4 pt-2 border-b border-slate-200 flex gap-4 sticky top-[72px] z-30">
        <button 
          onClick={() => setActiveTab('info')}
          className={`pb-3 px-2 text-sm font-semibold flex items-center gap-2 border-b-2 transition-colors ${activeTab === 'info' ? 'border-indigo-600 text-indigo-600' : 'border-transparent text-slate-500 hover:text-slate-700'}`}
        >
          <Info size={18} /> Thông tin
        </button>
        <button 
          onClick={() => setActiveTab('curriculum')}
          className={`pb-3 px-2 text-sm font-semibold flex items-center gap-2 border-b-2 transition-colors ${activeTab === 'curriculum' ? 'border-indigo-600 text-indigo-600' : 'border-transparent text-slate-500 hover:text-slate-700'}`}
        >
          <LayoutList size={18} /> Chương trình học
        </button>
      </div>

      {/* Content Area */}
      <div className="flex-1 overflow-y-auto p-4 sm:p-6 pb-32">
        {activeTab === 'info' ? (
          <div className="space-y-5 max-w-2xl mx-auto animate-in fade-in slide-in-from-bottom-2 duration-300">
            {/* Ảnh đại diện */}
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Ảnh bìa khóa học</label>
              <div className="w-full aspect-video bg-slate-100 rounded-2xl border-2 border-dashed border-slate-300 flex flex-col items-center justify-center text-slate-400 overflow-hidden relative group cursor-pointer hover:bg-slate-50 transition-colors">
                {editedCourse.thumbnail ? (
                  <>
                    <img src={editedCourse.thumbnail} alt="Thumbnail" className="w-full h-full object-cover" />
                    <div className="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                      <span className="text-white font-medium bg-black/50 px-4 py-2 rounded-lg">Thay đổi ảnh</span>
                    </div>
                  </>
                ) : (
                  <>
                    <ImageIcon size={40} className="mb-2" />
                    <span className="text-sm font-medium text-slate-500">Nhấn để tải ảnh lên</span>
                  </>
                )}
              </div>
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Tên khóa học</label>
              <input 
                type="text" 
                value={editedCourse.title}
                onChange={(e) => handleUpdateInfo('title', e.target.value)}
                className="w-full bg-white border border-slate-200 rounded-xl px-4 py-3 text-slate-800 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-shadow"
                placeholder="Nhập tên khóa học..."
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Mô tả ngắn</label>
              <textarea 
                value={editedCourse.description}
                onChange={(e) => handleUpdateInfo('description', e.target.value)}
                rows={4}
                className="w-full bg-white border border-slate-200 rounded-xl px-4 py-3 text-slate-800 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-shadow resize-none"
                placeholder="Khóa học này dành cho ai? Kết quả đạt được?..."
              />
            </div>

            <div className="flex gap-4">
              <div className="flex-1">
                <label className="block text-sm font-semibold text-slate-700 mb-2">Giá tiền (VNĐ)</label>
                <input 
                  type="text" 
                  value={editedCourse.price}
                  onChange={(e) => handleUpdateInfo('price', e.target.value)}
                  className="w-full bg-white border border-slate-200 rounded-xl px-4 py-3 text-slate-800 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-shadow"
                  placeholder="Ví dụ: 1,500,000"
                />
              </div>
              <div className="flex-1">
                <label className="block text-sm font-semibold text-slate-700 mb-2">Trạng thái</label>
                <select 
                  value={editedCourse.status}
                  onChange={(e) => handleUpdateInfo('status', e.target.value)}
                  className="w-full bg-white border border-slate-200 rounded-xl px-4 py-3 text-slate-800 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-shadow appearance-none"
                >
                  <option value="draft">Bản nháp</option>
                  <option value="published">Công khai</option>
                </select>
              </div>
            </div>
          </div>
        ) : (
          <CurriculumBuilder 
            chapters={editedCourse.chapters} 
            onChange={handleUpdateChapters} 
          />
        )}
      </div>
    </div>
  );
};

// --- TÍNH NĂNG KÉO THẢ & QUẢN LÝ CHƯƠNG TRÌNH ---
const CurriculumBuilder = ({ chapters, onChange }) => {
  const [modalConfig, setModalConfig] = useState({ isOpen: false, type: '', targetId: null, parentId: null, defaultValue: '' });
  
  // Refs cho kéo thả (Desktop)
  const dragChapterItem = useRef(null);
  const dragChapterOverItem = useRef(null);
  const dragLessonItem = useRef(null);
  const dragLessonOverItem = useRef(null);

  // --- Chức năng Modal ---
  const openModal = (type, defaultValue = '', targetId = null, parentId = null) => {
    setModalConfig({ isOpen: true, type, defaultValue, targetId, parentId });
  };

  const closeModal = () => {
    setModalConfig({ isOpen: false, type: '', targetId: null, parentId: null, defaultValue: '' });
  };

  const handleModalConfirm = (value) => {
    const { type, targetId, parentId } = modalConfig;
    let newChapters = [...chapters];

    if (type === 'add_chapter') {
      newChapters.push({ id: `ch_${Date.now()}`, title: value, lessons: [] });
    } else if (type === 'edit_chapter') {
      const idx = newChapters.findIndex(c => c.id === targetId);
      if (idx > -1) newChapters[idx].title = value;
    } else if (type === 'add_lesson') {
      const chapterIdx = newChapters.findIndex(c => c.id === parentId);
      if (chapterIdx > -1) {
        newChapters[chapterIdx].lessons.push({ id: `l_${Date.now()}`, title: value, type: 'video' });
      }
    } else if (type === 'edit_lesson') {
      const chapterIdx = newChapters.findIndex(c => c.id === parentId);
      if (chapterIdx > -1) {
        const lessonIdx = newChapters[chapterIdx].lessons.findIndex(l => l.id === targetId);
        if (lessonIdx > -1) newChapters[chapterIdx].lessons[lessonIdx].title = value;
      }
    }
    
    onChange(newChapters);
    closeModal();
  };

  // --- Quản lý Xóa ---
  const deleteChapter = (id) => {
    if(window.confirm('Xóa chương này sẽ xóa toàn bộ bài học bên trong. Trở lại?')) {
      onChange(chapters.filter(c => c.id !== id));
    }
  };

  const deleteLesson = (chapterId, lessonId) => {
    const newChapters = chapters.map(ch => {
      if (ch.id === chapterId) {
        return { ...ch, lessons: ch.lessons.filter(l => l.id !== lessonId) };
      }
      return ch;
    });
    onChange(newChapters);
  };

  const toggleLessonType = (chapterId, lessonId) => {
     const newChapters = chapters.map(ch => {
      if (ch.id === chapterId) {
        return { 
          ...ch, 
          lessons: ch.lessons.map(l => l.id === lessonId ? { ...l, type: l.type === 'video' ? 'text' : 'video' } : l) 
        };
      }
      return ch;
    });
    onChange(newChapters);
  }

  // --- Kéo Thả & Sắp Xếp (Mobile & Desktop) ---
  const moveChapter = (index, direction) => {
    if (index + direction < 0 || index + direction >= chapters.length) return;
    const newChapters = [...chapters];
    const temp = newChapters[index];
    newChapters[index] = newChapters[index + direction];
    newChapters[index + direction] = temp;
    onChange(newChapters);
  };

  const moveLesson = (chapterIndex, lessonIndex, direction) => {
    const newChapters = [...chapters];
    const lessons = newChapters[chapterIndex].lessons;
    if (lessonIndex + direction < 0 || lessonIndex + direction >= lessons.length) return;
    
    const temp = lessons[lessonIndex];
    lessons[lessonIndex] = lessons[lessonIndex + direction];
    lessons[lessonIndex + direction] = temp;
    onChange(newChapters);
  };

  const handleChapterDragSort = () => {
    const newChapters = [...chapters];
    const dragItemContent = newChapters.splice(dragChapterItem.current, 1)[0];
    newChapters.splice(dragChapterOverItem.current, 0, dragItemContent);
    dragChapterItem.current = null;
    dragChapterOverItem.current = null;
    onChange(newChapters);
  };

  const handleLessonDragSort = (chapterIndex) => {
    const newChapters = [...chapters];
    const lessons = newChapters[chapterIndex].lessons;
    const dragItemContent = lessons.splice(dragLessonItem.current, 1)[0];
    lessons.splice(dragLessonOverItem.current, 0, dragItemContent);
    dragLessonItem.current = null;
    dragLessonOverItem.current = null;
    onChange(newChapters);
  };

  return (
    <div className="max-w-2xl mx-auto pb-10 animate-in fade-in slide-in-from-bottom-2 duration-300">
      
      {/* Modal Reusable */}
      <PromptModal 
        isOpen={modalConfig.isOpen}
        title={
          modalConfig.type === 'add_chapter' ? 'Thêm Chương Mới' :
          modalConfig.type === 'edit_chapter' ? 'Đổi Tên Chương' :
          modalConfig.type === 'add_lesson' ? 'Thêm Bài Học Mới' : 'Đổi Tên Bài Học'
        }
        placeholder={modalConfig.type.includes('chapter') ? 'Tên chương...' : 'Tên bài học...'}
        defaultValue={modalConfig.defaultValue}
        onConfirm={handleModalConfirm}
        onCancel={closeModal}
      />

      {chapters.length === 0 && (
        <div className="text-center py-10 px-4 bg-white rounded-2xl border border-slate-200 shadow-sm mb-6">
          <LayoutList size={40} className="mx-auto text-slate-300 mb-3" />
          <p className="text-slate-500">Khóa học chưa có nội dung nào.</p>
        </div>
      )}

      {/* Danh sách Chương */}
      <div className="space-y-4 sm:space-y-6">
        {chapters.map((chapter, cIndex) => (
          <div 
            key={chapter.id}
            draggable
            onDragStart={() => (dragChapterItem.current = cIndex)}
            onDragEnter={() => (dragChapterOverItem.current = cIndex)}
            onDragEnd={handleChapterDragSort}
            onDragOver={(e) => e.preventDefault()}
            className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden"
          >
            {/* Header Chương */}
            <div className="bg-slate-50 border-b border-slate-200 p-3 sm:p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 group/chapter cursor-grab active:cursor-grabbing">
              <div className="flex items-center gap-3">
                {/* Grip cho Desktop */}
                <div className="text-slate-400 hidden sm:block">
                  <GripVertical size={20} />
                </div>
                <h3 className="font-bold text-slate-800 text-base flex-1">{chapter.title}</h3>
              </div>
              
              {/* Hành động của Chương */}
              <div className="flex items-center justify-between sm:justify-end gap-1">
                <div className="flex sm:hidden items-center bg-slate-200/50 rounded-lg p-0.5 mr-2">
                   <button onClick={() => moveChapter(cIndex, -1)} disabled={cIndex === 0} className="p-1.5 text-slate-600 disabled:opacity-30"><ChevronUp size={18}/></button>
                   <button onClick={() => moveChapter(cIndex, 1)} disabled={cIndex === chapters.length - 1} className="p-1.5 text-slate-600 disabled:opacity-30"><ChevronDown size={18}/></button>
                </div>
                <div className="flex gap-1">
                  <button onClick={() => openModal('edit_chapter', chapter.title, chapter.id)} className="p-2 text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors">
                    <Edit2 size={16} />
                  </button>
                  <button onClick={() => deleteChapter(chapter.id)} className="p-2 text-rose-500 hover:bg-rose-50 rounded-lg transition-colors">
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            </div>

            {/* Danh sách Bài học */}
            <div className="p-3 sm:p-4 space-y-2 bg-white">
              {chapter.lessons.length === 0 ? (
                <p className="text-sm text-slate-400 italic py-2 text-center border-2 border-dashed border-slate-100 rounded-xl">Chưa có bài học</p>
              ) : (
                chapter.lessons.map((lesson, lIndex) => (
                  <div 
                    key={lesson.id}
                    draggable
                    onDragStart={(e) => { e.stopPropagation(); dragLessonItem.current = lIndex; }}
                    onDragEnter={(e) => { e.stopPropagation(); dragLessonOverItem.current = lIndex; }}
                    onDragEnd={(e) => { e.stopPropagation(); handleLessonDragSort(cIndex); }}
                    onDragOver={(e) => e.preventDefault()}
                    className="flex flex-col sm:flex-row sm:items-center justify-between p-3 bg-slate-50 rounded-xl border border-slate-100 hover:border-indigo-100 hover:shadow-sm transition-all group/lesson cursor-grab active:cursor-grabbing gap-2"
                  >
                    <div className="flex items-center gap-3 w-full sm:w-auto overflow-hidden">
                       <div className="text-slate-400 hidden sm:block">
                          <GripVertical size={16} />
                       </div>
                       <button 
                          onClick={() => toggleLessonType(chapter.id, lesson.id)}
                          title="Nhấn để đổi loại bài học"
                          className={`p-1.5 rounded-lg shrink-0 transition-colors ${lesson.type === 'video' ? 'bg-indigo-100 text-indigo-600' : 'bg-emerald-100 text-emerald-600'}`}
                        >
                         {lesson.type === 'video' ? <PlayCircle size={18} /> : <FileText size={18} />}
                       </button>
                       <span className="text-sm font-medium text-slate-700 truncate">{lesson.title}</span>
                    </div>

                    {/* Hành động của Bài học */}
                    <div className="flex items-center justify-between sm:justify-end border-t border-slate-200 sm:border-0 pt-2 sm:pt-0 mt-1 sm:mt-0">
                       <div className="flex sm:hidden items-center gap-1 bg-slate-200/50 rounded-lg px-1 mr-2">
                         <button onClick={() => moveLesson(cIndex, lIndex, -1)} disabled={lIndex === 0} className="p-1 text-slate-500 disabled:opacity-30"><ChevronUp size={16}/></button>
                         <button onClick={() => moveLesson(cIndex, lIndex, 1)} disabled={lIndex === chapter.lessons.length - 1} className="p-1 text-slate-500 disabled:opacity-30"><ChevronDown size={16}/></button>
                       </div>
                       <div className="flex gap-1">
                          <button onClick={() => openModal('edit_lesson', lesson.title, lesson.id, chapter.id)} className="p-1.5 text-slate-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-md transition-colors">
                            <Edit2 size={16} />
                          </button>
                          <button onClick={() => deleteLesson(chapter.id, lesson.id)} className="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-rose-50 rounded-md transition-colors">
                            <Trash2 size={16} />
                          </button>
                       </div>
                    </div>
                  </div>
                ))
              )}

              {/* Thêm bài học Button */}
              <button 
                onClick={() => openModal('add_lesson', '', null, chapter.id)}
                className="w-full mt-2 py-2.5 border-2 border-dashed border-indigo-200 text-indigo-600 font-medium text-sm rounded-xl hover:bg-indigo-50 hover:border-indigo-300 transition-colors flex items-center justify-center gap-2"
              >
                <Plus size={16} /> Thêm bài học mới
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Thêm Chương Button */}
      <button 
        onClick={() => openModal('add_chapter')}
        className="w-full mt-6 py-4 bg-indigo-50 text-indigo-700 font-bold rounded-2xl hover:bg-indigo-100 active:scale-95 transition-all shadow-sm flex items-center justify-center gap-2"
      >
        <Plus size={20} /> THÊM CHƯƠNG MỚI
      </button>

      <div className="mt-8 text-center text-xs text-slate-400">
        <p>💡 Mẹo: Trên máy tính, bạn có thể kéo thả để sắp xếp.</p>
        <p>Trên điện thoại, sử dụng các mũi tên lên/xuống để thao tác chính xác hơn.</p>
      </div>
    </div>
  );
};