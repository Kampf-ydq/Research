import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.powergrid.pojo.NumberFrequency;
import com.powergrid.utils.ExcelReader;

/**
 * 
 */
/*** @author 作者 E-mail:* @version 创建时间：2022年3月1日 下午11:00:53* 类说明*/
/**
 * @author ydq
 * @version 2022年3月1日
 */
public class StartClass {
	
	public static void main(String[] args) {
		
		// 加载数据集
		ExcelReader excelReader = new ExcelReader("G:/Cybersecurity/DT-PRO/LiaoElectricity/data_process/update/cipin_feature.xlsx", "page_1");
		List<NumberFrequency> list = excelReader.readExcelData();
		
		// 获取聚类实例
		CosineKmeans cluster = new CosineKmeans();
		// 类别数量
		int K = 5;
		cluster.setK(K);
		// 设置迭代次数
		cluster.setMaxClusterTimes(300);
		for (NumberFrequency nf : list) {
			cluster.addRecord(nf);
		}
//		System.out.println(cluster.getDataArray().size());
		List<List<NumberFrequency>> clusterResult = cluster.clustering();
		List<NumberFrequency> center = cluster.getClusteringCenterT();
		HashMap<NumberFrequency, List<NumberFrequency>> result = new HashMap<>();
		
		// 设置类别值label，并存储到List中
		System.out.println("+-----------------------------------------------------------------+");
		System.out.println("|Iteration round: " + cluster.getMaxClusterTimes());
		System.out.println("|-----------------------------------------------------------------+");
		List<NumberFrequency> listNf = new ArrayList<>();
		for (int i = 0; i < center.size(); i++) {
			System.out.println("|Cluster: " + (i + 1) + "\tSize: " + clusterResult.get(i).size());
			result.put(center.get(i), clusterResult.get(i));
			// 先添加簇心
			NumberFrequency centeri = center.get(i);
			centeri.setLabel("-" + (i + 1));// 簇心的标志
			listNf.add(centeri);
			for (NumberFrequency nf : clusterResult.get(i)) {
				nf.setLabel("" + (i + 1));
				listNf.add(nf);
			}
		}
		System.out.println("|-----------------------------------------------------------------+");
		
		// 计算当前聚类的轮廓系数
		/*System.out.println("+-----------------------------------------------------------------+");
		System.out.println("|The value of K: " + cluster.getK());
		System.out.println("|Silhouette Coefficient: " + cluster.silhouetteCoefficient(center, clusterResult, listNf.size()));
		System.out.println("+-----------------------------------------------------------------+");*/
		
		// 计算簇内余弦相识度均值 Cosine Similarity Mean 
		/*System.out.println("+-----------------------------------------------------------------+");
		System.out.println("|Iteration round: " + cluster.getMaxClusterTimes());
		System.out.println("|-----------------------------------------------------------------+");
		for (int i = 0; i < center.size(); i++) {
			double CSM = cluster.theAvgOfClusteri(center.get(i), clusterResult.get(i));
			System.out.println("|Cluster: "+ (i + 1) + "\tCosine Similarity Mean: " + CSM);	
		}
		System.out.println("+-----------------------------------------------------------------+");*/
		
		// 打印聚类结果
		/*Set<Entry<NumberFrequency, List<NumberFrequency>>> mSet = result.entrySet();
		for (Map.Entry entry : mSet) {
			System.out.println("Center Point" + entry.getKey() + ":\n" + entry.getValue());
		}*/
		
		/*for (List<NumberFrequency> nfList : clusterResult) {
			for (NumberFrequency numberFrequency : nfList) {
				System.out.println(numberFrequency);
			}
		}*/
		// 写数据到excel
		/*String filepath = "G:/Cybersecurity/DT-PRO/LiaoElectricity/data_process/update/cluster_result - maxiterate200.xlsx";
		List<String> title = new ArrayList<String>();
		title.add("user_id");
		title.add("label");
		title.add("vector");
		excelReader.writeDatatoExcel(listNf, filepath, "page_1", title);*/
	}
}
