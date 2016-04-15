package oo;

/**
 * Created by DESTR on 2016/4/15.
 */
public class Test implements Runnable
{
	@Override
	public void run()
	{
		_file file=new _file();
		//file.create_file("C:\\Users\\DESTR\\Desktop\\test\\123\\zxc");
		//file.create_file(null);
		//file.create_file("C:\\Users\\DESTR\\Desktop\\test\\123\\zxc");
		file.delete_file("C:\\Users\\DESTR\\Desktop\\test\\123\\新建文件夹 (2)");
	}
}
