package com.studytor.app.interfaces;

/**
 * Created by Dawid on 2015-12-16.
 */
public interface ApplicationConstants {

    // Gimnazjum Chocianow
    static final String SCHOOL_SERVER_1 = "http://gimchocianow.pl";

    // ZS Chocianow
    static final String SCHOOL_SERVER_2 = "http://zschocianow.pl";


    // Php Application URL to store Reg ID created
    static final String APP_SERVER_URL_INSERT_USER = "/replacement_gcm/insertuser.php";
    static final String APP_SERVER_URL_REMOVE_USER = "/replacement_gcm/removeuser.php";

    // Php Application URL to store Data Set of User
    static final String APP_SERVER_URL_INSERT_USER_DATA_IDS = "/replacement_gcm/insertuserset.php";
    static final String APP_SERVER_URL_INSERT_USER_MODULES = "/replacement_gcm/insertusermodules.php";

    // Php Application URL schedule
    static final String APP_SERVER_URL_SCHEDULE = "/plan/";

    // Php Application URL repl data
    static final String APP_SERVER_URL_REPL_DATA = "/json/replacements-data.php";

    // Php Application URL repl
    static final String APP_SERVER_URL_REPL = "/json/replacements.php?date=";

    // Php Application URL lesson plan
    static final String APP_SERVER_URL_LESSON_PLAN = "/json/lessonplan.php";




    // Google Project Number
    static final String GOOGLE_PROJ_ID = "670012122702";
    static final String MSG_KEY = "m";

    //Intent constants
    static final String INTENT_INSTITUTION_ID = "INTENT_INSTITUTION_ID";

}