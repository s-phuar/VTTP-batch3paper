package vttp2023.batch3.csf.assessment.cnserver.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class NewsRepository {
	@Autowired
	private MongoTemplate template;

	// TODO: Task 1 
	// Write the native Mongo query in the comment above the method
	/*db.collectionName.insertOne({
		"postDate": long
		"title": "some title",
		"description": "some description",
		"tags": ["tag1", "tag2"],
		"image": "example.png"
	 })
	*/
	public String postNews(Document doc, String collectionName){
		Document insertedDoc = template.insert(doc, collectionName);
		return insertedDoc.getObjectId("_id").toString();
	}
	

	// TODO: Task 2 
	// Write the native Mongo query in the comment above the method
	/* 
		db.news.aggregate([
		{ $match: { postDate: { $gt: 174244687335 }} },
		{ $unwind: "$tags"},
		{ $match: { tags: { $ne: null, $ne: "" } } },
		{ $group: {_id: "$tags", count: {$sum:1}}},
		{ $project: {_id: 0 , tag: "$_id", count :1}},
		{ $sort: {count: -1, tag: 1}},
		{ $limit :10}
		])
	 */
	public List<Document> retrieveTags(long threshold){
		Criteria criteria = Criteria.where("postDate").gte(threshold);
		MatchOperation filterByDuration = Aggregation.match(criteria);

		UnwindOperation unwindByTag = Aggregation.unwind("tags");

		Criteria criteria2 = Criteria.where("tags").ne("").ne(null);
		MatchOperation filterByTag = Aggregation.match(criteria2);

		GroupOperation groupByTag = Aggregation.group("tags")
			.count().as("count");
		ProjectionOperation projectTag = Aggregation.project("count")
			.and("_id").as("tag");

		SortOperation sortByCountAndTag = Aggregation.sort(Sort.by(Sort.Order.desc("count"), Sort.Order.asc("tag")));

		LimitOperation limit = Aggregation.limit(10);


		Aggregation pipeline = Aggregation.newAggregation(filterByDuration, unwindByTag, filterByTag, groupByTag, projectTag, sortByCountAndTag, limit);
		AggregationResults<Document> results = template.aggregate(pipeline, "news", Document.class);

		// List<Document> mappedResults = results.getMappedResults();
		// System.out.println("Aggregation results: " + mappedResults);
	
		return results.getMappedResults();
	}


	// TODO: Task 3
	// Write the native Mongo query in the comment above the method
	public List<Document> retrieveNewsByTag(String tag, int duration){
		Criteria criteria = Criteria.where("tags").in(tag).and("postDate").gte(duration);
		MatchOperation filterByTagAndDuration = Aggregation.match(criteria);
		SortOperation sortByDate = Aggregation.sort(Sort.Direction.DESC, "postDate");
		Aggregation pipeline = Aggregation.newAggregation(filterByTagAndDuration, sortByDate);

		AggregationResults<Document> results = template.aggregate(pipeline, "news", Document.class);
		return results.getMappedResults();
	}


}
