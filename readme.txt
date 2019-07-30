此版本v4开始完成WebServer流程中的第二块：处理请求

这一步要做的事情就是让ClientHandler根据请求中的抽象路径寻找服务端对于的资源，然后做好分支处理，
以便后续相应客户端的工作

首先我们要添加一个页面用于测试，但是WebServer是一个Web容器，下面会管理多个不同的WebApp，而一
个页面应当是从属于某个WebApp的。因此我们在WebServer项目下新建一个目录：webapps，用于保存所有
的webapp，么一个webapp以一个子目录形式保存其所有内容。并且目录名作为该webapp的名字

实现：
1. 在WebServer项目下新建一个目录：webspps用于保存每个app的
2. 在webapps下新建一个目录：myweb作为我们第一个webapp，所有测试页面全放在这
3. 在myweb目录下新建第一个界面：index.html
4. 在浏览器中想访问改页面，我们应当输入的地址为：
    http://localhost:8088/myweb/index.html
    其中抽象路径部分问 :/myweb/index.html
    而这里的第一段myweb就是指获取webapps目录下的哪个应用，这里的名字要和webapps目录中对应的
    子目录名一致。再往后的内容就是myweb目录里的资源了。

    因此我们再XlientHandler处理第二步，处理请求的部分使用File从webapps目录下寻找URL中资源
    抽象路径对的资源并分支打桩

 ---------------------------------------------------------------------------------

 V5
 此版本开始完成WebServer流程中的第三块：响应客户端
 上个版本中已经根据浏览器输入的URL找到了webapps目录下的对应资源，因此我们在本环节将该资源
 发送回客户端
 若希望响应客户端，我们需要了解HTTP协议中的响应规则，然后按照一个标准的Response格式将其
 请求的资源发回客户端

 Status-Line

 The first line of a Response message is the Status-Line, consisting of the
 protocol version followed by a numeric status code and its associated textual
 phrase, with each element separated by SP characters. No CR or LF is allowed
 except in the final CRLF sequence.

       Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF


 The Status-Code element is a 3-digit integer result code of the attempt to
 understand and satisfy the request. These codes are fully defined in section
 The Reason-Phrase is intended to give a short textual description of the
 Status-Code. The Status-Code is intended for use by automata and the
 Reason-Phrase is intended for the human user. The client is not required
 to examine or display the Reason- Phrase.

 The first digit of the Status-Code defines the class of response. The last
 two digits do not have any categorization role. There are 5 values for the
 first digit:

       - 1xx: Informational - Request received, continuing process
       - 2xx: Success - The action was successfully received,
         understood, and accepted
       - 3xx: Redirection - Further action must be taken in order to
         complete the request
       - 4xx: Client Error - The request contains bad syntax or cannot
         be fulfilled
       - 5xx: Server Error - The server failed to fulfill an apparently
         valid request

 实现：
 在ClientHandler的分支处理（v4实现）中，在找到资源后，通过socket获取输出流，发送一个
 标准的响应给客户端。 并在最后的catch后面添加finally，在里面处理与客户端断开连接的操作
 （HTTP要求）

 --------------------------------------------------------------------------

 V6
 此版本添加对404的响应
 当客户端请求的资源服务器无法找到时，应当为客户端响应404状态代码并且响应一个404的提示页面

 实现：
 1. 定义一个404页面
    在webapps目录下新建一个子目录root，然后在里面定义一个页面404.html
    该页面没有放在myweb目录下的原因是，无论服务器端将来哪个webapp中请求的资源不存在都应当
    响应404，因此该页面应当是一个公共页面
 2.修改ClientHandler的分支，当资源不存在时，响应客户端404
   首先发送状态代码：HTTP/1.1 404 NOT FOUND
   响应头还是 Content-Type与Content-Length
   正文内容为404.html

 ----------------------------------------------------------------------------

 V8
 此版本重构代码，从doc.tedu.cn网站将学子商城页面素材下载后，在webapps目录中导入，使用
 浏览器访问该应用下的index.html首页和里面超链接指定的页面时发现内容不能完全展示。
 究其原因，是因为浏览器在请求页面上各种素材时（js，css等）服务端在响应头的content-type
 告知类型时现在时固定的text/html，这导致浏览器不能正确理解其请求的资源，所以无法正常显示

 次版本主要改正响应头内容，要按照正确的资源类型进行响应

 修改氛围两步：1 先将HTTPResponse发送响应头的工作从原有的发送固定格式，改为可以根据设置
 的响应头发送。因此我们在HTTPResponse中定义一个Map，用于保存所有要发送的响头，并将发送
 响头的方法sendHeaders改为根据Map内容将所有响应头发布。
 2.解决response根据正文实体文件的类型发送对应的Content-Type与Content-Length
   经过改动，将设置说明正文的两个响应头：Content-Type与Content-Length的工作放在
   了Constructor中

 w3c将所有的资源类型定义好了，常见的有：
 文件类型    MIME类型
 png       image/png
 gif       image/gif
 jpg       image/jpeg
 css       text/css
 html      text/html
 js        application/javascript

