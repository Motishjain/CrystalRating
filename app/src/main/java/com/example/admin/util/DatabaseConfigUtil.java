package com.example.admin.util;

import com.example.admin.database.DBHelper;
import com.example.admin.database.Outlet;
import com.example.admin.database.Question;
import com.example.admin.database.Reward;
import com.example.admin.database.RewardHistory;
import com.example.admin.database.User;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by mjai37 on 1/22/2016.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = DBHelper.DB_CLASSES;

    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile(new File("C:\\Users\\Admin\\ormlite_config.txt"), classes);
    }
}