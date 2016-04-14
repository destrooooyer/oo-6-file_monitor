package oo;

import javafx.scene.control.TableRow;
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
	private String target;
	private File target_file;
	private WatchService watchService;
	private WatchService watchService_delete;
	private Map<String, Pair<Long, Long>> fList;
	private Map<String, Boolean> f_bo;
	private int trigger;
	private boolean target_is_file;
	private int action;

	public Filemonitor(int x, String target)
	{
		//这货记录根目录下所有文件和其修改时间以及大小
		fList = new HashMap<String, Pair<Long, Long>>();
		//这货记录注册过的子文件夹
		f_bo = new HashMap<String, Boolean>();
		this.trigger = x;
		this.target = new String(target);
		target_file = new File(target);
		target_is_file = target_file.isFile();
		if (target_is_file)
			this.root = new String(target_file.getParent());
		else
			this.root = new String(target_file.getAbsolutePath());

	}

	/**
	 * 循环注册所有子文件夹
	 */
	private void register()
	{
		while (true)
		{
			for (String i : fList.keySet())
			{
				if (f_bo.get(i) == true)
					continue;
				else
					f_bo.put(i, true);
				//System.out.println(i);
				File i_file = new File(i);
				if (i_file.listFiles() == null)
					continue;
				//遍历目录下文件
				for (File j : i_file.listFiles())
				{
					fList.put(j.getAbsolutePath(), new Pair<Long, Long>(j.lastModified(), j.length()));
					f_bo.put(j.getAbsolutePath(), false);
					if (j.isDirectory())
					{
						//依次注册子目录
						try
						{

							//System.out.println(j.getAbsoluteFile());
							Paths.get(j.getAbsolutePath()).register(watchService
									, StandardWatchEventKinds.ENTRY_CREATE
									, StandardWatchEventKinds.ENTRY_MODIFY);
							Paths.get(j.getAbsolutePath()).register(watchService_delete
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
			for (String i : f_bo.keySet())
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

	private long update_folder_size(String path)
	{
		File temp_file = new File(path);
		if (temp_file.isDirectory())
		{
			long size = 0;
			for (File i : temp_file.listFiles())
			{
				size += update_folder_size(i.getAbsolutePath());
			}
			fList.put(path, new Pair<Long, Long>(temp_file.lastModified(), size));
			if (!f_bo.containsKey(path))
				f_bo.put(path, false);
			return size;
		}
		else
			return temp_file.length();
	}

	private long get_folder_size(String path)
	{
		File temp_file = new File(path);
		if (temp_file.isDirectory())
		{
			long size = 0;
			for (File i : temp_file.listFiles())
			{
				size += get_folder_size(i.getAbsolutePath());
			}
			return size;
		}
		else
			return temp_file.length();
	}

	private void init()
	{
		try
		{
			watchService = FileSystems.getDefault().newWatchService();
			watchService_delete = FileSystems.getDefault().newWatchService();
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
					, StandardWatchEventKinds.ENTRY_MODIFY);
			Paths.get(root).register(watchService_delete
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


		fList.put(file.getAbsolutePath(), new Pair<Long, Long>(file.lastModified(), file.length()));  //添加根目录到fList
		f_bo.put(file.getAbsolutePath(), false);
		//LinkedList<File> fList = new LinkedList<File>();
		register();
		update_folder_size(root);

	}

	@Override
	public void run()
	{
		init();

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

				if (!temp.getAbsolutePath().equals(target_file.getAbsolutePath()))
				{
					fList.put(temp.getAbsolutePath(), new Pair<Long, Long>(temp.lastModified(), temp.length()));  //添加根目录到fList
					f_bo.put(temp.getAbsolutePath(), false);

					System.out.println(fList.get(temp.getAbsolutePath()).getKey());
					System.out.println(fList.get(temp.getAbsolutePath()).getValue());
				}
				if (target_is_file)
				{
					System.out.println(target_file.getParent());
					if (temp.getParent().equals(target_file.getParent()))    //所处路径相同
					{
						System.out.println(target_file.getAbsolutePath());
						System.out.println(fList.get(target_file.getAbsolutePath()).getKey());
						System.out.println(fList.get(target_file.getAbsolutePath()).getValue());
						if (temp.lastModified() == fList.get(target_file.getAbsolutePath()).getKey() &&   //最后修改时间相同
								temp.length() == fList.get(target_file.getAbsolutePath()).getValue())   //大小相同
						{
							System.out.println("111");
							if (!temp.getAbsolutePath().equals(target_file.getAbsolutePath())   //文件名不相同
									&& !target_file.exists())    //原文件不存在
							{
								//判定为renamed
								System.out.println("renamed");
								target_file = temp;
							}
						}
					}
				}
				if (temp.equals(target_file))
				{
					fList.put(temp.getAbsolutePath(), new Pair<Long, Long>(temp.lastModified(), temp.length()));  //添加根目录到fList
					f_bo.put(temp.getAbsolutePath(), false);
				}
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


//					System.out.println(temp.getAbsoluteFile());
//					System.out.println(f_bo.get(temp));
					//LinkedList<File> fList = new LinkedList<File>();
					register();


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

	public void test()
	{
		init();

		while (true)
		{
			// 获取下一个文件改动事件
			Vector<WatchKey> key = new Vector<WatchKey>();
			try
			{
				WatchKey key_temp = null;
				while (true)
				{
					key_temp = watchService.poll();
					if (key_temp != null)
						break;
					key_temp = watchService_delete.poll();
					if (key_temp != null)
						break;
					Thread.sleep(10);
				}
				while (key_temp != null)
				{
					key.add(key_temp);
					key_temp = watchService_delete.poll();
				}
				while (key_temp != null)
				{
					key.add(key_temp);
					key_temp = watchService.poll();
				}
			}
			catch (InterruptedException e)
			{
				System.out.println(e.toString());
				System.exit(0);
			}

			System.out.println("------------------------");
			Vector<String> delete_list = new Vector<String>();
			for (int i = 0; i < key.size(); i++)
			{
				for (WatchEvent<?> event : key.get(i).pollEvents())
				{
					if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
						delete_list.add((((Path) (key.get(i).watchable())).toAbsolutePath().toString() + "\\" + event.context()));

//					System.out.println(((Path) (key.get(i).watchable())).toAbsolutePath().toString() + "\\" + event.context() + " --> " + event.kind());
					File temp = new File(((Path) (key.get(i).watchable())).toAbsolutePath().toString() + "\\" + event.context());

//					if (!temp.getAbsolutePath().equals(target_file.getAbsolutePath()) && temp.exists())
//					{
//						fList.put(temp.getAbsolutePath(), new Pair<Long, Long>(temp.lastModified(), temp.length()));  //添加根目录到fList
//						f_bo.put(temp.getAbsolutePath(), false);
//
////					System.out.println(fList.get(temp.getAbsolutePath()).getKey());
////					System.out.println(fList.get(temp.getAbsolutePath()).getValue());
//					}

//					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && !fList.containsKey(temp.getAbsolutePath()))
//					{
////					System.out.println("123");
//						fList.put(temp.getAbsolutePath(), new Pair<Long, Long>(temp.lastModified(), temp.length()));  //添加根目录到fList
//						f_bo.put(temp.getAbsolutePath(), false);
//					}

					////////////////////////////renamed////////////////////////////////
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
					{
						if (target_is_file)
						{
							if (temp.getParent().equals(target_file.getParent()))    //所处路径相同
							{
								if (temp.lastModified() == fList.get(target_file.getAbsolutePath()).getKey() &&   //最后修改时间相同
										temp.length() == fList.get(target_file.getAbsolutePath()).getValue())   //大小相同
								{
									if (!temp.getAbsolutePath().equals(target_file.getAbsolutePath())   //文件名不相同
											&& !target_file.exists())    //原文件不存在
									{
										//判定为renamed
										if (this.trigger == Trigger_kinds.renamed)
										{
											System.out.println("renamed:\t" + target_file.getAbsolutePath() + "\t=>\t" + temp.getAbsolutePath());
											if (this.action == Action_kind.recover)
											{
												_file temp_file = new _file();
												temp_file.move_file(temp.getAbsolutePath(), target_file.getAbsolutePath());
											}
											else
												target_file = temp;
										}
										else
											target_file = temp;
									}
								}
							}
						}
						else
						{
							for (String iter : fList.keySet())
							{
								target_file = new File(iter);
								if (temp.getParent().equals(target_file.getParent()))    //所处路径相同
								{
									if (temp.lastModified() == fList.get(target_file.getAbsolutePath()).getKey() &&   //最后修改时间相同
											temp.length() == fList.get(target_file.getAbsolutePath()).getValue())   //大小相同
									{
										if (!temp.getAbsolutePath().equals(target_file.getAbsolutePath())   //文件名不相同
												&& !target_file.exists())    //原文件不存在
										{
											//判定为renamed
											if (this.trigger == Trigger_kinds.renamed)
												System.out.println("renamed:\t" + iter + "\t=>\t" + temp.getAbsolutePath());
											target_file = temp;
										}
									}
								}
							}
						}
					}
					////////////////////////////modified////////////////////////////////
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && this.trigger == Trigger_kinds.modified)
					{
						if (target_is_file)
						{
							if (temp.getParent().equals(target_file.getParent()) && fList.containsKey(target_file.getAbsolutePath()))    //所处路径相同
							{
								if (temp.lastModified() != fList.get(target_file.getAbsolutePath()).getKey())   //最后修改时间不相同
								{
									if (temp.getAbsolutePath().equals(target_file.getAbsolutePath()))   //文件名相同
									{
										//判定为modified
										System.out.println("modified:\t" + target_file.getAbsolutePath());
										target_file = temp;
									}
								}
							}
						}
						else
						{
							for (String iter : fList.keySet())
							{
								target_file = new File(iter);
								if (temp.getParent().equals(target_file.getParent()))    //所处路径相同
								{
									if (temp.lastModified() != fList.get(target_file.getAbsolutePath()).getKey())   //最后修改时间不相同
									{
										if (temp.getAbsolutePath().equals(target_file.getAbsolutePath()))   //文件名相同
										{
											//判定为modified
											System.out.println("modified:\t" + target_file.getAbsolutePath());
											target_file = temp;
										}
									}
								}
							}
						}
					}
					////////////////////////////path-changed////////////////////////////////
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
					{
						if (target_is_file)
						{
							if (!temp.getParent().equals(target_file.getParent()))    //所处路径不相同
							{
								if (temp.lastModified() == fList.get(target_file.getAbsolutePath()).getKey() &&   //最后修改时间相同
										temp.length() == fList.get(target_file.getAbsolutePath()).getValue())   //大小相同
								{
									if (temp.getName().equals(target_file.getName()))   //文件名相同
									{
										//判定为path-changed
										if (this.trigger == Trigger_kinds.path_changed)
											System.out.println("path-changed:\t" + target_file.getAbsolutePath() + "\t=>\t" + temp.getAbsolutePath());
										target_file = temp;
									}
								}
							}
						}
						else
						{
							for (String iter : fList.keySet())
							{
								target_file = new File(iter);
								if (!temp.getParent().equals(target_file.getParent()))    //所处路径不相同
								{
									if (temp.lastModified() == fList.get(target_file.getAbsolutePath()).getKey() &&   //最后修改时间相同
											temp.length() == fList.get(target_file.getAbsolutePath()).getValue())   //大小相同
									{
										if (temp.getName().equals(target_file.getName()) &&   //文件名相同
												temp.exists() && !target_file.exists()) //一个存在一个消失
										{
											//判定为path-changed
											if (this.trigger == Trigger_kinds.path_changed)
												System.out.println("path-changed:\t" + iter + "\t=>\t" + temp.getAbsolutePath());
											target_file = temp;
										}
									}
								}
							}
						}
					}
					////////////////////////////size-changed////////////////////////////////
					if (this.trigger == Trigger_kinds.size_changed)
					{
						if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
						{
							if (target_is_file)
							{
								if (temp.getAbsolutePath().equals(target_file.getAbsolutePath()) && fList.containsKey(target_file.getAbsolutePath()))    //文件名相同
								{
									if (temp.length() != fList.get(target_file.getAbsolutePath()).getValue())   //大小不相同
									{

										//判定为size-changed
										System.out.println("size-changed:\t" + target_file.getAbsolutePath());
										target_file = temp;

									}
								}
							}
							else
							{
								for (String iter : fList.keySet())
								{
									target_file = new File(iter);
									if (temp.getAbsolutePath().equals(target_file.getAbsolutePath()))    //文件名相同
									{
										if (temp.length() != fList.get(target_file.getAbsolutePath()).getValue())   //大小不相同
										{

											//判定为size-changed
											System.out.println("size-changed:\t" + target_file.getAbsolutePath());
											target_file = temp;

										}
									}
								}
								if (!fList.containsKey(temp.getAbsolutePath()))
								{
									//判定为size-changed
									System.out.println("size-changed:\t" + temp.getAbsolutePath());
									target_file = temp;
								}
							}
						}
						if (temp.isDirectory())
						{
							long temp_size = get_folder_size(temp.getAbsolutePath());
							if (fList.containsKey(temp.getAbsolutePath()))
							{
								if (temp_size != fList.get(temp.getAbsolutePath()).getValue())
								{
									System.out.println("size-changed:\t" + temp.getAbsolutePath());
									fList.put(temp.getAbsolutePath(), new Pair<Long, Long>(temp.lastModified(), temp_size));
									System.out.println(temp_size);
								}
							}

						}
						if (!temp.exists())
						{
							System.out.println("size-changed:\t" + temp.getAbsolutePath());
						}
					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
					{
//					System.out.println("123");
						fList.put(temp.getAbsolutePath(), new Pair<Long, Long>(temp.lastModified(), temp.length()));  //添加根目录到fList
						f_bo.put(temp.getAbsolutePath(), false);
					}
					//检测到创建了新目录，递归注册子目录
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && temp.isDirectory() == true)
					{
//						System.out.println(123);

						///////////////////////////////////////////////////////
						///////////////////////////////////////////////////////
						///////////////////////////////////////////////////////

						try
						{
							Paths.get(temp.getAbsolutePath()).register(watchService
									, StandardWatchEventKinds.ENTRY_CREATE
									, StandardWatchEventKinds.ENTRY_MODIFY);
							Paths.get(temp.getAbsolutePath()).register(watchService_delete
									, StandardWatchEventKinds.ENTRY_DELETE);
						}
						catch (IOException e)
						{
							System.out.println(e.toString());
							System.exit(0);
						}


//					System.out.println(temp.getAbsoluteFile());
//					System.out.println(f_bo.get(temp));
						//LinkedList<File> fList = new LinkedList<File>();
						register();


						///////////////////////////////////////////////////////
						///////////////////////////////////////////////////////
						///////////////////////////////////////////////////////


					}
				}
				//key.get(i).reset();
			}
			for (int i = 0; i < key.size(); i++)
			{
				key.get(i).reset();
			}
			for (int i = 0; i < delete_list.size(); i++)
			{
//				System.out.println(delete_list.get(i));
				File temp_delete = new File(delete_list.get(i));
				if (!temp_delete.exists())
				{
					fList.remove(delete_list.get(i));
					f_bo.remove(delete_list.get(i));
				}
			}
			delete_list.clear();
			if (target_is_file && target_file.exists() == false)
				return;

			// 重设WatchKey
			//boolean valid = key.reset();
			// 如果重设失败，退出监听
//			if (!valid)
//			{
//				break;
//			}
		}
	}
}
