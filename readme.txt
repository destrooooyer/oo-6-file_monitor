1.关于输入：
	输入格式：
		【IF "路径" 触发器 THEN 任务】
	
	为了便于测试者理解输入格式，此处给出匹配输入的正则表达式，请保证输入满足此正则表达式：
		IF\s+"(.*)"\s+(renamed|modified|path-changed|size-changed)\s+THEN\s+(record-summary|recover|record-detail)
	例如：
		IF "D:\kancolle\ShimakazeGo\error.log" renamed THEN recover
	
	关于输入的路径：
		必须为绝对路径且存在
	
	提示：
		路径的两侧必须有英文的双引号
		触发器有四种：renamed|modified|path-changed|size-changed
		任务有三种：record-summary|recover|record-detail
	
	其他要求：
		请按照作业要求中所说的，不要输入modified与recover的组合或size-changed与recover的组合
		同时也请按作业要求中所说的，不要监控多于8个工作区
	
	输入结束：
		输入run之后，输入结束，监控线程开始运行
		
2.关于输出：
	summary：
		summary线程每隔100ms会将输出队列中的全部内容输出到工程目录中的summary.txt
		用文本编辑器重新打开summary.txt才能看到内容的变化
		summary.txt中记录所有record-summary任务对应的触发器的触发次数。
		值得注意的是，如果两个触发器都监视同一个文件，那么发生变化时，两个触发器都会被触发，因此summary中记录的触发次数会增加两次，以此类推。
	
	detail:
		与summary类似，其记录的内容为检测到的具体变化，包括路径、修改时间和文件大小
		
3.关于测试：
	请在windows下测试
	请保证工作区内没有任何没有权限的目录或文件。
	请不要以太大的目录作为工作区
	
	关于扫描间隔：
		在两次扫描之间有多个操作的话，由于检测只是对比两次扫描结果之间的差别，有的操作就可能不被检测到，按课代表的意思，没有扫描到的是不用理他的，所以见谅。
		然而为了测试的操作能被确实检测到，请保证每两个对文件操作的间隔都大于1s，当然，如果工作区过于庞大，扫描的时间超过1s的话，请自行增加对文件的操作间隔。
	
	提供给测试者的文件操作类：
		_file类
		请按照作业要求所说的，仅使用_file类中给出的方法对工作区进行操作，其他任何操作引起的错误我都无法负责。
		
	_file类中的以下几个方法可供使用：
		public boolean create_file(String str)
			创建文件，参数为文件的绝对路径，若文件已存在则创建失败
			举例：[_file类的对象].create_file("D:\kancolle\ShimakazeGo\error.log");
			
		public boolean create_dir(String str)
			创建文件夹，参数为文件夹的绝对路径，若文件夹已存在则创建失败（若有同名的无后缀名文件也会创建失败，这个是直接调用File.mkdir()返回就是false）
			举例：[_file类的对象].create_dir("D:\kancolle\ShimakazeGo");
		
		public boolean delete_file(String str)
			删除文件或文件夹，参数为绝对路径，不存在则会失败。
			举例：[_file类的对象].delete_file("D:\kancolle\ShimakazeGo\error.log");
		
		public boolean rename_file(String src, String dest)
			重命名文件，参数为绝对路径，将src路径的文件重命名为dest路径的文件。
			若src路径的文件不存在或dest路径的文件已经存在则会失败。
			举例：[_file类的对象].rename_file("D:\kancolle\ShimakazeGo\error.log","D:\kancolle\ShimakazeGo\a.txt");
			注意：此方法也可以用来移动文件到另一个目录下
		
		public boolean move_file(String src, String dest)
			此方法为public boolean rename_file(String src, String dest)的马甲，两个方法没有区别。
			移动文件请用此方法
			举例：[_file类的对象].move_file("D:\kancolle\ShimakazeGo\error.log","D:\kancolle\error.log");
			
		public boolean file_append(String str)
			此方法会在指定文件的结尾添加字符串"wtf"，以改变该文件的大小和最后修改时间。
			参数为文件的绝对路径
			举例：[_file类的对象].file_append("D:\kancolle\ShimakazeGo\error.log");
			
		public String get_file_name(String str)
		public long get_last_modified(String str)
		public long get_size(String str)
			获取文件信息的三个方法，我懒得写了啊......我相信你看方法名就能理解的......
			
		其他：
			以上方法进行操作失败时都有可能输出提示信息，请检查是否参数有误
			
	关于测试代码的添加：
		添加位置：
			Main方法main函数的最后注释处，在113行处启动测试线程。
			也可以取消114行至116行的注释，并在Test类的run方法中添加测试代码。
		关于测试代码里的sleep
			请在每条操作之后都添加至少1s的sleep，原因已在前面的“3.关于测试-关于扫描间隔”中解释了，范例在Test类的run方法中，可供参考。
			
4.其他
	若监控的是文件，若该文件被删除，那么该监控线程将结束。

5.和谐六系
	这次作业写的本来就够蛋疼了，我一开始用watchservice写的，快写完了突然出了个要求说不能用，然后又重写(╯‵□′)╯︵┻━┻......看我代码就知道了，Filemonitor类的700多行全白写了......
	总之希望能本着和谐六系原则进行测试......
	望手下留情Orz......