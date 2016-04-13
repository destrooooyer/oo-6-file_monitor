package oo;

import javafx.util.Pair;
import javafx.util.converter.BooleanStringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Created by DESTR on 2016/4/13.
 * 文件监视器
 */
public class Filemonitor implements Runnable
{
	private String root;
	private WatchService watchService;
	private Map<File, Pair<Long, Long>> fList;
	Map<File, Boolean> f_bo;

	public Filemonitor(String str)
	{
		this.root = new String(str);
		try
		{
			watchService = FileSystems.getDefault().newWatchService();
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
			System.exit(0);
		}

		//注册根目录
		try
		{
			Paths.get(root).register(watchService
					, StandardWatchEventKinds.ENTRY_CREATE
					, StandardWatchEventKinds.ENTRY_DELETE);
		}
		catch (AccessDeniedException e)
		{
			System.out.println(e.toString());
			System.exit(0);
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
			System.exit(0);
		}

		File file = new File(root);

		//这货记录根目录下所有文件和其修改时间以及大小
		fList = new HashMap<File, Pair<Long, Long>>();
		//这货记录下面遍历子文件夹时已经遍历过的
		f_bo = new HashMap<File, Boolean>();

		fList.put(file, new Pair<Long, Long>(file.lastModified(), file.length()));  //添加根目录到fList
		f_bo.put(file, false);
		//LinkedList<File> fList = new LinkedList<File>();
		while (true)
		{
			for (File i : fList.keySet())
			{
				if (f_bo.get(i) == true)
					continue;
				else
					f_bo.put(i, true);
				//System.out.println(i.getAbsoluteFile());

				if (i.listFiles() == null)
					continue;
				//遍历目录下文件
				for (File j : i.listFiles())
				{
					fList.put(j, new Pair<Long, Long>(j.lastModified(), j.length()));
					f_bo.put(j, false);
					if (j.isDirectory())
					{
						//依次注册子目录
						try
						{
							Paths.get(j.getAbsolutePath()).register(watchService
									, StandardWatchEventKinds.ENTRY_CREATE
									, StandardWatchEventKinds.ENTRY_DELETE);
						}
						catch (AccessDeniedException e)
						{
							System.out.println(e.toString());
							System.exit(0);
						}
						catch (IOException e)
						{
							System.out.println(e.toString());
							System.exit(0);
						}
					}
				}
				break;
			}
			int flag = 0;
			for (File i : f_bo.keySet())
			{
				if (f_bo.get(i) == false)
				{
					flag = 1;
					break;
				}
			}
			if (flag == 0)
				break;
		}

	}

	@Override
	public void run()
	{
		while (true)
		{
			// 获取下一个文件改动事件
			WatchKey key = null;
			try
			{
				key = watchService.take();
			}
			catch (InterruptedException e)
			{
				System.out.println(e.toString());
				System.exit(0);
			}

			System.out.println("------------------------");

			for (WatchEvent<?> event : key.pollEvents())
			{
				System.out.println(((Path) (key.watchable())).toAbsolutePath().toString() + "\\" + event.context() + " --> " + event.kind());
				File temp = new File(((Path) (key.watchable())).toAbsolutePath().toString() + "\\" + event.context());
				//检测到创建了新目录，递归注册子目录
				if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && temp.isDirectory() == true)
				{


					///////////////////////////////////////////////////////
					///////////////////////////////////////////////////////
					///////////////////////////////////////////////////////

					try
					{
						Paths.get(temp.getAbsolutePath()).register(watchService
								, StandardWatchEventKinds.ENTRY_CREATE
								, StandardWatchEventKinds.ENTRY_DELETE);
					}
					catch (IOException e)
					{
						System.out.println(e.toString());
						System.exit(0);
					}



					fList.put(temp, new Pair<Long, Long>(temp.lastModified(), temp.length()));  //添加根目录到fList
					f_bo.put(temp, false);

//					System.out.println(temp.getAbsoluteFile());
//					System.out.println(f_bo.get(temp));
					//LinkedList<File> fList = new LinkedList<File>();
					while (true)
					{
						for (File i : fList.keySet())
						{
							if (f_bo.get(i) == true)
								continue;
							else
								f_bo.put(i, true);

//							System.out.println(i.getAbsoluteFile());

							//System.out.println(i.getAbsoluteFile());

							if (i.listFiles() == null)
								continue;
							//遍历目录下文件
							for (File j : i.listFiles())
							{
								fList.put(j, new Pair<Long, Long>(j.lastModified(), j.length()));
								f_bo.put(j, false);
								if (j.isDirectory())
								{
									//依次注册子目录
									try
									{
										Paths.get(j.getAbsolutePath()).register(watchService
												, StandardWatchEventKinds.ENTRY_CREATE
												, StandardWatchEventKinds.ENTRY_DELETE);
									}
									catch (AccessDeniedException e)
									{
										System.out.println(e.toString());
										System.exit(0);
									}
									catch (IOException e)
									{
										System.out.println(e.toString());
										System.exit(0);
									}
								}
							}
							break;
						}
						int flag = 0;
						for (File i : f_bo.keySet())
						{
							if (f_bo.get(i) == false)
							{
								flag = 1;
								break;
							}
						}
						if (flag == 0)
							break;
					}


					///////////////////////////////////////////////////////
					///////////////////////////////////////////////////////
					///////////////////////////////////////////////////////


//					try
//					{
//						//System.out.println(((Path)event.context()).toAbsolutePath().toString());
//						Paths.get(((Path) (key.watchable())).toAbsolutePath().toString() + "\\" + event.context().toString()).register(watchService
//								, StandardWatchEventKinds.ENTRY_CREATE
//								, StandardWatchEventKinds.ENTRY_DELETE);
//					}
//					catch (java.nio.file.AccessDeniedException e)
//					{
//						System.out.println(e.toString());
//						System.exit(0);
//					}
//					catch (IOException e)
//					{
//						System.out.println(e.toString());
//						System.exit(0);
//					}
				}
			}
			// 重设WatchKey
			boolean valid = key.reset();
			// 如果重设失败，退出监听
//			if (!valid)
//			{
//				break;
//			}
		}
	}
}
