/*** @author ydq 
 *   @version 1.0
 *   创建时间：2022年2月28日 上午10:53:09
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

 
public abstract class KMeans <T>{
	private List<T> dataArray;//待分类的原始值
	private int K = 3;//将要分成的类别个数
	private int maxClusterTimes = 500;//最大迭代次数
	private List<List<T>> clusterList;//聚类的结果
	private List<T> clusteringCenterT;//质心
	private double theSumOfSilhouCoef = 0.0;//总迭代次数的轮廓系数之和
	
	public int getK() {
		return K;
	}
	public void setK(int K) {
		if (K < 1) {
			throw new IllegalArgumentException("K must greater than 1");
		}
		this.K = K;
	}
	public List<T> getDataArray() {
		return dataArray;
	}
	public void setDataArray(List<T> dataArray) {
		this.dataArray = dataArray;
	}
	public int getMaxClusterTimes() {
		return maxClusterTimes;
	}
	public void setMaxClusterTimes(int maxClusterTimes) {
		if (maxClusterTimes < 10) {
			throw new IllegalArgumentException("maxClusterTimes must greater than 10");
		}
		this.maxClusterTimes = maxClusterTimes;
	}
	public List<T> getClusteringCenterT() {
		return clusteringCenterT;
	}
	/**
	 * @return
	 * @Author:ydq  
	 * @Description: 对数据进行聚类
	 */
	public List<List<T>> clustering() {
		if (dataArray == null) {
			return null;
		}
		//初始K个点为数组中的前K个点
		int size = K > dataArray.size() ? dataArray.size() : K;
		List<T> centerT = new ArrayList<T>(size);
		//对数据进行打乱
		// Collections.shuffle(dataArray);
		// 采用随机的方式初始化K个聚类质心
		Random seeds = new Random(System.currentTimeMillis());
		int id;
		for (int i = 0; i < size; i++) {
			id = seeds.nextInt(10000) % K;
			centerT.add(dataArray.get(id));
		}
		clustering(centerT, 0);
		return clusterList;
	}
	
	/**
	 * @param preCenter
	 * @param times
	 * @Author:ydq  
	 * @Description: 一轮聚类
	 */
	private void clustering(List<T> preCenter, int times) {
		if (preCenter == null || preCenter.size() < 2) {
			return;
		}
		//打乱质心的顺序
		Collections.shuffle(preCenter);
		List<List<T>> clusterList =  getListT(preCenter.size());
		for (T o1 : this.dataArray) {
			//寻找最相似的质心
			int max = 0;
			// 得分越高代表越相似，越有可能是同一类
			double maxScore = similarScore(o1, preCenter.get(0));
			for (int i = 1; i < preCenter.size(); i++) {
				if (maxScore < similarScore(o1, preCenter.get(i))) {
					maxScore = similarScore(o1, preCenter.get(i));
					max = i;
				}
			}
			clusterList.get(max).add(o1);
		}
		// 计算本次聚类结果每个类别的质心
		List<T> nowCenter = new ArrayList<T> ();
		// 记录空簇的数量
		int emptyClusterNumber = 0;
		for (int i = 0; i < clusterList.size(); i++) {
			if (clusterList.get(i).size() > 0) {
				nowCenter.add(getCenterT(clusterList.get(i)));
			}else {
				emptyClusterNumber++;
			}
		}
		// 簇中不包含任何点，及时调整质心：将其它簇心的平均值作为新的簇心
		for(int j = 0; j < emptyClusterNumber; j++){
			nowCenter.add(dealEmptyCenterT(nowCenter));
		}
		//是否达到最大迭代次数
		if (times >= this.maxClusterTimes || preCenter.size() < this.K) {
			this.clusterList = clusterList;
			return;
		}
		this.clusteringCenterT = nowCenter;
		
		// 计算每轮的轮廓系数
		double silhoueCoeff = 0.0;
		silhoueCoeff = silhouetteCoefficient(nowCenter, clusterList, dataArray.size());
		theSumOfSilhouCoef += silhoueCoeff;
		
		// 打印该轮次的信息
		System.out.println("Iteration round: " + (times + 1) + "th\tsilhouetteCoefficient: " + silhoueCoeff);
		
		//判断质心是否发生移动，如果没有移动，结束本次聚类，否则进行下一轮
		if (isCenterChange(preCenter, nowCenter)) {
			// 迭代运算
			clear(clusterList);
			clustering(nowCenter, times + 1); 
		} else {
			this.clusterList = clusterList;
		}
	}
	
	/**
	 * @param size
	 * @return
	 * @Author:ydq  
	 * @Description: 初始化一个聚类结果
	 */
	private List<List<T>> getListT(int size) {
		List<List<T>> list = new ArrayList<List<T>>(size);
		for (int i = 0; i < size; i++) {
			list.add(new ArrayList<T>());
		}
		return list;
	}
	
	/**
	 * @param lists
	 * @Author:ydq  
	 * @Description: 清空无用数组
	 */
	private void clear(List<List<T>> lists) {
		for (List<T> list : lists) {
			list.clear();
		}
		lists.clear();
	}
	
	/**
	 * @param value
	 * @Author:ydq  
	 * @Description: 向模型中添加记录
	 */
	public void addRecord(T value) {
		if (dataArray == null) {
			dataArray = new ArrayList<T>();
		}
		dataArray.add(value);
	}
	
	/**
	 * @param preT
	 * @param nowT
	 * @return
	 * @Author:ydq  
	 * @Description: 判断质心是否发生移动
	 */
	private boolean isCenterChange(List<T> preT, List<T> nowT) {
		if (preT == null || nowT == null) {
			return false;
		}
		for (T t1 : preT) {
			boolean bol = true;
			for (T t2 : nowT) {
				if (equals(t1, t2)) {//t1在t2中有相等的，认为该质心未移动
					bol = false;
					break;
				}
			}
			//有一个质心发生移动，认为需要进行下一次计算
			if (bol) {
				return bol;
			}
		}
		return false;
	}
	
	/**
	 * @param o1
	 * @param o2
	 * @return
	 * @Author:ydq
	 * @Description: o1 o2之间的相似度
	 */
	public abstract double similarScore(T o1, T o2);
	
	/**
	 * @param o1
	 * @param o2
	 * @return
	 * @Author:ydq  
	 * @Description: 判断o1 o2是否相等
	 */
	public abstract boolean equals(T o1, T o2);
	
	/**
	 * @param list
	 * @return
	 * @Author:ydq  
	 * @Description: 求一组数据的质心
	 */
	public abstract T getCenterT(List<T> list);
	
	/**
	 * @param list
	 * @Author:ydq  
	 * @Description: 空簇心的解决
	 */
	public abstract T dealEmptyCenterT(List<T> list);
	
	/**
	 * @param centeri
	 * @param clusteri
	 * @Author:ydq  
	 * @Description: 模型评估，计算簇内相似度之和
	 */
	public abstract double theAvgOfClusteri(T centeri, List<T> clusteri);
	
	/**
	 * @param center
     * @param allCluster
     * @param dataSize
     *  @Description: 聚类评估标准，轮廓系数
	 */
	public abstract double silhouetteCoefficient(List<T> center, List<List<T>> allCluster, int dataSize);
	
	/**
     * a(i) - b(i) / max(a(i), b(i))
     * a(i) the average of same cluster
     * b(i) the min average of not same cluster
     * @param data
     * @param result
     * @param flag
     * @param center
     * @Description: 聚类评估标准
     */
    /*private static double silhouetteCoefficient(List<Vector> data, List<Integer> result, int flag, Vector center, int centerSize) {
    	double sameClusterSum = 0.0;
    	double otherClusterSum = 0.0;
    	double min = Double.MAX_VALUE;
    	
    	for(int j = 0; j < centerSize; j++) {
    		if(j != flag) {
    			for(int i = 0; i < data.size(); i++) {
    				if(result.get(i) == j) {
    					otherClusterSum += euclideanDistance(data.get(i).toArray(), center.toArray());
    				}
    			}
	    		min = min<otherClusterSum ? min:otherClusterSum; //非同一cluster里面的最短distance
    		}
    	}
    	
    	for(int i = 0; i < data.size(); i++) {
    		if(result.get(i)==flag) {
    			sameClusterSum += euclideanDistance(data.get(i).toArray(), center.toArray());
    		} 
    	}
    	
    	double coef = (min/data.size() - sameClusterSum/data.size()) / Math.max(sameClusterSum/data.size(), min/data.size());
		return coef;
    }*/
}