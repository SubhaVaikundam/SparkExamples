package sparkWCExample.sparkWCExample;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import scala.Tuple2;

import au.com.bytecode.opencsv.CSVReader;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;

public class BasicLoadWholeCsv {

  public static class ParseLine implements FlatMapFunction<Tuple2<String, String>, String[]> {
 
	
	public Iterator<String[]> call(Tuple2<String, String> file) throws Exception {
      CSVReader reader = new CSVReader(new StringReader(file._2()));
      return (Iterator<String[]>) reader.readAll();
    }
  }

  public static void main(String[] args) throws Exception {
		/*if (args.length != 3) {
      throw new Exception("Usage BasicLoadCsv sparkMaster csvInputFile csvOutputFile key");
		}*/
    String master = "local"; //args[0];
    String csvInput = "C:/Endeca/Project/sparkWCExample/src/main/resources/products.csv"; //args[1];
    String outputFile = "C:/Endeca/Project/sparkWCExample/src/main/resources/products1.csv";// args[2];
    final String key = "SKU";

		JavaSparkContext sc = new JavaSparkContext(
      master, "loadwholecsv", System.getenv("SPARK_HOME"), System.getenv("JARS"));
    JavaPairRDD<String, String> csvData = sc.wholeTextFiles(csvInput);
    JavaRDD<String[]> keyedRDD = csvData.flatMap(new ParseLine());
    JavaRDD<String[]> result =
      keyedRDD.filter(new Function<String[], Boolean>() {
          public Boolean call(String[] input) { return input[0].equals(key); }});

    result.saveAsTextFile(outputFile);
	}
}
