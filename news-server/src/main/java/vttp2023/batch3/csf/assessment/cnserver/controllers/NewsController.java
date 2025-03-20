package vttp2023.batch3.csf.assessment.cnserver.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2023.batch3.csf.assessment.cnserver.models.News;
import vttp2023.batch3.csf.assessment.cnserver.models.TagCount;
import vttp2023.batch3.csf.assessment.cnserver.services.NewsService;

@RestController
@RequestMapping(path="/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class NewsController {

	@Autowired
	private NewsService newsService;

	// TODO: Task 1
	@PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadNews(
		@RequestPart("title") String title,
		@RequestPart("description") String description,
		@RequestPart("file") MultipartFile file,
		@RequestPart("tags") String tags) throws IOException{ //tags is a json string


		JsonReader reader = Json.createReader(new StringReader(tags));
		JsonArray jArray = reader.readArray();
		List<String> tagList = jArray.stream()
			.map(jsonValue -> jsonValue.toString().replace("\"", "")) // Remove all air quotes, need to escape			
			.toList();

		System.out.printf("parsed tags: %s\n", tagList);


		System.out.println("uploading....");
		//upload to mongo and get object id
		try{
			String postId = newsService.postNews(title, description, file, tagList);

			//return mongo objectid to angular as a jsonstring
			JsonObject success = Json.createObjectBuilder()
				.add("newsId", postId)
				.build();
	
			return ResponseEntity.ok(success.toString());
		}catch(Exception e){
			JsonObject failure = Json.createObjectBuilder()
				.add("fail", "no insert operation")
				.build();
		
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failure.toString());
		}



	}

	// TODO: Task 2

	@GetMapping(path ="/retrieve")
	public ResponseEntity<List<TagCount>> retrieve(@RequestParam int duration){
		//duration automatically converted from string to int
		try{
			List<TagCount> tagList = newsService.getTags(duration);		
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(tagList);
		}catch(Exception e){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}

	// TODO: Task 3
	@GetMapping(path = "/retrieveNews")
	public ResponseEntity<List<News>> retrieveNews(@RequestParam String tag, @RequestParam int duration){

		try{
			List<News> news = newsService.getNewsByTag(tag, duration);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(news);
		}catch(Exception e){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

}
