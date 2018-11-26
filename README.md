# NixoEditText
### NixoEditText 目前是仅支持输入框清除功能的一个扩展EditText！

# 使用方法
### 可以下载Demo查看使用布局

### 布局文件中
```xml 
<edittext.nixo.com.myapplication.NixoEditText
        android:id="@+id/nixo_edit"
        app:cancelIcon="@mipmap/moe"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        tools:layout_editor_absoluteX="0dp"
        tools:layout_editor_absoluteY="225dp" />
```
app:cancelIcon属性可以使用你自己的图标当做清除按钮，不写的话有一个默认icon。
NixoEditText editText = findViewById(R.id.nixo_edit);获取控件。
导入方法： 将java文件夹的NixoEditText放入你的项目中的java目录或子目录中，随后将res文件夹中的value里的attrs文件放入对应的文件夹即可。


# By me
### 本人Android实习小生一枚，后续会写一篇博客来讲解其源码。


# By futer
### 1.增加删除按钮的动画(默认清除后缩小，输入后放大)(做完之后会导出个jar包放在Gradle上,方便导入项目)
### 2.增加显隐按钮(类似于密码显示隐藏,清除显隐二选一功能)
### 3.在上面两个的基础上扩展为可同时实现
### 4.提供几种输入框样式(目前暂定为圆角输入框，非圆角输入框)
### 5.使用Kotlin重构，将支持使用者进行扩展函数

# 如果你喜欢这个控件，欢迎fork or star 我会不断维护以及改进改控件，它会随我一起进步。
# QQ1649883744 (欢迎共同探讨Android知识)
