package com.rednovo.libs.cache;

import android.support.v4.util.LruCache;

import com.rednovo.libs.common.CacheKey;
import com.rednovo.libs.common.Constant;
import com.rednovo.libs.common.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对象缓存，可能会被缓存在文件中
 *
 * @author ll
 * @version 1.0.0
 */
public final class ObjectCache {
    private static final float MIN_MEM_CACHE_PERCENT = 0.05f;
    private static final float MAX_MEM_CACHE_PERCENT = 0.5f;

    private static final long CLEAR_OBJECT_INTERVAL = 30 * 60 * 1000;

    private static final long MAX_EXPIRED_DURATION = 10 * 365 * Constant.MILLS_PER_DAY;

    private static ObjectCache sInstance;

    private static final ReentrantLock FILE_CRITICAL = new ReentrantLock();

    private File mDiskCacheDir;
    private boolean mClosed = false;
    private SaveObjectThread mSaveObjectThread = new SaveObjectThread();
    private LruCache<String, Entity> mSerializableLruCache;
    private HashMap<String, Entity> mMemCache;

    /**
     * 打开对象缓存，如果不存在创建
     *
     * @param memCacheSizePercent 内存占用比例
     * @param path                缓存路径
     * @return ObjectCache实例
     * @throws IOException 创建目录异常
     */
    public synchronized static ObjectCache open(float memCacheSizePercent, String path) throws IOException {
        if (sInstance == null) {
            sInstance = new ObjectCache(memCacheSizePercent, path);
        } else {
            throw new IllegalStateException("ObjectCache already existed!");
        }

        return sInstance;
    }

    private ObjectCache(float memCacheSizePercent, String path) throws IOException {
        mDiskCacheDir = FileUtils.createFolder(path);
        if (mDiskCacheDir == null) {
            throw new IOException("Create Folder:" + path + " failed!");
        }

        if (memCacheSizePercent < MIN_MEM_CACHE_PERCENT || memCacheSizePercent > MAX_MEM_CACHE_PERCENT) {
            throw new IllegalArgumentException("memCacheSizePercent - percent must be between"
                    + MIN_MEM_CACHE_PERCENT + "and" + MAX_MEM_CACHE_PERCENT + " (inclusive)");
        }


        mSerializableLruCache = new LruCache<String, Entity>(Math.round(memCacheSizePercent * Runtime.getRuntime().maxMemory()) / Constant.KILO);

        mMemCache = new HashMap<String, Entity>();

        mSaveObjectThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mSaveObjectThread.start();
    }

    /**
     * 添加一个对象
     *
     * @param key    KEY
     * @param object object
     */
    public synchronized void add(String key, Object object) {
        add(key, object, MAX_EXPIRED_DURATION);
    }

    /**
     * 添加一个带实效的对象
     *
     * @param key             KEY
     * @param object          object
     * @param expiredDuration 有效时长
     */
    public synchronized void add(String key, Object object, long expiredDuration) {
        if (mClosed) {
            throw new IllegalStateException("Cache has been closed!");
        }
        addObject(key, object, expiredDuration);
        setLastUpdateTime(key);
    }

    /**
     * 获取最近一次更新缓存时间
     *
     * @param key key
     * @return 最近一次更新缓存时间
     */
    public synchronized long getLastCacheTime(String key) {
        Map<String, Long> updateTimeMap = (Map<String, Long>) getSerializableObject(CacheKey.LAST_CACHE_TIME);
        if (updateTimeMap != null) {
            Long updateUpdateTime = updateTimeMap.get(key);
            return updateUpdateTime != null ? updateUpdateTime : 0;
        }
        return 0;
    }

    /**
     * 内存吃紧时调用，会将一些内存缓存文件写入磁盘
     */
    public synchronized void notifyMemoryLow() {
        mSerializableLruCache.evictAll();
    }

    /**
     * 关闭缓存区，如果需要序列化指定对象，需要调用save
     */
    public synchronized void close() {
        mClosed = true;
        mMemCache.clear();
        mSerializableLruCache.evictAll();
        mSaveObjectThread.close();
    }

    /**
     * 是否存在
     *
     * @param key key
     * @return true false
     */
    public synchronized boolean contain(String key) {
        return getObject(key) != null || getSerializableObject(key) != null;
    }

    /**
     * 获取某个可实例化的对象
     *
     * @param key key
     * @return 对象实例
     */
    public synchronized Object get(String key) {
        Object object = getObject(key);
        if (object == null) {
            object = getSerializableObject(key);
        }

        return object;
    }

    private synchronized void addObject(String key, Object object, long expiredDuration) {
        long invalidTimeStamp = System.currentTimeMillis() + expiredDuration;
        if (object instanceof Serializable) {
            Entity entity = new Entity(object, invalidTimeStamp);
            mSerializableLruCache.put(key, entity);
            synchronized (mSaveObjectThread) {
                mSaveObjectThread.addObject(key, entity);
                mSaveObjectThread.notify();
            }
        } else {
            mMemCache.put(key, new Entity(object, invalidTimeStamp));
        }
    }

    private void setLastUpdateTime(String key) {
        Map<String, Long> updateTimeMap = (Map<String, Long>) getSerializableObject(CacheKey.LAST_CACHE_TIME);
        if (updateTimeMap == null) {
            updateTimeMap = new HashMap<String, Long>();
        }
        updateTimeMap.put(key, System.currentTimeMillis());

        Entity entity = new Entity(updateTimeMap, Long.MAX_VALUE);
        mSerializableLruCache.put(CacheKey.LAST_CACHE_TIME, entity);
        synchronized (mSaveObjectThread) {
            mSaveObjectThread.addObject(CacheKey.LAST_CACHE_TIME, entity);
            mSaveObjectThread.notify();
        }
    }

