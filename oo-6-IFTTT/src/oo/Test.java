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
//		file.delete_file("C:\\Users\\DESTR\\Desktop\\test\\123\\123");
//		file.rename_file("C:\\Users\\DESTR\\Desktop\\test\\123\\新建文件夹 (3)","C:\\Users\\DESTR\\Desktop\\test\\123\\新建");
//		file.delete_file("C:\\Users\\DESTR\\Desktop\\test\\123\\新建文件夹 (3)");
//		file.copy_file("bug记录.txt","123");
//		file.copy_file("bug记录.txt","asd (2).bak");
//		file.create_dir("C:\\Users\\DESTR\\Desktop\\test\\123\\12");
//		file.move_file("C:\\Users\\DESTR\\Desktop\\test\\123\\123","C:\\Users\\DESTR\\Desktop\\test\\123\\12\\bug记录.txt");

//		System.out.println(file.get_last_modified("C:\\Users\\DESTR\\Desktop\\test\\123"));
//		file.file_append("C:\\Users\\DESTR\\Desktop\\test\\123\\123");
//		file.move_file("C:\\Users\\DESTR\\Desktop\\test\\123\\123","C:\\Users\\DESTR\\Desktop\\test\\123\\src\\123");
//		System.out.println(file.get_last_modified("C:\\Users\\DESTR\\Desktop\\test\\123"));

		while (true)
		{
			file.move_file("C:\\Users\\DESTR\\Desktop\\test\\123\\123.bak", "C:\\Users\\DESTR\\Desktop\\test\\123\\src\\123.bak");
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

	}

}
