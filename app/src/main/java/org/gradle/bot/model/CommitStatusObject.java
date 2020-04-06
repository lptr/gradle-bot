
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
    "state",
    "target_url",
    "description",
    "context"
})
public class CommitStatusObject {

    @JsonProperty("state")
    private String state;
    @JsonProperty("target_url")
    private String targetUrl;
    @JsonProperty("description")
    private String description;
    @JsonProperty("context")
    private String context;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public CommitStatusObject() {
    }

    /**
     * 
     * @param context
     * @param description
     * @param state
     * @param targetUrl
     */
    public CommitStatusObject(String state, String targetUrl, String description, String context) {
        super();
        this.state = state;
        this.targetUrl = targetUrl;
        this.description = description;
        this.context = context;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("target_url")
    public String getTargetUrl() {
        return targetUrl;
    }

    @JsonProperty("target_url")
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("context")
    public String getContext() {
        return context;
    }

    @JsonProperty("context")
    public void setContext(String context) {
        this.context = context;
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
        sb.append(CommitStatusObject.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("state");
        sb.append('=');
        sb.append(((this.state == null)?"<null>":this.state));
        sb.append(',');
        sb.append("targetUrl");
        sb.append('=');
        sb.append(((this.targetUrl == null)?"<null>":this.targetUrl));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("context");
        sb.append('=');
        sb.append(((this.context == null)?"<null>":this.context));
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
        result = ((result* 31)+((this.context == null)? 0 :this.context.hashCode()));
        result = ((result* 31)+((this.description == null)? 0 :this.description.hashCode()));
        result = ((result* 31)+((this.state == null)? 0 :this.state.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.targetUrl == null)? 0 :this.targetUrl.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CommitStatusObject) == false) {
            return false;
        }
        CommitStatusObject rhs = ((CommitStatusObject) other);
        return ((((((this.context == rhs.context)||((this.context!= null)&&this.context.equals(rhs.context)))&&((this.description == rhs.description)||((this.description!= null)&&this.description.equals(rhs.description))))&&((this.state == rhs.state)||((this.state!= null)&&this.state.equals(rhs.state))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.targetUrl == rhs.targetUrl)||((this.targetUrl!= null)&&this.targetUrl.equals(rhs.targetUrl))));
    }

}
