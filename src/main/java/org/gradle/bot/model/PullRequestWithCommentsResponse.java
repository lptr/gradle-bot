
package org.gradle.bot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data"
})
public class PullRequestWithCommentsResponse {

    @JsonProperty("data")
    private PullRequestWithCommentsResponse.Data data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public PullRequestWithCommentsResponse() {
    }

    /**
     * 
     * @param data
     */
    public PullRequestWithCommentsResponse(PullRequestWithCommentsResponse.Data data) {
        super();
        this.data = data;
    }

    @JsonProperty("data")
    public PullRequestWithCommentsResponse.Data getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(PullRequestWithCommentsResponse.Data data) {
        this.data = data;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PullRequestWithCommentsResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("data");
        sb.append('=');
        sb.append(((this.data == null)?"<null>":this.data));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.data == null)? 0 :this.data.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PullRequestWithCommentsResponse) == false) {
            return false;
        }
        PullRequestWithCommentsResponse rhs = ((PullRequestWithCommentsResponse) other);
        return (((this.data == rhs.data)||((this.data!= null)&&this.data.equals(rhs.data)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "login"
    })
    public static class Author {

        @JsonProperty("login")
        private String login;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public Author() {
        }

        /**
         * 
         * @param login
         */
        public Author(String login) {
            super();
            this.login = login;
        }

        @JsonProperty("login")
        public String getLogin() {
            return login;
        }

        @JsonProperty("login")
        public void setLogin(String login) {
            this.login = login;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(PullRequestWithCommentsResponse.Author.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("login");
            sb.append('=');
            sb.append(((this.login == null)?"<null>":this.login));
            sb.append(',');
            sb.append("additionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
            sb.append(',');
            if (sb.charAt((sb.length()- 1)) == ',') {
                sb.setCharAt((sb.length()- 1), ']');
            } else {
                sb.append(']');
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = ((result* 31)+((this.login == null)? 0 :this.login.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof PullRequestWithCommentsResponse.Author) == false) {
                return false;
            }
            PullRequestWithCommentsResponse.Author rhs = ((PullRequestWithCommentsResponse.Author) other);
            return (((this.login == rhs.login)||((this.login!= null)&&this.login.equals(rhs.login)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "nodes"
    })
    public static class Comments {

        @JsonProperty("nodes")
        private List<PullRequestWithCommentsResponse.Node> nodes = new ArrayList<PullRequestWithCommentsResponse.Node>();
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public Comments() {
        }

        /**
         * 
         * @param nodes
         */
        public Comments(List<PullRequestWithCommentsResponse.Node> nodes) {
            super();
            this.nodes = nodes;
        }

        @JsonProperty("nodes")
        public List<PullRequestWithCommentsResponse.Node> getNodes() {
            return nodes;
        }

        @JsonProperty("nodes")
        public void setNodes(List<PullRequestWithCommentsResponse.Node> nodes) {
            this.nodes = nodes;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(PullRequestWithCommentsResponse.Comments.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("nodes");
            sb.append('=');
            sb.append(((this.nodes == null)?"<null>":this.nodes));
            sb.append(',');
            sb.append("additionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
            sb.append(',');
            if (sb.charAt((sb.length()- 1)) == ',') {
                sb.setCharAt((sb.length()- 1), ']');
            } else {
                sb.append(']');
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = ((result* 31)+((this.nodes == null)? 0 :this.nodes.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof PullRequestWithCommentsResponse.Comments) == false) {
                return false;
            }
            PullRequestWithCommentsResponse.Comments rhs = ((PullRequestWithCommentsResponse.Comments) other);
            return (((this.nodes == rhs.nodes)||((this.nodes!= null)&&this.nodes.equals(rhs.nodes)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "nodes"
    })
    public static class Commits {

        @JsonProperty("nodes")
        private List<PullRequestWithCommentsResponse.Node> nodes = new ArrayList<PullRequestWithCommentsResponse.Node>();
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public Commits() {
        }

        /**
         * 
         * @param nodes
         */
        public Commits(List<PullRequestWithCommentsResponse.Node> nodes) {
            super();
            this.nodes = nodes;
        }

        @JsonProperty("nodes")
        public List<PullRequestWithCommentsResponse.Node> getNodes() {
            return nodes;
        }

        @JsonProperty("nodes")
        public void setNodes(List<PullRequestWithCommentsResponse.Node> nodes) {
            this.nodes = nodes;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(PullRequestWithCommentsResponse.Commits.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("nodes");
            sb.append('=');
            sb.append(((this.nodes == null)?"<null>":this.nodes));
            sb.append(',');
            sb.append("additionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
            sb.append(',');
            if (sb.charAt((sb.length()- 1)) == ',') {
                sb.setCharAt((sb.length()- 1), ']');
            } else {
                sb.append(']');
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = ((result* 31)+((this.nodes == null)? 0 :this.nodes.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof PullRequestWithCommentsResponse.Commits) == false) {
                return false;
            }
            PullRequestWithCommentsResponse.Commits rhs = ((PullRequestWithCommentsResponse.Commits) other);
            return (((this.nodes == rhs.nodes)||((this.nodes!= null)&&this.nodes.equals(rhs.nodes)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "repository"
    })
    public static class Data {

        @JsonProperty("repository")
        private PullRequestWithCommentsResponse.Repository repository;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public Data() {
        }

        /**
         * 
         * @param repository
         */
        public Data(PullRequestWithCommentsResponse.Repository repository) {
            super();
            this.repository = repository;
        }

        @JsonProperty("repository")
        public PullRequestWithCommentsResponse.Repository getRepository() {
            return repository;
        }

        @JsonProperty("repository")
        public void setRepository(PullRequestWithCommentsResponse.Repository repository) {
            this.repository = repository;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(PullRequestWithCommentsResponse.Data.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("repository");
            sb.append('=');
            sb.append(((this.repository == null)?"<null>":this.repository));
            sb.append(',');
            sb.append("additionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
            sb.append(',');
            if (sb.charAt((sb.length()- 1)) == ',') {
                sb.setCharAt((sb.length()- 1), ']');
            } else {
                sb.append(']');
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = ((result* 31)+((this.repository == null)? 0 :this.repository.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof PullRequestWithCommentsResponse.Data) == false) {
                return false;
            }
            PullRequestWithCommentsResponse.Data rhs = ((PullRequestWithCommentsResponse.Data) other);
            return (((this.repository == rhs.repository)||((this.repository!= null)&&this.repository.equals(rhs.repository)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "target",
        "repository",
        "name"
    })
    public static class HeadRef {

        @JsonProperty("target")
        private PullRequestWithCommentsResponse.Target target;
        @JsonProperty("repository")
        private PullRequestWithCommentsResponse.Repository repository;
        @JsonProperty("name")
        private String name;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public HeadRef() {
        }

        /**
         * 
         * @param name
         * @param repository
         * @param target
         */
        public HeadRef(PullRequestWithCommentsResponse.Target target, PullRequestWithCommentsResponse.Repository repository, String name) {
            super();
            this.target = target;
            this.repository = repository;
            this.name = name;
        }

        @JsonProperty("target")
        public PullRequestWithCommentsResponse.Target getTarget() {
            return target;
        }

        @JsonProperty("target")
        public void setTarget(PullRequestWithCommentsResponse.Target target) {
            this.target = target;
        }

        @JsonProperty("repository")
        public PullRequestWithCommentsResponse.Repository getRepository() {
            return repository;
        }

        @JsonProperty("repository")
        public void setRepository(PullRequestWithCommentsResponse.Repository repository) {
            this.repository = repository;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(PullRequestWithCommentsResponse.HeadRef.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("target");
            sb.append('=');
            sb.append(((this.target == null)?"<null>":this.target));
            sb.append(',');
            sb.append("repository");
            sb.append('=');
            sb.append(((this.repository == null)?"<null>":this.repository));
            sb.append(',');
            sb.append("name");
            sb.append('=');
            sb.append(((this.name == null)?"<null>":this.name));
            sb.append(',');
            sb.append("additionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
            sb.append(',');
            if (sb.charAt((sb.length()- 1)) == ',') {
                sb.setCharAt((sb.length()- 1), ']');
            } else {
                sb.append(']');
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            result = ((result* 31)+((this.repository == null)? 0 :this.repository.hashCode()));
            result = ((result* 31)+((this.target == null)? 0 :this.target.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof PullRequestWithCommentsResponse.HeadRef) == false) {
                return false;
            }
            PullRequestWithCommentsResponse.HeadRef rhs = ((PullRequestWithCommentsResponse.HeadRef) other);
            return (((((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.repository == rhs.repository)||((this.repository!= null)&&this.repository.equals(rhs.repository))))&&((this.target == rhs.target)||((this.target!= null)&&this.target.equals(rhs.target))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "databaseId",
        "author",
        "authorAssociation",
        "body"
    })
    public static class Node {

        @JsonProperty("databaseId")
        private Integer databaseId;
        @JsonProperty("author")
        private PullRequestWithCommentsResponse.Author author;
        @JsonProperty("authorAssociation")
        private String authorAssociation;
        @JsonProperty("body")
        private String body;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public Node() {
        }

        /**
         * 
         * @param author
         * @param databaseId
         * @param body
         * @param authorAssociation
         */
        public Node(Integer databaseId, PullRequestWithCommentsResponse.Author author, String authorAssociation, String body) {
            super();
            this.databaseId = databaseId;
            this.author = author;
            this.authorAssociation = authorAssociation;
            this.body = body;
        }

        @JsonProperty("databaseId")
        public Integer getDatabaseId() {
            return databaseId;
        }

        @JsonProperty("databaseId")
        public void setDatabaseId(Integer databaseId) {
            this.databaseId = databaseId;
        }

        @JsonProperty("author")
        public PullRequestWithCommentsResponse.Author getAuthor() {
            return author;
        }

        @JsonProperty("author")
        public void setAuthor(PullRequestWithCommentsResponse.Author author) {
            this.author = author;
        }

        @JsonProperty("authorAssociation")
        public String getAuthorAssociation() {
            return authorAssociation;
        }

        @JsonProperty("authorAssociation")
        public void setAuthorAssociation(String authorAssociation) {
            this.authorAssociation = authorAssociation;
        }

        @JsonProperty("body")
        public String getBody() {
            return body;
        }

        @JsonProperty("body")
        public void setBody(String body) {
            this.body = body;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(PullRequestWithCommentsResponse.Node.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("databaseId");
            sb.append('=');
            sb.append(((this.databaseId == null)?"<null>":this.databaseId));
            sb.append(',');
            sb.append("author");
            sb.append('=');
            sb.append(((this.author == null)?"<null>":this.author));
            sb.append(',');
            sb.append("authorAssociation");
            sb.append('=');
            sb.append(((this.authorAssociation == null)?"<null>":this.authorAssociation));
            sb.append(',');
            sb.append("body");
            sb.append('=');
            sb.append(((this.body == null)?"<null>":this.body));
            sb.append(',');
            sb.append("additionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
            sb.append(',');
            if (sb.charAt((sb.length()- 1)) == ',') {
                sb.setCharAt((sb.length()- 1), ']');
            } else {
                sb.append(']');
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            result = ((result* 31)+((this.databaseId == null)? 0 :this.databaseId.hashCode()));
            result = ((result* 31)+((this.body == null)? 0 :this.body.hashCode()));
            result = ((result* 31)+((this.authorAssociation == null)? 0 :this.authorAssociation.hashCode()));
            result = ((result* 31)+((this.author == null)? 0 :this.author.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof PullRequestWithCommentsResponse.Node) == false) {
                return false;
            }
            PullRequestWithCommentsResponse.Node rhs = ((PullRequestWithCommentsResponse.Node) other);
            return ((((((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties)))&&((this.databaseId == rhs.databaseId)||((this.databaseId!= null)&&this.databaseId.equals(rhs.databaseId))))&&((this.body == rhs.body)||((this.body!= null)&&this.body.equals(rhs.body))))&&((this.authorAssociation == rhs.authorAssociation)||((this.authorAssociation!= null)&&this.authorAssociation.equals(rhs.authorAssociation))))&&((this.author == rhs.author)||((this.author!= null)&&this.author.equals(rhs.author))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "id",
        "body",
        "url",
        "headRef",
        "baseRefName",
        "comments",
        "commits"
    })
    public static class PullRequest {

        @JsonProperty("id")
        private String id;
        @JsonProperty("body")
        private String body;
        @JsonProperty("url")
        private String url;
        @JsonProperty("headRef")
        private PullRequestWithCommentsResponse.HeadRef headRef;
        @JsonProperty("baseRefName")
        private String baseRefName;
        @JsonProperty("comments")
        private PullRequestWithCommentsResponse.Comments comments;
        @JsonProperty("commits")
        private PullRequestWithCommentsResponse.Commits commits;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public PullRequest() {
        }

        /**
         * 
         * @param baseRefName
         * @param comments
         * @param headRef
         * @param commits
         * @param id
         * @param body
         * @param url
         */
        public PullRequest(String id, String body, String url, PullRequestWithCommentsResponse.HeadRef headRef, String baseRefName, PullRequestWithCommentsResponse.Comments comments, PullRequestWithCommentsResponse.Commits commits) {
            super();
            this.id = id;
            this.body = body;
            this.url = url;
            this.headRef = headRef;
            this.baseRefName = baseRefName;
            this.comments = comments;
            this.commits = commits;
        }

        @JsonProperty("id")
        public String getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("body")
        public String getBody() {
            return body;
        }

        @JsonProperty("body")
        public void setBody(String body) {
            this.body = body;
        }

        @JsonProperty("url")
        public String getUrl() {
            return url;
        }

        @JsonProperty("url")
        public void setUrl(String url) {
            this.url = url;
        }

        @JsonProperty("headRef")
        public PullRequestWithCommentsResponse.HeadRef getHeadRef() {
            return headRef;
        }

        @JsonProperty("headRef")
        public void setHeadRef(PullRequestWithCommentsResponse.HeadRef headRef) {
            this.headRef = headRef;
        }

        @JsonProperty("baseRefName")
        public String getBaseRefName() {
            return baseRefName;
        }

        @JsonProperty("baseRefName")
        public void setBaseRefName(String baseRefName) {
            this.baseRefName = baseRefName;
        }

        @JsonProperty("comments")
        public PullRequestWithCommentsResponse.Comments getComments() {
            return comments;
        }

        @JsonProperty("comments")
        public void setComments(PullRequestWithCommentsResponse.Comments comments) {
            this.comments = comments;
        }

        @JsonProperty("commits")
        public PullRequestWithCommentsResponse.Commits getCommits() {
            return commits;
        }

        @JsonProperty("commits")
        public void setCommits(PullRequestWithCommentsResponse.Commits commits) {
            this.commits = commits;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(PullRequestWithCommentsResponse.PullRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("id");
            sb.append('=');
            sb.append(((this.id == null)?"<null>":this.id));
            sb.append(',');
            sb.append("body");
            sb.append('=');
            sb.append(((this.body == null)?"<null>":this.body));
            sb.append(',');
            sb.append("url");
            sb.append('=');
            sb.append(((this.url == null)?"<null>":this.url));
            sb.append(',');
            sb.append("headRef");
            sb.append('=');
            sb.append(((this.headRef == null)?"<null>":this.headRef));
            sb.append(',');
            sb.append("baseRefName");
            sb.append('=');
            sb.append(((this.baseRefName == null)?"<null>":this.baseRefName));
            sb.append(',');
            sb.append("comments");
            sb.append('=');
            sb.append(((this.comments == null)?"<null>":this.comments));
            sb.append(',');
            sb.append("commits");
            sb.append('=');
            sb.append(((this.commits == null)?"<null>":this.commits));
            sb.append(',');
            sb.append("additionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
            sb.append(',');
            if (sb.charAt((sb.length()- 1)) == ',') {
                sb.setCharAt((sb.length()- 1), ']');
            } else {
                sb.append(']');
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = ((result* 31)+((this.baseRefName == null)? 0 :this.baseRefName.hashCode()));
            result = ((result* 31)+((this.comments == null)? 0 :this.comments.hashCode()));
            result = ((result* 31)+((this.headRef == null)? 0 :this.headRef.hashCode()));
            result = ((result* 31)+((this.commits == null)? 0 :this.commits.hashCode()));
            result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            result = ((result* 31)+((this.body == null)? 0 :this.body.hashCode()));
            result = ((result* 31)+((this.url == null)? 0 :this.url.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof PullRequestWithCommentsResponse.PullRequest) == false) {
                return false;
            }
            PullRequestWithCommentsResponse.PullRequest rhs = ((PullRequestWithCommentsResponse.PullRequest) other);
            return (((((((((this.baseRefName == rhs.baseRefName)||((this.baseRefName!= null)&&this.baseRefName.equals(rhs.baseRefName)))&&((this.comments == rhs.comments)||((this.comments!= null)&&this.comments.equals(rhs.comments))))&&((this.headRef == rhs.headRef)||((this.headRef!= null)&&this.headRef.equals(rhs.headRef))))&&((this.commits == rhs.commits)||((this.commits!= null)&&this.commits.equals(rhs.commits))))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.body == rhs.body)||((this.body!= null)&&this.body.equals(rhs.body))))&&((this.url == rhs.url)||((this.url!= null)&&this.url.equals(rhs.url))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "nameWithOwner",
        "pullRequest"
    })
    public static class Repository {

        @JsonProperty("nameWithOwner")
        private String nameWithOwner;
        @JsonProperty("pullRequest")
        private PullRequestWithCommentsResponse.PullRequest pullRequest;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public Repository() {
        }

        /**
         * 
         * @param nameWithOwner
         * @param pullRequest
         */
        public Repository(String nameWithOwner, PullRequestWithCommentsResponse.PullRequest pullRequest) {
            super();
            this.nameWithOwner = nameWithOwner;
            this.pullRequest = pullRequest;
        }

        @JsonProperty("nameWithOwner")
        public String getNameWithOwner() {
            return nameWithOwner;
        }

        @JsonProperty("nameWithOwner")
        public void setNameWithOwner(String nameWithOwner) {
            this.nameWithOwner = nameWithOwner;
        }

        @JsonProperty("pullRequest")
        public PullRequestWithCommentsResponse.PullRequest getPullRequest() {
            return pullRequest;
        }

        @JsonProperty("pullRequest")
        public void setPullRequest(PullRequestWithCommentsResponse.PullRequest pullRequest) {
            this.pullRequest = pullRequest;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(PullRequestWithCommentsResponse.Repository.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("nameWithOwner");
            sb.append('=');
            sb.append(((this.nameWithOwner == null)?"<null>":this.nameWithOwner));
            sb.append(',');
            sb.append("pullRequest");
            sb.append('=');
            sb.append(((this.pullRequest == null)?"<null>":this.pullRequest));
            sb.append(',');
            sb.append("additionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
            sb.append(',');
            if (sb.charAt((sb.length()- 1)) == ',') {
                sb.setCharAt((sb.length()- 1), ']');
            } else {
                sb.append(']');
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            result = ((result* 31)+((this.nameWithOwner == null)? 0 :this.nameWithOwner.hashCode()));
            result = ((result* 31)+((this.pullRequest == null)? 0 :this.pullRequest.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof PullRequestWithCommentsResponse.Repository) == false) {
                return false;
            }
            PullRequestWithCommentsResponse.Repository rhs = ((PullRequestWithCommentsResponse.Repository) other);
            return ((((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties)))&&((this.nameWithOwner == rhs.nameWithOwner)||((this.nameWithOwner!= null)&&this.nameWithOwner.equals(rhs.nameWithOwner))))&&((this.pullRequest == rhs.pullRequest)||((this.pullRequest!= null)&&this.pullRequest.equals(rhs.pullRequest))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "oid"
    })
    public static class Target {

        @JsonProperty("oid")
        private String oid;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public Target() {
        }

        /**
         * 
         * @param oid
         */
        public Target(String oid) {
            super();
            this.oid = oid;
        }

        @JsonProperty("oid")
        public String getOid() {
            return oid;
        }

        @JsonProperty("oid")
        public void setOid(String oid) {
            this.oid = oid;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(PullRequestWithCommentsResponse.Target.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("oid");
            sb.append('=');
            sb.append(((this.oid == null)?"<null>":this.oid));
            sb.append(',');
            sb.append("additionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
            sb.append(',');
            if (sb.charAt((sb.length()- 1)) == ',') {
                sb.setCharAt((sb.length()- 1), ']');
            } else {
                sb.append(']');
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = ((result* 31)+((this.oid == null)? 0 :this.oid.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof PullRequestWithCommentsResponse.Target) == false) {
                return false;
            }
            PullRequestWithCommentsResponse.Target rhs = ((PullRequestWithCommentsResponse.Target) other);
            return (((this.oid == rhs.oid)||((this.oid!= null)&&this.oid.equals(rhs.oid)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
        }

    }

}
