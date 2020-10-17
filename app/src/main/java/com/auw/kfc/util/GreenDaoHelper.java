package com.auw.kfc.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;


import com.auw.kfc.greendao.CellStateModelDao;
import com.auw.kfc.greendao.DaoMaster;
import com.auw.kfc.greendao.DaoSession;
import com.auw.kfc.model.CellStateModel;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;


public class GreenDaoHelper {
    private static DaoMaster.DevOpenHelper devOpenHelper;
    private static SQLiteDatabase database;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    //单例模式
    private static GreenDaoHelper mInstance;
    static Context mContext;

    private GreenDaoHelper() {
        //初始化建议放在Application中进行
        if (mInstance == null) {
            //创建数据库"info.db"
            devOpenHelper = new DaoMaster.DevOpenHelper( mContext, "auv.db", null );
            //获取可写数据库
            database = devOpenHelper.getWritableDatabase();
            //获取数据库对象
            daoMaster = new DaoMaster( database );
            //获取Dao对象管理者
            daoSession = daoMaster.newSession();
            setDebug( false );
        }
    }

    public static GreenDaoHelper getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            //保证异步处理安全操作
            synchronized (GreenDaoHelper.class) {
                if (mInstance == null) {
                    mInstance = new GreenDaoHelper();
                }
            }
        }
        return mInstance;
    }


    public DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 设置debug模式开启或关闭，默认关闭
     *
     * @param flag
     */
    public void setDebug(boolean flag) {


        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;

    }

    /**
     * 关闭数据库
     */
    public void closeDataBase() {
        closeHelper();
        closeDaoSession();
    }

    public void closeDaoSession() {
        if (null != daoSession) {
            daoSession.clear();
            daoSession = null;
        }
    }

    public void closeHelper() {
        if (devOpenHelper != null) {
            devOpenHelper.close();
            devOpenHelper = null;
        }
    }

    public static void initData() {

    }

    public void deleteInitData() {
        this.getDaoSession().getCellStateModelDao().deleteAll();
    }

    /**
     * @param boardId 板子号
     * @return
     */
    public List<CellStateModel> getCellStateList(int boardId) {
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
//        String orderRaw = "CELL_ALIAS + '0'";
        List<CellStateModel> resList = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.BoardId.eq( boardId ) ).orderAsc( CellStateModelDao.Properties.CellNo ).list();
        return resList;
    }

    /**
     *
     */
    public List<CellStateModel> getAllDeviceList() {
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
//        String orderRaw = CellStateModelDao.Properties.CellAlias + "'0'";
        List<CellStateModel> resList = cellStateModelDao.queryBuilder().distinct().orderAsc( CellStateModelDao.Properties.CellNo ).list();
        return resList;
    }


    public List<Integer> getMainBoardCount() {
        String SQL_DISTINCT_ENAME = "SELECT DISTINCT " + CellStateModelDao.Properties.BoardId.columnName + " FROM " + CellStateModelDao.TABLENAME;
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        List<Integer> result = new ArrayList<>();
        Cursor cursor = cellStateModelDao.getDatabase().rawQuery( SQL_DISTINCT_ENAME, null );
        try {
            if (cursor.moveToFirst()) {
                do {
                    result.add( cursor.getInt( cursor.getColumnIndex( CellStateModelDao.Properties.BoardId.columnName ) ) );
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return result;
    }


    /**
     * 根据 存取餐码获取对应格子
     *
     * @param code
     * @return
     */
    public List<CellStateModel> getListByCode(String code) {
        if (TextUtils.isEmpty( code )) {
            return null;
        }
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        List<CellStateModel> resList = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.Code.eq( code ) ).orderAsc( CellStateModelDao.Properties.CellNo ).list();
        return resList;
    }

    /**
     * 根据 订单号获取对应格子
     *
     * @param orderNumber
     * @return
     */
    public List<CellStateModel> getListByOrderNumber(String orderNumber) {
        if (TextUtils.isEmpty( orderNumber )) {
            return null;
        }
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        List<CellStateModel> resList = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.OrderNumber.eq( orderNumber ) ).orderAsc( CellStateModelDao.Properties.CellNo ).list();
        return resList;
    }

    /**
     * 根据 user获取对应格子
     *
     * @param user
     * @return
     */
    public List<CellStateModel> getListByUser(String user) {
        if (TextUtils.isEmpty( user )) {
            return null;
        }
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        List<CellStateModel> resList = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.User.eq( user ) ).orderAsc( CellStateModelDao.Properties.CellNo ).list();
        return resList;
    }

    /**
     * 根据 存餐码,订单号获取对应格子
     *
     * @param code
     * @param orderNumber
     * @return
     */
    public CellStateModel getByCodeAndOrderNum(String code, String orderNumber) {
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        return cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.Code.eq( code ), CellStateModelDao.Properties.OrderNumber.eq( orderNumber ) ).orderAsc( CellStateModelDao.Properties.CellNo ).unique();
    }

    /**
     * 获取未使用的格子
     *
     * @param isBig  是否为大格子
     * @param isHeat 是否开启了加热
     * @return
     */
    public List<CellStateModel> getNoUseCell(int isBig, int isHeat) {
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        List<CellStateModel> resList = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.IsUse.eq( 0 ), CellStateModelDao.Properties.IsBig.eq( isBig ), CellStateModelDao.Properties.HeatingOpen.eq( isHeat ), CellStateModelDao.Properties.LockOpen.eq( 0 ) ).orderAsc( CellStateModelDao.Properties.CellNo ).list();
        return resList;
    }

    /**
     * 获取未使用的格子
     *
     * @return
     */
    public List<CellStateModel> getNoUseCell() {
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        List<CellStateModel> resList = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.IsUse.eq( 0 ), CellStateModelDao.Properties.LockOpen.eq( 0 ) ).orderAsc( CellStateModelDao.Properties.CellNo ).list();
        return resList;
    }

    /**
     * 获取已经使用的格子
     *
     * @return
     */
    public List<CellStateModel> getUseCell() {
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        List<CellStateModel> resList = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.IsUse.eq( 1 ), CellStateModelDao.Properties.LockOpen.eq( 0 ) ).orderAsc( CellStateModelDao.Properties.CellNo ).list();
        return resList;
    }

    /**
     * 根据 板子号和格子号获取对应格子
     *
     * @param cellNo
     * @return
     */
    public CellStateModel getByCellNo(int cellNo) {
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        CellStateModel cellStateModel = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.CellNo.eq( cellNo ) ).orderAsc( CellStateModelDao.Properties.CellNo ).unique();
        return cellStateModel;
    }

    public void insertCellState(CellStateModel cellStateModel) {
        getDaoSession().getCellStateModelDao().insert( cellStateModel );
    }

    public void updateCellState(CellStateModel cellStateModel) {
        getDaoSession().getCellStateModelDao().update( cellStateModel );
    }

    /**
     * 根据 初始化格子
     *
     * @param cellNo
     * @return
     */
    public void initCellStateModel(int cellNo) {
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        CellStateModel cellStateModel = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.CellNo.eq( cellNo ) ).unique();
        Log.d( "GreenDaoHelper", "initCellStateModel::" );
        if (cellStateModel != null) {
            Log.d( "GreenDaoHelper", "initCellStateModel::cellStateModel:" + cellStateModel.getCode() );
            cellStateModel.setOrderNumber( "" );
            cellStateModel.setCode( "" );
            cellStateModel.setUser( "" );
            cellStateModel.setIsUse( 0 );
            cellStateModelDao.update( cellStateModel );
        }
    }

    public void updateCellLightState(int cellNo, int isLight) {
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        CellStateModel cellStateModel = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.CellNo.eq( cellNo ) ).unique();
        if (cellStateModel != null) {
            cellStateModel.setLightOpen( isLight );
            cellStateModelDao.update( cellStateModel );
        }
    }

    public void updateCellHeatState(int cellNo, int isHeat) {
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        CellStateModel cellStateModel = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.CellNo.eq( cellNo ) ).unique();
        if (cellStateModel != null) {
            cellStateModel.setHeatingOpen( isHeat );
            cellStateModelDao.update( cellStateModel );
        }
    }

    /**
     * 批量更新格子状态
     *
     * @param cellStateModelList
     * @return
     */
    public boolean updateCellList(List<CellStateModel> cellStateModelList) {
        boolean resFlag;
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        try {
            cellStateModelDao.updateInTx( cellStateModelList );
            resFlag = true;
        } catch (Exception e) {
            resFlag = false;
            e.printStackTrace();
        }
        return resFlag;
    }


    public void clearUserInfo(int cellNo) {
        if (cellNo < 0) {
            return;
        }
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        CellStateModel cellStateModel = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.CellNo.eq( cellNo ) ).unique();
        if (cellStateModel != null) {
            cellStateModel.setUserCardId( "" );
            cellStateModel.setUserId( "" );
            cellStateModel.setCode( "" );
            cellStateModel.setUserName( "" );
            cellStateModelDao.update( cellStateModel );
        }
    }

    public void saveUserInfo(int cellNo, String code, String idCard, String userId, String userName) {
        if (cellNo < 0) {
            return;
        }
        CellStateModelDao cellStateModelDao = getDaoSession().getCellStateModelDao();
        CellStateModel cellStateModel = cellStateModelDao.queryBuilder().where( CellStateModelDao.Properties.CellNo.eq( cellNo ) ).unique();
        if (cellStateModel != null) {
            cellStateModel.setUserCardId( idCard );
            cellStateModel.setCode( code );
            cellStateModel.setUserName( userName );
            cellStateModel.setUserId( userId );
            cellStateModel.setIsUse( 1 );
            cellStateModelDao.update( cellStateModel );
        }
    }


}
