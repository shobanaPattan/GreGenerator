package org.gregenai;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkDemo {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .master("local[*]")
                .appName("SparkExample")
                .getOrCreate();

        System.out.println("Details of employees");
        Dataset<Row> details = spark.read().option("multiLine", true)
                                           .option("mode", "PERMISSIVE")
                                           .json("src/main/resources/people.json");
        details.show();

        spark.stop();

    }
}
