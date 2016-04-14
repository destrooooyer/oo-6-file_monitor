package oo;

/**
 * Created by DESTR on 2016/4/13.
 */
public class Main
{
	public static void main(String argv[])
	{
		Summary sm=new Summary();
		Thread T_summary=new Thread(sm);
		T_summary.start();
//		Filemonitor fm=new Filemonitor(Trigger_kinds.renamed,"C:\\Users\\DESTR\\Desktop\\test");
		Filemonitor fm=new Filemonitor(Trigger_kinds.path_changed,"C:\\Users\\DESTR\\Desktop\\test\\123\\a.txt",sm);
//		Thread T=new Thread(fm);
//		T.run();
//		try
//		{
//			T.join();
//		}
//		catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		}
		fm.test();
	}
}
