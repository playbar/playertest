package com.rednovo.libs.net.fresco;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.internal.Supplier;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.ByteConstants;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ExecutorSupplier;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.PriorityThreadFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.rednovo.libs.common.StorageUtils;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class FrescoEngine {

    public static final String CACHE_FILE_NAME = "cache_images";
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();// 分配的可用内存
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;// 使用的缓存数量

    private static final int MAX_MEMORY_BITMAP_SIZE = 256; // 内存中最大图片数量

    private static final int MEMORY_CLEAN_BITMAP_SIZE = 128;// 内存缓存中准备清除的总图片的最大数量

    private static final int MEMORY_SINGLE_BITMAP_SIZE = 384;// 内存缓存中单个图片的最大大小

    private static final int MAX_SMALL_DISK_VERYLOW_CACHE_SIZE = 5 * ByteConstants.MB;// 小图极低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    private static final int MAX_SMALL_DISK_LOW_CACHE_SIZE = 10 * ByteConstants.MB;// 小图低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    private static final int MAX_SMALL_DISK_CACHE_SIZE = 20 * ByteConstants.MB;// 小图磁盘缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）

    private static final int MAX_DISK_CACHE_VERYLOW_SIZE = 10 * ByteConstants.MB;// 默认图极低磁盘空间缓存的最大值
    private static final int MAX_DISK_CACHE_LOW_SIZE = 30 * ByteConstants.MB;// 默认图低磁盘空间缓存的最大值
    private static final int MAX_DISK_CACHE_SIZE = 50 * ByteConstants.MB;// 默认图磁盘缓存的最大值

    /**
     * fresco初始化配置
     *
     * @param mContext
     */
    public static void init(Context mContext) {
        /** 初始化OKhttp **/
        OkHttpClient mOkHttpClient = new OkHttpClient();
        // 设置连接池大小
        mOkHttpClient.setConnectionPool(new ConnectionPool(3, 60000L));
        // 连接超时
        mOkHttpClient.setConnectTimeout(5000L, TimeUnit.MILLISECONDS);
        // 读取超时
        mOkHttpClient.setReadTimeout(3000L, TimeUnit.MILLISECONDS);
        // 写入超时
        mOkHttpClient.setWriteTimeout(2000L, TimeUnit.MILLISECONDS);
        /**
         * fresco默认使用httpUrlconnection去下载图片 这里设置采用okhttp去下载图片
         **/

        ExecutorSupplier mExecutorSupplier = initExecutorSupplier();
        DiskCacheConfig mainDiskCacheConfig = initDiskCache(mContext);
        DiskCacheConfig smallImageDiskCacheConfig = initSmallImageDisk(mContext);
        Supplier<MemoryCacheParams> bitmapMemoryCacheParamsSupplier = initBitmapMemoryCacheParamsSupplier();

        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder(mContext, mOkHttpClient).setBitmapsConfig(Bitmap.Config.RGB_565)// 设置图片格式
                .setExecutorSupplier(mExecutorSupplier) // 线程池配置
                .setMainDiskCacheConfig(mainDiskCacheConfig)// /磁盘缓存配置（总，三级缓存
                .setDownsampleEnabled(true).setSmallImageDiskCacheConfig(smallImageDiskCacheConfig).setBitmapMemoryCacheParamsSupplier(bitmapMemoryCacheParamsSupplier).build();
        // .setCacheKeyFactory(cacheKeyFactory)//缓存Key工厂
        // .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)//内存缓存和未解码的内存缓存的配置（二级缓存）
        // .setImageCacheStatsTracker(imageCacheStatsTracker)//统计缓存的命中率
        // .setMemoryTrimmableRegistry(memoryTrimmableRegistry)/内存用量的缩减,
        // .setNetworkFetchProducer(networkFetchProducer)//自定的网络层配置：如OkHttp，Volley
        // .setPoolFactory(poolFactory)//线程池工厂配置
        // .setProgressiveJpegConfig(progressiveJpegConfig)//渐进式JPEG图
        // .setRequestListeners(requestListeners)//图片请求监听
        Fresco.initialize(mContext, config);
    }

    private static Supplier<MemoryCacheParams> initBitmapMemoryCacheParamsSupplier() {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(MAX_MEMORY_CACHE_SIZE, // 内存缓存中总图片的最大大小,以字节为单位。
                MAX_MEMORY_BITMAP_SIZE, // 内存缓存中图片的最大数量。
                MAX_MEMORY_CACHE_SIZE, // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                MEMORY_CLEAN_BITMAP_SIZE, // 内存缓存中准备清除的总图片的最大数量。
                MEMORY_SINGLE_BITMAP_SIZE); // 内存缓存中单个图片的最大大小。

        // 修改内存图片缓存数量，空间策略（这个方式有点恶心）
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {

            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };
        return mSupplierMemoryCacheParams;
    }

    private static DiskCacheConfig initSmallImageDisk(Context mContext) {
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(mContext).setBaseDirectoryPath(StorageUtils.getCacheDirectory(mContext)).setBaseDirectoryName(CACHE_FILE_NAME).setMaxCacheSize(MAX_SMALL_DISK_CACHE_SIZE).setMaxCacheSizeOnLowDiskSpace(MAX_SMALL_DISK_LOW_CACHE_SIZE)
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_SMALL_DISK_VERYLOW_CACHE_SIZE).setVersion(1).build();
        return diskCacheConfig;
    }

    private static DiskCacheConfig initDiskCache(Context mContext) {
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(mContext).setBaseDirectoryPath(StorageUtils.getCacheDirectory(mContext))// 缓存图片基路径
                .setBaseDirectoryName(CACHE_FILE_NAME)// 文件夹名
                // .setCacheErrorLogger(cacheErrorLogger)//日志记录器用于日志错误的缓存。
                // .setCacheEventListener(cacheEventListener)//缓存事件侦听器。
                // .setDiskTrimmableRegistry(diskTrimmableRegistry)//类将包含一个注册表的缓存减少磁盘空间的环境。
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)// //默认缓存的最大大小
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)// 缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)// 缓存的最大大小,当设备极低磁盘空间
                .setVersion(0).build();
        return diskCacheConfig;
    }

    /**
     * 配置线程池
     *
     * @return
     */
    private static ExecutorSupplier initExecutorSupplier() {
        final ThreadFactory mTrheadFactory = new PriorityThreadFactory(10);
        final Executor forBackExecutor = Executors.newFixedThreadPool(8, mTrheadFactory);
        final Executor ecodeExecutor = Executors.newFixedThreadPool(4, mTrheadFactory);
        final Executor forLightExecutor = Executors.newFixedThreadPool(6, mTrheadFactory);
        final Executor loExecutor = Executors.newFixedThreadPool(2);
        ExecutorSupplier executorSupplier = new ExecutorSupplier() {

            @Override
            public Executor forLocalStorageWrite() {

                return loExecutor;
            }

            @Override
            public Executor forLocalStorageRead() {

                return loExecutor;
            }

            @Override
            public Executor forLightweightBackgroundTasks() {

                return forLightExecutor;
            }

            @Override
            public Executor forDecode() {

                return ecodeExecutor;

            }

            @Override
            public Executor forBackgroundTasks() {

                return forBackExecutor;
            }
        };
        return executorSupplier;
    }


    public static void setSimpleDraweeView(SimpleDraweeView paramSimpleDraweeView, String paramUrl, ImageRequest.ImageType paramImageType, ControllerListener<? super ImageInfo> paramControllerListener) {
        if (paramUrl == null)
            paramUrl = "";
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(paramUrl)).setImageType(paramImageType).build();
        paramSimpleDraweeView.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setControllerListener((ControllerListener<? super ImageInfo>) paramControllerListener)).setImageRequest(imageRequest)).build());
    }

    public static void setSimpleDraweeView(SimpleDraweeView paramSimpleDraweeView, String paramUrl, ImageRequest.ImageType paramImageType) {
        String urlTag = (String) paramSimpleDraweeView.getTag() + "";
        if (paramUrl == null) {
            paramUrl = "";
        }
        if (TextUtils.isEmpty(paramUrl) || (!TextUtils.isEmpty(paramUrl) && paramUrl.compareTo(urlTag) != 0)) {
            paramSimpleDraweeView.setTag(paramUrl);
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(paramUrl)).setImageType(paramImageType).build();
            paramSimpleDraweeView.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setOldController(paramSimpleDraweeView.getController())).setImageRequest(imageRequest)).build());
        }
    }


    /**
     * @param paramSimpleDraweeView
     * @param paramUrl              设置url
     * @param paramImageType        设置type值SMALL ,DEFAULT
     * @param postprocessor         设置后处理器
     */
    public static void setSimpleDraweeView(SimpleDraweeView paramSimpleDraweeView, String paramUrl, ImageRequest.ImageType paramImageType, Postprocessor postprocessor) {
        String urlTag = (String) paramSimpleDraweeView.getTag() + "";
        if (paramUrl == null)
            paramUrl = "";
        if (!TextUtils.isEmpty(paramUrl) && paramUrl.compareTo(urlTag) != 0) {
            paramSimpleDraweeView.setTag(paramUrl);
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(paramUrl)).setImageType(paramImageType).setPostprocessor(postprocessor).build();
            paramSimpleDraweeView.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setOldController(paramSimpleDraweeView.getController())).setImageRequest(imageRequest)).build());
        }
    }

    /**
     * 图片多路复用
     *
     * @param paramSimpleDraweeView
     * @param paramUrl
     * @param paramUrl2
     * @param paramImageType
     * @param paramControllerListener
     */
    public static void setSimpleDraweeView(SimpleDraweeView paramSimpleDraweeView, String paramUrl, String paramUrl2, ImageRequest.ImageType paramImageType, ControllerListener<? super ImageInfo> paramControllerListener) {
        if (paramUrl == null)
            paramUrl = "";
        ImageRequest[] arrayOfImageRequest = {ImageRequestBuilder.newBuilderWithSource(Uri.parse(paramUrl)).setImageType(paramImageType).build(), ImageRequestBuilder.newBuilderWithSource(Uri.parse(paramUrl2)).setImageType(paramImageType).build()};
        paramSimpleDraweeView.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setControllerListener((ControllerListener<? super ImageInfo>) paramControllerListener)).setFirstAvailableImageRequests(arrayOfImageRequest)).build());
    }


    /**
     * 多路图片复用不带监听
     *
     * @param paramSimpleDraweeView
     * @param paramUrl
     * @param paramImageType
     */
    public static void setSimpleDraweeView(SimpleDraweeView paramSimpleDraweeView, String paramUrl, String paramUrl2, ImageRequest.ImageType paramImageType) {
        ImageRequest[] arrayOfImageRequest = {ImageRequestBuilder.newBuilderWithSource(Uri.parse(paramUrl)).setImageType(paramImageType).build(), ImageRequestBuilder.newBuilderWithSource(Uri.parse(paramUrl2)).setImageType(paramImageType).build()};
        PipelineDraweeController pipelineDraweeController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().setFirstAvailableImageRequests(arrayOfImageRequest).setOldController(paramSimpleDraweeView.getController()).build();
        paramSimpleDraweeView.setController(pipelineDraweeController);
    }

    /**
     * 根据图片url获取图片的Bitmap对象
     *
     * @param mContext
     * @param paramUrl
     * @param bitmapDataSubscriber
     */
    public static void setSimpleDraweeView(final Context mContext, String paramUrl, BaseBitmapDataSubscriber bitmapDataSubscriber) {
        if (paramUrl == null) {
            paramUrl = "";
        }
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(paramUrl)).setProgressiveRenderingEnabled(true).build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, mContext);

        dataSource.subscribe(bitmapDataSubscriber, CallerThreadExecutor.getInstance());

    }

    public static void clearMemory() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();

    }

    public static void cleanAllMemory() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
    }
}