-----------------------------------------------------------------------------

v9
此版本解决WebServer支持所有资源类型的问题
上一个版本我么在HTTPContext中设计了一个Map，保存了6个资源类型对应的Content-Type值，这里
我们要 借用Tomcat提供的conf/web.xml文件，将其整理的1000多种类型全部支持进来

实现：
1. 在当前项目目录下新建一个目录conf，然后将Tomcat中conf/web.xml文件导入到该目录
2. 在HTTPContext的initMime方法中解析该xml文件，将里面的文件类型表读取并
   存入到mime中


-----------------------------------------------------------------------------

V10
此版本开始完成业务操作，首先实现用户注册功能
需要了解的内容：页面上用于提交用户输入信息的元素：form表单

注册流程：用户访问 -> 点击注册按钮 -> 数据提交到服务端做注册处理
        -> 服务端响应注册结果并显示到浏览器

实现:
1.在webapps/myweb目录下新建一个注册页面：reg.html，在这里创建一个表单
2.当一个表单使用GET形式提交，会发现请求中请求行里的抽象路径部分带有参数了，格式例如：
  reg.html?username=iin&nickname=98nn&password=8123912i8&telephone=38192

  URL的抽象路径部分运行使用 "？"分割请求部分和参数部分并且每个参数格式为name=value，
  多个参数之间用"&"分割

在HttpRequest解析请求中要处理参数，首先在HttpRequest中再定义三个参数：
String requestURI: 保存抽象路径中的请求部分，"？"左侧
String queryString: 保存"？"右侧的参数部分
Map Para： 保存每一个参数
再定义一个方法parseURI，用来解析抽象路径部分，然后在解析请求行的方法parseRequest中，得到抽象路径
部分后调用parseURI进一步解析

------------------------------------------------------------------------------

V11

此版本开始完成处理业务操作
继续实现用户注册功能。

上一个版本中，已经将HttpRequest处理表单提交的数据
功能实现了。

本版本开始，要在ClientHandler的第二步处理请求中
添加新的分支，判别该请求是寻求业务处理，还是需求一个
静态资源。

由于我们在reg.html页面的form表单中指定action提交
的路径为action="./reg"。
因此该表单提交时的实际路径为:
http://localhost:8088/myweb/reg
那么HttpRequest解析请求后，requestURI的值应当就是
这里的/myweb/reg。而如果是这个值我们就判定这个请求
是form表单提交的要做注册操作的。



实现:
1:新建一个包:com.webserver.servlet
2:在该包中新建处理注册业务的类:RegServlet
    并定义service方法
3:在webapps/myweb目录下新建reg_success.html
    该页面用于给用户展示注册成功的提示
4:在ClientHandler第二步处理请求中添加一个新的分支
    如果uri的值为:/myweb/reg，那么就实例化RegServlet
    并调用其service方法处理业务
    否则执行原有的处理逻辑，查看是否为静态资源。    

V11.1

此中间版本解决提交中中文信息问题
页面form表单以GET信息提交时，所有表单数据会被拼接到URI中抽象路径部分，
并且以"？"分割，放在其右侧

但是这部分内容会被体现在请求的请求行中URI的位置，HTTP协议要求，这部分的文本内容
只能使用ISO8859-1的字符集，而这是一个欧洲编码集，不支持中文

浏览器提交中文时的解决办法是：
将中文以UTF-8编码转换为2进制，再将2进制以16进制的字符表示，然后再字符前面加上"%"
所以每个字节的8位2进制就变为一个%XX的形式，而这个内容是ISO8859-1支持的字符

