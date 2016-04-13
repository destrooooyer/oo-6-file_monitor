package oo;

/**
 * Created by DESTR on 2016/4/13.
 */
public class Main
{
	public static void main(String argv[])
	{
//		Filemonitor fm=new Filemonitor(Trigger_kinds.renamed,"C:\\Users\\DESTR\\Desktop\\test");
		Filemonitor fm=new Filemonitor(Trigger_kinds.path_changed,"C:\\Users\\DESTR\\Desktop\\test\\123");
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
