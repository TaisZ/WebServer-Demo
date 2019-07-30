###课程回顾
1. 内容标题 h1-h6  字体加粗 独占一行 自带上下间距   align=left/right/center
2. 段落标签 p  独占一行 自带上下间距 
3. hr水平分割线
4. br 换行
5. 字体加粗  b    斜体 i
6. 列表标签
- 无序列表  ul:type  li
- 有序列表  ol:type start reversed   li 
- 定义列表  dl  dt  dd  
- 列表嵌套  有序和无序可以任意无限嵌套
7. 图片标签 img
- src： 路径 
	1. 相对路径：访问站内资源使用
		同目录，直接写图片名
		上级目录，../图片名
		下级目录， 文件夹名/图片名
	2. 绝对路径：访问站外资源使用以http开头
- alt: 图片不显示时显示的文本
- title： 鼠标悬停时显示的文本
8. 超链接 a
- 如果不写href就是一个纯文本
- href属性：资源路径，可以指向页面资源和文件资源（能浏览浏览不能浏览则下载）
- target="_blank" 在新的窗口中显示页面
- 包裹文本就是文本超链接 包裹图片就是图片超链接  
9. 表格 table
- 子标签：  tr   td   th(加粗并且居中)   caption表格标题 
- table:border边框粗细  cellspacing单元格的间距    cellpadding边框与内容的距离
- td：rowspan跨行    colspan跨列
10. 表单 form 
- 文本框   <input type="text" name value设置默认值 placeholder占位文本 maxlength最大长度  readonly只读
- 密码框   type="password"  属性同上 
- 单选   type="radio" name value  checked  id    <label for="id">
- 多选   type="checkbox"  属性同上
- 下拉选 <select name>  <option value="yyyy" selected >xxxx</option>
- 日期选择  type="date" 
- 文件选择  type="file"  
- 文本域   <textarea name  rows   cols placeholder>
- 提交按钮  type="submit"
- 重置按钮   type="reset"
- 自定义按钮  type="button"




###特殊字符
1. 空格    &nbsp;
2. 小于号  &lt;
3. 大于号  &gt;
###分区标签  <xxx>
- 分区标签类似一个容器，对多个相关标签进行统一管理 


1. div ： 块级分区元素，独占一行
2. span： 行内分区元素，共占一行

- html5标准中新增的分区标签
	1. header头
	2. article正文/文章
	3. section块/区域
	4. nav 导航
	5. footer 脚

- 以前版本：所有都是div
	
		<div>头</div>
		<div>体</div>
		<div>脚</div>

- h5写法： 可读性提高  

		<header>头</header>
		<article>正文</article>/<section>区域</section>
		<footer>脚</footer>
###CSS
- Casecading层叠Style样式Sheet表
- 作用：用于美化页面 
####CSS的引入方式
1. 内联样式：在标签的style属性中添加样式代码，弊端：不能复用

2. 内部样式：在head标签中添加style标签在标签体内通过选择器选择元素再添加样式代码， 弊端：不能多页面复用

3. 外部样式：在单独的css文件中写样式代码，写法和内部一样通过选择器，在html页面中通过link标签引入css文件，好处：可以多页面复用 
####选择器
1. 标签名选择器
- 选取页面中所有该名称的标签 
- 格式:   标签名{样式代码}
2. id选择器
- 页面中标签的id必须是唯一的 
- 选取页面中此id的标签
- 格式：   #id{样式代码}
3. 类选择器
- 选取页面中此class属性值得标签
- 格式： .class{样式代码}
4. 分组选择器
- 可以将多个选择器合并成一个选择器
- 格式： div,#abc,.xyz{样式代码}
5. 子孙后代选择器
- 通过元素之间的关系选择元素
- 格式： div div span{样式代码}
6. 子元素选择器
- 通过元素之间的关系选择元素
- 格式： div>div>span{样式代码}
7. 属性选择器
- 通过属性选择元素
- 格式： input[属性名='值']{样式代码}
8. 伪类选择器
- 选择的是元素的状态：未访问、访问过、悬停、点击
- 格式: a:link/visited/hover/active{样式代码}
9. 任意元素选择器
- 选取页面中所有标签
- 格式： *{样式代码}


###颜色赋值
- 三原色  红 绿 蓝   red green blue  每个颜色取值范围0-255   

1. 颜色单词赋值
2. 6位16进制赋值  #0000ff 每两位表示一个颜色   ************
3. 3位16进制赋值  #f00   每一位表示一个颜色
4. 3位10进制赋值  rgb(255,0,0) 
5. 4位10进制赋值  rgba(255,0,0,0-1)   a=alpha 透明度  值越小越透明
###背景图片

		/* 背景图片 */
		background-image: url("../imgs/c.jpg");
		/* 图片尺寸 */
		background-size: 100px 100px;
		/* 禁止重复 */
		background-repeat: no-repeat;
		/* 控制位置 */
		background-position: 100% 100%;

###元素的显示方式 display
1. block:  块级元素的默认值，独占一行，可以修改宽高
	h1-h6, p,  hr, div
2. inline： 行内元素的默认值， 共占一行，不能修改宽高
	span, a, b, i
3. inline-block: 行内块元素默认值，共占一行，并且可以修改宽高
	img
###盒子模型
- 盒子模型= 外边距+边框+内边距+宽高

####宽高
- 赋值方式： 1. 像素 单位px    2. 上级元素的百分比  50%
- 行内元素不能修改宽高

####外边距
- 什么是外边距：元素距上级元素或相邻兄弟元素的距离称为外边距
- 赋值方式：
		 margin-left/right/top/bottom：10px;
		/*margin:50px; 4个方向外边距50 */
		/*margin: 10px 20px;  上下10 左右20 */
		/*margin: 0 auto;上下0 左右居中 */ 
		/*margin: 10px 20px 30px 40px;  上右下左 */
- 如果元素的上边缘和上级元素的上边缘重叠时，给元素添加上外边距会出现粘连问题，通过给上级元素添加： overflow：hidden
- 行内元素上下外边距无效
####边框
- 赋值方式： border: 粗细 边框样式  颜色
	border-left/right/top/bottom: 粗细 边框样式  颜色
- 行内元素添加边框影响元素所占宽度，但不影响所占高度
####内边距
- 什么是内边距：元素边框距内容的距离
- 赋值方式和外边距类似
- 块级元素添加内边距会影响元素所占的宽高
- 行内元素影响所占宽度 但不影响所占高度









 
















