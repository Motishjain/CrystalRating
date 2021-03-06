package com.admin.constants;

/**
 * Created by mjai37 on 2/29/2016.
 */
public class AppConstants {

    public static final String BASE_URL = "http://www.midwayideas.com/";

    public static final String REGISTER_OUTLET = "registerOutlet";

    public static final String FETCH_QUESTIONS = "fetchQuestions/{outletType}";

    public static final String FETCH_REWARDS = "fetchRewards/{outletType}";

    public static final String SUBMIT_FEEDBACK = "submitFeedback";

    public static final String FETCH_FEEDBACK = "fetchFeedback/{outletCode}/{fromDate}/{toDate}";

    public static final String FETCH_SALES_DATA = "fetchSalesData/{outletCode}/{year}/{month}";

    public static final String FETCH_SUBSCRIPTION = "fetchSubscription/{outletCode}";

    public static final String EXTEND_SUBSCRIPTION = "extendSubscription";

    public static final String SAVE_GCM_TOKEN = "saveGCMToken/{outletCode}/{token}";

    public static final String SAVE_REWARDS = "saveRewards";

    public static final String OUTLET_TYPE = "RET";

    public static final String BRONZE_CD = "BZ";

    public static final String SILVER_CD = "SL";

    public static final String GOLD_CD = "GO";

    public static final String PRODUCT_QUESTION_TYPE = "PRD";

    public static final String SERVICE_QUESTION_TYPE = "SVC";

    public static final String MISCELLENOUS_QUESTION_TYPE = "MISC";

    public static final String REGISTER_WELCOME_MSG = "Freddy Welcomes you. Please Enter your outlet details";

    public static final String USER_WELCOME_MSG = "Hope you had a great time here. Please leave your feedback to help us improve";

    public static final String USER_CONGRATULATION_MSG = "Congratulations {userName}. You won a {rewardName}";

    public static final int MAXIMUM_QUESTIONS = 3;

    public static final String MERCHANT_ID = "5474224";

    public static final String PAYU_KEY = "58FbMCBm";

    public static final String PAYU_SALT = "AUR87LDlIS";

    public static final String TEST_MERCHANT_ID = "4937958";

    public static final String TEST_PAYU_KEY = "2fpmrgvH";

    public static final String TEST_PAYU_SALT = "97Bf1j7yGO";

    public static final String OPTION_RATING = "OPT";

    public static final String STAR_RATING = "STR";

    public static final String SUBSCRIPTION_TRIAL = "TR";

    public static final String SUBSCRIPTION_ACTIVE = "ACT";

    public static final String SUBSCRIPTION_PENDING = "PEN";

    public static final String SUBSCRIPTION_EXPIRED = "EXP";

    public static final String SUBMIT_FEEDBACK_FAILURE_SID = "1";

    public static final String EXTEND_SUBSCRIPTION_FAILURE_SID = "2";

    public static final String OUTLET_PIN = "1000";

}