之所以每个字节的8位2进制用"%XX"表示而不是 直接用"XX"表示是为了区分该内容是2进制
数据还是实际的字符
比如：
.../reg?username=AB&password=...
.../reg?username=%AB&password=...
第一个用户名字是AB，而第二个就理解为是一个字节的2进制数据了。 %在URI中也是关键字

实现：
修改HttpRequest的parseURI代码，在截取出的queryString后使用javaAPI提供的类：
URLDecoder的decode方法将queryString中所有%XX的内容按照UTF-8编码还原

-------------------------------------------------------------------

V13
此版本解决空请求问题

HTTP协议允许空请求，即：客户端连接后没有发送标准的请求内容。这导致我们在解析时，
按照"\\s" 拆分得不到三项，此时会出现下标越界。并且客户端发送空请求后即与客户端
断开了连接，如果我们按照流程继续处理请求并发送响应，此时还会出现SocketException

实现思路：
HttpRequest在解析请求时首先读取请求行，若此时发现该请求是空请求则可以抛出一个
异常，并最终抛给ClientHandler，从而忽略了后续处理请求和响应客户端的操作，这样
等于忽略本次请求处理了

我们使用自定义异常来创建空请求异常。

------------------------------------------------------------------------

V14
此版本完成用户登陆的操作

流程：
访问登陆页面，输入登陆信息（用户名及密码），点击登陆按钮提交登陆信息，服务端比对登陆信息
并响应登陆结果

实现：
1.在webapps/myweb目录下准备三个页面
  a.login.html登陆界面，要求用户输入用户名和密码 form表单action指定路径
    action="./login"
  b.login_success.html 登陆成功界面
  c.login_fail.html  登陆失败页面

2.在servlet包中添加用于处理登陆业务的类，并完成service方法

service方式代码逻辑思路：
 a 通过request方法获取登陆页面表单提交上来的用户名与密码
 b 创建RandomAccessFile获取user.dat文件，遍历文件每个用户
   并对比用户名与密码，只有用户名与密码一致才能响应登陆成功页面
   密码错误或没有该用户都将登陆失败

3.在ClientHandler下创建一个分支判断是否为登陆业务

-----------------------------------------------------------------------------------

V15
重构代码
1.约束Servlet功能和重用部分功能
2.利用反射机制使得后期再添加新的业务（添加Servlet）时ClientHandler无需再添加分支根着修改

实现：
1.在com.webserver.servlet包中定义类：HttpServlet定义一个抽象方法service，要求所有子类必须有
该方法并且自行定义逻辑
提取一个方法  forward用来实现setEntity方法（跳转页面的逻辑）

2.将现有的Servlet都继承HttpServlet，并且后续若添加新的业务，新的Servlet也需要继承它

3.设计一个Map， Key存放请求，value存放用于处理该请求的Servlet。然后ClientHandler在处理
请求时判断是否为请求业务处，更改为根据uri到这个Map中匹配，若匹配上则获取该请求对应的Servlet
并调用其Service方法处理
而这个Map的初始化工作则利用反射机制，根据配置文件的类型来初始化Servlet并存入该Map

3.1 在com.webserver.context包中定义一个类：ServerContext，用来存放服务器端配置信息
3.2 在ServerContext中定义一个Map用于保存所有请求与对应处理业务的Servlet
3.3 定义静态模块用于初始化
3.4 定义一个静态方法，可以根据请求获取相应的Servlet
3.5 修改ClientHandler，将处理分支部分改为根据uri获取Servlet并调用service

-----------------------------------------------------------------------------------

V. 2.0 可以扩展的功能

1.第一代webserver中，页面数量十分有限，可以扩展更多的页面
2. 在提交密码表单时候 仍然使用的是GET请求，安全性非常差，可以改为使用POST请求，并且避免储存
   用户的明文密码，可以使用哈希加密后的舒适储存
3. 增加更多的业务，如登陆只有用户对webapp的反馈功能，可以在用户注册时，为其生成一个独一无二的
   UID
4. 优化对用户数据的储存结构，使用性能更好的算法进行注册和登陆操作，而不是简单的顺序查找，这可
   能会使用数据库

