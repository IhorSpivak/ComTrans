package ru.comtrans.helpers;

/**
 * Created by Artco on 21.02.2016.
 */
public class Const {
    public static final String PREFERENCES_NAME = "ComTrans";
    public static final String VEHICLE_TYPE = "vehicle_type";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "token";
    public static final String IS_FROM_REGISTRATION = "is_from_registration";
    public static final String EXTRA_USER_OBJECT = "extra_user_object";

    public static final String EXTRA_SENT_INFOBLOCK = "extra_sent_infoblock";
    public static final String TAKE_PHOTO_BROADCAST = "takePhotoBroadcast";

    public static final String EXTRA_PHOTO_ITEM = "extra_photo_item";
    public static final String EXTRA_SELECTED_POSITION = "extra_selected_position";
    public static final String IS_FIRST_CAMERA_LAUNCH = "is_first_camera_launch";
    public static final String CAMERA_PREVIEW = "camera_preview";
    public static final String PHOTO_VIEWER = "photo_viewer";
    public static final String CAMERA_MODE = "camera_mode";
    public static final int MODE_PHOTO = 1;
    public static final int MODE_VIDEO = 2;

    public static final String RECEIVE_UPDATE_COUNT = "update_count";
    public static final String RE_PHOTO = "re_photo";

    public static final int GALLERY_RESULT = 101;
    public static final String GALLERY_RESULT_STRING = "gallery_result";
    public static final int GALLERY_RESULT_RE_PHOTO = 102;
    public static final int GALLERY_RESULT_DELETE = 103;


    public static final int REQUEST_PERMISSION_CAMERA = 101;
    public static final int REQUEST_PERMISSION_VIDEO = 102;
    public static final int REQUEST_PERMISSION_AUDIO_RECORDING = 103;



    public static final String SETTINGS_ALLOWS_MOBILE_CONN = "allow_mobile_conn";
    public static final String SETTINGS_ALLOWS_BIG_DATA = "allow_big_data";

   // public static final String JSON_PROP = "json_prop";
    public static final String IS_MAIN_JSON_DOWNLOADED = "is_main_json_downloaded";
    public static final String JSON_PROP_CODE = "json_prop_code";
    public static final String EXTRA_PROP_CODE = "extra_prop_code";


    //////////////////////////AddInfoBlockActivity/////////////////////////
    public static final String IS_NEW_INFO_BLOCK = "is_new_info_block";

    public static final String EXTRA_INFO_BLOCK_ID = "extra_info_block_id";
    public static final String EXTRA_INFO_BLOCK_PAGE = "extra_info_block_page";




    ////////////////Add info block Fragment//////////////////////
    public static final String PAGE = "page";
    public static final String TOTAL_PAGES = "total_pages";
    public static final String EXTRA_VALUES = "extra_values";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_POSITION = "extra_pos"; ///POSITION OF MAIN ITEM IN ADAPTER;
    public static final String EXTRA_IMAGE_POSITION = "extra_img_pos"; /// POSITION OF CLICKED PHOTO
    public static final String EXTRA_VALUE = "extra_value";
    public static final String DEFAULT_DEFECT_NAME = "default_defect_name";
    public static final String EXTRA_SCREEN_NUM = "extra_screen";
    public static final String EXTRA_MARK = "extra_mark";
    public static final String EXTRA_DATE = "extra_date";
    public static final int SEARCH_VALUE_RESULT = 102;
    public static final int CAMERA_PHOTO_RESULT = 103;
    public static final int CAMERA_VIDEO_RESULT = 104;
    public static final int TUTORIAL_FRAGMENT_REQUEST = 105;
    public static final String phone_regex = "[0-9,\\+]7\\([0-9]{3}[0-9,\\)][0-9]{3}[0-9,-][0-9]{2}[0-9,-][0-9]{2}";


    //////////////////////InfoBlocksStorage/////////////////////



    ///////////////////////MyInfoBlocksFragment/////////////////////////
    public static final String REFRESH_INFO_BLOCKS_FILTER = "refreshInfoBlocks";
    public static final String UPDATE_PROGRESS_INFO_BLOCKS_FILTER = "update_progress_info_block";
    public static final String UPDATE_STATUS_INFO_BLOCKS_FILTER = "update_status_info_block";

    public static final String INFO_BLOCK_FULL_DATE_FORMAT = "dd.MM.yyyy, HH:mm:ss";
    public static final String INFO_BLOCK_DATE_FORMAT = "dd.MM.yyyy";

    public static final String IS_FIRST_ADD_INFOBLOCK_LAUNCH = "info_block_launch";

    public static final String EXTRA_PROGRESS = "extra_progress";
    public static final String EXTRA_STATUS = "extra_status";



    public static final String IS_FLASH_ENABLED = "is_flash_enabled";
    public static final String IS_VIDEO = "is_video";





}
