path = r'D:\University\Third\MB\cinx\app\src\main\res\layout\activity_course_detail.xml'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()
content = content.replace('android:background="#FFF9E6"', 'android:background="@drawable/bg_icon_yellow"')
content = content.replace('android:background="#EEF2FF"', 'android:background="@drawable/bg_icon_indigo"')
content = content.replace('android:background="#F3E8FF"', 'android:background="@drawable/bg_icon_purple"')
content = content.replace('android:background="#F1F5F9"', 'android:background="@drawable/bg_icon_gray"')
with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
print('Done')
