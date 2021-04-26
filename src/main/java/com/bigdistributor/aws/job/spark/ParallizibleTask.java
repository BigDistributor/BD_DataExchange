//package com.bigdistributor.aws.job.spark;
//
//import com.bigdistributor.aws.job.utils.TaskLog;
//import com.bigdistributor.aws.job.utils.TimeLabel;
//import com.bigdistributor.aws.spimloader.SpimLoadSupplier;
//import com.bigdistributor.biglogger.adapters.Log;
//import com.bigdistributor.core.blockmanagement.blockinfo.BasicBlockInfo;
//import com.bigdistributor.core.task.BlockTask;
//import net.imglib2.RandomAccessibleInterval;
//import net.imglib2.util.Util;
//import net.preibisch.mvrecon.fiji.spimdata.SpimData2;
//import net.preibisch.mvrecon.fiji.spimdata.boundingbox.BoundingBox;
//import org.janelia.saalfeldlab.n5.N5Writer;
//import org.janelia.saalfeldlab.n5.imglib2.N5Utils;
//
//public class ParallizibleTask {
//    public static void set(Class<BlockTask> mainTask, BasicBlockInfo binfo) throws IllegalAccessException, InstantiationException {
//        BlockTask application = mainTask.newInstance();
//        int blockID = binfo.getBlockId();
//
//        TaskLog log = new TaskLog(blockID);
//        log.setStatus(TimeLabel.TaskStarted);
//        Log logger = Log.getLogger("Block: " + blockID);
//        logger.blockStarted(blockID, " start processing..");
//
//        SpimData2 sp =  new SpimLoadSupplier(credSupplier, input).getLoader().getSpimdata();
//        BoundingBox bb = new BoundingBox(Util.long2int(binfo.getMin()), Util.long2int(binfo.getMax()));
//
////            SpimLoadSupplier inputSupplier = new SpimLoadSupplier(credSupplier, input);
//
//        RandomAccessibleInterval result = application.blockTask(sp, finalParams, bb);
//
//        log.setStatus(TimeLabel.FinishProcess);
//        logger.blockLog(blockID, " Got processed image");
//        N5Writer writer = n5OutputSupplier.getWriter();
//        N5Utils.saveBlock(result , writer, dataset, binfo.getGridOffset());
//        log.setStatus(TimeLabel.DataSaved);
//        logger.blockDone(blockID, " Task done.");
//        log.setStatus(TimeLabel.FinishProcess);
//    }
//}
