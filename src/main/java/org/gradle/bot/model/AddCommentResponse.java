
package org.gradle.bot.model;

import java.util.HashMap;
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
public class AddCommentResponse {

    @JsonProperty("data")
    private AddCommentResponse.Data data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public AddCommentResponse() {
    }

    /**
     * 
     * @param data
     */
    public AddCommentResponse(AddCommentResponse.Data data) {
        super();
        this.data = data;
    }

    @JsonProperty("data")
    public AddCommentResponse.Data getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(AddCommentResponse.Data data) {
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
        sb.append(AddCommentResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        if ((other instanceof AddCommentResponse) == false) {
            return false;
        }
        AddCommentResponse rhs = ((AddCommentResponse) other);
        return (((this.data == rhs.data)||((this.data!= null)&&this.data.equals(rhs.data)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "clientMutationId",
        "subject",
        "commentEdge"
    })
    public static class AddComment {

        @JsonProperty("clientMutationId")
        private Object clientMutationId;
        @JsonProperty("subject")
        private AddCommentResponse.Subject subject;
        @JsonProperty("commentEdge")
        private AddCommentResponse.CommentEdge commentEdge;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public AddComment() {
        }

        /**
         * 
         * @param clientMutationId
         * @param subject
         * @param commentEdge
         */
        public AddComment(Object clientMutationId, AddCommentResponse.Subject subject, AddCommentResponse.CommentEdge commentEdge) {
            super();
            this.clientMutationId = clientMutationId;
            this.subject = subject;
            this.commentEdge = commentEdge;
        }

        @JsonProperty("clientMutationId")
        public Object getClientMutationId() {
            return clientMutationId;
        }

        @JsonProperty("clientMutationId")
        public void setClientMutationId(Object clientMutationId) {
            this.clientMutationId = clientMutationId;
        }

        @JsonProperty("subject")
        public AddCommentResponse.Subject getSubject() {
            return subject;
        }

        @JsonProperty("subject")
        public void setSubject(AddCommentResponse.Subject subject) {
            this.subject = subject;
        }

        @JsonProperty("commentEdge")
        public AddCommentResponse.CommentEdge getCommentEdge() {
            return commentEdge;
        }

        @JsonProperty("commentEdge")
        public void setCommentEdge(AddCommentResponse.CommentEdge commentEdge) {
            this.commentEdge = commentEdge;
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
            sb.append(AddCommentResponse.AddComment.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("clientMutationId");
            sb.append('=');
            sb.append(((this.clientMutationId == null)?"<null>":this.clientMutationId));
            sb.append(',');
            sb.append("subject");
            sb.append('=');
            sb.append(((this.subject == null)?"<null>":this.subject));
            sb.append(',');
            sb.append("commentEdge");
            sb.append('=');
            sb.append(((this.commentEdge == null)?"<null>":this.commentEdge));
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
            result = ((result* 31)+((this.clientMutationId == null)? 0 :this.clientMutationId.hashCode()));
            result = ((result* 31)+((this.subject == null)? 0 :this.subject.hashCode()));
            result = ((result* 31)+((this.commentEdge == null)? 0 :this.commentEdge.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof AddCommentResponse.AddComment) == false) {
                return false;
            }
            AddCommentResponse.AddComment rhs = ((AddCommentResponse.AddComment) other);
            return (((((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties)))&&((this.clientMutationId == rhs.clientMutationId)||((this.clientMutationId!= null)&&this.clientMutationId.equals(rhs.clientMutationId))))&&((this.subject == rhs.subject)||((this.subject!= null)&&this.subject.equals(rhs.subject))))&&((this.commentEdge == rhs.commentEdge)||((this.commentEdge!= null)&&this.commentEdge.equals(rhs.commentEdge))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "node"
    })
    public static class CommentEdge {

        @JsonProperty("node")
        private AddCommentResponse.Node node;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public CommentEdge() {
        }

        /**
         * 
         * @param node
         */
        public CommentEdge(AddCommentResponse.Node node) {
            super();
            this.node = node;
        }

        @JsonProperty("node")
        public AddCommentResponse.Node getNode() {
            return node;
        }

        @JsonProperty("node")
        public void setNode(AddCommentResponse.Node node) {
            this.node = node;
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
            sb.append(AddCommentResponse.CommentEdge.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("node");
            sb.append('=');
            sb.append(((this.node == null)?"<null>":this.node));
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
            result = ((result* 31)+((this.node == null)? 0 :this.node.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof AddCommentResponse.CommentEdge) == false) {
                return false;
            }
            AddCommentResponse.CommentEdge rhs = ((AddCommentResponse.CommentEdge) other);
            return (((this.node == rhs.node)||((this.node!= null)&&this.node.equals(rhs.node)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "addComment"
    })
    public static class Data {

        @JsonProperty("addComment")
        private AddCommentResponse.AddComment addComment;
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
         * @param addComment
         */
        public Data(AddCommentResponse.AddComment addComment) {
            super();
            this.addComment = addComment;
        }

        @JsonProperty("addComment")
        public AddCommentResponse.AddComment getAddComment() {
            return addComment;
        }

        @JsonProperty("addComment")
        public void setAddComment(AddCommentResponse.AddComment addComment) {
            this.addComment = addComment;
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
            sb.append(AddCommentResponse.Data.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("addComment");
            sb.append('=');
            sb.append(((this.addComment == null)?"<null>":this.addComment));
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
            result = ((result* 31)+((this.addComment == null)? 0 :this.addComment.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof AddCommentResponse.Data) == false) {
                return false;
            }
            AddCommentResponse.Data rhs = ((AddCommentResponse.Data) other);
            return (((this.addComment == rhs.addComment)||((this.addComment!= null)&&this.addComment.equals(rhs.addComment)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "databaseId"
    })
    public static class Node {

        @JsonProperty("databaseId")
        private Integer databaseId;
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
         * @param databaseId
         */
        public Node(Integer databaseId) {
            super();
            this.databaseId = databaseId;
        }

        @JsonProperty("databaseId")
        public Integer getDatabaseId() {
            return databaseId;
        }

        @JsonProperty("databaseId")
        public void setDatabaseId(Integer databaseId) {
            this.databaseId = databaseId;
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
            sb.append(AddCommentResponse.Node.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("databaseId");
            sb.append('=');
            sb.append(((this.databaseId == null)?"<null>":this.databaseId));
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
            result = ((result* 31)+((this.databaseId == null)? 0 :this.databaseId.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof AddCommentResponse.Node) == false) {
                return false;
            }
            AddCommentResponse.Node rhs = ((AddCommentResponse.Node) other);
            return (((this.databaseId == rhs.databaseId)||((this.databaseId!= null)&&this.databaseId.equals(rhs.databaseId)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "id"
    })
    public static class Subject {

        @JsonProperty("id")
        private String id;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         * 
         */
        public Subject() {
        }

        /**
         * 
         * @param id
         */
        public Subject(String id) {
            super();
            this.id = id;
        }

        @JsonProperty("id")
        public String getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
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
            sb.append(AddCommentResponse.Subject.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("id");
            sb.append('=');
            sb.append(((this.id == null)?"<null>":this.id));
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
            result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
            result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof AddCommentResponse.Subject) == false) {
                return false;
            }
            AddCommentResponse.Subject rhs = ((AddCommentResponse.Subject) other);
            return (((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
        }

    }

}
