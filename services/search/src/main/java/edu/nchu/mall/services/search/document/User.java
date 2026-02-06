package edu.nchu.mall.services.search.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(indexName = "users")
public class User {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "my_analyzer")
    private String name;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Text, analyzer = "my_analyzer")
    private String username;

    @Field(type = FieldType.Keyword)
    private String password;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Text, analyzer = "my_analyzer_max")
    private String address;

    @Field(type = FieldType.Date)
    private Date registryDate;
}
