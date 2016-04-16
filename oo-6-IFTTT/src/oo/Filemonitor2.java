package oo;

import javafx.util.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DESTRooooYER on 2016/4/14.
 */
public class Filemonitor2 implements Runnable
{
	private File root;
	private File target;
	private Map<File, Pair<Long, Long>> fmap_prev;
	private Map<File, Pair<Long, Long>> fmap_pres;
	private int trigger;
	private boolean target_is_file;
	private int action;
	private Summary sm;
	private Detail dt;
	private int run_flag;

	public Filemonitor2(int x, String target, Summary sm, Detail dt,int y)
	{
		//用来记录工作区下所有文件和其修改时间以及大小
		fmap_pres = new HashMap<File, Pair<Long, Long>>();
		fmap_prev = new HashMap<File, Pair<Long, Long>>();

		this.trigger = x;
		this.target = new File(target);
		this.target_is_file = this.target.isFile();

		if (target_is_file)
			this.root = this.target.getParentFile();
		else
			this.root = this.target;

		this.sm = sm;
		this.dt = dt;

		this.run_flag = 1;

		this.action = y;

		synchronized (_file.lock)
		{
			init();
		}

	}

	public long update(File file)
	{
		try
		{
			if (file.isDirectory())
			{
				long temp_size = 0;
				for (File i : file.listFiles())
				{
					temp_size += update(i);
				}
				fmap_pres.put(file, new Pair<Long, Long>(file.lastModified(), temp_size));
				return 0;
			}
			else
			{
				fmap_pres.put(file, new Pair<Long, Long>(file.lastModified(), file.length()));
				return file.length();
			}
		}
		catch (Exception e)
		{
			System.out.println("要监测的路径下可能包含没有访问权限的文件或文件夹，该线程结束");
			this.run_flag=0;
			return 123;
		}
	}

	private void init()
	{
		update(root);
		this.fmap_prev.putAll(fmap_pres);
	}

