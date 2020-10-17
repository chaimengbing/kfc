package com.auv.utils;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.auv.constant.Constants;
import com.auv.standard.hardware.utils.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadLogUtils {

    private final String TAG = getClass().getSimpleName();
    public static SimpleDateFormat AUV_LOG_FILE_SDF = new SimpleDateFormat( "yyyy-MM-dd" );
    public static String AUV_LOG_NAME = "_auvlog.log";
    private OSS oss;


    private static UploadLogUtils auvLogUtils;


    public static UploadLogUtils getInstance() {
        if (auvLogUtils == null) {
            auvLogUtils = new UploadLogUtils();
        }
        return auvLogUtils;
    }


    private void initOSS(Context context) {
        if (oss == null) {
            OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider( Constants.STS_SERVER_URL );

            // 配置类如果不设置，会有默认配置。
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout( 15 * 1000 ); // 连接超时，默认15秒。
            conf.setSocketTimeout( 15 * 1000 ); // socket超时，默认15秒。
            conf.setMaxConcurrentRequest( 5 ); // 最大并发请求数，默认5个。
            conf.setMaxErrorRetry( 2 ); // 失败后最大重试次数，默认2次。

            oss = new OSSClient( context, Constants.ENDPOINT, credentialProvider );
        }
    }

    private void upLoadLog(String fileName, String filePath, Context context) {
        initOSS( context );
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest( Constants.BUCKET_NAME, fileName, filePath );
        put.setCRC64( OSSRequest.CRC64Config.YES );
        // 异步上传时可以设置进度回调。
        put.setProgressCallback( (request, currentSize, totalSize) -> {

        } );

        OSSAsyncTask task = oss.asyncPutObject( put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                AUVLogUtil.d( "PutObject", "UploadSuccess" );
                AUVLogUtil.d( "ETag", result.getETag() );
                AUVLogUtil.d( "RequestId", result.getRequestId() );
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    AUVLogUtil.e( "ErrorCode", serviceException.getErrorCode() );
                    AUVLogUtil.e( "RequestId", serviceException.getRequestId() );
                    AUVLogUtil.e( "HostId", serviceException.getHostId() );
                    AUVLogUtil.e( "RawMessage", serviceException.getRawMessage() );
                }
            }
        } );

    }

    /**
     * 上传所有的日志
     */
    public void uploadAllLog(Context context) {
        ThreadPoolUtil.pool.execute( () -> {
            File file = new File( Constants.AUV_LOG_PATH_SDCARD_DIR );
            if (file.isDirectory() && file.exists()) {
                for (File fileLog : file.listFiles()) {
                    upLoadLog( fileLog.getName(), Constants.AUV_LOG_PATH_SDCARD_DIR + fileLog.getName(), context );
                }
            }
        } );

    }


    /**
     * 上传当天的日志
     */
    public void uploadCurrentLog(Context context) {
        String deviceName = AliYunIotUtils.getInstance().getDeviceName();
        if (TextUtils.isEmpty( deviceName )) {
            return;
        }
        ThreadPoolUtil.pool.execute( () -> {
            Date currentTime = new Date();
            String logFileName = AUV_LOG_FILE_SDF.format( currentTime );
            String fileName = logFileName + "_" + deviceName + AUV_LOG_NAME;
            String filePath = Constants.AUV_LOG_PATH_SDCARD_DIR + fileName;
            upLoadLog( fileName, filePath, context );
        } );
    }
}
