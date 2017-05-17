package sparkWCExample.sparkWCExample;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class LineCount {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SparkConf sparkConf = new SparkConf().setAppName("LineCount");

		JavaSparkContext ctx = new JavaSparkContext(sparkConf);

		JavaRDD<String> textLoad = ctx.textFile("C:/Endeca/Project/sparkWCExample/src/main/resources/how.txt");
		
		System.out.println(textLoad.count());

	}
}
