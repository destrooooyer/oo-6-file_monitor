1.�������룺
	�����ʽ��
		��IF "·��" ������ THEN ����
	
	Ϊ�˱��ڲ�������������ʽ���˴�����ƥ�������������ʽ���뱣֤���������������ʽ��
		IF\s+"(.*)"\s+(renamed|modified|path-changed|size-changed)\s+THEN\s+(record-summary|recover|record-detail)
	���磺
		IF "D:\kancolle\ShimakazeGo\error.log" renamed THEN recover
	
	���������·����
		����Ϊ����·���Ҵ���
	
	��ʾ��
		·�������������Ӣ�ĵ�˫����
		�����������֣�renamed|modified|path-changed|size-changed
		���������֣�record-summary|recover|record-detail
	
	����Ҫ��
		�밴����ҵҪ������˵�ģ���Ҫ����modified��recover����ϻ�size-changed��recover�����
		ͬʱҲ�밴��ҵҪ������˵�ģ���Ҫ��ض���8��������
	
	���������
		����run֮���������������߳̿�ʼ����
		
2.���������
	summary��
		summary�߳�ÿ��100ms�Ὣ��������е�ȫ���������������Ŀ¼�е�summary.txt
		���ı��༭�����´�summary.txt���ܿ������ݵı仯
		summary.txt�м�¼����record-summary�����Ӧ�Ĵ������Ĵ���������
		ֵ��ע����ǣ��������������������ͬһ���ļ�����ô�����仯ʱ���������������ᱻ���������summary�м�¼�Ĵ����������������Σ��Դ����ơ�
	
	detail:
		��summary���ƣ����¼������Ϊ��⵽�ľ���仯������·�����޸�ʱ����ļ���С
		
3.���ڲ��ԣ�
	����windows�²���
	�뱣֤��������û���κ�û��Ȩ�޵�Ŀ¼���ļ���
	�벻Ҫ��̫���Ŀ¼��Ϊ������
	
	����ɨ������
		������ɨ��֮���ж�������Ļ������ڼ��ֻ�ǶԱ�����ɨ����֮��Ĳ���еĲ����Ϳ��ܲ�����⵽�����δ������˼��û��ɨ�赽���ǲ��������ģ����Լ��¡�
		Ȼ��Ϊ�˲��ԵĲ����ܱ�ȷʵ��⵽���뱣֤ÿ�������ļ������ļ��������1s����Ȼ����������������Ӵ�ɨ���ʱ�䳬��1s�Ļ������������Ӷ��ļ��Ĳ��������
	
	�ṩ�������ߵ��ļ������ࣺ
		_file��
		�밴����ҵҪ����˵�ģ���ʹ��_file���и����ķ����Թ��������в����������κβ�������Ĵ����Ҷ��޷�����
		
	_file���е����¼��������ɹ�ʹ�ã�
		public boolean create_file(String str)
			�����ļ�������Ϊ�ļ��ľ���·�������ļ��Ѵ����򴴽�ʧ��
			������[_file��Ķ���].create_file("D:\kancolle\ShimakazeGo\error.log");
			
		public boolean create_dir(String str)
			�����ļ��У�����Ϊ�ļ��еľ���·�������ļ����Ѵ����򴴽�ʧ�ܣ�����ͬ�����޺�׺���ļ�Ҳ�ᴴ��ʧ�ܣ������ֱ�ӵ���File.mkdir()���ؾ���false��
			������[_file��Ķ���].create_dir("D:\kancolle\ShimakazeGo");
		
		public boolean delete_file(String str)
			ɾ���ļ����ļ��У�����Ϊ����·�������������ʧ�ܡ�
			������[_file��Ķ���].delete_file("D:\kancolle\ShimakazeGo\error.log");
		
		public boolean rename_file(String src, String dest)
			�������ļ�������Ϊ����·������src·�����ļ�������Ϊdest·�����ļ���
			��src·�����ļ������ڻ�dest·�����ļ��Ѿ��������ʧ�ܡ�
			������[_file��Ķ���].rename_file("D:\kancolle\ShimakazeGo\error.log","D:\kancolle\ShimakazeGo\a.txt");
			ע�⣺�˷���Ҳ���������ƶ��ļ�����һ��Ŀ¼��
		
		public boolean move_file(String src, String dest)
			�˷���Ϊpublic boolean rename_file(String src, String dest)����ף���������û������
			�ƶ��ļ����ô˷���
			������[_file��Ķ���].move_file("D:\kancolle\ShimakazeGo\error.log","D:\kancolle\error.log");
			
		public boolean file_append(String str)
			�˷�������ָ���ļ��Ľ�β����ַ���"wtf"���Ըı���ļ��Ĵ�С������޸�ʱ�䡣
			����Ϊ�ļ��ľ���·��
			������[_file��Ķ���].file_append("D:\kancolle\ShimakazeGo\error.log");
			
		public String get_file_name(String str)
		public long get_last_modified(String str)
		public long get_size(String str)
			��ȡ�ļ���Ϣ������������������д�˰�......�������㿴��������������......
			
		������
			���Ϸ������в���ʧ��ʱ���п��������ʾ��Ϣ�������Ƿ��������
			
	���ڲ��Դ������ӣ�
		���λ�ã�
			Main����main���������ע�ʹ�����113�д����������̡߳�
			Ҳ����ȡ��114����116�е�ע�ͣ�����Test���run��������Ӳ��Դ��롣
		���ڲ��Դ������sleep
			����ÿ������֮���������1s��sleep��ԭ������ǰ��ġ�3.���ڲ���-����ɨ�������н����ˣ�������Test���run�����У��ɹ��ο���
			
4.����
	����ص����ļ��������ļ���ɾ������ô�ü���߳̽�������

5.��г��ϵ
	�����ҵд�ı����͹������ˣ���һ��ʼ��watchserviceд�ģ���д����ͻȻ���˸�Ҫ��˵�����ã�Ȼ������д(�s�F����)�s��ߩ���......���Ҵ����֪���ˣ�Filemonitor���700����ȫ��д��......
	��֮ϣ���ܱ��ź�г��ϵԭ����в���......
	����������Orz......