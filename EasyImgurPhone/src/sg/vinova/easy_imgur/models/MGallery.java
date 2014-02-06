package sg.vinova.easy_imgur.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MGallery {

	@SerializedName("id")
	private String id;
	
	@SerializedName("title")
	private String title;
	
	@SerializedName("description")
	private String description;
	
	@SerializedName("datetime")
	private String datetime;
	
	@SerializedName("type")
	private String type;
	
	@SerializedName("animated")
	private boolean animated;
	
	@SerializedName("width")
	private int width;
	
	@SerializedName("height")
	private int height;
	
	@SerializedName("size")
	private int size;
	
	@SerializedName("views")
	private int views;
	
	@SerializedName("bandwidth")
	private float bandwidth;
	
	@SerializedName("vote")
	private String vote;
	
	@SerializedName("favorite")
	private boolean favorite;
	
	@SerializedName("nsfw")
	private boolean nsfw;
	
	@SerializedName("section")
	private String section;
	
	@SerializedName("account_url")
	private String accountUrl;
	
	@SerializedName("link")
	private String link;
	
	@SerializedName("subtype")
	private String subType;
	
	@SerializedName("ups")
	private int ups;
	
	@SerializedName("downs")
	private int downs;
	
	@SerializedName("score")
	private int score;
	
	@SerializedName("is_album")
	private boolean album;
	
	@SerializedName("images_count")
	private int imagesCount;
	
	@SerializedName("deletehash")
	private String deleteHash;
	
	private List<MGallery> images;
	
	private boolean isExplored;

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getDeleteHash() {
		return deleteHash;
	}

	public void setDeleteHash(String deleteHash) {
		this.deleteHash = deleteHash;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getImagesCount() {
		return imagesCount;
	}

	public void setImagesCount(int imagesCount) {
		this.imagesCount = imagesCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAnimated() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public float getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(float bandwidth) {
		this.bandwidth = bandwidth;
	}

	public String getVote() {
		return vote;
	}

	public void setVote(String vote) {
		this.vote = vote;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public boolean isNsfw() {
		return nsfw;
	}

	public void setNsfw(boolean nsfw) {
		this.nsfw = nsfw;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getAccountUrl() {
		return accountUrl;
	}

	public void setAccountUrl(String accountUrl) {
		this.accountUrl = accountUrl;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getUps() {
		return ups;
	}

	public void setUps(int ups) {
		this.ups = ups;
	}

	public int getDowns() {
		return downs;
	}

	public void setDowns(int downs) {
		this.downs = downs;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isAlbum() {
		return album;
	}

	public void setAlbum(boolean album) {
		this.album = album;
	}

	public List<MGallery> getImages() {
		return images;
	}

	public void setImages(List<MGallery> images) {
		this.images = images;
	}

	public boolean isExplored() {
		return isExplored;
	}

	public void setExplored(boolean isExplored) {
		this.isExplored = isExplored;
	}
}
