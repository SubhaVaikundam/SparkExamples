package sparkWCExample.sparkWCExample;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkJDBCMySQL implements Serializable {

	private static final String MYSQL_USERNAME = "root";
	private static final String MYSQL_PWD = "root";
	private static final String MYSQL_CONNECTION_URL = "jdbc:mysql://localhost:3306/employees?user=" + MYSQL_USERNAME
			+ "&password=" + MYSQL_PWD;

	// private static final SQLContext sqlContext = new SQLContext(sc);

	public static void main(String[] args) {

		SparkSession spark = SparkSession.builder().appName("Java Spark SQL basic example")
				.config("spark.some.config.option", "some-value").getOrCreate();

		// Sample data-frame loaded from a JSON file
		// DataFrame usersDf =
		// sqlContext.jsonFile("spark-save-to-db/src/main/resources/users.json");
		Dataset<Row> usersDf = spark.read().json("C:/Endeca/Project/sparkWCExample/src/main/resources/users.json");

		// Displays the content of the DataFrame to stdout
		System.out.println("SUbha :=" + usersDf.count());
		// usersDf.show();

		runJdbcDatasetExample(spark);

		runJdbcDatasetMongoExample(spark);
		// spark.stop();

	}

	private static void runJdbcDatasetExample(SparkSession spark) {

		// Load properties from file
		Properties dbProperties = new Properties();
		try {
			dbProperties.load(new FileInputStream(
					new File("C:/Endeca/Project/sparkWCExample/src/main/resources/db-properties.flat")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String jdbcUrl = dbProperties.getProperty("jdbcUrl");

		System.out.println("A DataFrame loaded from the entire contents of a table over JDBC." + jdbcUrl);
		String where = "sparkour.people";
		Dataset<Row> entireDF = spark.read().jdbc(jdbcUrl, where, dbProperties);
		entireDF.printSchema();
		entireDF.show();

		System.out.println("Filtering the table to just show the males.");
		entireDF.filter("is_male = 1").show();

		System.out.println("Alternately, pre-filter the table for males before loading over JDBC.");
		where = "(select * from sparkour.people where is_male = 1) as subset";
		Dataset<Row> malesDF = spark.read().jdbc(jdbcUrl, where, dbProperties);
		malesDF.show();

		System.out.println("Update weights by 2 pounds (results in a new DataFrame with same column names)");
		Dataset<Row> heavyDF = entireDF.withColumn("updated_weight_lb", entireDF.col("weight_lb").plus(2));
		Dataset<Row> updatedDF = heavyDF.select("id", "name", "is_male", "height_in", "updated_weight_lb")
				.withColumnRenamed("updated_weight_lb", "weight_lb");
		updatedDF.show();

		System.out.println("Save the updated data to a new table with JDBC");
		where = "sparkour.updated_people";
		updatedDF.write().mode("error").jdbc(jdbcUrl, where, dbProperties);

		System.out.println("Load the new table into a new DataFrame to confirm that it was saved successfully.");
		Dataset<Row> retrievedDF = spark.read().jdbc(jdbcUrl, where, dbProperties);
		retrievedDF.show();

		spark.stop();
	}

	private static void runJdbcDatasetMongoExample(SparkSession spark) {

	}
}