    private synchronized Object getObject(String key) {
        if (mMemCache.containsKey(key)) {
            if (mMemCache.get(key).getInvalidTimeStamp() >= System.currentTimeMillis()) {
                return mMemCache.get(key).getObject();
            } else {
                mMemCache.remove(key);
            }
        }

        return null;
    }

    private synchronized Object getSerializableObject(String key) {
        if (mSerializableLruCache.get(key) != null) {
            if (mSerializableLruCache.get(key).getInvalidTimeStamp() >= System.currentTimeMillis()) {
                return mSerializableLruCache.get(key).getObject();
            }
        } else {
            FILE_CRITICAL.lock();
            File file = new File(getAbsolutePath(key));
            if (file.isFile()) {
                boolean isFileInvalid = false;
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(new FileInputStream(file));
                    Entity entity = (Entity) ois.readObject();
                    if (entity.getInvalidTimeStamp() > System.currentTimeMillis()) {
                        mSerializableLruCache.put(key, entity);
                        return entity.getObject();
                    } else {
                        isFileInvalid = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        ois.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isFileInvalid) {
                        file.delete();
                    }

                    FILE_CRITICAL.unlock();
                }
            } else {
                FILE_CRITICAL.unlock();
            }
        }

        return null;
    }

    /**
     * 删除某个对象
     *
     * @param key key
     */
    public synchronized void delete(String key) {
        if (mMemCache.remove(key) == null) {
            mSerializableLruCache.remove(key);
            deleteObject(key);
        }
    }

    /**
     * 清除keys对应的数据
     *
     * @param keys keys
     */
    public synchronized void delete(String[] keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    /**
     * 序列化指定key的对象
     *
     * @param key key
     */
    public synchronized void save(String key) {
        if (mMemCache.containsKey(key)) {
            throw new IllegalArgumentException("value of key must be instance of Serializable!");
        }

        if (mSerializableLruCache.get(key) != null) {
            saveObject(key, mSerializableLruCache.get(key));
        }
    }

    /**
     * 序列化指定keys的对象
     *
     * @param keys keys
     */
    public synchronized void save(String[] keys) {
        for (String key : keys) {
            save(key);
        }
    }

    /**
     * 清除所有缓存实例，包括磁盘缓存
     */
    public synchronized void clear() {
        FileUtils.clearFolder(mDiskCacheDir, 0);
        mMemCache.clear();
        mSerializableLruCache.evictAll();
    }

    private void deleteObject(String key) {
        FILE_CRITICAL.lock();
        FileUtils.delete(getAbsolutePath(key));
        FILE_CRITICAL.unlock();
    }

    private String getAbsolutePath(String key) {
        return mDiskCacheDir.getAbsolutePath() + File.separator + key;
    }

    private void saveObject(String key, Entity entity) {
        FILE_CRITICAL.lock();
        ObjectOutputStream oos = null;
        try {
            File file = new File(getAbsolutePath(key));
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(entity);
            file.setLastModified(entity.getInvalidTimeStamp());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FILE_CRITICAL.unlock();
    }

    private static final class Entity implements Serializable {
        private Object mObject;
        private long mInvalidTimeStamp;

        private Entity(Object object, long timeStamp) {
            mObject = object;
            mInvalidTimeStamp = timeStamp;
        }

        public Object getObject() {
            return mObject;
        }

        public long getInvalidTimeStamp() {
            return mInvalidTimeStamp;
        }
    }

    private final class SaveObjectThread extends Thread {
        private LinkedHashMap<String, Entity> mLinkedHashMap = new LinkedHashMap<String, Entity>();
        private long mPreClearTimeStamp = 0;

        private ReentrantLock mMemCritical = new ReentrantLock();

        public void addObject(String key, Entity entity) {
            mMemCritical.lock();
            mLinkedHashMap.put(key, entity);
            mMemCritical.unlock();
        }

        private void saveObjects() {
            if (mLinkedHashMap == null) {
                return;
            }
            mMemCritical.lock();
            LinkedHashMap<String, Entity> linkedHashMap = new LinkedHashMap<String, Entity>(mLinkedHashMap);
            mMemCritical.unlock();

            for (String key : linkedHashMap.keySet()) {
                saveObject(key, linkedHashMap.get(key));

                mMemCritical.lock();
                mLinkedHashMap.remove(key);
                mMemCritical.unlock();
            }
        }

        private void clearExpiredObjects() {
            FILE_CRITICAL.lock();
            File[] files = mDiskCacheDir.listFiles();
            FILE_CRITICAL.unlock();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.lastModified() <= System.currentTimeMillis()) {
                    boolean isFileInvalid = false;
                    ObjectInputStream ois = null;
                    FILE_CRITICAL.lock();
                    try {
                        ois = new ObjectInputStream(new FileInputStream(file));
                        Entity entity = (Entity) ois.readObject();
                        isFileInvalid = (entity.getInvalidTimeStamp() <= System.currentTimeMillis());
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        isFileInvalid = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            ois.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (isFileInvalid) {
                        FileUtils.delete(file);
                    }
                    FILE_CRITICAL.unlock();
                }
            }
        }

        private void close() {
            interrupt();
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                if (System.currentTimeMillis() > mPreClearTimeStamp + CLEAR_OBJECT_INTERVAL) {
                    mPreClearTimeStamp = System.currentTimeMillis();
                    clearExpiredObjects();
                }

                saveObjects();

                try {
                    synchronized (this) {
                        if (mLinkedHashMap != null && mLinkedHashMap.size() <= 0) {
                            wait();
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
