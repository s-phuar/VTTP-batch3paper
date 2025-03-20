package vttp2023.batch3.csf.assessment.cnserver.services;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vttp2023.batch3.csf.assessment.cnserver.models.News;
import vttp2023.batch3.csf.assessment.cnserver.models.TagCount;
import vttp2023.batch3.csf.assessment.cnserver.repositories.ImageRepository;
import vttp2023.batch3.csf.assessment.cnserver.repositories.NewsRepository;

@Service
public class NewsService {
	@Autowired
	private NewsRepository newsRepository;
	
	@Autowired
	private ImageRepository imageRepository;

	// TODO: Task 1
	// Do not change the method name and the return type
	// You may add any number of parameters
	// Returns the news id
	public String postNews(String title, String description, MultipartFile file, List<String> tags)  throws IOException, Exception{

		//upload to digital ocean
		String url = imageRepository.upload(file, title, description);
		System.out.printf("Uploaded to digital ocean,url: %s\n", url);

		//epoch milli
		long epochMilli = new Date().getTime();


		//create document
		Document toInsert = new Document();
		toInsert.append("postDate", epochMilli);
		toInsert.append("title", title);
		toInsert.append("description", description);
		toInsert.append("image", url);
		toInsert.append("tags", tags); //apend List<String> directly
		System.out.println("uploaded to mongoDB");
		//upload to mongo
		String postId = newsRepository.postNews(toInsert, "news");

		return postId;
	}
	 
	// TODO: Task 2
	// Do not change the method name and the return type
	// You may add any number of parameters
	// Returns a list of tags and their associated count
	public List<TagCount> getTags(int duration) {
		//get current time, subtract duration from current time, all in epoch milli
		long currEpochMilli = new Date().getTime();
		long dur = duration * 60000;
		//mongo query > threshold
		long threshold = currEpochMilli - dur;
		System.out.println("threshold: " + threshold);

		List<Document> docList = newsRepository.retrieveTags(threshold);

		List<TagCount> tagList = docList.stream()
			.map(doc -> {
				TagCount temp = new TagCount(doc.getString("tag"), doc.getInteger("count"));
				return temp;
			})
			.toList();
		return tagList;
	}

	// TODO: Task 3
	// Do not change the method name and the return type
	// You may add any number of parameters
	// Returns a list of news
	public List<News> getNewsByTag(String tag, int duration) {
		//get current time, subtract duration from current time, all in epoch milli
		long currEpochMilli = new Date().getTime();
		long dur = duration * 60000;
		//mongo query > threshold
		long threshold = currEpochMilli - dur;
		System.out.println("threshold: " + threshold);

		List<Document> docList = newsRepository.retrieveNewsByTag(tag, duration);
		System.out.println("task3 doclist: " + docList);

		List<News> news = new LinkedList<>();
		for(Document doc : docList){
			News n = new News();
			n.setId(doc.getObjectId("_id").toString());
			n.setPostDate(doc.getLong("postDate"));
			n.setTitle(doc.getString("title"));
			n.setDescription(doc.getString("description"));
			n.setImage(doc.getString("image"));
			n.setTags(doc.getList("tags", String.class));
			news.add(n);
		}
		return news;
	}
	
}
