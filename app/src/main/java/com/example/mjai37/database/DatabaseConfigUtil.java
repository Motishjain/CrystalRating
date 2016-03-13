package com.example.mjai37.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by mjai37 on 1/22/2016.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[] {
            Outlet.class,User.class,Reward.class,Question.class,RewardHistory.class
    };

    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile(new File("C:\\Users\\Admin\\ormlite_config.txt"), classes);
    }
}