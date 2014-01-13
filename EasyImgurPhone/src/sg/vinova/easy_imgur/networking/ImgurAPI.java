package sg.vinova.easy_imgur.networking;

public class ImgurAPI {

	private static ImgurAPI imgurClient;
	
	private static final String imgurClientUrl = "https://api.imgur.com/3/";
	
	private String url;
	
	public static ImgurAPI getClient(){
		if (imgurClient == null) {
			imgurClient = new ImgurAPI();
			imgurClient.url = imgurClientUrl;
		}
		return imgurClient;
	}
	
	public String getUrl(String path) {
		return this.url + "/" + path;
	}
}