	@Override
	public void run()
	{
		try
		{
			while (this.run_flag == 1)
			{
				//更新present
				fmap_pres.clear();
				synchronized (_file.lock)
				{
					update(root);
				}
//			for(int i=0;i<5;i++)
//			{
//				fmap_pres.clear();
//				update(root);
//				if(fmap_pres.size()==fmap_prev.size())
//					break;
//			}


				////////////////////////////////////检查previous和present的变化//////////////////////////////////////////////

				if (target_is_file)
				{
					if (!fmap_pres.containsKey(target))
					{

						String target_parent = target.getParent();
						String target_name = target.getName();
						long target_lastmodified = fmap_prev.get(target).getKey();
						long target_size = fmap_prev.get(target).getValue();

						int find_new = 0;

						for (File i : fmap_pres.keySet())
						{
							//----------------------------------renamed-----------------------------------------------------
							if (i.getParent().equals(target_parent) &&                        //所处路径相同
									fmap_pres.get(i).getKey() == target_lastmodified &&       //最后修改时间相同
									fmap_pres.get(i).getValue() == target_size &&             //大小相同
									i.isDirectory() == false &&                               //不是文件夹
									!i.getName().equals(target_name) &&                       //文件名不同
									!fmap_prev.containsKey(i))                                //previous不包含
							{
								//判断为renamed
								find_new = 1;
								if (this.trigger == Trigger_kinds.renamed)
								{
									switch (this.action)
									{
										case Action_kind.record_detail:
										{
											String str_detail = new String();
											str_detail += "renamed:\r\n";
											str_detail += "路径变化:\t";
											str_detail += target.getAbsolutePath() + "\t=>\t" + i.getAbsolutePath() + "\r\n";
											str_detail += "修改时间变化:\t";
											str_detail += fmap_prev.get(target).getKey().toString() + "\t=>\t" + fmap_pres.get(i).getKey() + "\r\n";
											str_detail += "文件大小变化:\t";
											str_detail += fmap_prev.get(target).getValue().toString() + "\t=>\t" + fmap_pres.get(i).getValue() + "\r\n";
											str_detail += "//////////////////////////////////////////////////////////////////////";
//										System.out.println(str_detail);
											dt.push_back(str_detail);
											target = i;
											break;
										}
										case Action_kind.record_summary:
										{
											sm.Renamed_count_plus();
											target = i;
											break;
										}
										case Action_kind.recover:
										{
											try
											{
												Thread.sleep(100);
											}
											catch (InterruptedException e)
											{
												e.printStackTrace();
											}
											_file temp_file = new _file();
											temp_file.rename_file(i.getAbsolutePath(), target.getAbsolutePath());
											break;
										}
										default:
									}
								}
								else
									target = i;
							}

							//----------------------------------path-changed------------------------------------------------
							if (i.getName().equals(target_name) &&
									!i.getParent().equals(target_parent) &&
									fmap_pres.get(i).getKey() == target_lastmodified &&
									fmap_pres.get(i).getValue() == target_size &&
									!fmap_prev.containsKey(i))
							{
								//System.out.println("123");
								//判断为path-changed
								find_new = 1;
								if (this.trigger == Trigger_kinds.path_changed)
								{
									switch (this.action)
									{
										case Action_kind.record_detail:
										{
											String str_detail = new String();
											str_detail += "path-changed:\r\n";
											str_detail += "路径变化:\t";
											str_detail += target.getAbsolutePath() + "\t=>\t" + i.getAbsolutePath() + "\r\n";
											str_detail += "修改时间变化:\t";
											str_detail += fmap_prev.get(target).getKey().toString() + "\t=>\t" + fmap_pres.get(i).getKey() + "\r\n";
											str_detail += "文件大小变化:\t";
											str_detail += fmap_prev.get(target).getValue().toString() + "\t=>\t" + fmap_pres.get(i).getValue() + "\r\n";
											str_detail += "//////////////////////////////////////////////////////////////////////";
//										System.out.println(str_detail);
											dt.push_back(str_detail);
											target = i;
											break;
										}
										case Action_kind.record_summary:
										{
											sm.Path_changed_count_plus();
											target = i;
											break;
										}
										case Action_kind.recover:
										{
											try
											{
												Thread.sleep(100);
											}
											catch (InterruptedException e)
											{
												e.printStackTrace();
											}
											_file temp_file = new _file();
											temp_file.rename_file(i.getAbsolutePath(), target.getAbsolutePath());
											break;
										}
										default:
									}
								}
								else
									target = i;
							}
						}
						if (find_new == 0)
						{
							//----------------------------------size-changed------------------------------------------------
							if (this.trigger == Trigger_kinds.size_changed)
							{
								switch (this.action)
								{
									case Action_kind.record_detail:
									{
										String str_detail = new String();
										str_detail += "size-changed:\r\n";
										str_detail += "路径变化:\t";
										str_detail += target.getAbsolutePath() + "\t=>\t" + "没了" + "\r\n";
										str_detail += "修改时间变化:\t";
										str_detail += fmap_prev.get(target).getKey().toString() + "\t=>\t" + "0" + "\r\n";
										str_detail += "文件大小变化:\t";
										str_detail += fmap_prev.get(target).getValue().toString() + "\t=>\t" + "0" + "\r\n";
										str_detail += "//////////////////////////////////////////////////////////////////////";
//									System.out.println(str_detail);
										dt.push_back(str_detail);
										//target = i;
										break;
									}
									case Action_kind.record_summary:
									{
										sm.Size_changed_count_plus();
										//target = i;
										break;
									}
									default:
								}
							}
							System.out.println("监视目标" + target.getAbsolutePath() + "丢失，该线程结束");
							return;
						}
					}
					else if (fmap_prev.containsKey(target))
					{
						String target_path = target.getAbsolutePath();
						long target_lastmodified = fmap_prev.get(target).getKey();
						long target_size = fmap_prev.get(target).getValue();


						for (File i : fmap_pres.keySet())
						{
							//----------------------------------modified----------------------------------------------------
							if (i.getAbsolutePath().equals(target_path) &&              //路径相同
									fmap_pres.get(i).getKey() != target_lastmodified)   //修改时间不同
							{
								if (this.trigger == Trigger_kinds.modified)
								{
									switch (this.action)
									{
										case Action_kind.record_detail:
										{
											String str_detail = new String();
											str_detail += "modified:\r\n";
											str_detail += "路径变化:\t";
											str_detail += target.getAbsolutePath() + "\t=>\t" + i.getAbsolutePath() + "\r\n";
											str_detail += "修改时间变化:\t";
											str_detail += fmap_prev.get(target).getKey().toString() + "\t=>\t" + fmap_pres.get(i).getKey() + "\r\n";
											str_detail += "文件大小变化:\t";
											str_detail += fmap_prev.get(target).getValue().toString() + "\t=>\t" + fmap_pres.get(i).getValue() + "\r\n";
											str_detail += "//////////////////////////////////////////////////////////////////////";
//										System.out.println(str_detail);
											dt.push_back(str_detail);
											//target = i;
											break;
										}
										case Action_kind.record_summary:
										{
											sm.Modified_count_plus();
											//target = i;
											break;
										}
										default:
									}
								}
//							else
//								target = i;

							}

							//----------------------------------size-changed------------------------------------------------
							if (i.getAbsolutePath().equals(target_path) &&              //路径相同
									fmap_pres.get(i).getValue() != target_size)         //大小不同
							{
								if (this.trigger == Trigger_kinds.size_changed)
								{
									switch (this.action)
									{
										case Action_kind.record_detail:
										{
											String str_detail = new String();
											str_detail += "size-changed:\r\n";
											str_detail += "路径变化:\t";
											str_detail += target.getAbsolutePath() + "\t=>\t" + i.getAbsolutePath() + "\r\n";
											str_detail += "修改时间变化:\t";
											str_detail += fmap_prev.get(target).getKey().toString() + "\t=>\t" + fmap_pres.get(i).getKey() + "\r\n";
											str_detail += "文件大小变化:\t";
											str_detail += fmap_prev.get(target).getValue().toString() + "\t=>\t" + fmap_pres.get(i).getValue() + "\r\n";
											str_detail += "//////////////////////////////////////////////////////////////////////";
//										System.out.println(str_detail);
											dt.push_back(str_detail);
											//target = i;
											break;
										}
										case Action_kind.record_summary:
										{
											sm.Size_changed_count_plus();
											//target = i;
											break;
										}
										default:
									}
								}
//							else
//								target = i;

							}
						}
					}
				}
				else
				{
					//----------------------------------modified------------------------------------------------------------
					if (this.trigger == Trigger_kinds.modified)
					{
						for (File i : fmap_prev.keySet())
						{
							if (fmap_pres.containsKey(i))
							{
								if (!fmap_prev.get(i).getKey().equals(fmap_pres.get(i).getKey()))
								{
									switch (this.action)
									{
										case Action_kind.record_detail:
										{
											String str_detail = new String();
											str_detail += "modified:\r\n";
											str_detail += "路径变化:\t";
											str_detail += i.getAbsolutePath() + "\t=>\t" + i.getAbsolutePath() + "\r\n";
											str_detail += "修改时间变化:\t";
											str_detail += fmap_prev.get(i).getKey().toString() + "\t=>\t" + fmap_pres.get(i).getKey() + "\r\n";
											str_detail += "文件大小变化:\t";
											str_detail += fmap_prev.get(i).getValue().toString() + "\t=>\t" + fmap_pres.get(i).getValue() + "\r\n";
											str_detail += "//////////////////////////////////////////////////////////////////////";
//										System.out.println(str_detail);
											dt.push_back(str_detail);
											//target = i;
											break;
										}
										case Action_kind.record_summary:
										{
											sm.Modified_count_plus();
											//target = i;
											break;
										}
										default:
									}
								}
							}
						}
					}
					//----------------------------------size-changed--------------------------------------------------------
					else if (this.trigger == Trigger_kinds.size_changed)
					{
						for (File i : fmap_prev.keySet())
						{
							if (fmap_pres.containsKey(i))
							{
								if (!fmap_prev.get(i).getValue().equals(fmap_pres.get(i).getValue()))
								{
									switch (this.action)
									{
										case Action_kind.record_detail:
										{
											String str_detail = new String();
											str_detail += "size-changed:\r\n";
											str_detail += "路径变化:\t";
											str_detail += i.getAbsolutePath() + "\t=>\t" + i.getAbsolutePath() + "\r\n";
											str_detail += "修改时间变化:\t";
											str_detail += fmap_prev.get(i).getKey().toString() + "\t=>\t" + fmap_pres.get(i).getKey() + "\r\n";
											str_detail += "文件大小变化:\t";
											str_detail += fmap_prev.get(i).getValue().toString() + "\t=>\t" + fmap_pres.get(i).getValue() + "\r\n";
											str_detail += "//////////////////////////////////////////////////////////////////////";
//										System.out.println(str_detail);
											dt.push_back(str_detail);
											//target = i;
											break;
										}
										case Action_kind.record_summary:
										{
											sm.Size_changed_count_plus();
											//target = i;
											break;
										}
										default:
									}
								}
							}
							else    //删除
							{
								switch (this.action)
								{
									case Action_kind.record_detail:
									{
										String str_detail = new String();
										str_detail += "size-changed:\r\n";
										str_detail += "路径变化:\t";
										str_detail += i.getAbsolutePath() + "\t=>\t" + "没了" + "\r\n";
										str_detail += "修改时间变化:\t";
										str_detail += fmap_prev.get(i).getKey().toString() + "\t=>\t" + "0" + "\r\n";
										str_detail += "文件大小变化:\t";
										str_detail += fmap_prev.get(i).getValue().toString() + "\t=>\t" + "0" + "\r\n";
										str_detail += "//////////////////////////////////////////////////////////////////////";
//									System.out.println(str_detail);
										dt.push_back(str_detail);
										//target = i;
										break;
									}
									case Action_kind.record_summary:
									{
										sm.Size_changed_count_plus();
										//target = i;
										break;
									}
									default:
								}
							}
						}
						for (File i : fmap_pres.keySet())
						{
							if (!fmap_prev.containsKey(i))    //新建
							{
								switch (this.action)
								{
									case Action_kind.record_detail:
									{
										String str_detail = new String();
										str_detail += "size-changed:\r\n";
										str_detail += "路径变化:\t";
										str_detail += "新建\t" + i.getAbsolutePath() + "\r\n";
										str_detail += "修改时间变化:\t";
										str_detail += "0\t=>\t" + fmap_pres.get(i).getKey().toString() + "\r\n";
										str_detail += "文件大小变化:\t";
										str_detail += "0\t=>\t" + fmap_pres.get(i).getValue().toString() + "\r\n";
										str_detail += "//////////////////////////////////////////////////////////////////////";
//									System.out.println(str_detail);
										dt.push_back(str_detail);
										//target = i;
										break;
									}
									case Action_kind.record_summary:
									{
										sm.Size_changed_count_plus();
										//target = i;
										break;
									}
									default:
								}
							}
						}
					}
					//----------------------------------renamed-------------------------------------------------------------
					else if (this.trigger == Trigger_kinds.renamed)
					{
						for (File i : fmap_prev.keySet())
						{
							for (File j : fmap_pres.keySet())
							{
								if (i.getParent().equals(j.getParent()) &&                                    //所处路径相同
										fmap_prev.get(i).getKey().equals(fmap_pres.get(j).getKey()) &&      //最后修改时间相同
										fmap_prev.get(i).getValue().equals(fmap_pres.get(j).getValue()) &&    //大小相同
										!j.isDirectory() &&                                        //不是文件夹
										!i.getName().equals(j.getName()) &&                                //文件名不同
										!fmap_prev.containsKey(j) &&                                            //prev不包含新的
										!fmap_pres.containsKey(i))                                          //pres不含旧的
								{
									switch (this.action)
									{
										case Action_kind.record_detail:
										{
											String str_detail = new String();
											str_detail += "renamed:\r\n";
											str_detail += "路径变化:\t";
											str_detail += i.getAbsolutePath() + "\t=>\t" + j.getAbsolutePath() + "\r\n";
											str_detail += "修改时间变化:\t";
											str_detail += fmap_prev.get(i).getKey().toString() + "\t=>\t" + fmap_pres.get(j).getKey() + "\r\n";
											str_detail += "文件大小变化:\t";
											str_detail += fmap_prev.get(i).getValue().toString() + "\t=>\t" + fmap_pres.get(j).getValue() + "\r\n";
											str_detail += "//////////////////////////////////////////////////////////////////////";
//										System.out.println(str_detail);
											dt.push_back(str_detail);
											target = i;
											break;
										}
										case Action_kind.record_summary:
										{
											sm.Renamed_count_plus();
											break;
										}
										case Action_kind.recover:
										{
											try
											{
												Thread.sleep(100);
											}
											catch (InterruptedException e)
											{
												e.printStackTrace();
											}
											_file temp_file = new _file();
											temp_file.rename_file(j.getAbsolutePath(), i.getAbsolutePath());
											fmap_pres.remove(j);
											fmap_pres.put(i, fmap_prev.get(i));
											break;
										}
										default:
									}
									break;

								}
							}
						}
					}
					//----------------------------------path_changed--------------------------------------------------------
					else if (this.trigger == Trigger_kinds.path_changed)
					{
						for (File i : fmap_prev.keySet())
						{
							for (File j : fmap_pres.keySet())
							{
								if (!i.getParent().equals(j.getParent()) &&                                    //所处路径不同
										fmap_prev.get(i).getKey().equals(fmap_pres.get(j).getKey()) &&      //最后修改时间相同
										fmap_prev.get(i).getValue().equals(fmap_pres.get(j).getValue()) &&    //大小相同
										j.isDirectory() == false &&                                        //不是文件夹
										i.getName().equals(j.getName()) &&                                //文件名同
										!fmap_prev.containsKey(j) &&                                            //prev不包含新的
										!fmap_pres.containsKey(i))                                          //pres不含旧的
								{

									switch (this.action)
									{
										case Action_kind.record_detail:
										{
											String str_detail = new String();
											str_detail += "path-changed:\r\n";
											str_detail += "路径变化:\t";
											str_detail += i.getAbsolutePath() + "\t=>\t" + j.getAbsolutePath() + "\r\n";
											str_detail += "修改时间变化:\t";
											str_detail += fmap_prev.get(i).getKey().toString() + "\t=>\t" + fmap_pres.get(j).getKey() + "\r\n";
											str_detail += "文件大小变化:\t";
											str_detail += fmap_prev.get(i).getValue().toString() + "\t=>\t" + fmap_pres.get(j).getValue() + "\r\n";
											str_detail += "//////////////////////////////////////////////////////////////////////";
//										System.out.println(str_detail);
											dt.push_back(str_detail);
											target = i;
											break;
										}
										case Action_kind.record_summary:
										{
											sm.Path_changed_count_plus();
											break;
										}
										case Action_kind.recover:
										{
											try
											{
												Thread.sleep(100);
											}
											catch (InterruptedException e)
											{
												e.printStackTrace();
											}
											_file temp_file = new _file();
											temp_file.rename_file(j.getAbsolutePath(), i.getAbsolutePath());
											fmap_pres.remove(j);
											fmap_pres.put(i, fmap_prev.get(i));
											break;
										}
										default:
									}
									break;

								}
							}
						}
					}

				}


				//将previous弄成present
				fmap_prev.clear();
				fmap_prev.putAll(fmap_pres);


				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("发生异常，该线程结束");
		}
	}

	public void test()
	{


	}


}
