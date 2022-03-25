import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.powergrid.pojo.*;

/**
 * 改进的KMeans聚类算法：
 * 		用余弦相似度代替欧式距离作为新的度量指标 
 */
/*** @author 作者 E-mail:* @version 创建时间：2022年2月28日 下午4:11:58* 类说明*/
/**
 * @author ydq
 * @version 2022年2月28日
 */
public class CosineKmeans extends KMeans<NumberFrequency> {

	// 采用余弦相似度作为新的聚类标准
	@Override
	public double similarScore(NumberFrequency o1, NumberFrequency o2) {
		int molecular = 0, o1Square = 0, o2Square = 0; 
		for (int i = 0; i < o1.getVector().length; i++) {
			molecular += o1.getVector()[i] * o2.getVector()[i];
			o1Square += o1.getVector()[i] * o1.getVector()[i];
			o2Square += o2.getVector()[i] * o2.getVector()[i];
		}
		double denominator = Math.sqrt(o1Square) * Math.sqrt(o2Square);
		double score = 0;
		try {
			score = molecular/denominator;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return score;
	}

	// 简单的判断各元素是否相等，即两个数组是否相等
	@Override
	public boolean equals(NumberFrequency o1, NumberFrequency o2) {
		return Arrays.equals(o1.getVector(), o2.getVector());
	}

	// 不采用计算均值向量的方式更新质心，而采用在该类簇下随机选择的方式
	@Override
	public NumberFrequency getCenterT(List<NumberFrequency> list) {
		/*Random seeds = new Random(System.currentTimeMillis());
		int id = 0;
		try {
			id = seeds.nextInt(10000) % list.size();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list.get(id);*/
		float[] newCenter = new float[10];
		float col = 0;
		int w = list.size();
		try {
			for(int i = 0; i < list.get(0).getVector().length; i++){
				for (NumberFrequency nf : list) {
					col += nf.getVector()[i];
				}
				newCenter[i] = col / w;
				col = 0; // 刷新
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new NumberFrequency(newCenter);
	}

	// 处理空簇
	@Override
	public NumberFrequency dealEmptyCenterT(List<NumberFrequency> list) {
		float[] newCenter = new float[10];
		float col = 0;
		int w = list.size();
		try {
			for(int i = 0; i < list.get(0).getVector().length; i++){
				for (NumberFrequency nf : list) {
					col += nf.getVector()[i];
				}
				newCenter[i] = col / w;
				col = 0; // 刷新
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new NumberFrequency(newCenter);
	}
	
	// 模型评估，计算簇内余弦相识度均值 Cosine Similarity Mean 
	public double theAvgOfClusteri(NumberFrequency centeri, List<NumberFrequency> clusteri){
		double sum = 0;
		for (NumberFrequency bf : clusteri) {
			sum += similarScore(centeri, bf);
		}
		return sum / clusteri.size();
	}

	
	@Override
	public double silhouetteCoefficient(List<NumberFrequency> center, List<List<NumberFrequency>> allCluster, int dataSize) {
		double coeff = 0.0;
		double sameClusterSum = 0.0, otherClusterSum = 0.0;
		List<NumberFrequency> sameCluster = null, otherCluster = null;
		double ao = 0.0;
		double bo = Double.MAX_VALUE;
		double tmpbo = 0.0;
		double so = 0.0;
		double theSumOfSo = 0.0;
		for(int i = 0; i < center.size(); i++){
			// 先计算一个簇中的样本点o
			sameCluster = allCluster.get(i);
			for (int j = 0; j < sameCluster.size(); j++) {
				for (int k = 0; k < sameCluster.size(); k++) {
					if (j != k) {
						sameClusterSum += similarScore(sameCluster.get(j), sameCluster.get(k));
					}
				}
				// a(o) the average of same cluster
				ao = sameClusterSum / (sameCluster.size() - 1);
				// b(o) the min average of not same cluster
				for(int t = 0; t < center.size(); t++){
					if (i != t) { // 判断是否是当前簇
						otherCluster = allCluster.get(t);
						for (NumberFrequency other : otherCluster) {
							otherClusterSum += similarScore(sameCluster.get(j), other);
						}
						tmpbo = otherClusterSum / otherCluster.size();
						bo = (bo < tmpbo) ? bo : tmpbo;
						otherClusterSum = 0.0; //清零
					}
				}
				// 计算第i个样本的轮廓系数
				so = (ao - bo) / Math.max(ao, bo);
				theSumOfSo += so;
			}
		}
		// 求出平均值即为当前聚类的整体轮廓系数
		coeff = theSumOfSo / dataSize;
		return coeff;
	}
}

