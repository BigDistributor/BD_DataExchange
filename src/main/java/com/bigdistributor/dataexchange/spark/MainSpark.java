package com.bigdistributor.dataexchange.spark;


import com.amazonaws.services.s3.AmazonS3;
import com.bigdistributor.dataexchange.aws.s3.model.S3ClientInstance;
import com.bigdistributor.dataexchange.spark.interf.SparkTask;
import com.bigdistributor.dataexchange.utils.DEFAULT;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.util.Intervals;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Reader;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Command(name = "generator", description = "To give the task just one block from the data, "
        + "This generator take as parameter the data, the metadata and the id of the block needed "
        + "and the generate a block as tif file", version = "generator 0.1")

public class MainSpark implements Callable<Void> {


    private static final int MAX_PARTITIONS = 15000;

    @Option(names = {"-i", "--input"}, required = true, description = "The path of n5 inside s3")
    private String n5InputPath;

    @Option(names = {"-d", "--dataset"}, required = true, description = "The path of the dataset inside container")
    private String inputDatasetPath;

    @Option(names = {"-o", "--output"}, required = true, description = "The path of the output dataset")
    private String n5OutputPath;

    public static void main(String[] args) throws Exception {
        CommandLine.call(new MainSpark(), args);
        System.exit(0);
    }

    @Override
    public Void call() throws Exception {
        Logger.getLogger("org").setLevel(Level.ERROR);

        final JavaSparkContext sparkContext = new JavaSparkContext(new SparkConf()
                .setAppName("N5ConvertSpark")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        );

        AmazonS3 s3 = S3ClientInstance.get();
        N5AmazonS3Reader reader = new N5AmazonS3Reader(s3, DEFAULT.id);

        final DatasetAttributes inputAttributes = reader.getDatasetAttributes(inputDatasetPath);
        final DatasetAttributes outputAttributes = reader.getDatasetAttributes(n5OutputPath);

        final long[] dimensions = inputAttributes.getDimensions();

        final int[] outputBlockSize = outputAttributes.getBlockSize();

        final long numOutputBlocks = Intervals.numElements(new CellGrid(dimensions, outputBlockSize).getGridDimensions());

        final List<Long> outputBlockIndexes = LongStream.range(0, numOutputBlocks).boxed().collect(Collectors.toList());

        sparkContext.parallelize(outputBlockIndexes, Math.min(outputBlockIndexes.size(), MAX_PARTITIONS)).foreach(new SparkTask());

        return null;
    }

}