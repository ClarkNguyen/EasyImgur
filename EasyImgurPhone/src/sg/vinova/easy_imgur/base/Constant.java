package sg.vinova.easy_imgur.base;

public class Constant {
	/**
	 * App Settings
	 */
	public static final boolean IS_APP_LOG_ENABLED = true;
	public static final int APP_DEFAULT_VERSION = 1;
	public static final int ITEM_PER_PAGE = 10;
	public static final String CLIENT_ID = "689dac97ad042ec";
	public static final String CLIENT_SECRET = "880120ee38e14a04e330ba9665f06fed566ed1b3";
	
	/**
	 * Preferences key
	 */
	public static final String KEY_USER = "imgur_user";
	
	/**
	 *  Error
	 */
	public static final String ERROR = "Error:";
	public static final String PARSE_ERROR = "ParseError:";
	public static final String REQUEST_TIMEOUT = "TimeoutError:";
	
	/**
	 *  Error message
	 */
	public static final String MESSAGE_NETWORK_UNREACHABLE = "Network is unreachable";
	public static final String MESSAGE_REQUEST_TIMEOUT = "Request timeout";
	public static final String MESSSAGE_SERVER_ERROR = "Server error";
	
	/**
	 * Params
	 */
	public static final String PARAM_SECTION = "section";			// hot | top | user - defaults to hot
	public static final String PARAM_SORT = "sort";					// viral | time - defaults to viral
	public static final String PARAM_PAGE = "page";
	public static final String PARAM_PER_PAGE = "perPage";
	public static final String PARAM_WINDOW = "window";				// Change the date range of the request if the section is "top", day | week | month | year | all, defaults to day
	public static final String PARAM_SHOW_VIRAL = "showViral";		// true | false - Show or hide viral images from the 'user' section. Defaults to true
	public static final String PARAM_REFRESH_TOKEN = "refresh_token";
	public static final String PARAM_CLIENT_ID = "client_id";
	public static final String PARAM_CLIENT_SECRET = "client_secret";
	public static final String PARAM_GRANT_TYPE = "grant_type";
	
	/**
	 * Params type
	 */
	public static final String PARAM_TYPE_SECTION_HOT = "hot";
	public static final String PARAM_TYPE_SECTION_TOP = "top";
	public static final String PARAM_TYPE_SECTION_USER = "user";
	public static final String PARAM_TYPE_SORT_VIRAL = "viral";
	public static final String PARAM_TYPE_SORT_TIME = "time";
	public static final String PARAM_TYPE_REFRESH_TOKEN = "refresh_token";
	
	/**
	 * TAG data parsing
	 */
	public static final String TAG_PARSE_DATA = "data";
	public static final String TAG_PARSE_IMAGES = "images";
	public static final String TAG_PARSE_SUCCESS = "success";
	public static final String TAG_PARSE_FAVORITED = "favorited";
	
	public static final String ERROR_PARSE_GALLERY_DETAIL = "parse gallery error";
	public static final String ERROR_PARSE_LIST_IMAGES = "parse images error";
	
	/**
	 * Gallery detail constants
	 */
	public static final String VOTE_UP = "up";
	public static final String VOTE_DOWN = "down";
	public static final String VOTE_UNDEFINED = "veto";
	public static final String REMOVEABLE_STRINGS[] = {"http://", ".jpg", ".png", ".gif"};
	public static final String TAG_CLIPBOARD_LINK = "EasyImgur link";
	public static final String TAG_CLIPBOARD_DIRECT = "EasyImgur direct";
	public static final String TAG_ALBUM = "Album";
	public static final String TAG_IMAGE = "Image";
}
