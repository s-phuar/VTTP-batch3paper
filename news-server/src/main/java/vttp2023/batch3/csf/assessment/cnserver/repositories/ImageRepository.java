package vttp2023.batch3.csf.assessment.cnserver.repositories;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
// import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Repository
public class ImageRepository {
	@Autowired
	private AmazonS3 s3Client;

	@Value("${s3.bucket.bucket}")
	private String bucketName;

	@Value("${s3.bucket.endpoint}")
	private String finalPoint;

	// TODO: Task 1
	public String upload(MultipartFile file, String title, String description) throws IOException, Exception{
		Map<String, String> data = new HashMap<String, String>();
		
		data.put("title", title);
		data.put("description", description);
		data.put("filename", file.getOriginalFilename());
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());
		metadata.setContentType(file.getContentType());
		metadata.setUserMetadata(data);
		//actual name
		System.out.println("debugging: " + file.getOriginalFilename());
		String filename = file.getOriginalFilename().split("\\.")[0];
		String fileNameExt = file.getOriginalFilename().split("\\.")[1];

		System.out.println("debugging" + filename);
		System.out.println("debugging" + fileNameExt);
		if (filename.equals("blob")){
			fileNameExt = "png";
		}

		if(fileNameExt.equals("fail")){
			throw new Exception("Simulated error: Invalid file extension");
		}

		//file extension
			// StringTokenizer tk = new StringTokenizer(file.getOriginalFilename(), ".");
			// String fileNameExt = "";
			// while(tk.hasMoreTokens()){
			// 	fileNameExt = tk.nextToken(); // grab th file extension
			// }
			// if(fileNameExt.equals("blob")){
			// 	fileNameExt = fileNameExt + ".png";
			// }

		PutObjectRequest putReq = new PutObjectRequest(bucketName,"picture%s.%s".formatted(filename, fileNameExt),
					file.getInputStream(), metadata);
		putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);		

		s3Client.putObject(putReq);

        return "https://%s.%s/picture%s.%s".formatted(bucketName, finalPoint, filename, fileNameExt);
	}




}
